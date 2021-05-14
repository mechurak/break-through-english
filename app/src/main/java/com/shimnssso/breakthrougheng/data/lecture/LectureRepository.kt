package com.shimnssso.breakthrougheng.data.lecture

import com.shimnssso.breakthrougheng.data.Result
import com.shimnssso.breakthrougheng.model.Lecture

interface LectureRepository {
    suspend fun getLectures(): Result<List<Lecture>>
    suspend fun getLecture(date: String): Result<Lecture?>
}