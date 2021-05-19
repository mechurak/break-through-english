package com.shimnssso.headonenglish.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.data.lecture.LectureRepository
import com.shimnssso.headonenglish.model.Lecture
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import com.shimnssso.headonenglish.ui.state.UiState
import com.shimnssso.headonenglish.utils.produceUiState
import com.shimnssso.headonenglish.utils.supportWideScreen
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Stateful HomeScreen which manages state using [produceUiState]
 *
 * @param lectureRepository data source for this screen
 * @param navigateToArticle (event) request navigation to Article screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    lectureRepository: LectureRepository,
    navigateToArticle: (String) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val (postUiState, refreshPost, clearError) = produceUiState(lectureRepository) {
        Timber.i("invoke produceUiState block")
        getRawData()
        getLectures()
    }

    HomeScreen(
        lectures = postUiState.value,
        onRefreshPosts = refreshPost,
        onErrorDismiss = clearError,
        navigateToArticle = navigateToArticle,
        openDrawer = openDrawer,
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
 * @param navigateToArticle (event) request navigation to Article screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    lectures: UiState<List<Lecture>>,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: () -> Unit,
    navigateToArticle: (String) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val coroutineScope = rememberCoroutineScope()

    if (lectures.hasError) {
        val errorMessage = "temp error msg"
        val retryMessage = "temp retry msg"

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(onRefreshPosts)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        // Show snackbar using a coroutine, when the coroutine is cancelled the snackbar will
        // automatically dismiss. This coroutine will cancel whenever posts.hasError is false
        // (thanks to the surrounding if statement) or if scaffoldState changes.
        LaunchedEffect(scaffoldState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = retryMessage
            )
            when (snackbarResult) {
                SnackbarResult.ActionPerformed -> onRefreshPostsState()
                SnackbarResult.Dismissed -> onErrorDismissState()
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val title = stringResource(id = R.string.app_name)
            InsetAwareTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { openDrawer() } }) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "temp description"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = lectures.initialLoad,
            emptyContent = { FullScreenLoading() },
            loading = lectures.loading,
            onRefresh = onRefreshPosts,
            content = {
                HomeScreenErrorAndContent(
                    lectures = lectures,
                    onRefresh = {
                        onRefreshPosts()
                    },
                    navigateToArticle = navigateToArticle,
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
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = onRefresh,
            content = content,
        )
    }
}


/**
 * Responsible for displaying any error conditions around [PostList].
 *
 * @param posts (state) list of posts and error state to display
 * @param onRefresh (event) request to refresh data
 * @param navigateToArticle (event) request navigation to Article screen
 * @param modifier modifier for root element
 */
@Composable
private fun HomeScreenErrorAndContent(
    lectures: UiState<List<Lecture>>,
    onRefresh: () -> Unit,
    navigateToArticle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (lectures.data != null) {
        LectureList(lectures.data, navigateToArticle, modifier)
    } else if (!lectures.hasError) {
        // if there are no posts, and no error, let the user refresh manually
        TextButton(onClick = onRefresh, modifier.fillMaxSize()) {
            Text("Tap to load content", textAlign = TextAlign.Center)
        }
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
@Composable
private fun LectureList(
    lectures: List<Lecture>,
    navigateToLecture: (postId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(top = false)
    ) {
        item { LectureListMainSection(lectures, navigateToLecture) }
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
    lectures: List<Lecture>,
    navigateToLecture: (String) -> Unit
) {
    Column {
        lectures.forEach { post ->
            LectureCard(post, navigateToLecture)
            PostListDivider()
        }
    }
}

/**
 * Full-width divider with padding for [PostList]
 */
@Composable
private fun PostListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}