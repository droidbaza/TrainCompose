package com.droidbaza.storypager

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@ExperimentalAnimationApi
@Composable
internal fun StoryPage(
    item: Story,
    nextPage: () -> Unit,
    backPage: () -> Unit,
    content: @Composable ((StoryChild) -> Unit)? = null,
) {
    val stepCount = item.childCount
    var pressTime = 0L
    var position by remember {
        mutableStateOf(item.position)
    }
    var isPlaying by remember {
        mutableStateOf(false)
    }
    val playChanged: (Boolean) -> Unit = {
        isPlaying = it
    }
    val positionSave: (Int) -> Unit = {
        item.position = it
    }

    val positionChange: (Int) -> Unit = {
        position = it
    }

    var isReset by remember {
        mutableStateOf(false)
    }
    var isSkip by remember {
        mutableStateOf(false)
    }

    if (!item.isResumed()) {
        if (isPlaying) {
            isPlaying = false
        }
    } else {
        position = item.position
        isPlaying = true
        isReset = false
        isSkip = false
    }

    BoxWithConstraints(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        val actionsModifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val time = (System.currentTimeMillis() - pressTime)
                        if (time < 300) {
                            val targetPosition =
                                if (offset.x < constraints.maxWidth / 2) {
                                    max(-1, position - 1)
                                } else {
                                    min(stepCount, position + 1)
                                }
                            when {
                                targetPosition < 0 -> {
                                    isReset = true
                                    isSkip = false
                                }
                                targetPosition == stepCount -> {
                                    isSkip = true
                                    isReset = false
                                }
                                else -> {
                                    position = targetPosition
                                    isPlaying = true
                                    isReset = false
                                    isSkip = false
                                }
                            }
                        }
                    },
                    onPress = {
                        pressTime = System.currentTimeMillis()
                        try {
                            isPlaying = false
                            awaitRelease()
                        } finally {
                            isPlaying = true
                            pressTime = System.currentTimeMillis()
                        }
                    }
                )
            }

        val child = item.items[position]

        StoryContent(
            storyChild = child,
            isReady = isPlaying,
            modifier = actionsModifier,
            content = content,
            playChanged = playChanged
        )

        StoryProgress(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 12.dp),
            size = stepCount,
            durationInMillis = child.duration,
            backgroundColor = Color.Gray,
            activeColor = Color.White,
            isReset = isReset,
            isSkip = isSkip,
            position = position,
            positionSave = positionSave,
            positionChange = positionChange,
            nextPage = nextPage,
            backPage = backPage,
            isPaused = !isPlaying
        )
    }

}


