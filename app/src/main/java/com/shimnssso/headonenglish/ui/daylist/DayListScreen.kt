package com.shimnssso.headonenglish.ui.daylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import timber.log.Timber

@ExperimentalAnimationApi
@Composable
fun DayListScreen(
    navigateToLecture: (Int, String) -> Unit,
    onBack: () -> Unit,
) {

    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity).get(HomeViewModel::class.java)

    val lectures by viewModel.lectures.observeAsState(listOf())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val subject by viewModel.subject.observeAsState(FakeData.DEFAULT_SUBJECTS[0])


    LaunchedEffect(Unit) {
        Timber.i("LaunchedEffect.")
        viewModel.refresh(true)
    }

    Scaffold(
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
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LectureList(
            lectures = lectures,
            navigateToLecture = navigateToLecture,
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
@ExperimentalAnimationApi
@Composable
private fun LectureList(
    lectures: List<DatabaseLecture>,
    navigateToLecture: (Int, String) -> Unit,
    loading: Boolean,
    modifier: Modifier = Modifier
) {
    LoadingAwareBox(isLoading = loading) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colors.surface)
                .verticalScroll(rememberScrollState()),
        ) {
            lectures.forEach { lecture ->
                val isDateBase = lecture.date.startsWith("20")
                LectureCard(lecture, navigateToLecture)
                val color = if (isDateBase) {
                    if (DateConverter.weekInYear(lecture.date) % 2 == 0 || DateConverter.isMonday(lecture.date)) {
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
    }
}