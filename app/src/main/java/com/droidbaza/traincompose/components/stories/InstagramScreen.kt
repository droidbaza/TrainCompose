package com.droidbaza.traincompose.components.stories

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min


@Composable
fun MyInstagramScreen(
    story: Story,
    next: () -> Unit,
    back: () -> Unit
) {
    val stepCount = story.childCount
    var currentStep by remember {
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
    var status by remember {
        mutableStateOf("in progress")
    }

    if (!story.isResumed.value) {
        //msg("PAUSED ${story.page}")
        if (!isPaused) {
            isPaused = true
        }
    } else {
        //  msg("RESUMED ${story.page}")
        currentStep = story.position
        isPaused = false
        isReset = false
        isSkip = false
        //isSkip = false
    }

    var pressTime = 0L
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
                            val position =
                                if (offset.x < constraints.maxWidth / 2) {
                                    max(-1, currentStep - 1)
                                } else {
                                    min(stepCount, currentStep + 1)
                                }
                            when {
                                position < 0 -> {
                                    isReset = true
                                    currentStep = 0
                                    status = "on stop"
                                }
                                position == stepCount -> {
                                    currentStep = position - 1
                                    isSkip = true
                                    isReset = false
                                }
                                else -> {
                                    currentStep = position
                                    isPaused = false
                                    isReset = false
                                    isSkip = false
                                }
                            }
                        }
                    },
                    onPress = {
                        pressTime = System.currentTimeMillis()
                        // msg("PRESS TRIGGER")
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
        val child = story.items[currentStep]

        StoryContent(
            storyChild = child,
            isPaused = isPaused,
            modifier = imageModifier,
            isReady = {
            }
        )

        MyInstagramProgressIndicator(
            page = story.page,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            stepCount = stepCount,
            stepDuration = child.duration,
            unselectedColor = Color.LightGray,
            selectedColor = Color.Blue,
            currentStep = currentStep,
            onStepChanged = {
                // isPaused = true
                currentStep = it
            },
            isReset = isReset,
            isPaused = isPaused,
            onComplete = next,
            savePosition = {
                story.position = it
            },
            isSkip = isSkip,
            goBack = back
        )
    }
}

@Composable
fun StoryContent(
    storyChild: StoryChild,
    isPaused: Boolean,
    isReady: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .fillMaxHeight()
    ) {
        VideoPlayer(sourceUrl = storyChild.source, isPaused, isReady)

        Text(
            text = " page ${storyChild.parentPage} position ${storyChild.position} ",
            modifier = Modifier
                .padding(50.dp)
                .wrapContentWidth()
        )
    }
}

@Composable
fun MyInstagramProgressIndicator(
    page: Int = 0,
    modifier: Modifier = Modifier,
    stepCount: Int,
    stepDuration: Int,
    unselectedColor: Color,
    selectedColor: Color,
    isReset: Boolean = false,
    isSkip: Boolean = false,
    currentStep: Int,
    savePosition: (Int) -> Unit,
    onStepChanged: (Int) -> Unit,
    onComplete: () -> Unit,
    goBack: () -> Unit,
    isPaused: Boolean = false
) {
    var currentStepState by remember(currentStep) {
        mutableStateOf(currentStep)
    }
    val progress = remember(currentStep) {
        Animatable(0f)
    }
    Row(modifier) {
        for (i in 0 until stepCount) {
            val stepProgress = when {
                i == currentStepState -> progress.value
                i > currentStepState -> 0f
                else -> 1f
            }
            LinearProgressIndicator(
                color = selectedColor,
                backgroundColor = unselectedColor,
                progress = stepProgress,
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
            )
        }
    }

    LaunchedEffect(isPaused, currentStep, isReset, isSkip) {
        when {
            isPaused -> {
                //  msg("PAUSED PAGE$page step $currentStepState")
                savePosition(currentStepState)
                progress.stop()
            }
            isReset -> {
                //   msg("RESET PRESSED PAGE$page step $currentStepState")
                progress.animateTo(0f)
                currentStepState = 0
                savePosition(currentStepState)
                goBack()
            }
            isSkip -> {
                //  msg("SKIP PRESSED PAGE$page step $currentStepState")
                currentStepState = stepCount - 1
                savePosition(currentStepState)
                progress.animateTo(1f)
                progress.stop()
                onComplete()
            }
            else -> {
                if (progress.value == 1f) {
                    progress.animateTo(0f)
                }
                for (i in currentStep until stepCount) {

                    progress.animateTo(
                        1f,
                        animationSpec = tween(
                            durationMillis =
                            ((1f - progress.value) * stepDuration)
                                .toInt(),
                            easing = LinearEasing
                        )
                    )
                    if (currentStepState + 1 <= stepCount - 1) {
                        progress.snapTo(0f)
                        currentStepState += 1
                        savePosition(currentStepState)
                        onStepChanged(currentStepState)
                    } else {
                        if (progress.value == 1f) {
                            currentStepState = stepCount - 1
                            savePosition(currentStepState)
                            onComplete()
                        }
                    }
                }
            }
        }
    }
}



