package com.shimnssso.headonenglish.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.shimnssso.headonenglish.googlesheet.SheetException
import com.shimnssso.headonenglish.googlesheet.SheetHelper
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.room.DatabaseGlobal
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.room.DatabaseSubject
import com.shimnssso.headonenglish.room.FakeData
import com.shimnssso.headonenglish.room.LectureDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LectureRepository(
    private val database: LectureDatabase,
) {
    private val _currentGlobal: LiveData<DatabaseGlobal?> = database.globalDao.currentData()
    val currentGlobal: LiveData<DatabaseGlobal> =
        Transformations.map(_currentGlobal) {
            it ?: DatabaseGlobal(1, 1)
        }

    val subjects = database.subjectDao.getSubjects()

    private val _currentSubject: LiveData<DatabaseSubject?> =
        Transformations.switchMap(currentGlobal) {
            database.subjectDao.currentSubject(it.subjectId)
        }

    val currentSubject: LiveData<DatabaseSubject> =
        Transformations.map(_currentSubject) {
            it ?: FakeData.DEFAULT_SUBJECTS[0]
        }

    val lectures: LiveData<List<DatabaseLecture>> =
        Transformations.switchMap(currentGlobal) {
            database.lectureDao.getLectures(it.subjectId)
        }

    val recentLecture: LiveData<DatabaseLecture> =
        Transformations.switchMap(currentGlobal) {
            database.lectureDao.getRecentLecture(it.subjectId)
        }

    suspend fun changeSubject(newSubjectId: Int) {
        withContext(Dispatchers.IO) {
            val newGlobalInfo = currentGlobal.value!!.copy(subjectId = newSubjectId)
            Timber.i("newGlobalInfo: %s", newGlobalInfo)
            database.globalDao.update(newGlobalInfo)
        }
    }

    suspend fun removeSubject(subjectId: Int) {
        withContext(Dispatchers.IO) {
            database.subjectDao.deleteSubject(subjectId)
            Timber.i("remove subjectId: $subjectId")
            val tempSubject = database.subjectDao.getFirstSubject()
            Timber.i("tempSubject: $tempSubject")
            changeSubject(tempSubject.subjectId)
        }
    }

    suspend fun refresh(shouldCheckInterval: Boolean) {
        val globalInfo = database.globalDao.getGlobal()
        val subject = database.subjectDao.getSubject(globalInfo.subjectId)

        Timber.i("refresh(). shouldCheckInterval:%s, subject: %s", shouldCheckInterval, subject)
        if (shouldCheckInterval && System.currentTimeMillis() - subject.lastUpdateTime < 2 * 24 * 60 * 60 * 1000) {
            Timber.i("refreshOrSkip(). skip refresh")
            return
        }

        val remainedLectureMap = mutableMapOf<String, DatabaseLecture>()
        val newLectures = mutableListOf<DatabaseLecture>()
        val updateLectures = mutableListOf<DatabaseLecture>()
        val newCards = mutableListOf<DatabaseCard>()

        val prevLectures = database.lectureDao.getLecturesNormal(subject.subjectId)

        prevLectures.forEach {
            remainedLectureMap[it.date] = it
        }

        try {
            val spreadsheet: Spreadsheet = SheetHelper.fetchSpreadsheet(subject.sheetId)
            val newSubject = SheetHelper.getLectureCardListPair(
                spreadsheet,
                subject,
                remainedLectureMap,
                newLectures,
                updateLectures,
                newCards
            )

            Timber.d("remainedLectureMap.size: ${remainedLectureMap.size}")
            Timber.d("newLectures.size: ${newLectures.size}")
            Timber.d("updateLectures.size: ${updateLectures.size}")

            if (newSubject == null) {
                Timber.e("newSubject == null!!!!")
                throw SheetException("no 'doc_info' sheet. Please make sure the doc has 'doc_info' sheet")
            }

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

            if (newSubject.subjectForUrl != null) {
                Timber.d("process for newSubject.subjectForUrl. ${newSubject.subjectForUrl}")
                val lectures = database.lectureDao.getLecturesNormal(subject.subjectId)
                val retLectures = SheetHelper.updateRemoteUrl(newSubject.subjectForUrl, lectures)
                database.lectureDao.updateLectures(retLectures)
            }

            if (newCards.isNotEmpty()) {
                database.lectureDao.clearCards(globalInfo.subjectId)
                database.lectureDao.insertCards(newCards)
                isUpdated = true
            }

            if (isUpdated) {
                Timber.i("update current subject to %s", newSubject)
                database.subjectDao.update(newSubject)
            } else {
                Timber.e("no update")
                Timber.e("newLectures.size: ${newLectures.size}")
                Timber.e("newCards.size: ${newCards.size}")
            }
        } catch (e: SheetException) {
            throw e
        }
    }

    fun getCards(subjectId: Int, date: String): LiveData<List<DatabaseCard>> {
        return database.lectureDao.getCards(subjectId, date)
    }

    fun getCardsForQuiz(subjectId: Int, date: String): LiveData<List<DatabaseCard>> {
        return database.lectureDao.getCardsForQuiz(subjectId, date)
    }

    fun getLecture(subjectId: Int, date: String): LiveData<DatabaseLecture> {
        return database.lectureDao.getLecture(subjectId, date)
    }

    suspend fun createSubject(name: String, spreadsheetId: String): Int {
        Timber.i("name: $name, spreadsheetId: $spreadsheetId")
        val newSubject = DatabaseSubject(
            subjectId = 0,
            title = name,
            sheetId = spreadsheetId,
            lastUpdateTime = 0L,
        )
        val rowId = database.subjectDao.insert(newSubject)
        Timber.d("rowId: $rowId")
        val subjectId = database.subjectDao.getSubjectId(rowId)
        Timber.d("subjectId: $subjectId")

        return subjectId.toInt()
    }

    suspend fun importSpreadsheet(spreadsheet: Spreadsheet, subjectId: Int) {
        val remainedLectureMap = mutableMapOf<String, DatabaseLecture>()
        val newLectures = mutableListOf<DatabaseLecture>()
        val updateLectures = mutableListOf<DatabaseLecture>()
        val subject = database.subjectDao.getSubject(subjectId)
        val newCards = mutableListOf<DatabaseCard>()

        try {
            val newSubject =
                SheetHelper.getLectureCardListPair(
                    spreadsheet,
                    subject,
                    remainedLectureMap,
                    newLectures,
                    updateLectures,
                    newCards
                )

            Timber.d("remainedLectureMap.size: ${remainedLectureMap.size}")
            Timber.d("newLectures.size: ${newLectures.size}")
            Timber.d("updateLectures.size: ${updateLectures.size}")

            if (newSubject == null) {
                Timber.e("newSubject == null!!!!")
                throw SheetException("no 'doc_info' sheet. Please make sure the doc has 'doc_info' sheet")
            }

            database.lectureDao.insertLectures(newLectures)
            database.lectureDao.insertCards(newCards)

            if (newSubject.subjectForUrl != null) {
                Timber.e("process for newSubject.subjectForUrl. ${newSubject.subjectForUrl}")
                val lectures = database.lectureDao.getLecturesNormal(subject.subjectId)
                val retLectures = SheetHelper.updateRemoteUrl(newSubject.subjectForUrl, lectures)
                database.lectureDao.updateLectures(retLectures)
            }

            Timber.i("importSpreadsheet() %s", newSubject)
            database.subjectDao.update(newSubject)
        } catch (e: SheetException) {
            database.subjectDao.deleteSubject(subject.subjectId)
            throw e
        }

        changeSubject(subjectId)
    }

    suspend fun updateLecture(lecture: DatabaseLecture) {
        database.lectureDao.updateLecture(lecture)
    }
}