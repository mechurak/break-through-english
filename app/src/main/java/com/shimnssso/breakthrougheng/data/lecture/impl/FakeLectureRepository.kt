package com.shimnssso.breakthrougheng.data.lecture.impl

import com.shimnssso.breakthrougheng.data.Result
import com.shimnssso.breakthrougheng.data.lecture.LectureRepository
import com.shimnssso.breakthrougheng.model.Lecture
import com.shimnssso.breakthrougheng.model.Row

class FakeLectureRepository : LectureRepository {
    private val rowsOne by lazy {
        listOf(
            Row(1, "Test spelling 1", "test meaning 1", ""),
            Row(2, "Test spelling 2", "test meaning 2", ""),
            Row(3, "Test spelling 3", "test meaning 3", ""),
        )
    }

    private val rowsTwo by lazy {
        listOf(
            Row(1, "test spelling 1", "Test meaning 1", ""),
            Row(2, "test spelling 2", "Test meaning 2", ""),
            Row(3, "test spelling 3", "Test meaning 3", ""),
        )
    }

    private val lectures by lazy {
        listOf(
            Lecture(
                "2021.05.12",
                "발음 강세 Unit 553. 체중",
                "https://m4strssl.ebse.co.kr/2021/er2017h0spe01zz/1m/20210512_063000_6b6f5fd4_m10.mp4",
                rowsOne
            ),
            Lecture(
                "2021.05.13",
                "발음 강세 Unit 554. 운동",
                "https://m4strssl.ebse.co.kr/2021/er2017h0spe01zz/1m/20210513_063000_26343de3_m10.mp4",
                rowsOne
            ),
        )
    }


    override suspend fun getLectures(): Result<List<Lecture>> {
        return Result.Success(lectures)
    }

    override suspend fun getLecture(date: String): Result<Lecture> {
        return Result.Success(lectures[0])
    }
}