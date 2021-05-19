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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import com.shimnssso.headonenglish.utils.supportWideScreen
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Stateful HomeScreen which manages state using [produceUiState]
 *
 * @param navigateToArticle (event) request navigation to Article screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    navigateToArticle: (String) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val viewModel = viewModel(HomeViewModel::class.java)
    val lectures by viewModel.lectures.observeAsState(listOf())
    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        Timber.e("LaunchedEffect")
        viewModel.refresh(shouldCheckInterval = true)
    }

    HomeScreen(
        lectures = lectures,
        isLoading = isLoading,
        onRefreshPosts = { viewModel.refresh() },
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
    lectures: List<DatabaseLecture>,
    isLoading: Boolean,
    onRefreshPosts: () -> Unit,
    navigateToArticle: (String) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val coroutineScope = rememberCoroutineScope()

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
            empty = lectures.isEmpty(),
            emptyContent = { FullScreenLoading() },
            loading = isLoading,
            onRefresh = onRefreshPosts,
            content = {
                HomeScreenErrorAndContent(
                    lectures = lectures,
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
    lectures: List<DatabaseLecture>,
    navigateToArticle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (lectures.isNotEmpty()) {
        LectureList(lectures, navigateToArticle, modifier)
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
    lectures: List<DatabaseLecture>,
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
    lectures: List<DatabaseLecture>,
    navigateToLecture: (String) -> Unit
) {
    Column {
        lectures.forEach { lecture ->
            LectureCard(lecture, navigateToLecture)
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