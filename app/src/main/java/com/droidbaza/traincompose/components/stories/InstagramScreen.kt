package com.droidbaza.traincompose.components.stories

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
        msg("PAUSED ${story.page}")
        if (!isPaused) {
            isPaused = true
        }
    } else {
        msg("RESUMED ${story.page}")
        currentStep = story.position
        isPaused = false
        isReset = false
        isSkip = false
        //isSkip = false
    }

    var pressTime = 0L
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val imageModifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        pressTime = System.currentTimeMillis()
                        //msg("TAP TRIGGER")
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
                                back()
                                msg("START RESET TRIGGER")
                            }
                            position == stepCount -> {
                                msg("START TRIGGER")
                                currentStep = position - 1
                                isSkip = true
                                isReset = false
                                next()
                            }
                            else -> {
                                msg("START ELSE TRIGGER")
                                currentStep = position
                                isPaused = false
                                isReset = false
                                isSkip = false
                                status = "in progress"
                            }
                        }
                    },
                    onPress = {
                        /* val time = (System.currentTimeMillis() - pressTime) / 1000000000
                          msg("time is $time")
                          if (time > 2000) {*/
                        //  msg("PRESSS TRIGGER")
                        try {
                            //      isPaused = true
                            awaitRelease()
                        } finally {
                            //  isPaused = false
                            // isReset = false
                            // isSkip = false
                        }

                    },
                    onLongPress = {
                        // must be here to avoid call onTap
                        // for play/pause behavior
                    }
                )
            }
        val child = story.items[currentStep]
        Box(modifier = imageModifier) {
            Text(text = " page ${story.page} step ${currentStep + 1} time ${System.currentTimeMillis() / 1000} status ${status}")
        }
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
                currentStep = it
            },
            isReset = isReset,
            isPaused = isPaused,
            onComplete = {
                msg("completed ${story.position}")
                //   msg("oncomplete story ${story.page}")
                // isPaused = true
                //currentStep = stepCount-2
                status = "complete"
                //isPaused = true

                next()
            },
            savePosition = {
                story.position = it
            },
            isSkip = isSkip
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
                msg("PAUSED PAGE$page step $currentStepState")
                savePosition(currentStepState)
                progress.stop()
            }
            isReset -> {
                msg("RESET PRESSED PAGE$page step $currentStepState")
                progress.animateTo(0f)
                currentStepState = 0
                savePosition(currentStepState)
            }
            isSkip -> {
                msg("SKIP PRESSED PAGE$page step $currentStepState")
                currentStepState = stepCount - 1
                savePosition(currentStepState)
                progress.snapTo(0f)
                progress.stop()
                //   onComplete()
            }
            else -> {
                if (progress.value == 1f) {
                    progress.animateTo(0f)
                }
                msg("RESUME PAGE$page step $currentStepState")
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



