package com.shimnssso.headonenglish.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.shimnssso.headonenglish.ui.MainDestinations.DATE_KEY
import com.shimnssso.headonenglish.ui.home.HomeScreen
import com.shimnssso.headonenglish.ui.lecture.LectureScreen
import kotlinx.coroutines.launch


/**
 * Destinations used in the ([HeadOnEnglishApp]).
 */
object MainDestinations {
    const val HOME_ROUTE = "home"
    const val LECTURE_ROUTE = "lecture"
    const val SIGN_IN_ROUTE = "sign-in"

    const val DATE_KEY = "date"
    const val TITLE_KEY = "title"
    const val URL_KEY = "url"
}

@Composable
fun HeadOnEnglishNavGraph(
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = MainDestinations.HOME_ROUTE
) {
    val actions = remember(navController) { MainActions(navController) }
    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            HomeScreen(
                navigateToArticle = actions.navigateToArticle,
                openDrawer = openDrawer
            )
        }
        composable("${MainDestinations.LECTURE_ROUTE}/{$DATE_KEY}") { backStackEntry ->
            LectureScreen(
                date = backStackEntry.arguments?.getString(DATE_KEY),
                onBack = actions.upPress,
            )
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val navigateToArticle: (String) -> Unit = { postId: String ->
        navController.navigate("${MainDestinations.LECTURE_ROUTE}/$postId")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}
