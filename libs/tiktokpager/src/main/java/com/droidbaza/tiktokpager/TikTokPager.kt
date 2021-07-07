package com.droidbaza.tiktokpager

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TikTokModel(
    val playUrl: String,
    var active: MutableState<Boolean> = mutableStateOf(false),
    var page: Int = 0,
) {
    fun onResume() {
        if (!active.value) {
            active.value = true
        }
    }

    fun onPause() {
        if (active.value) {
            active.value = false
        }
    }

    fun isResumed(): Boolean {
        return active.value
    }
}


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
    Box() {
        VerticalPager(
            modifier = Modifier.padding(bottom = 56.dp),
            state = pagerState
        ) { page ->
            Card(shape = RoundedCornerShape(10.dp)) {
                TikTokPageItem(item = items[page])
            }
        }

        TabRow(modifier = Modifier.align(Alignment.TopCenter)) {

        }
    }


    LaunchedEffect(pagerState, pageListener) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedPage.value = page
            items.forEach {
                if (it.page != page) {
                    it.onPause()
                }
            }
            items[page].onResume()
            selectedModel.value = items[page]
            pageListener?.invoke(page, items.lastIndex)
        }
    }
}





