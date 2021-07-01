package com.droidbaza.traincompose.components.stories

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@ExperimentalPagerApi
@Composable
fun StoriesPager(
    items: List<Story>,
    finish: () -> Unit = {},
    storyOrientation: StoryOrientation = StoryOrientation.HORIZONTAL,
    content: @Composable (StoryChild) -> Unit
) {
    val selectedStory = remember { mutableStateOf(items[0]) }
    val selectedPage = remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = items.size, initialOffscreenLimit = 1)
    val scope = rememberCoroutineScope()

    val lifecycleScreen = rememberStoryLifecycle()
    lifecycleScreen.state.value.let { resumed ->
        selectedStory.value.apply {
            if (active.value) {
                if (!resumed) {
                    active.value = false
                }
            } else {
                if (resumed) {
                    active.value = true
                }

            }
        }
    }

    val nextPage: () -> Unit = {
        val target = selectedPage.value + 1
        if (target < items.size) {
            scope.launch {
                pagerState.animateScrollToPage(target)
            }
        } else {
            finish()
        }
    }

    val backPage: () -> Unit = {
        val target = selectedPage.value - 1
        if (target >= 0) {
            scope.launch {
                pagerState.animateScrollToPage(target)
            }
        } else {
            finish()
        }
    }

    when (storyOrientation) {
        StoryOrientation.HORIZONTAL -> {
            HorizontalPager(
                state = pagerState
            ) { page ->
                StoryPage(
                    items[page],
                    nextPage = nextPage,
                    backPage = backPage,
                    content = content
                )
            }
        }
        StoryOrientation.VERTICAL -> {
            VerticalPager(
                state = pagerState
            ) { page ->
                StoryPage(
                    items[page],
                    nextPage = nextPage,
                    backPage = backPage,
                    content = content
                )
            }
        }
    }


    LaunchedEffect(pagerState) {

        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedPage.value = page
            //  msg("launch effect select page $page")
            items.forEachIndexed { index, story ->
                if (!story.active.value && index == page) {
                    story.active.value = true
                    //msg("CHANGE FROM PAUSE TO RESUME № ${story.page} :${story.isResumed.value}")
                } else {
                    if (story.active.value) {
                        story.active.value = false
                        // msg("CHANGE FROM RESUME TO PAUSE № ${story.page} :${story.isResumed.value}")
                    }
                }
            }
            selectedStory.value = items[page]
        }
    }
}


class StoryLifecycle(val state: MutableState<Boolean> = mutableStateOf(true))

@Composable
fun rememberStoryLifecycle(): StoryLifecycle {
    val storyLifecycle = remember { StoryLifecycle() }
    val observer = remeberStoryLifecycleObserver(storyLifecycle = storyLifecycle)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    return storyLifecycle
}

@Composable
private fun remeberStoryLifecycleObserver(storyLifecycle: StoryLifecycle): LifecycleEventObserver =
    remember(storyLifecycle) {
        LifecycleEventObserver { _, event ->
            storyLifecycle.state.value = event == Lifecycle.Event.ON_RESUME
        }
    }

