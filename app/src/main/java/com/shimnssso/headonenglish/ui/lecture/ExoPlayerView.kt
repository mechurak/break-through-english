package com.shimnssso.headonenglish.ui.lecture

import android.app.Application
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.shimnssso.headonenglish.ui.MainActivity
import timber.log.Timber

@Composable
fun ExoPlayerView(
    url: String
) {
    val app = LocalContext.current.applicationContext as Application
    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity, MediaViewModel.Factory(app)).get(MediaViewModel::class.java)

    LaunchedEffect(url) {
        Timber.d("LaunchedEffect. url: %s", url)
        viewModel.prepare(url)
    }

    DisposableEffect(Unit) {
        Timber.d("DisposableEffect setup")
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
