package com.shimnssso.headonenglish.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.shimnssso.headonenglish.HeadOnEnglishApplication

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appContainer = (application as HeadOnEnglishApplication).container
        setContent {
            BreakThroughApp(appContainer)
        }
    }
}