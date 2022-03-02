package com.shimnssso.headonenglish.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shimnssso.headonenglish.ui.theme.HeadOnEnglishTheme

@Composable
fun HeadOnEnglishApp() {
    HeadOnEnglishTheme {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
            }

            val navController = rememberNavController()

            HeadOnEnglishNavGraph(
                navController = navController,
            )
        }
    }
}
