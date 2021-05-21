package com.shimnssso.headonenglish.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shimnssso.headonenglish.network.MyGoogleSheetService
import com.shimnssso.headonenglish.network.RowDataItem
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.room.LectureDatabase
import com.shimnssso.headonenglish.utils.CellConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LectureRepository(
    private val database: LectureDatabase,
    private val network: MyGoogleSheetService,
) {
    private var curSubjectId = 0
    private var _lectures: MutableLiveData<List<DatabaseLecture>> = MutableLiveData(listOf())
    val lectures: LiveData<List<DatabaseLecture>>
        get() = _lectures

    init {
        CoroutineScope(Dispatchers.IO).launch {
            curSubjectId = database.globalDao.getGlobal().subjectId
            _lectures.postValue(database.lectureDao.getLectures(curSubjectId))
        }
    }

    suspend fun changeSubject(newSubjectId: Int) {
        withContext(Dispatchers.IO) {
            val globalInfo = database.globalDao.getGlobal()
            curSubjectId = newSubjectId
            database.globalDao.update(globalInfo.copy(subjectId = curSubjectId))
            _lectures.postValue(database.lectureDao.getLectures(curSubjectId))
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
        val newLectures = mutableListOf<DatabaseLecture>()
        val newCards = mutableListOf<DatabaseCard>()
        for (row: RowDataItem in rawDataResponse.sheets[0].data[0].rowData) {
            val cells = row.values
            if (cells[1].formattedValue == "id") {
                continue
            } else if (cells[1].formattedValue == "0") {  // title row
                val url = if (cells.size > 4) cells[4].formattedValue else null
                newLectures.add(
                    DatabaseLecture(
                        cells[0].formattedValue!!,  // date
                        cells[3].formattedValue!!,  // category
                        cells[2].formattedValue!!,  // title,
                        url,

                        globalInfo.subjectId,
                    )
                )
            } else {
                val description = if (cells.size > 4) cells[4].formattedValue else null
                val meaning = if (cells.size > 3) cells[3].formattedValue else null
                newCards.add(
                    DatabaseCard(
                        cells[0].formattedValue!!,  // date
                        cells[1].formattedValue!!.toInt(),  // id
                        CellConverter.toJsonStr(cells[2]),  // spelling,
                        meaning,  // meaning
                        description,

                        globalInfo.subjectId,
                    )
                )
            }
        }
        database.lectureDao.insertLectures(newLectures)
        database.lectureDao.insertCards(newCards)

        val newSubject = subject.copy(lastUpdateTime = System.currentTimeMillis())
        Timber.i("update current subject to %s", newSubject)
        database.subjectDao.update(newSubject)

        _lectures.postValue(database.lectureDao.getLectures(globalInfo.subjectId))
    }

    fun getCards(subjectId: Int, date: String): LiveData<List<DatabaseCard>> {
        return database.lectureDao.getCards(subjectId, date)
    }

    fun getLecture(date: String): LiveData<DatabaseLecture> {
        return database.lectureDao.getLecture(date)
    }
}