package com.shimnssso.headonenglish.repository

import androidx.lifecycle.LiveData
import com.shimnssso.headonenglish.network.MyGoogleSheetService
import com.shimnssso.headonenglish.network.RowDataItem
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.room.LectureDatabase
import timber.log.Timber

class LectureRepository(
    private val database: LectureDatabase,
    private val network: MyGoogleSheetService,
) {
    val lectures: LiveData<List<DatabaseLecture>> = database.lectureDao.getLectures()

    suspend fun refresh(shouldCheckInterval: Boolean) {
        val globalInfo = database.lectureDao.getGlobalInfo()

        Timber.e("refresh(). shouldCheckInterval:%s, globalInfo: %s", shouldCheckInterval, globalInfo)
        if (shouldCheckInterval && System.currentTimeMillis() - globalInfo.lastUpdateTime < 24 * 60 * 60 * 1000) {
            Timber.e("refreshOrSkip(). skip refresh")
            return
        }

        val rawDataResponse = network.getRawData()
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
                        cells[0].formattedValue,  // date
                        cells[3].formattedValue,  // category
                        cells[2].formattedValue,  // title,
                        url
                    )
                )
            } else {
                val description = if (cells.size > 4) cells[4].formattedValue else null
                newCards.add(
                    DatabaseCard(
                        cells[0].formattedValue,  // date
                        cells[1].formattedValue.toInt(),  // id
                        cells[2].formattedValue,  // spelling,
                        cells[3].formattedValue,  // meaning
                        description
                    )
                )
            }
        }
        database.lectureDao.insertLectures(newLectures)
        database.lectureDao.insertCards(newCards)

        val newGlobalInfo = globalInfo.copy(lastUpdateTime = System.currentTimeMillis())
        Timber.i("update GlobalInfo to %s", newGlobalInfo)
        database.lectureDao.updateGlobalInfo(newGlobalInfo)
    }

    fun getCards(date: String): LiveData<List<DatabaseCard>> {
        return database.lectureDao.getCards(date)
    }

    fun getLecture(date: String): LiveData<DatabaseLecture> {
        return database.lectureDao.getLecture(date)
    }
}