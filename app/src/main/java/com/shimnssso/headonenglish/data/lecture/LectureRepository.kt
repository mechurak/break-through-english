package com.shimnssso.headonenglish.data.lecture

import com.shimnssso.headonenglish.data.Result
import com.shimnssso.headonenglish.model.Lecture
import com.shimnssso.headonenglish.network.RawDataResponse

interface LectureRepository {
    suspend fun getLectures(): Result<List<Lecture>>
    suspend fun getLecture(date: String): Result<Lecture?>
    suspend fun getRawData(): RawDataResponse
}