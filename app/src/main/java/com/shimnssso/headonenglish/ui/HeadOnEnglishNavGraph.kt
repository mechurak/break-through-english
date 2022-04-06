package com.shimnssso.headonenglish.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shimnssso.headonenglish.ui.MainDestinations.DATE_KEY
import com.shimnssso.headonenglish.ui.MainDestinations.SUBJECT_KEY
import com.shimnssso.headonenglish.ui.daylist.DayListScreen
import com.shimnssso.headonenglish.ui.lecture.LectureScreen
import com.shimnssso.headonenglish.ui.quiz.bold.BoldQuizScreen
import com.shimnssso.headonenglish.ui.subject.SubjectScreen

/**
 * Destinations used in the ([HeadOnEnglishApp]).
 */
object MainDestinations {
    const val SELECT_ROUTE = "select"
    const val DAY_LIST_ROUTE = "dayList"
    const val LECTURE_ROUTE = "lecture"
    const val QUIZ_ROUTE = "quiz"

    const val SUBJECT_KEY = "subject"
    const val DATE_KEY = "date"
}

@Composable
fun HeadOnEnglishNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.SELECT_ROUTE
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.SELECT_ROUTE) {
            SubjectScreen(
                navigateToDayList = actions.navigateToDayList,
            )
        }
        composable(MainDestinations.DAY_LIST_ROUTE) {
            DayListScreen(
                navigateToLecture = actions.navigateToLecture,
                navigateToQuiz = actions.navigateToQuiz,
                onBack = actions.upPress,
            )
        }
        composable("${MainDestinations.LECTURE_ROUTE}?$DATE_KEY={$DATE_KEY}&$SUBJECT_KEY={$SUBJECT_KEY}") { backStackEntry ->
            LectureScreen(
                subject = backStackEntry.arguments?.getString(SUBJECT_KEY),
                date = backStackEntry.arguments?.getString(DATE_KEY),
                onBack = actions.upPress,
            )
        }
        composable("${MainDestinations.QUIZ_ROUTE}?$DATE_KEY={$DATE_KEY}&$SUBJECT_KEY={$SUBJECT_KEY}") { backStackEntry ->
            BoldQuizScreen(
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
    val navigateToDayList: () -> Unit = {
        navController.navigate(MainDestinations.DAY_LIST_ROUTE)
    }
    val navigateToLecture: (Int, String) -> Unit = { subjectId, date: String ->
        navController.navigate("${MainDestinations.LECTURE_ROUTE}?$DATE_KEY=$date&$SUBJECT_KEY=$subjectId")
    }
    val navigateToQuiz: (Int, String) -> Unit = { subjectId, date: String ->
        navController.navigate("${MainDestinations.QUIZ_ROUTE}?$DATE_KEY=$date&$SUBJECT_KEY=$subjectId")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}
