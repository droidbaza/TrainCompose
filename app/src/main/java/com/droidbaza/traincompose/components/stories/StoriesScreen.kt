package com.droidbaza.traincompose.components.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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

data class StoryChild(val storyType: StoryType, val source: String, var duration: Int = 15_000)

@ExperimentalPagerApi
@Composable
fun StoriesScreen(finish: () -> Unit = {}) {
    val childs = listOf(
        StoryChild(StoryType.IMAGE, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4" ),
        StoryChild(StoryType.IMAGE, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"),
        StoryChild(StoryType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        StoryChild(StoryType.IMAGE, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"),
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
    val currentPageStete = remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = pages.size, initialOffscreenLimit = 1)
    val scope = rememberCoroutineScope()

    val lifecycleScreen = rememberLifeCycleScreen()
    val isPause:MutableState<Boolean> = lifecycleScreen.stateResume
    if (!isPause.value) {
        currentStory.value.apply {
            if (isResumed.value) {
                //  isResumed.value = false
            }
        }
    } else {
        currentStory.value.apply {
            if (!isResumed.value) {
                //  isResumed.value = true
            }
        }
    }

    val next: () -> Unit = {
        if (currentPageStete.value + 1 < pages.size) {
            // story.value = pages[currentPage + 1]
            msg("next Page ${currentPageStete.value + 1} from$currentPageStete.value")
            //    currentStory.value.isResumed.value = false
            //  if(currentPage==currentPageStete.value)return@MyInstagramScreen
            scope.launch {
                pagerState.scrollToPage(currentPageStete.value + 1)
            }
        } else {
            finish()
        }
    }

    val back: () -> Unit = {
        if (currentPageStete.value - 1 >= 0) {
            msg("back Page ${currentPageStete.value - 1} from ${currentPageStete.value}")
            //   currentStory.value.isResumed.value = false
            //if(currentPage==currentPageStete.value)return@MyInstagramScreen
            scope.launch {
                pagerState.scrollToPage(currentPageStete.value - 1)
            }
        } else {
            finish()
        }
    }

    Box(contentAlignment = Alignment.Center) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            MyInstagramScreen(
                pages[page],
                next = next,
                back = back
            )
        }
        Text(
            text = "${currentPageStete.value}|${pagerState.pageCount}",
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .fillMaxWidth()
                .background(color = Color.White),
        )
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPageStete.value = page
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


class LifecycleScreen(val stateResume:MutableState<Boolean> = mutableStateOf(true))

@Composable
fun rememberLifeCycleScreen(): LifecycleScreen {
    val lifeCycleScreen = remember { LifecycleScreen() }
    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberLifecycleObserver(lifecycleScreen = lifeCycleScreen)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return lifeCycleScreen
}

@Composable
private fun rememberLifecycleObserver(lifecycleScreen: LifecycleScreen): LifecycleEventObserver =
    remember(lifecycleScreen) {
        LifecycleEventObserver { _, event ->
            val result = when (event) {
                Lifecycle.Event.ON_START -> true
                Lifecycle.Event.ON_RESUME -> true
                else -> false
            }
            lifecycleScreen.stateResume.value = result

        }
    }
