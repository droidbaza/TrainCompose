package com.droidbaza.tiktokpager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalAnimationApi
@Composable
fun TikTokPageItem(item: TikTokModel) {

    var isLoading by remember {
        mutableStateOf(true)
    }
    var isPlaying by remember {
        mutableStateOf(true)
    }
    var isError by remember {
        mutableStateOf(false)
    }
    // var isClicked = false

    val mediaStatus = remember {
        object : MediaStatus {
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

    Box(modifier = Modifier.fillMaxSize()) {
        val player = videoPlayer(sourceUrl = item.playUrl, mediaStatus)
        Box(
            Modifier
                .fillMaxSize()
                .clickable {
                    player.playPause()
                })

        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        AnimatedVisibility(
            visible = !isPlaying && !isLoading,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(Icons.Filled.PlayArrow, "", modifier = Modifier.size(90.dp))
        }

        AnimatedVisibility(
            visible = isError,
            modifier = Modifier.align(Alignment.Center)
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

    }
    //  TikTokPlayer(sourceUrl = item.playUrl, item.active.value)


}

