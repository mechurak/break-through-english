package com.shimnssso.breakthrougheng.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shimnssso.breakthrougheng.getOrAwaitValue
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LectureDaoTest : TestCase() {

    private lateinit var lectureDao: LectureDao
    private lateinit var db: LectureDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    public override fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context, LectureDatabase::class.java
        ).build()
        lectureDao = db.lectureDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private val tempLectures = listOf(
        DatabaseLecture(
            "2021-05-12",
            "Maintaining Our Health",
            "발음 강세 Unit 553. 체중",
            "https://m4strssl.ebse.co.kr/2021/er2017h0spe01zz/1m/20210512_063000_6b6f5fd4_m10.mp4"
        ),
        DatabaseLecture(
            "2021-05-13",
            "Maintaining Our Health",
            "발음 강세 Unit 554. 운동",
            "https://m4strssl.ebse.co.kr/2021/er2017h0spe01zz/1m/20210513_063000_26343de3_m10.mp4"
        ),
    )

    private val tempCards = listOf(
        DatabaseCard("2021-05-12", 1, "test spelling1", "test meaning1", "test description1"),
        DatabaseCard("2021-05-12", 2, "test spelling2", "test meaning2", "test description2"),
        DatabaseCard("2021-05-13", 1, "13 test spelling1", "13 test meaning1", "13 test description1"),
        DatabaseCard("2021-05-13", 2, "13 test spelling2", "13 test meaning2", "13 test description2"),
    )

    @Test
    fun insertLecturesTest()= runBlockingTest {
        lectureDao.insertLectures(tempLectures)

        val lectures = lectureDao.getLectures().getOrAwaitValue()
        Timber.d(lectures.toString())
        assertTrue(lectures.containsAll(tempLectures))
    }

    @Test
    fun insertCardsTest() = runBlockingTest {
        lectureDao.insertCards(tempCards)

        val cards = lectureDao.getCards("2021-05-12").getOrAwaitValue()
        Timber.d(cards.toString())
        assertTrue(cards.containsAll(tempCards.subList(0, 1)))
    }

    @Test
    fun deleteTest() = runBlockingTest {
        lectureDao.insertLectures(tempLectures)
        lectureDao.insertCards(tempCards)
        val lecturesBefore = lectureDao.getLectures().getOrAwaitValue()
        val cardsBefore = lectureDao.getCards("2021-05-12").getOrAwaitValue()
        Timber.d(lecturesBefore.toString())
        assertTrue(lecturesBefore.containsAll(tempLectures))
        Timber.d(cardsBefore.toString())
        assertTrue(cardsBefore.containsAll(tempCards.subList(0, 1)))

        lectureDao.deleteCards(tempLectures[0].date)
        lectureDao.deleteLecture(tempLectures[0])

        val lecturesAfter = lectureDao.getLectures().getOrAwaitValue()
        val cardsAfter = lectureDao.getCards("2021-05-12").getOrAwaitValue()
        Timber.d(lecturesAfter.toString())
        assertTrue(lecturesAfter.containsAll(tempLectures.subList(1,1)))
        Timber.d(cardsAfter.toString())
        assertTrue(cardsAfter.isEmpty())
    }
}