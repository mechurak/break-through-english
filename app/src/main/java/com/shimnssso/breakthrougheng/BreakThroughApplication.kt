package com.shimnssso.breakthrougheng

import android.app.Application
import com.shimnssso.breakthrougheng.data.AppContainer
import com.shimnssso.breakthrougheng.data.AppContainerImpl

class BreakThroughApplication : Application() {
    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}