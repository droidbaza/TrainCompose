package com.droidbaza.tiktokpager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@ExperimentalAnimationApi
@Composable
fun TikTokPageItem(item: TikTokModel) {

    val scope = rememberCoroutineScope()
    var isLoading by remember {
        mutableStateOf(true)
    }
    var isPlaying by remember {
        mutableStateOf(true)
    }
    var isError by remember {
        mutableStateOf(false)
    }
    var isClicked by remember {
        mutableStateOf(false)
    }
    var timeProgress by remember {
        mutableStateOf(0L)
    }

    if (isPlaying) {
        isClicked = false
    }
    val mediaStatus = remember {
        object : MediaStatus {
            override fun timeProgress(duration: Long) {
                msg("DURR $duration")
                timeProgress = duration
            }

            override fun isLoading(value: Boolean) {
                isLoading = value
            }

            override fun isError(value: Boolean) {
                isError = value
            }

            override fun isPlaying(value: Boolean) {
                isPlaying = value
            }
        }
    }
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = Modifier.fillMaxSize()) {
        val player = videoPlayer(sourceUrl = item.playUrl, mediaStatus)
        Box(
            Modifier
                .clickable(interactionSource, null) {
                    if (isClicked) {
                        isClicked = false
                        player.play()
                    } else {
                        isClicked = true
                        player.pause()
                    }
                }
                .fillMaxSize()
        )

        if (isLoading && !isPlaying) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        AnimatedVisibility(
            visible = isClicked,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(0.8f),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(Icons.Filled.PlayArrow, "", modifier = Modifier.size(90.dp))
        }

        AnimatedVisibility(
            visible = isError,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(0.7f)
        ) {
            Icon(Icons.Filled.Delete, "")
        }

        DisposableEffect(key1 = item.active.value) {
            if (item.active.value) {
                player.reset()
                player.play()
            } else {
                player.reset()
                player.pause()
            }
            onDispose {
                player.pause()
                player.reset()
            }
        }
        if(timeProgress!=0L){
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
            )
            LaunchedEffect(timeProgress,isPlaying,progress){

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
            }
        }

    }


}

