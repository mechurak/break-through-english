package com.shimnssso.breakthrougheng.data

import android.content.Context
import com.shimnssso.breakthrougheng.data.lecture.LectureRepository
import com.shimnssso.breakthrougheng.data.lecture.impl.MyLectureRepository


/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val lectureRepository: LectureRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val lectureRepository: LectureRepository by lazy {
        MyLectureRepository()
    }
}