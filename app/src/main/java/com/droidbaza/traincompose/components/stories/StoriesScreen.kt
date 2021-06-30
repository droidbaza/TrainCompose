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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class StoryType {
    IMAGE, VIDEO
}

data class Story(
    val page: Int,
    val items: List<StoryChild>,
    var position: Int = 0,
    val childCount: Int = items.size,
    var isResumed: MutableState<Boolean> = mutableStateOf(false)
)

data class StoryChild(val storyType: StoryType, val source: String, var duration: Int = 20_000)

@ExperimentalPagerApi
@Composable
fun StoriesScreen(finish: () -> Unit = {}) {
    val childs = listOf(
        StoryChild(StoryType.IMAGE, ""),
        StoryChild(StoryType.IMAGE, ""),
        StoryChild(StoryType.VIDEO, ""),
        StoryChild(StoryType.IMAGE, ""),
    )
    val pages = remember {
        (0..100).mapIndexed { index, _ ->
            Story(
                page = index,
                listOf(
                    childs.get(Random.nextInt(0, childs.size)),
                    childs.get(Random.nextInt(0, childs.size)),
                    childs.get(Random.nextInt(0, childs.size)),
                    childs.get(Random.nextInt(0, childs.size)),
                    childs.get(Random.nextInt(0, childs.size)),
                    childs.get(Random.nextInt(0, childs.size)),
                    childs.get(Random.nextInt(0, childs.size))
                )
            )
        }
    }
    val currentStory = remember { mutableStateOf(pages[0]) }
    val currentPage = remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = pages.size, initialOffscreenLimit = 1)
    val scope = rememberCoroutineScope()

    Box(contentAlignment = Alignment.Center) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            MyInstagramScreen(
                currentStory.value,
                next = {
                    if (currentPage.value + 1 < pages.size) {
                        // story.value = pages[currentPage + 1]
                        msg("next Page ${currentPage.value + 1}")
                        scope.launch {
                            pagerState.scrollToPage(currentPage.value + 1)
                        }
                    } else {
                        finish()
                    }
                },
                back = {
                    if (currentPage.value - 1 >= 0) {
                        msg("back Page ${currentPage.value - 1}")
                        scope.launch {
                            pagerState.scrollToPage(currentPage.value - 1)
                        }
                    } else {
                        finish()
                    }
                }
            )
        }
        Text(
            text = "${currentPage.value}|${pagerState.pageCount}",
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .fillMaxWidth()
                .background(color = Color.White),
        )
    }

    LaunchedEffect(pagerState, pages, currentStory, currentPage) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPage.value = page
            msg("launch effect select page $page")
            pages.forEachIndexed { index, story ->
                if (!story.isResumed.value && index == page) {
                    story.isResumed.value = true
                    msg("CHANGE FROM PAUSE TO RESUME № ${story.page} :${story.isResumed.value}")
                } else {
                    if (story.isResumed.value) {
                        story.isResumed.value = false
                        msg("CHANGE FROM RESUME TO PAUSE № ${story.page} :${story.isResumed.value}")
                    }
                }
            }
            currentStory.value = pages[page]
        }
    }
}

@Composable
fun StoriesPage(
    modelStory: Story,
    isReady: Boolean = false,
    isRunning: Boolean = false,
    statePlaying: (isPaused: Boolean) -> Unit = {},
    previousStory: () -> Unit = {},
    nextStory: () -> Unit = {},
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
    indicatorWeight: Float = 1f,
    indicatorPadding: Dp = 2.dp,
    storyDuration: Int = 4_000,
) {
    val items = modelStory.items
    val count = items.size
    val position = modelStory.position
    val story = items[position]

    var isPaused by remember {
        mutableStateOf(true)
    }
    isPaused = !isRunning
    if (!isPaused && !isReady) {
        isPaused = true
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val boxModifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        if (offset.x < constraints.maxWidth / 2) {
                            if (modelStory.position - 1 >= 0) {
                                modelStory.position = modelStory.position - 1
                            } else {
                                previousStory()
                            }
                        } else {
                            if (modelStory.position + 1 < count) {
                                modelStory.position = modelStory.position + 1
                            } else {
                                nextStory()
                            }
                        }
                    },
                    onPress = {
                        try {
                            statePlaying(true)
                            awaitRelease()
                        } finally {
                            statePlaying(false)
                        }
                    }
                )
            }

        StoryProgress(
            modifier = modifier,
            indicatorWeight = indicatorWeight,
            indicatorPadding = indicatorPadding,
            stepCount = count,
            stepDuration = storyDuration,
            backgroundColor = Color.LightGray,
            color = Color.Blue,
            currentPosition = modelStory.position,
            positionChanged = {
                modelStory.position = it
            },
            isPaused = isPaused,
            progressComplete = { nextStory() }
        )
        Box(modifier = boxModifier) {
            when (story.storyType) {
                StoryType.IMAGE -> {
                    Text(text = "this is image position is $position")
                }
                else -> {
                    Text(text = "this is video position is $position")
                }
            }
        }
    }

}

@Composable
fun StoryProgress(
    modifier: Modifier = Modifier,
    indicatorWeight: Float,
    indicatorPadding: Dp,
    stepCount: Int,
    stepDuration: Int,
    backgroundColor: Color,
    color: Color,
    currentPosition: Int,
    positionChanged: (Int) -> Unit,
    progressComplete: () -> Unit,
    isPaused: Boolean = false
) {
    var currentStepState by remember(currentPosition) {
        mutableStateOf(currentPosition)
    }
    val progress = remember(currentPosition) {
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
                color = color,
                backgroundColor = backgroundColor,
                progress = stepProgress,
                modifier = Modifier
                    .weight(indicatorWeight)
                    .padding(indicatorPadding)
            )
        }
    }

    LaunchedEffect(isPaused, currentPosition) {
        if (isPaused) {
            progress.stop()
        } else {
            for (i in currentPosition until stepCount) {
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
                    positionChanged(currentStepState)
                } else {
                    progressComplete()
                }
            }
        }
    }
}