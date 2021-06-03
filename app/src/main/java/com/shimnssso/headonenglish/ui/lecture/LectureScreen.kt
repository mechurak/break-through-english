package com.shimnssso.headonenglish.ui.lecture

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SubtitlesOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.room.FakeData
import com.shimnssso.headonenglish.ui.MainActivity
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import com.shimnssso.headonenglish.utils.supportWideScreen

/**
 * Stateless Article Screen that displays a single post.
 *
 * @param date (state) item to display
 * @param onBack (event) request navigate back
 */
@ExperimentalAnimationApi
@Composable
fun LectureScreen(
    subject: String?,
    date: String?,
    onBack: () -> Unit
) {
    val subjectId = subject!!.toInt()
    val viewModel = viewModel(LectureViewModel::class.java, factory = LectureViewModel.Factory(subjectId, date!!))
    val cards by viewModel.cards.observeAsState(listOf())
    val lecture by viewModel.lecture.observeAsState(FakeData.DEFAULT_LECTURE)

    var defaultMode: CardMode by remember { mutableStateOf(CardMode.Default) }
    var defaultShowKeyword by remember { mutableStateOf(false) }

    var showBackdrop by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = lecture.title,
                        color = LocalContentColor.current
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "temp up"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                defaultMode = defaultMode,
                defaultShowKeyword = defaultShowKeyword,
                changeDefaultMode = {
                    defaultMode = defaultMode.next()
                },
                changeShowKeyword = {
                    defaultShowKeyword = !defaultShowKeyword
                },
                showBackdrop = showBackdrop,
                setShowBackdrop = { newShowBackdrop ->
                    showBackdrop = newShowBackdrop
                }
            )
        }
    ) { innerPadding ->
        LectureContent(
            lecture = lecture,
            cards = cards,
            modifier = Modifier
                // innerPadding takes into account the top and bottom bar
                .padding(innerPadding)
                // offset content in landscape mode to account for the navigation bar
                .navigationBarsPadding(bottom = false)
                // center content in landscape mode
                .supportWideScreen(),
            defaultMode = defaultMode,
            defaultShowKeyword = defaultShowKeyword,
            showBackdrop = showBackdrop,
            changeShowBackdrop = { showBackdrop = !showBackdrop },
        )
    }
}

@ExperimentalAnimationApi
@Composable
private fun BottomBar(
    defaultMode: CardMode,
    defaultShowKeyword: Boolean,
    changeDefaultMode: () -> Unit,
    changeShowKeyword: () -> Unit,
    showBackdrop: Boolean,
    setShowBackdrop: (Boolean) -> Unit
) {
    val app = LocalContext.current.applicationContext as Application
    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity, MediaViewModel.Factory(app)).get(MediaViewModel::class.java)

    var showSetting by remember { mutableStateOf(showBackdrop) }
    val speed by viewModel.speed.observeAsState(initial = 1.0f)

    LaunchedEffect(showBackdrop) {
        showSetting = showBackdrop
    }

    Surface(elevation = 8.dp) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
        ) {
            AnimatedVisibility(showSetting) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable{ }
                ) {
                    Text(
                        "play speed",
                        modifier = Modifier.padding(16.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        IconButton(onClick = { viewModel.speedDown() }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "temp thumb up"
                            )
                        }
                        Text(text = "${speed}x")
                        IconButton(onClick = { viewModel.speedUp() }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = "temp thumb up"
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val showKeywordIcon = when (defaultShowKeyword) {
                    true -> Icons.Filled.Visibility
                    else -> Icons.Filled.VisibilityOff
                }

                IconButton(onClick = { changeShowKeyword() }) {
                    Icon(
                        imageVector = showKeywordIcon,
                        contentDescription = "temp settings"
                    )
                }
                IconButton(onClick = {
                    showSetting = !showSetting
                    setShowBackdrop(showSetting)
                }) {
                    Box(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        val iconVector =
                            if (showSetting) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp
                        Icon(
                            imageVector = iconVector,
                            contentDescription = "temp settings",
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopStart)
                        )
                    }
                    Text(text = "${speed}x")
                }
                val modeIcon = when (defaultMode) {
                    CardMode.HideText -> Icons.Filled.Lightbulb
                    CardMode.HideDescription -> Icons.Filled.SubtitlesOff  // 2
                    else -> Icons.Filled.Subtitles
                }
                IconButton(onClick = { changeDefaultMode() }) {
                    Icon(
                        imageVector = modeIcon,
                        contentDescription = "temp settings"
                    )
                }
            }
        }
    }
}