package com.droidbaza.storypager

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

interface IndicatorCallBack {
    fun pause()
    fun resume()
    fun skip()
    fun reset()
}

@Composable
internal fun StoryProgress(
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
    Row(
        modifier
    ) {
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
                    .height(6.dp)
                    .alpha(0.8f)
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