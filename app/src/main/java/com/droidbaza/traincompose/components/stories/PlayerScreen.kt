package com.droidbaza.traincompose.components.stories

import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@Composable
fun VideoPlayer(sourceUrl: String, isPlay: Boolean = false, changePlay: (Boolean) -> Unit) {
    val context = LocalContext.current
    val playUrl = Uri.parse("asset:///$sourceUrl")
    val currentUrl: MutableState<Uri?> = remember {
        mutableStateOf(null)
    }
    val dataSourceFactory: DataSource.Factory = remember {
        DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )
    }
    val source = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(playUrl)

    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }

    if (currentUrl.value != playUrl) {
        currentUrl.value = playUrl
        exoPlayer.stop(true)
        exoPlayer.prepare(source)
    }

    if (isPlay) {
        exoPlayer.pause()
    } else {
        exoPlayer.play()
    }
    exoPlayer.addListener(object : Player.Listener {
        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            changePlay(false)
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            super.onIsLoadingChanged(isLoading)
            if (!isLoading) {
                changePlay(true)
            } else {
                changePlay(false)
            }
        }
    })
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

/*    AndroidView(factory = {
        PlayerView(context).apply {
            hideController()
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    })*/

    DisposableEffect(AndroidView(factory = {
        PlayerView(context).apply {
            hideController()
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    })) {
        onDispose {
            exoPlayer.release()
        }
    }

}