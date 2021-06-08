package com.shimnssso.headonenglish.ui.lecture

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
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
import androidx.compose.material.primarySurface
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.room.DatabaseLecture
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
                    if (defaultMode == CardMode.HideText) {
                        defaultShowKeyword = false
                    }
                },
                changeShowKeyword = {
                    defaultShowKeyword = !defaultShowKeyword
                },
                showBackdrop = showBackdrop,
                setShowBackdrop = { newShowBackdrop ->
                    showBackdrop = newShowBackdrop
                },
                lecture = lecture,
                onRemoveLocal = {
                    viewModel.updateLocalUrl(null)
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
    setShowBackdrop: (Boolean) -> Unit,
    lecture: DatabaseLecture,
    onRemoveLocal: () -> Unit,
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
            AnimatedVisibility(showSetting)
            {
                Column(
                    modifier = Modifier.clickable { }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "media source",
                                modifier = Modifier.padding(16.dp)
                            )

                            Row {
                                if (lecture.remoteUrl != null) {
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .border(2.dp, MaterialTheme.colors.primaryVariant, RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colors.primarySurface, RoundedCornerShape(4.dp))
                                    ) {
                                        Text(
                                            "Remote",
                                            style = MaterialTheme.typography.caption,
                                            color = MaterialTheme.colors.onPrimary,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .border(2.dp, Color.LightGray, RoundedCornerShape(4.dp))
                                    ) {
                                        Text(
                                            "Remote",
                                            style = MaterialTheme.typography.caption,
                                            modifier = Modifier
                                                .padding(4.dp)
                                        )
                                    }
                                }
                                if (lecture.localUrl != null) {
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .border(2.dp, MaterialTheme.colors.primaryVariant, RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colors.primarySurface, RoundedCornerShape(4.dp))
                                    ) {
                                        Text(
                                            "Local",
                                            style = MaterialTheme.typography.caption,
                                            color = MaterialTheme.colors.onPrimary,
                                            modifier = Modifier
                                                .padding(4.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .border(2.dp, Color.LightGray, RoundedCornerShape(4.dp))
                                    ) {
                                        Text(
                                            "Local",
                                            style = MaterialTheme.typography.caption,
                                            modifier = Modifier
                                                .padding(4.dp)
                                        )
                                    }
                                }
                            }
                            val btnText = if (lecture.localUrl == null) "Set Local" else "Remove Local"
                            Button(onClick = {
                                if (lecture.localUrl == null) activity.launchAudioChooser(lecture)
                                else onRemoveLocal()
                            }) {
                                Text(
                                    btnText, style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onPrimary
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "play speed",
                                modifier = Modifier.padding(16.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp)
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
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val titleText = if (lecture.link1 != null || lecture.link2 != null) "links" else ""
                            Text(
                                titleText,
                                modifier = Modifier.padding(16.dp)
                            )

                            if (lecture.link1 != null) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lecture.link1))
                                        activity.startActivity(intent)
                                    },
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        "link1", style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.onPrimary
                                    )
                                }
                            }
                            if (lecture.link2 != null) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lecture.link2))
                                        activity.startActivity(intent)
                                    }
                                ) {
                                    Text(
                                        "link2", style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.onPrimary
                                    )
                                }
                            }
                        }
                    }

                    Divider(
                        color = MaterialTheme.colors.onSurface.copy(alpha = .2f),
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier.width(100.dp)
                ) {
                    val showKeywordIcon = when (defaultShowKeyword) {
                        true -> Icons.Filled.Visibility
                        else -> Icons.Filled.VisibilityOff
                    }
                    val showKeywordText = when (defaultShowKeyword) {
                        true -> "Show"
                        else -> "Hide"
                    }

                    IconButton(
                        onClick = { changeShowKeyword() },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = showKeywordIcon,
                            contentDescription = "temp settings"
                        )
                    }

                    Text(
                        "default keyword", style = MaterialTheme.typography.caption,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 2.dp)
                    )
                    Text(
                        showKeywordText,
                        letterSpacing = (-0.5).sp,
                        style = MaterialTheme.typography.overline,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 2.dp)
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

                Box(
                    modifier = Modifier.width(100.dp)
                ) {
                    val modeIcon = when (defaultMode) {
                        CardMode.HideText -> Icons.Filled.Lightbulb
                        CardMode.HideDescription -> Icons.Filled.SubtitlesOff  // 2
                        else -> Icons.Filled.Subtitles
                    }
                    val modeText = when (defaultMode) {
                        CardMode.HideText -> "Memorize"
                        CardMode.HideDescription -> "Hide memo"  // 2
                        else -> "Normal"
                    }
                    IconButton(
                        onClick = { changeDefaultMode() },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = modeIcon,
                            contentDescription = "temp settings"
                        )
                    }

                    Text(
                        "default mode",
                        style = MaterialTheme.typography.caption,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 2.dp)
                    )
                    Text(
                        modeText,
                        letterSpacing = (-0.5).sp,
                        style = MaterialTheme.typography.overline,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}