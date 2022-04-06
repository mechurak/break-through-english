package com.shimnssso.headonenglish.ui.daylist

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.room.FakeData
import com.shimnssso.headonenglish.ui.MainActivity
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import com.shimnssso.headonenglish.ui.components.LoadingAwareBox
import com.shimnssso.headonenglish.utils.DateConverter
import com.shimnssso.headonenglish.utils.supportWideScreen
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO: Remove Lint error
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DayListScreen(
    navigateToLecture: (Int, String) -> Unit,
    navigateToQuiz: (Int, String) -> Unit,
    onBack: () -> Unit,
) {

    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity).get(HomeViewModel::class.java)

    val lectures by viewModel.lectures.observeAsState(listOf())
    val recentLecture by viewModel.recentLecture.observeAsState(FakeData.DEFAULT_LECTURE)

    val isLoading by viewModel.isLoading.observeAsState(false)
    val subject by viewModel.subject.observeAsState(FakeData.DEFAULT_SUBJECTS[0])

    val errorPair by viewModel.errorPair.observeAsState(
        Pair(
            false,
            ""
        )
    )  // first: hasError, second: msg


    LaunchedEffect(Unit) {
        Timber.i("LaunchedEffect.")
        viewModel.refresh(true)
    }

    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val title = subject.title
            InsetAwareTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                    }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
            )
        },
        bottomBar = {
            DayListBottomBar(
                onRefresh = {
                    if (!isLoading) {
                        viewModel.refresh()
                    }
                },
                onBack = {
                    if (!isLoading) {
                        onBack()
                    }
                }
            )
        },
    ) { innerPadding ->
        if (errorPair.first) {
            val scope = rememberCoroutineScope()
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(errorPair.second)
                viewModel.setError(Pair(false, ""))
            }
        }

        val modifier = Modifier.padding(innerPadding)
        LectureList(
            lectures = lectures,
            recentLecture = recentLecture,
            navigateToLecture = navigateToLecture,
            navigateToQuiz = navigateToQuiz,
            loading = isLoading,
            modifier = modifier.supportWideScreen()
        )
    }
}

/**
 * Display a list of posts.
 *
 * When a post is clicked on, [navigateToLecture] will be called to navigate to the detail screen
 * for that post.
 *
 * @param lectures (state) the list to display
 * @param navigateToLecture (event) request navigation to Article screen
 * @param modifier modifier for the root element
 */
@Composable
private fun LectureList(
    lectures: List<DatabaseLecture>,
    recentLecture: DatabaseLecture,
    navigateToLecture: (Int, String) -> Unit,
    navigateToQuiz: (Int, String) -> Unit,
    loading: Boolean,
    modifier: Modifier = Modifier
) {
    LoadingAwareBox(isLoading = loading) {
        LazyColumn(
            modifier = modifier
                .background(MaterialTheme.colors.surface)
        ) {
            items(lectures) {
                val isDateBase = DateConverter.isDateBase(it.date)
                val isRecent = it.date == recentLecture.date
                LectureCard(it, navigateToLecture, navigateToQuiz, isRecent)
                val color = if (isDateBase) {
                    if (DateConverter.weekInYear(it.date) % 2 == 0 || DateConverter.isMonday(it.date)) {
                        MaterialTheme.colors.surface
                    } else {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
                    }
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
                }
                Divider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    color = color
                )
            }
        }

        // Column(
        //     modifier = modifier
        //         .background(MaterialTheme.colors.surface)
        //         .verticalScroll(rememberScrollState()),
        // ) {
        //     lectures.forEach { lecture ->
        //         val isDateBase = DateConverter.isDateBase(lecture.date)
        //         LectureCard(lecture, navigateToLecture, navigateToQuiz)
        //         val color = if (isDateBase) {
        //             if (DateConverter.weekInYear(lecture.date) % 2 == 0 || DateConverter.isMonday(lecture.date)) {
        //                 MaterialTheme.colors.surface
        //             } else {
        //                 MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
        //             }
        //         } else {
        //             MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
        //         }
        //         Divider(
        //             modifier = Modifier.padding(horizontal = 14.dp),
        //             color = color
        //         )
        //     }
        // }
    }
}