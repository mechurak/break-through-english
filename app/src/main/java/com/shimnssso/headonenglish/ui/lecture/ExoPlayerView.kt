package com.shimnssso.headonenglish.ui.lecture

import android.app.Application
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import timber.log.Timber

@Composable
fun ExoPlayerView(
    url: String
) {
    val app = LocalContext.current.applicationContext as Application
    val viewModel = viewModel(MediaViewModel::class.java,  factory = MediaViewModel.Factory(app))

    LaunchedEffect(url) {
        Timber.e("LaunchedEffect. url: %s", url)
        viewModel.prepare(url)
    }

    DisposableEffect(Unit) {
        Timber.e("DisposableEffect setup")
        onDispose {
            Timber.i("onDispose!!")
            viewModel.release()
        }
    }

    // TODO: Find proper way
    val isVideo = url.endsWith(".mp4")

    AndroidView(
        factory = { context ->
            if (isVideo) {
                PlayerView(context).apply {
                    player = viewModel.exoPlayer
                    setShowNextButton(false)
                }
            } else {
                PlayerControlView(context).apply {
                    player = viewModel.exoPlayer
                    showTimeoutMs = 0  // don't hide
                    setShowNextButton(false)
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
