package com.shimnssso.headonenglish.ui.lecture

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.launch
import timber.log.Timber

class MediaViewModel(
    private val app: Application
) : AndroidViewModel(app) {
    val exoPlayer = SimpleExoPlayer.Builder(app).build()

    var autoPlay: Boolean = true
    var window: Int = 0
    var position: Long = 0L

    fun updateState() {
        autoPlay = exoPlayer.playWhenReady
        window = exoPlayer.currentWindowIndex
        position = 0L.coerceAtLeast(exoPlayer.contentPosition)
    }

    fun prepare(url: String) {
        viewModelScope.launch {
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                app,
                Util.getUserAgent(app, app.packageName)
            )
            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    Uri.parse(url)
                )
            Timber.e("mediaUrl: ${source.mediaItem.mediaMetadata.mediaUri}")
            exoPlayer.prepare(source)
        }
    }

    fun release() {
        viewModelScope.launch {
            updateState()
            exoPlayer.release()
        }
    }

    override fun onCleared() {
        Timber.e("onCleared()!!")
    }

    class Factory(
        private val app: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MediaViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}