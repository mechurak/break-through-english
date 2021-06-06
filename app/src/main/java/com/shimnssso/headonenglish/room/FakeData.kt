package com.shimnssso.headonenglish.room

class FakeData {
    companion object {
        val DEFAULT_SUBJECT = DatabaseSubject(
            subjectId = 1,
            title = "정면돌파 스피킹",
            sheetId = "1veQzV0fyYHO_4Lu2l33ZRXbjy47_q8EI1nwVAQXJcVQ",
            lastUpdateTime = 0L,
            link = "https://home.ebse.co.kr/10mins_lee2/",
        )

        val DEFAULT_SUBJECTS = listOf(
            DatabaseSubject(
                subjectId = 1,
                title = "정면돌파 스피킹_template",
                sheetId = "1veQzV0fyYHO_4Lu2l33ZRXbjy47_q8EI1nwVAQXJcVQ",
                lastUpdateTime = 0L,
                link = "https://home.ebse.co.kr/10mins_lee2/main",
                image = "https://static.ebs.co.kr/images/public/courses/2021/02/19/20/ER2017H0SPE01ZZ/8f8797ce-8085-4a0f-9681-4df159c3de17.jpg"
            ),
            DatabaseSubject(
                subjectId = 2,
                title = "입트영 최고의 스피킹 60 (일상생활편)_template",
                sheetId = "1GeK1Kz8GycGMYviq52sqV3-WKoI8Gw7llSOvJekp01s",
                lastUpdateTime = 0L,
                link = "https://book.naver.com/bookdb/book_detail.nhn?bid=16744854",
                image = "http://image.kyobobook.co.kr/images/book/xlarge/937/x9788954753937.jpg"
            ),
        )

        val DEFAULT_GLOBAL = DatabaseGlobal(
            id = 1,
            subjectId = 1,
        )

        val DEFAULT_LECTURE = DatabaseLecture(
            subjectId = 1,
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