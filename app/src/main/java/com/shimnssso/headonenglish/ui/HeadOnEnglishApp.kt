package com.shimnssso.headonenglish.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shimnssso.headonenglish.room.DatabaseGlobal
import com.shimnssso.headonenglish.ui.theme.HeadOnEnglishTheme
import kotlinx.coroutines.launch

@Composable
fun HeadOnEnglishApp() {
    HeadOnEnglishTheme {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
            }

            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()
            // This top level scaffold contains the app drawer, which needs to be accessible
            // from multiple screens. An event to open the drawer is passed down to each
            // screen that needs it.
            val scaffoldState = rememberScaffoldState()

            val viewModel = viewModel(GlobalViewModel::class.java)
            val globalData by viewModel.currentGlobal.observeAsState(DatabaseGlobal(0))
            val subjects by viewModel.subjects.observeAsState(listOf())

            Scaffold(
                scaffoldState = scaffoldState,
                drawerContent = {
                    AppDrawer(
                        selectedId = globalData.subjectId,
                        subjects = subjects,
                        navigateToHome = { navController.navigate(MainDestinations.HOME_ROUTE) },
                        closeDrawer = { coroutineScope.launch { scaffoldState.drawerState.close() } },
                        changeSubject = { subjectId -> viewModel.changeSubject(subjectId) }
                    )
                }
            ) {
                HeadOnEnglishNavGraph(
                    navController = navController,
                    scaffoldState = scaffoldState
                )
            }
        }
    }
}
