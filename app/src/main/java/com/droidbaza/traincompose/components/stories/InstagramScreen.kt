package com.droidbaza.traincompose.components.stories

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.droidbaza.traincompose.R

import kotlin.math.max
import kotlin.math.min

@Composable
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
    if(!isReady){
        isPaused = true
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
                                min(stepCount - 1, currentStep + 1)
                            }
                        isPaused = false
                    },
                    onPress = {
                        try {
                            isPaused = true
                            awaitRelease()
                        } finally {
                            isPaused = false
                        }
                    },
                    onLongPress = {
                        // must be here to avoid call onTap
                        // for play/pause behavior
                    }
                )
            }
        val imgPosition = if(currentStep<0)0 else currentStep
        Box(modifier = imageModifier.fillMaxSize()){
            Text(text = "story ${story.position} current $currentStep")
        }

        StoryProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            storyCount = stepCount,
            storyDuration = 2_000,
            unselectedColor = Color.LightGray,
            selectedColor = Color.Blue,
            storyPosition = currentStep,
            onPositionChanged = {
                if(isPaused)isPaused = false
                currentStep = it
                story.position = it
                                },
            isPaused = isPaused,
            onComplete = {
                isPaused = true
                currentStep = story.position
                goNext()
            },
            onReset = {
                currentStep = 0
                story.position = 0
                isPaused = true
                goPrevious()
            }
        )
    }
}

@Composable
fun StoryProgressIndicator(
    modifier: Modifier = Modifier,
    storyCount: Int,
    storyDuration: Int,
    unselectedColor: Color,
    selectedColor: Color,
    storyPosition: Int,
    onPositionChanged: (childPosition:Int) -> Unit,
    onComplete: () -> Unit,
    onReset:()->Unit,
    isPaused: Boolean = false
) {
    val position = if(storyPosition>=0)storyPosition else 0
    var statePosition by remember(position) {
        mutableStateOf(position)
    }
    val stateProgress = remember(position) {
        Animatable(0f)
    }
    Row(modifier) {
        for (i in 0 until storyCount) {
            val progress = when {
                i == statePosition -> stateProgress.value
                i > statePosition -> 0f
                else -> 1f
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

    LaunchedEffect(isPaused, storyPosition) {
        if (isPaused) {
            stateProgress.stop()
        } else {
            if(storyPosition<0){
                stateProgress.snapTo(0f)
                onReset()
            }else {
                for (i in storyPosition until storyCount) {
                    stateProgress.animateTo(
                        1f,
                        animationSpec = tween(
                            durationMillis =
                            ((1f - stateProgress.value) * storyDuration)
                                .toInt(),
                            easing = LinearEasing
                        )
                    )
                    if (statePosition + 1 <= storyCount - 1) {

                        stateProgress.snapTo(0f)
                        statePosition += 1
                        onPositionChanged(statePosition)

                    } else {
                        onComplete()
                    }
                }
            }
        }
    }
}