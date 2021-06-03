package com.shimnssso.headonenglish.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.api.services.drive.model.File
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.room.DatabaseSubject
import com.shimnssso.headonenglish.room.FakeData
import com.shimnssso.headonenglish.ui.MainActivity
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import com.shimnssso.headonenglish.utils.DateConverter
import com.shimnssso.headonenglish.utils.supportWideScreen
import timber.log.Timber

/**
 * Stateful HomeScreen which manages state using [produceUiState]
 *
 * @param navigateToLecture (event) request navigation to Article screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@ExperimentalAnimationApi
@Composable
fun HomeScreen(
    navigateToLecture: (Int, String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity).get(HomeViewModel::class.java)

    val lectures by viewModel.lectures.observeAsState(listOf())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val subject by viewModel.subject.observeAsState(FakeData.DEFAULT_SUBJECT)
    val showDialog by viewModel.showDialog.observeAsState(false)
    val sheetFiles by viewModel.sheetFiles.observeAsState(listOf())
    val isLogIn by viewModel.isLogIn.observeAsState(false)

    if (showDialog) {
        ConfirmFetchPopup(
            files = sheetFiles,
            onConfirm = { name, sheetId -> viewModel.fetchSheet(name, sheetId) },
            onDismiss = { viewModel.dismissSheetFetchDialog() }
        )
    }

    LaunchedEffect(Unit) {
        Timber.e("LaunchedEffect")
    }

    HomeScreen(
        subject = subject,
        lectures = lectures,
        isLoading = isLoading,
        isLogIn = isLogIn,
        onRefreshPosts = { viewModel.refresh() },
        navigateToLecture = navigateToLecture,
        scaffoldState = scaffoldState
    )
}

/**
 * Responsible for displaying the Home Screen of this application.
 *
 * Stateless composable is not coupled to any specific state management.
 *
 * @param lectures (state) the data to show on the screen
 * @param onRefreshPosts (event) request a refresh of posts
 * @param onErrorDismiss (event) request the current error be dismissed
 * @param navigateToLecture (event) request navigation to Article screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@ExperimentalAnimationApi
@Composable
fun HomeScreen(
    subject: DatabaseSubject,
    lectures: List<DatabaseLecture>,
    isLoading: Boolean,
    isLogIn: Boolean,
    onRefreshPosts: () -> Unit,
    navigateToLecture: (Int, String) -> Unit,
    scaffoldState: ScaffoldState
) {
    var showBackdrop by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val title = subject.title
            InsetAwareTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "temp description"
                        )
                    }
                }
            )
        },
        bottomBar = {
            HomeBottomBar(
                showBackdrop = showBackdrop,
                setShowBackdrop = { newShowBackdrop ->
                    showBackdrop = newShowBackdrop
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = lectures.isEmpty(),
            emptyContent = { FullScreenLoading() },
            loading = isLoading,
            isLogIn = isLogIn,
            onRefresh = onRefreshPosts,
            showBackdrop = showBackdrop,
            changeShowBackdrop = { showBackdrop = !showBackdrop },
            content = {
                HomeScreenErrorAndContent(
                    lectures = lectures,
                    navigateToLecture = navigateToLecture,
                    showBackdrop = showBackdrop,
                    changeShowBackdrop = { showBackdrop = !showBackdrop },
                    modifier = modifier.supportWideScreen()
                )
            }
        )
    }
}

/**
 * Display an initial empty state or swipe to refresh content.
 *
 * @param empty (state) when true, display [emptyContent]
 * @param emptyContent (slot) the content to display for the empty state
 * @param loading (state) when true, display a loading spinner over [content]
 * @param onRefresh (event) event to request refresh
 * @param content (slot) the main content to show
 */
@ExperimentalAnimationApi
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    isLogIn: Boolean,
    onRefresh: () -> Unit,
    showBackdrop: Boolean,
    changeShowBackdrop: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isLogIn) {
        if (empty) {
            emptyContent()
        } else {
            SwipeRefresh(
                state = rememberSwipeRefreshState(loading),
                onRefresh = onRefresh,
                content = content,
            )
        }
    } else {
        Box() {
            val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.walking_broccoli) }
            val animationState = rememberLottieAnimationState(autoPlay = true, repeatCount = Integer.MAX_VALUE)

            val activity = LocalContext.current as MainActivity
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                LottieAnimation(
                    spec = animationSpec,
                    modifier = Modifier
                        .size(250.dp),
                    animationState = animationState,
                )

                Text("Google sign-in is required to access your google sheets.", textAlign = TextAlign.Center)
                Button(
                    onClick = { activity.requestSignIn() },
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("Sign in")
                }
            }
            AnimatedVisibility(
                showBackdrop,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            changeShowBackdrop()
                        },
                    color = Color.Black.copy(0.7f)
                ) {
                }
            }
        }
    }
}

/**
 * Responsible for displaying any error conditions around [PostList].
 *
 * @param posts (state) list of posts and error state to display
 * @param onRefresh (event) request to refresh data
 * @param navigateToLecture (event) request navigation to Article screen
 * @param modifier modifier for root element
 */
@ExperimentalAnimationApi
@Composable
private fun HomeScreenErrorAndContent(
    lectures: List<DatabaseLecture>,
    navigateToLecture: (Int, String) -> Unit,
    showBackdrop: Boolean,
    changeShowBackdrop: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (lectures.isNotEmpty()) {
        LectureList(lectures, navigateToLecture, showBackdrop, changeShowBackdrop, modifier)
    } else {
        // there's currently an error showing, don't show any content
        Box(modifier.fillMaxSize()) { /* empty screen */ }
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
    showBackdrop: Boolean,
    changeShowBackdrop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box() {
        LazyColumn(
            modifier = modifier.background(MaterialTheme.colors.surface),
            contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(top = false)
        ) {
            item { LectureListMainSection(lectures, navigateToLecture) }
        }
        AnimatedVisibility(
            showBackdrop,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        changeShowBackdrop()
                    },
                color = Color.Black.copy(0.7f)
            ) {
            }
        }
    }
}

/**
 * Full screen circular progress indicator
 */
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Full-width list items that display "based on your history" for [PostList]
 *
 * @param lectures (state) to display
 * @param navigateToLecture (event) request navigation to Lecture screen
 */
@Composable
private fun LectureListMainSection(
    lectures: List<DatabaseLecture>,
    navigateToLecture: (Int, String) -> Unit,
) {
    Column {
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

@Composable
private fun ConfirmFetchPopup(
    files: List<File>,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIdx by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Import the selected sheet?",
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                files.forEachIndexed { index, file ->
                    val modifier = if (selectedIdx == index) Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            MaterialTheme.colors.primaryVariant
                        ) else Modifier

                    TextButton(
                        onClick = { selectedIdx = index },
                        modifier = modifier
                    ) {
                        Text("${file.name} : ${file.owners[0].displayName}")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(files[selectedIdx].name, files[selectedIdx].id) }) {
                Text(text = "Confirm")
            }
        }
    )
}