package com.shimnssso.headonenglish.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.shimnssso.headonenglish.network.MyGoogleSheetService
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.room.DatabaseGlobal
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.room.DatabaseSubject
import com.shimnssso.headonenglish.room.LectureDatabase
import com.shimnssso.headonenglish.utils.CellConverter
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
            // DatabaseSubject(0, "", 0L, "", "", true)
            // database.subjectDao.currentSubject(it.subjectId)
        }

    val currentSubject: LiveData<DatabaseSubject> =
        Transformations.map(_currentSubject) {
            it ?: DatabaseSubject(0, "Temp Subject", 0L, "", "", true)
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

        val rawDataResponse = network.getRawData(subject.sheetId)
        val gridProps = rawDataResponse.sheets[0].properties.gridProperties
        val newLectures = mutableListOf<DatabaseLecture>()
        val newCards = mutableListOf<DatabaseCard>()

        var idxDate = 0
        var idxOrder = 1
        var idxText = 2
        var idxNote = 3
        var idxMemo = 4

        var idxMetaTitle = 2
        var idxMetaCategory = -1  // optional
        var idxMetaRemoteUrl = -1  // optional
        var idxMetaLink1 = -1  // optional
        var idxMetaLink2 = -1  // optional

        rawDataResponse.sheets[0].data[0].rowData.forEachIndexed { rowIndex, row ->
            val cells = row.values
            if (rowIndex < gridProps.frozenRowCount ?: 1) {
                cells.forEachIndexed { i, cell ->
                    when (cell.formattedValue) {
                        "date", "day" -> {
                            idxDate = i
                        }
                        "order" -> {
                            idxOrder = i
                        }
                        "text" -> {
                            idxText = i
                        }
                        "note" -> {
                            idxNote = i
                        }
                        "memo" -> {
                            idxMemo = i
                        }
                        "title" -> {
                            idxMetaTitle = i
                        }
                        "category" -> {
                            idxMetaCategory = i
                        }
                        "remoteUrl" -> {
                            idxMetaRemoteUrl = i
                        }
                        "link1" -> {
                            idxMetaLink1 = i
                        }
                        "link2" -> {
                            idxMetaLink2 = i
                        }
                        else -> {
                            Timber.w(
                                "unknown keyword (%s). skip it. rowIndex:%d, cell[%d]",
                                cell.formattedValue,
                                rowIndex,
                                i
                            )
                        }
                    }
                }
            } else if (cells[idxOrder].formattedValue == "0") {  // title row
                val category =
                    if (idxMetaCategory > 0 && cells.size > idxMetaCategory) cells[idxMetaCategory].formattedValue else null
                val remoteUrl =
                    if (idxMetaRemoteUrl > 0 && cells.size > idxMetaRemoteUrl) cells[idxMetaRemoteUrl].formattedValue else null
                val link1 =
                    if (idxMetaLink1 > 0 && cells.size > idxMetaLink1) cells[idxMetaLink1].formattedValue else null
                val link2 =
                    if (idxMetaLink2 > 0 && cells.size > idxMetaLink2) cells[idxMetaLink2].formattedValue else null
                newLectures.add(
                    DatabaseLecture(
                        subjectId = globalInfo.subjectId,
                        date = cells[idxDate].formattedValue!!,
                        title = cells[idxMetaTitle].formattedValue!!,
                        category = category,
                        remoteUrl = remoteUrl,
                        localUrl = null,  // TODO: Update existing data
                        link1 = link1,
                        link2 = link2,
                    )
                )
            } else {
                val note = if (cells.size > idxNote) cells[idxNote].formattedValue else null
                val memo = if (cells.size > idxMemo) cells[idxMemo].formattedValue else null
                newCards.add(
                    DatabaseCard(
                        subjectId = globalInfo.subjectId,
                        date = cells[idxDate].formattedValue!!,
                        order = cells[idxOrder].formattedValue!!.toInt(),
                        text = CellConverter.toJsonStr(cells[idxText]),
                        note = note,
                        memo = memo,
                    )
                )
            }
        }

        if (newLectures.isNotEmpty() && newCards.isNotEmpty()) {
            database.lectureDao.clearLectures(globalInfo.subjectId)
            database.lectureDao.clearCards(globalInfo.subjectId)
            database.lectureDao.insertLectures(newLectures)
            database.lectureDao.insertCards(newCards)

            val newSubject = subject.copy(lastUpdateTime = System.currentTimeMillis())
            Timber.i("update current subject to %s", newSubject)
            database.subjectDao.update(newSubject)
        } else {
            Timber.e("empty lectures or empty cards")
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
}