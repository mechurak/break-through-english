package com.shimnssso.headonenglish.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.shimnssso.headonenglish.googlesheet.SheetHelper
import com.shimnssso.headonenglish.network.MyGoogleSheetService
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.room.DatabaseGlobal
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.room.DatabaseSubject
import com.shimnssso.headonenglish.room.LectureDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LectureRepository(
    private val database: LectureDatabase,
    private val network: MyGoogleSheetService,
) {
    private val _currentGlobal: LiveData<DatabaseGlobal?> = database.globalDao.currentData()
    val currentGlobal: LiveData<DatabaseGlobal> =
        Transformations.map(_currentGlobal) {
            it ?: DatabaseGlobal(0, 0)
        }

    val subjects = database.subjectDao.getSubjects()

    private val _currentSubject: LiveData<DatabaseSubject?> =
        Transformations.switchMap(currentGlobal) {
            database.subjectDao.currentSubject(it.subjectId)
        }

    val currentSubject: LiveData<DatabaseSubject> =
        Transformations.map(_currentSubject) {
            it ?: DatabaseSubject(0, "Temp Subject", "", 0L, "")
        }

    val lectures: LiveData<List<DatabaseLecture>> =
        Transformations.switchMap(currentGlobal) {
            database.lectureDao.getLectures(it.subjectId)
        }

    suspend fun changeSubject(newSubjectId: Int) {
        withContext(Dispatchers.IO) {
            val newGlobalInfo = currentGlobal.value!!.copy(subjectId = newSubjectId)
            Timber.e("newGlobalInfo: %s", newGlobalInfo)
            database.globalDao.update(newGlobalInfo)
            refresh(true)
        }
    }

    suspend fun refresh(shouldCheckInterval: Boolean) {
        val globalInfo = database.globalDao.getGlobal()
        val subject = database.subjectDao.getSubject(globalInfo.subjectId)

        Timber.e("refresh(). shouldCheckInterval:%s, subject: %s", shouldCheckInterval, subject)
        if (shouldCheckInterval && System.currentTimeMillis() - subject.lastUpdateTime < 24 * 60 * 60 * 1000) {
            Timber.e("refreshOrSkip(). skip refresh")
            return
        }

        val remainedLectureMap = mutableMapOf<String, DatabaseLecture>()
        val newLectures = mutableListOf<DatabaseLecture>()
        val updateLectures = mutableListOf<DatabaseLecture>()

        val prevLectures = database.lectureDao.getLecturesNormal(subject.subjectId)

        prevLectures.forEach {
            remainedLectureMap[it.date] = it
        }

        val spreadsheet: Spreadsheet = SheetHelper.fetchSpreadsheet(subject.sheetId)
        val newCards = SheetHelper.getLectureCardListPair(
            spreadsheet,
            subject.subjectId,
            remainedLectureMap,
            newLectures,
            updateLectures
        )

        Timber.d("remainedLectureMap.size: ${remainedLectureMap.size}")
        Timber.d("newLectures.size: ${newLectures.size}")
        Timber.d("updateLectures.size: ${updateLectures.size}")

        var isUpdated = false

        val remainedLectures = remainedLectureMap.values.toList()
        if (remainedLectures.isNotEmpty()) {
            database.lectureDao.deleteLectures(remainedLectures)
            isUpdated = true
        }
        if (updateLectures.isNotEmpty()) {
            database.lectureDao.updateLectures(updateLectures)
            isUpdated = true
        }
        if (newLectures.isNotEmpty()) {
            database.lectureDao.insertLectures(newLectures)
            isUpdated = true
        }

        if (newCards.isNotEmpty()) {
            database.lectureDao.clearCards(globalInfo.subjectId)
            database.lectureDao.insertCards(newCards)
            isUpdated = true
        }

        if (isUpdated) {
            val newSubject = subject.copy(lastUpdateTime = System.currentTimeMillis())
            Timber.i("update current subject to %s", newSubject)
            database.subjectDao.update(newSubject)
        } else {
            Timber.e("no update")
            Timber.e("newLectures.size: ${newLectures.size}")
            Timber.e("newCards.size: ${newCards.size}")
        }
    }

    fun getCards(subjectId: Int, date: String): LiveData<List<DatabaseCard>> {
        return database.lectureDao.getCards(subjectId, date)
    }

    fun getLecture(date: String): LiveData<DatabaseLecture> {
        return database.lectureDao.getLecture(date)
    }

    suspend fun createSubject(name: String, spreadsheetId: String): Int {
        Timber.e("name: $name, spreadsheetId: $spreadsheetId")
        val newSubject = DatabaseSubject(
            subjectId = 0,
            title = name,
            sheetId = spreadsheetId,
            lastUpdateTime = 0L,
            link = null,
        )
        val rowId = database.subjectDao.insert(newSubject)
        Timber.e("rowId: $rowId")
        val subjectId = database.subjectDao.getSubjectId(rowId)
        Timber.e("subjectId: $subjectId")

        return subjectId.toInt()
    }

    suspend fun importSpreadsheet(spreadsheet: Spreadsheet, subjectId: Int) {
        val remainedLectureMap = mutableMapOf<String, DatabaseLecture>()
        val newLectures = mutableListOf<DatabaseLecture>()
        val updateLectures = mutableListOf<DatabaseLecture>()
        val newCards =
            SheetHelper.getLectureCardListPair(spreadsheet, subjectId, remainedLectureMap, newLectures, updateLectures)

        Timber.d("remainedLectureMap.size: ${remainedLectureMap.size}")
        Timber.d("newLectures.size: ${newLectures.size}")
        Timber.d("updateLectures.size: ${updateLectures.size}")

        database.lectureDao.insertLectures(newLectures)
        database.lectureDao.insertCards(newCards)

        val subject = database.subjectDao.getSubject(subjectId)
        val newSubject = subject.copy(lastUpdateTime = System.currentTimeMillis())
        Timber.i("importSpreadsheet() %s", newSubject)
        database.subjectDao.update(newSubject)

        changeSubject(subjectId)
    }
}