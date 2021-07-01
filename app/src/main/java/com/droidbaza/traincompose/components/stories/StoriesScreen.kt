package com.droidbaza.traincompose.components.stories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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

data class StoryChild(
    val storyType: StoryType,
    val source: String,
    var duration: Int = 15_000,
    var position: Int = 0,
    var parentPage: Int = 0
)

@ExperimentalPagerApi
@Composable
fun StoriesScreen(finish: () -> Unit = {}) {
    val childs = listOf(
        StoryChild(StoryType.IMAGE, "food.mp4"),
        StoryChild(StoryType.IMAGE, "castle.mp4"),
        StoryChild(StoryType.VIDEO, "food.mp4"),
        StoryChild(StoryType.IMAGE, "food.mp4"),
    )
    childs.forEachIndexed { index, storyChild -> storyChild.position = index }
    val pages = remember {
        (0..100).mapIndexed { index, _ ->
            Story(
                page = index,
                items = childs
            )
        }
    }
    val currentStory = remember { mutableStateOf(pages[0]) }
    val currentPageStete = remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = pages.size, initialOffscreenLimit = 1)
    val scope = rememberCoroutineScope()

    val lifecycleScreen = rememberLifeCycleScreen()
    lifecycleScreen.stateResume.value.let { resumed ->
        currentStory.value.apply {
            if (isResumed.value) {
                if (!resumed) {
                    isResumed.value = false
                }
            } else {
                if (resumed) {
                    isResumed.value = true
                }

            }
        }
    }

    val next: () -> Unit = {
        val target = currentPageStete.value + 1
        if (target < pages.size) {
            scope.launch {
                pagerState.scrollToPage(target)
            }
        } else {
            finish()
        }
    }

    val back: () -> Unit = {
        val target = currentPageStete.value - 1
        if (target >= 0) {
            scope.launch {
                pagerState.scrollToPage(target)
            }
        } else {
            finish()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            MyInstagramScreen(
                pages[page],
                next = next,
                back = back
            )
        }
    }

    LaunchedEffect(pagerState) {

        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPageStete.value = page
          //  msg("launch effect select page $page")
            pages.forEachIndexed { index, story ->
                if (!story.isResumed.value && index == page) {
                    story.isResumed.value = true
                    //msg("CHANGE FROM PAUSE TO RESUME № ${story.page} :${story.isResumed.value}")
                } else {
                    if (story.isResumed.value) {
                        story.isResumed.value = false
                        // msg("CHANGE FROM RESUME TO PAUSE № ${story.page} :${story.isResumed.value}")
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
            lifecycleScreen.stateResume.value = event == Lifecycle.Event.ON_RESUME
        }
    }

