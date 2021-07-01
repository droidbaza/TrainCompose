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
                                currentStep = 0
                                isReset = true
                                status = "on stop"
                                msg("START RESET TRIGGER")
                                isSkip = false

                            }
                            position == stepCount -> {
                                msg("START TRIGGER")
                                currentStep = position - 1
                                isSkip = true
                                isReset = false
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
                            isPaused = true
                            awaitRelease()
                        } finally {
                            isPaused = false
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
            VideoPlayer(uri = child.source,isPaused) {
                isPaused = !it
            }
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
                if (currentStep == 0) {
                    status = "is start position"
                    msg("TRY GO START")
                    back()
                } else {
                    status = "in progress"
                }
            },
            isReset = isReset,
            isPaused = isPaused,
            onComplete = {
                msg("completed ${story.position}")
                //   msg("oncomplete story ${story.page}")
                // isPaused = true
                //currentStep = stepCount-2
                status = "complete"
                next()
            },
            savePosition = {
                story.position = it
            },
            onBack = back,
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
    onBack: () -> Unit,
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
                currentStepState = 0
                savePosition(currentStepState)
                progress.snapTo(0f)
                progress.stop()
                onStepChanged(0)
            }
            isSkip -> {
                msg("SKIP PRESSED PAGE$page step $currentStepState")
                currentStepState = stepCount - 1
                savePosition(currentStepState)
                onComplete()
            }
            else -> {
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
                        currentStepState = stepCount - 1
                        savePosition(currentStepState)
                        onComplete()
                    }
                }
            }
        }
    }
}
/*@Composable
fun MyInstagramScreen(story: Story,
                      isReady: Boolean = false,
                      goNext:()->Unit,
                      goPrevious:()->Unit
) {
    val items = remember { story.items }
    val stepCount = items.count()

    var currentStep by remember {
        mutableStateOf(story.position)
    }
    story.position = currentStep

    var isPaused by remember {
        mutableStateOf(false)
    }
    var isSkipped by remember {
        mutableStateOf(false)
    }
    var isReset by remember {
        mutableStateOf(false)
    }
    var isClear by remember {
        mutableStateOf(false)
    }
    if (!isReady) {
        // isPaused = true
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val imageModifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        currentStep =
                            if (offset.x < constraints.maxWidth / 2) {
                                max(-1, currentStep - 1)
                            } else {
                                min(stepCount, currentStep + 1)
                            }
                        when {
                            currentStep < 0 -> {
                                currentStep = 0
                                isReset = true
                                // isSkipped = false
                            }
                            currentStep >= stepCount -> {
                                currentStep = stepCount - 1
                                //isReset = false
                                isSkipped = true
                            }
                            else -> {
                                isReset = false
                                isSkipped = false
                            }
                        }
                        isClear = if (isClear) {
                            false
                        }else{
                            story.position == items.size - 1
                        }
                        story.position = currentStep
                        // isPaused = false
                    },
                    onPress = {
                        try {
                            //  isPaused = true
                            awaitRelease()
                        } finally {
                            // isPaused = false
                        }
                    },
                    onLongPress = {
                        // must be here to avoid call onTap
                        // for play/pause behavior
                    }
                )
            }

        Box(modifier = imageModifier.fillMaxSize()){
            Text(text = "story ${story.position} current $currentStep")
        }

        StoryProgressIndicator(
            size = stepCount,
            position = currentStep,
            isPaused = isPaused,
            isSkipped = isSkipped,
            isReset = isReset,
            onComplete = goNext,
            isClear = isClear,
            goNextPosition = {
                currentStep = it
                //isPaused = false
                story.position = currentStep
            },
            goPrevious = goPrevious
        )
    }
}

@Composable
fun StoryProgressIndicator(
    size: Int,
    position: Int,
    goPrevious: () -> Unit,
    goNextPosition: (position: Int) -> Unit,
    onComplete: () -> Unit,
    isPaused: Boolean = false,
    isReset: Boolean = false,
    isSkipped: Boolean = false,
    isClear: Boolean = false,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
    time: Int = 3_000,
    unselectedColor: Color = Color.LightGray,
    selectedColor: Color = Color.Blue,
) {

    var statePosition by remember(position) { mutableStateOf(position) }
    val stateProgress = remember(position) { Animatable(0f) }

    Row(modifier) {
        (0 until size).forEach {

            var progress = 0f
            when {
                it == statePosition && !isClear -> progress = stateProgress.value
                it == statePosition && isClear -> progress = 0f
                it > statePosition -> progress = 0f
                else -> {
                    progress = 1f

                }
            }

            LinearProgressIndicator(
                color = selectedColor,
                backgroundColor = unselectedColor,
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
            )
        }
    }

    LaunchedEffect(isPaused, position, isSkipped, isReset, isClear) {
        when {
            isPaused -> stateProgress.stop()
            isSkipped -> {
                stateProgress.animateTo(1f)
                stateProgress.stop()
                statePosition = size - 1
                onComplete()
            }
            isClear -> {
                stateProgress.animateTo(0f)
            }
            isReset -> {
                stateProgress.animateTo(0f)
                //  stateProgress.stop()
                statePosition -= 1
                goPrevious()
            }
            else -> {
                (position until size).onEach {
                    val duration = ((1f - stateProgress.value) * time).toInt()
                    stateProgress.animateTo(
                        1f,
                        animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                    )
                    if (statePosition + 1 < size - 1) {
                        stateProgress.snapTo(0f)
                        statePosition += 1
                        goNextPosition(statePosition)

                    } else {
                        onComplete()
                    }
                }
            }
        }
    }
}*/


