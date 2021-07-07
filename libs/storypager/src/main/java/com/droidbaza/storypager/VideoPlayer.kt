package com.droidbaza.storypager

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@ExperimentalAnimationApi
@Composable
fun videoPlayer(
    sourceUrl: String,
    mediaStatus: MediaStatus,
    withReset: Boolean = true
): MediaPlayback {

    val context = LocalContext.current
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = false
        }
    }
    val mediaPlayback = remember(exoPlayer) {
        object : MediaPlayback {
            override fun playPause() {
                exoPlayer.playWhenReady = !exoPlayer.playWhenReady
            }

            override fun play() {
                exoPlayer.changePlay(false)
            }

            override fun reset() {
                if (withReset) {
                    exoPlayer.seekTo(0)
                }
            }

            override fun pause() {
                exoPlayer.changePlay(true)
            }


            override fun forward(durationInMillis: Long) {
                exoPlayer.seekTo(exoPlayer.currentPosition + durationInMillis)
            }

            override fun rewind(durationInMillis: Long) {
                exoPlayer.seekTo(exoPlayer.currentPosition - durationInMillis)
            }
        }
    }
    val eventListener = remember {
        object : Player.Listener {
            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                mediaStatus.isError(true)
                mediaStatus.isPlaying(false)
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    mediaStatus.isError(false)
                }
                mediaStatus.isPlaying(isPlaying)
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                mediaStatus.isLoading(isLoading)
            }
        }
    }

    LaunchedEffect(sourceUrl) {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )
        val mediaItem = MediaItem.fromUri(sourceUrl)
        val source =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        exoPlayer.prepare(source)
    }

    DisposableEffect(AndroidView(factory = {
        PlayerView(context).apply {
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            exoPlayer.addListener(eventListener)
        }

    })) {
        onDispose {
            exoPlayer.removeListener(eventListener)
            exoPlayer.release()
        }
    }
    return mediaPlayback
}


private fun SimpleExoPlayer.changePlay(isPaused: Boolean) {
    if (!isPaused) {
        if (!isPlaying) {
            play()
        }
    } else {
        if (isPlaying) {
            pause()
        }
    }
}

interface MediaPlayback {
    fun pause()
    fun play()
    fun playPause()
    fun reset()
    fun forward(durationInMillis: Long)
    fun rewind(durationInMillis: Long)
}

interface MediaStatus {
    fun isLoading(value: Boolean)
    fun isError(value: Boolean)
    fun isPlaying(value: Boolean)
}
