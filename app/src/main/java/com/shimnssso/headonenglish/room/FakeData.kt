package com.shimnssso.headonenglish.room

class FakeData {
    companion object {
        val DEFAULT_SUBJECT = DatabaseSubject(
            subjectId = 0,
            title = "정면돌파 스피킹",
            sheetId = "1veQzV0fyYHO_4Lu2l33ZRXbjy47_q8EI1nwVAQXJcVQ",
            lastUpdateTime = 0L,
            link = "https://home.ebse.co.kr/10mins_lee2/",
        )

        val DEFAULT_LECTURE = DatabaseLecture(
            subjectId = 0,
            date = "2021-05-12",
            title = "발음 강세 Unit 553. 체중",
            category = "Maintaining Our Health",
            remoteUrl = null,
            localUrl = null,
            link1 = null,
            link2 = null
        )
    }
}