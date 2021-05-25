package com.shimnssso.headonenglish.ui.lecture

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.ui.MainActivity
import timber.log.Timber

@Composable
fun AudioLectureContent(
    lecture: DatabaseLecture,
    cards: List<DatabaseCard>,
    modifier: Modifier = Modifier
) {
    // This is the official way to access current context from Composable functions
    val context = LocalContext.current

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val activity = LocalContext.current as MainActivity

    // Do not recreate the player everytime this Composable commits
    val exoPlayer = remember {
        Timber.d("SimpleExoPlayer build")
        SimpleExoPlayer.Builder(context).build()
    }

    val autoPlay = rememberSaveable { mutableStateOf(true) }
    val window = rememberSaveable { mutableStateOf(0) }
    val position = rememberSaveable { mutableStateOf(0L) }

    fun updateState() {
        autoPlay.value = exoPlayer.playWhenReady
        window.value = exoPlayer.currentWindowIndex
        position.value = 0L.coerceAtLeast(exoPlayer.contentPosition)
    }

    // We only want to react to changes in sourceUrl.
    // This effect will be executed at each commit phase if
    // [sourceUrl] has changed.
    LaunchedEffect(lecture) {
        Timber.i("LaunchedEffect. lecture: %s", lecture)
        Timber.i("LaunchedEffect. cards: %s", cards)

        if (lecture.localUrl != null) {
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.packageName)
            )

            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    Uri.parse(lecture.localUrl)
                )
            exoPlayer.prepare(source)
        }
    }

    DisposableEffect(Unit) {
        Timber.d("DisposableEffect setup")
        onDispose {
            Timber.i("onDispose!!")
            updateState()
            exoPlayer.release()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (lecture.remoteUrl == null && lecture.localUrl == null) {
            Button(
                onClick = { activity.launchAudioChooser(lecture) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Set Audio file")
            }
        } else if (lecture.remoteUrl == null && lecture.localUrl != null) {
            // Gateway to traditional Android Views
            AndroidView(
                factory = { context ->
                    PlayerControlView(context).apply {
                        player = exoPlayer
                        showTimeoutMs = 0  // don't hide
                        setShowNextButton(false)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            cards.forEach { card ->
                RowCard(card)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
