package com.shimnssso.headonenglish.ui.lecture

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.shimnssso.headonenglish.model.Lecture
import timber.log.Timber

private val defaultSpacerSize = 16.dp

@Composable
fun LectureContent(lecture: Lecture, modifier: Modifier = Modifier) {

    // This is the official way to access current context from Composable functions
    val context = LocalContext.current

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val autoPlay = rememberSaveable { mutableStateOf(true) }
    val window = rememberSaveable { mutableStateOf(0) }
    val position = rememberSaveable { mutableStateOf(0L) }

    // Do not recreate the player everytime this Composable commits
    val exoPlayer = remember {
        Timber.d("SimpleExoPlayer build")
        SimpleExoPlayer.Builder(context).build()
    }

    fun updateState() {
        autoPlay.value = exoPlayer.playWhenReady
        window.value = exoPlayer.currentWindowIndex
        position.value = 0L.coerceAtLeast(exoPlayer.contentPosition)
    }

    val playerView = remember {
        val playerView = PlayerView(context)
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                Timber.i("onStart()")
                exoPlayer.playWhenReady = autoPlay.value
                // exoPlayer.seekTo(window.value, position.value)
                playerView.onResume()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                Timber.i("onStop()")
                updateState()
                playerView.onPause()
                exoPlayer.playWhenReady = false
            }
        })
        playerView
    }

    // We only want to react to changes in sourceUrl.
    // This effect will be executed at each commit phase if
    // [sourceUrl] has changed.
    LaunchedEffect(lecture) {
        Timber.i("LaunchedEffect. lecture: %s", lecture.title)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )

        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                Uri.parse(
                    // Big Buck Bunny from Blender Project
                    lecture.url
                )
            )

        exoPlayer.prepare(source)
    }

    DisposableEffect(Unit) {
        Timber.d("DisposableEffect setup")
        onDispose {
            Timber.i("onDispose!!")
            updateState()
            exoPlayer.release()
        }
    }

    Column {
        // Gateway to traditional Android Views
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = modifier.padding(horizontal = defaultSpacerSize)
        ) {
            item {
                Text(text = lecture.title, style = MaterialTheme.typography.h4)
                Spacer(Modifier.height(8.dp))
            }
            items(lecture.rows) { row ->
                Text(text = row.spelling, style = MaterialTheme.typography.h5)
                Text(text = row.meaning, style = MaterialTheme.typography.h5)
            }
            item {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}