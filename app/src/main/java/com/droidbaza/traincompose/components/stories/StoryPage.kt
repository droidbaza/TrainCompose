package com.droidbaza.traincompose.components.stories

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min


@Composable
fun StoryPage(
    story: Story,
    nextPage: () -> Unit,
    backPage: () -> Unit,
    content: @Composable (StoryChild) -> Unit,
) {
    val stepCount = story.childCount
    var pressTime = 0L

    var position by remember {
        mutableStateOf(story.position)
    }
    var isPaused by remember {
        mutableStateOf(true)
    }
    var isReset by remember {
        mutableStateOf(false)
    }
    var isSkip by remember {
        mutableStateOf(false)
    }

    if (!story.active.value) {
        if (!isPaused) {
            isPaused = true
        }
    } else {
        position = story.position
        isPaused = false
        isReset = false
        isSkip = false
    }

    BoxWithConstraints(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        val imageModifier = Modifier
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
                                    isPaused = false
                                    isReset = false
                                    isSkip = false
                                }
                            }
                        }
                    },
                    onPress = {
                        pressTime = System.currentTimeMillis()
                        try {
                            isPaused = true
                            awaitRelease()
                        } finally {
                            isPaused = false
                            pressTime = System.currentTimeMillis()
                        }
                    },
                )
            }
        val child = story.items[position]

        StoryContent(
            storyChild = child,
            isPaused = isPaused,
            modifier = imageModifier,
            isReady = {
            },
            content = content
        )

        StoryProgress(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            size = stepCount,
            durationInMillis = child.duration,
            backgroundColor = Color.LightGray,
            activeColor = Color.Blue,
            isReset = isReset,
            isSkip = isSkip,
            position = position,
            positionSave = { story.position = it },
            positionChange = { position = it },
            nextPage = nextPage,
            backPage = backPage,
            isPaused = isPaused
        )
    }
}


@Composable
fun StoryProgress(
    modifier: Modifier = Modifier,
    size: Int,
    durationInMillis: Int,
    backgroundColor: Color,
    activeColor: Color,
    isReset: Boolean = false,
    isSkip: Boolean = false,
    isPaused: Boolean = false,
    position: Int,
    positionSave: (Int) -> Unit,
    positionChange: (Int) -> Unit,
    nextPage: () -> Unit,
    backPage: () -> Unit,
) {
    var statePosition by remember(position) {
        mutableStateOf(position)
    }
    val progress = remember(position) {
        Animatable(0f)
    }
    Row(modifier) {
        for (i in 0 until size) {
            val progressValue = when {
                i == statePosition -> progress.value
                i > statePosition -> 0f
                else -> 1f
            }
            LinearProgressIndicator(
                color = activeColor,
                backgroundColor = backgroundColor,
                progress = progressValue,
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
            )
        }
    }

    LaunchedEffect(isPaused, position, isReset, isSkip) {
        when {
            isPaused -> {
                positionSave(statePosition)
                progress.stop()
            }
            isReset -> {
                progress.animateTo(0f)
                statePosition = 0
                positionSave(statePosition)
                backPage()
            }
            isSkip -> {
                statePosition = size - 1
                positionSave(statePosition)
                progress.animateTo(1f)
                progress.stop()
                nextPage()
            }
            else -> {
                if (progress.value == 1f) {
                    progress.animateTo(0f)
                }
                for (i in position until size) {
                    val durationRemain = ((1f - progress.value) * durationInMillis).toInt()
                    progress.animateTo(
                        1f,
                        animationSpec = tween(
                            durationMillis = durationRemain,
                            easing = LinearEasing
                        )
                    )
                    if (statePosition + 1 <= size - 1) {
                        progress.snapTo(0f)
                        statePosition += 1
                        positionSave(statePosition)
                        positionChange(statePosition)
                    } else {
                        if (progress.value == 1f) {
                            statePosition = size - 1
                            positionSave(statePosition)
                            nextPage()
                        }
                    }
                }
            }
        }
    }
}



