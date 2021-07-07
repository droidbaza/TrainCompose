package com.droidbaza.storypager

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun StoriesPager(
    items: List<Story>,
    finish: () -> Unit = {},
    targetPage: Int = 0,
    content: @Composable ((StoryChild) -> Unit)? = null
) {
    val selectedStory = remember { mutableStateOf(items[0]) }
    val selectedPage = remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(
        pageCount = items.size,
        initialOffscreenLimit = 1,
        initialPage = targetPage
    )
    val scope = rememberCoroutineScope()

    val storyLifecycle = rememberStoryLifecycle()
    storyLifecycle.state.value.let { resumed ->
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

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedPage.value = page
            selectedStory.value = items[page]
            items.forEach {
                if (it.page != page) {
                    it.onPause()
                }
            }
            items[page].onResume()
        }
    }
}




