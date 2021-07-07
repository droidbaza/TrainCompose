package com.droidbaza.tiktokpager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@ExperimentalAnimationApi
@Composable
fun TikTokPageItem(item: TikTokModel) {
    val state = remember { TikTokUiState() }
    val mediaStatus = remember {
        object : MediaStatus {
            override fun timeProgress(duration: Long) {
                msg("DURR $duration")
                // timeProgress = duration
            }

            override fun isLoading(value: Boolean) {
                state.isLoading = value
            }

            override fun isError(value: Boolean) {
                state.isError = value
            }

            override fun isPlaying(value: Boolean) {
                state.isPlaying = value
            }
        }
    }
    val interactionSource = remember { MutableInteractionSource() }
    Box(modifier = Modifier
        .fillMaxSize()) {
        val player = videoPlayer(sourceUrl = item.playUrl, mediaStatus)
        Box(
            Modifier
                .clickable(interactionSource, null) {
                    if (state.isClicked) {
                        state.isClicked = false
                        player.play()
                    } else {
                        state.isClicked = true
                        player.pause()
                    }
                }
                .fillMaxSize()
        )

        if (state.isLoading && !state.isPlaying) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        AnimatedVisibility(
            visible = state.isClicked,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(0.8f),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(Icons.Filled.PlayArrow, "", modifier = Modifier.size(90.dp))
        }

        /*AnimatedVisibility(
            visible = state.isError,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(0.7f)
        ) {
            Icon(Icons.Filled.Delete, "")
        }*/

        DisposableEffect(key1 = item.isResumed()) {
            if (item.isResumed()) {
                player.reset()
                player.play()
            } else {
                player.reset()
                player.pause()
            }
            onDispose {
                player.pause()
                player.reset()
                state.reset()
            }
        }
        /* if(timeProgress!=0L){
             val progress = remember {
                 Animatable(0f)
             }
             LinearProgressIndicator(
                 color = Color.Magenta,
                 backgroundColor = Color.Yellow,
                 progress = progress.value,
                 modifier = Modifier
                     .padding(2.dp)
                     .fillMaxWidth()
                     .align(Alignment.Center)
                     .height(2.dp)
                     .alpha(0.8f)
             )*/
        /* LaunchedEffect(timeProgress,isPlaying,progress){

             if (isPlaying) {

                 var durationRemain = ((1f - progress.value) * timeProgress).toInt()
                 msg("duration $durationRemain")

                 if(progress.value==1f){
                     progress.animateTo(0f)
                     durationRemain = ((1f - progress.value) * timeProgress).toInt()
                 }
                 progress.animateTo(
                     1f,
                     animationSpec = tween(
                         durationMillis = durationRemain,
                         easing = LinearEasing
                     )
                 )

             } else {
                 progress.stop()
             }
         }*/
    }

}


class TikTokUiState() {

    private var _isLoading: MutableState<Boolean> = mutableStateOf(false)
    private var _isPlaying: MutableState<Boolean> = mutableStateOf(false)
    private var _isError: MutableState<Boolean> = mutableStateOf(false)
    private var _isClicked: MutableState<Boolean> = mutableStateOf(false)

    var isLoading: Boolean
        get() = _isLoading.value
        set(value) {
            _isLoading.value = value
        }
    var isPlaying: Boolean
        get() = _isPlaying.value
        set(value) {
            _isPlaying.value = value
        }
    var isError: Boolean
        get() = _isError.value
        set(value) {
            _isError.value = value
        }

    var isClicked: Boolean
        get() = _isClicked.value
        set(value) {
            _isClicked.value = value
        }

    init {
        if (isPlaying) {
            isClicked = false
        }
    }

    fun reset() {
        isLoading = false
        isPlaying = false
        isError = false
        isClicked = false
    }
}




