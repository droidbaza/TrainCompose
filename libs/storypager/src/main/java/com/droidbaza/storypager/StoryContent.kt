package com.droidbaza.storypager

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState

@ExperimentalAnimationApi
@Composable
fun StoryContent(
    storyChild: StoryChild,
    isReady: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ((StoryChild) -> Unit)? = null,
    playChanged: (isPlaying: Boolean) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .fillMaxHeight()
    ) {
        when (storyChild.storyType) {
            StoryType.VIDEO -> {
                StoryVideo(storyChild.source, isReady, playChanged)
            }
            StoryType.IMAGE -> {
                StoryImage(storyChild.source, playChanged)
            }
        }
        content?.invoke(storyChild)
    }
}

@Composable
private fun StoryImage(
    sourceUrl: String,
    playChanged: (isPlaying: Boolean) -> Unit,
) {

    LaunchedEffect(key1 = sourceUrl) {
        playChanged(false)
    }
    val painter = rememberCoilPainter(sourceUrl)
    var isError by remember {
        mutableStateOf(false)
    }
    if (isError) {
        playChanged(false)
    }
    Box {
        Image(
            painter = painter,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        when (painter.loadState) {
            is ImageLoadState.Loading -> {
                playChanged(false)
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            is ImageLoadState.Error -> {
                isError = true
                playChanged(false)
            }
            is ImageLoadState.Success -> {
                playChanged(true)
            }
            else -> {

            }
        }
    }


}

@ExperimentalAnimationApi
@Composable
private fun StoryVideo(
    sourceUrl: String,
    isReady: Boolean = false,
    playChanged: (isPlaying: Boolean) -> Unit
) {

    var isLoading by remember {
        mutableStateOf(false)
    }
    var isPlaying by remember {
        mutableStateOf(false)
    }
    var isError by remember {
        mutableStateOf(false)
    }
    var isFirstly by remember {
        mutableStateOf(true)
    }

    if (isError) {
        playChanged(false)
    }

    val mediaStatus = remember {
        object : MediaStatus {
            override fun isLoading(value: Boolean) {
                isLoading = value
            }

            override fun isError(value: Boolean) {
                isError = value
                playChanged(false)
            }

            override fun isPlaying(value: Boolean) {
                isPlaying = value
                playChanged(value)
            }
        }
    }
    LaunchedEffect(key1 = isFirstly) {
        if (isFirstly) {
            isFirstly = false
            playChanged(false)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val player = videoPlayer(sourceUrl = sourceUrl, mediaStatus)
        if (isLoading && !isPlaying) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        DisposableEffect(key1 = isReady) {
            if (isReady) {
                player.play()
            } else {
                player.pause()
            }
            onDispose {
                player.pause()
            }
        }
    }

}