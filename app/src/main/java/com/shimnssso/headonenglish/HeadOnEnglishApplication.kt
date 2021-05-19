package com.shimnssso.headonenglish

import android.app.Application
import com.shimnssso.headonenglish.data.AppContainer
import com.shimnssso.headonenglish.data.AppContainerImpl
import timber.log.Timber

class HeadOnEnglishApplication : Application() {
    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        Timber.plant(Timber.DebugTree())
    }
}