package com.shimnssso.breakthrougheng.ui.lecture

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.shimnssso.breakthrougheng.model.Lecture

private val defaultSpacerSize = 16.dp

@Composable
fun LectureContent(post: Lecture, modifier: Modifier = Modifier) {

    // This is the official way to access current context from Composable functions
    val context = LocalContext.current

    // Do not recreate the player everytime this Composable commits
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build()
    }

    // We only want to react to changes in sourceUrl.
    // This effect will be executed at each commit phase if
    // [sourceUrl] has changed.
    LaunchedEffect(post) {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context,
            Util.getUserAgent(context, context.packageName))

        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                Uri.parse(
                // Big Buck Bunny from Blender Project
                post.url
            ))

        exoPlayer.prepare(source)
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
                Text(text = post.title, style = MaterialTheme.typography.h4)
                Spacer(Modifier.height(8.dp))
            }
            items(post.rows) { row ->
                Text(text = row.spelling, style = MaterialTheme.typography.h5)
                Text(text = row.meaning, style = MaterialTheme.typography.h5)
            }
            item {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}