package com.droidbaza.traincompose.components.stories

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.droidbaza.tiktokpager.TikTokModel
import com.droidbaza.tiktokpager.TikTokPager
import com.droidbaza.tiktokpager.msg
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun TikTokScreen(finish: () -> Unit) {
    val items = remember {
        loadItems()
    }


    TikTokPager(
        items = items,
        finish = finish,
        targetPage = 0,
        pageListener = { currentPage: Int, lastPage: Int ->
            msg(" PAGE EVENT $currentPage|$lastPage")
        }
    )
}


private fun loadItems(): List<TikTokModel> {
    val models = listOf(
        TikTokModel("asset:///sample1.mp4"),
        TikTokModel("asset:///sample2.mp4"),
        TikTokModel("asset:///sample3.mp4"),
        TikTokModel("asset:///sample4.mp4"),
        TikTokModel("asset:///sample5.mp4"),
        TikTokModel("asset:///sample6.mp4"),
        TikTokModel("asset:///sample7.mp4"),
        TikTokModel("asset:///sample8.mp4"),
    )
    models.forEachIndexed { index, tikTokModel -> tikTokModel.page = index }

    return models

}