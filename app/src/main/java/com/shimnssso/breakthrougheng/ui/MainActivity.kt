package com.shimnssso.breakthrougheng.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.shimnssso.breakthrougheng.BreakThroughApplication

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appContainer = (application as BreakThroughApplication).container
        setContent {
            BreakThroughApp(appContainer)
        }
    }
}