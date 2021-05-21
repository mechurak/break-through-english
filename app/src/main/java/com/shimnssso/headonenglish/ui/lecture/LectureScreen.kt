package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import com.shimnssso.headonenglish.utils.supportWideScreen

/**
 * Stateless Article Screen that displays a single post.
 *
 * @param date (state) item to display
 * @param onBack (event) request navigate back
 */
@Composable
fun LectureScreen(
    subject: String?,
    date: String?,
    onBack: () -> Unit
) {
    val subjectId = subject!!.toInt()
    val viewModel = viewModel(LectureViewModel::class.java, factory = LectureViewModel.Factory(subjectId, date!!))
    val cards by viewModel.cards.observeAsState(listOf())
    val lecture by viewModel.lecture.observeAsState(null)

    var showDialog by rememberSaveable { mutableStateOf(false) }
    if (showDialog) {
        FunctionalityNotAvailablePopup { showDialog = false }
    }

    Scaffold(
        topBar = {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = lecture?.title ?: "",
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
                onUnimplementedAction = { showDialog = true },
            )
        }
    ) { innerPadding ->
        if (subjectId == 0) {
            LectureContent(
                lecture = lecture,
                cards = cards,
                modifier = Modifier
                    // innerPadding takes into account the top and bottom bar
                    .padding(innerPadding)
                    // offset content in landscape mode to account for the navigation bar
                    .navigationBarsPadding(bottom = false)
                    // center content in landscape mode
                    .supportWideScreen()
            )
        } else {
            AudioLectureContent(
                lecture = lecture,
                cards = cards,
                modifier = Modifier
                    // innerPadding takes into account the top and bottom bar
                    .padding(innerPadding)
                    // offset content in landscape mode to account for the navigation bar
                    .navigationBarsPadding(bottom = false)
                    // center content in landscape mode
                    .supportWideScreen()
            )
        }
    }
}

/**
 * Bottom bar for Article screen
 *
 * @param lecture (state) used in share sheet to share the post
 * @param onUnimplementedAction (event) called when the user performs an unimplemented action
 * @param isFavorite (state) if this post is currently a favorite
 * @param onToggleFavorite (event) request this post toggle it's favorite status
 */
@Composable
private fun BottomBar(
    onUnimplementedAction: () -> Unit,
) {
    Surface(elevation = 8.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .navigationBarsPadding()
                .height(56.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = onUnimplementedAction) {
                Icon(
                    imageVector = Icons.Filled.ThumbUp,
                    contentDescription = "temp thumb up"
                )
            }
            val context = LocalContext.current
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "temp share"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onUnimplementedAction) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "temp settings"
                )
            }
        }
    }
}

/**
 * Display a popup explaining functionality not available.
 *
 * @param onDismiss (event) request the popup be dismissed
 */
@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}