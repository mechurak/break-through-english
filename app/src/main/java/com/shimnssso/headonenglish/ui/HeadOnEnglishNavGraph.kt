package com.shimnssso.headonenglish.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shimnssso.headonenglish.ui.MainDestinations.DATE_KEY
import com.shimnssso.headonenglish.ui.MainDestinations.SUBJECT_KEY
import com.shimnssso.headonenglish.ui.home.HomeScreen
import com.shimnssso.headonenglish.ui.lecture.LectureScreen

/**
 * Destinations used in the ([HeadOnEnglishApp]).
 */
object MainDestinations {
    const val HOME_ROUTE = "home"
    const val LECTURE_ROUTE = "lecture"
    const val SIGN_IN_ROUTE = "sign-in"

    const val SUBJECT_KEY = "subject"
    const val DATE_KEY = "date"
    const val TITLE_KEY = "title"
    const val URL_KEY = "url"
}

@ExperimentalAnimationApi
@Composable
fun HeadOnEnglishNavGraph(
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = MainDestinations.HOME_ROUTE
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            HomeScreen(
                navigateToLecture = actions.navigateToLecture,
            )
        }
        composable("${MainDestinations.LECTURE_ROUTE}?$DATE_KEY={$DATE_KEY}&$SUBJECT_KEY={$SUBJECT_KEY}") { backStackEntry ->
            LectureScreen(
                subject = backStackEntry.arguments?.getString(SUBJECT_KEY),
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
    val navigateToLecture: (Int, String) -> Unit = { subjectId, date: String ->
        navController.navigate("${MainDestinations.LECTURE_ROUTE}?$DATE_KEY=$date&$SUBJECT_KEY=$subjectId")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}
