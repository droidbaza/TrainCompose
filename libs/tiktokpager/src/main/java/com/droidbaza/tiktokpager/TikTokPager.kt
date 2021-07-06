package com.droidbaza.tiktokpager

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TikTokModel(
    val playUrl: String,
    var active: MutableState<Boolean> = mutableStateOf(false)
)

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun TikTokPager(
    items: List<TikTokModel>,
    finish: () -> Unit = {},
    targetPage: Int = 0,
    pageListener: ((currentPage: Int, lastPage: Int) -> Unit)? = null,
    content: @Composable ((TikTokModel) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val selectedModel = remember { mutableStateOf(items[0]) }
    val selectedPage = remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(
        pageCount = items.size,
        initialOffscreenLimit = 1,
        initialPage = targetPage
    )

    val jetLifecycle = rememberJetLifecycle()
    jetLifecycle.state.value.let { resumed ->
        selectedModel.value.apply {
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

    VerticalPager(
        state = pagerState
    ) { page ->
        TikTokPageItem(item = items[page])
    }

    LaunchedEffect(pagerState, pageListener) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedPage.value = page
            items.forEachIndexed { index, model ->
                if (!model.active.value && index == page) {
                    model.active.value = true
                } else {
                    if (model.active.value) {
                        model.active.value = false
                    }
                }
            }
            selectedModel.value = items[page]
            pageListener?.invoke(page, items.lastIndex)
        }
    }
}





