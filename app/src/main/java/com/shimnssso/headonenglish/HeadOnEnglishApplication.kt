package com.shimnssso.headonenglish

import android.app.Application
import timber.log.Timber

class HeadOnEnglishApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        Timber.plant(Timber.DebugTree())
    }
}