package com.droidbaza.traincompose.components.stories

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.droidbaza.storypager.StoriesPager
import com.droidbaza.storypager.Story
import com.droidbaza.storypager.StoryChild
import com.droidbaza.storypager.StoryType
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun StoryScreen(finish: () -> Unit) {
    val items = remember {
        loadItems()
    }

    StoriesPager(
        items = items,
        finish = finish,
        targetPage = 0,
    )
}

private fun loadItems(): List<Story> {
    val child0 = listOf(
        StoryChild(StoryType.VIDEO, "asset:///sampleh3.mp4", duration = 11_000),
        StoryChild(
            StoryType.IMAGE,
            "https://free-imatges.com/lg/2e33/touring_europa_s_52150167.jpg"
        ),
        StoryChild(StoryType.VIDEO, "asset:///sample4.mp4"),
        StoryChild(StoryType.VIDEO, "asset:///sample2.mp4"),
        StoryChild(StoryType.IMAGE, "https://free-images.com/md/a22a/gfp_sun_behind_clouds.jpg"),
        StoryChild(StoryType.VIDEO, "asset:///sample1.mp4", duration = 48_000),
        StoryChild(StoryType.IMAGE, "https://free-images.com/or/2ef6/f14_tomcats_uss_theodore.jpg"),
        StoryChild(StoryType.VIDEO, "asset:///sample5.mp4", duration = 31_000),
        StoryChild(StoryType.VIDEO, "asset:///sample4.mp4"),
        StoryChild(StoryType.VIDEO, "asset:///sample2.mp4"),
        StoryChild(StoryType.IMAGE, "https://free-images.com/md/a22a/gfp_sun_behind_clouds.jpg"),
        StoryChild(StoryType.VIDEO, "asset:///sample1.mp4", duration = 48_000),
        StoryChild(StoryType.IMAGE, "https://free-images.com/or/2ef6/f14_tomcats_uss_theodore.jpg"),
        StoryChild(StoryType.VIDEO, "asset:///sample5.mp4", duration = 31_000),
        StoryChild(
            StoryType.IMAGE,
            "https://free-images.com/lg/1107/city_landmark_lights_night.jpg"
        )
    )
    child0.forEachIndexed { index, storyChild -> storyChild.position = index }

    val child1 = listOf(
        StoryChild(
            StoryType.IMAGE,
            "https://free-images.com/lg/5a02/london_eye_ferris_wheel_20.jpg"
        ),
        StoryChild(
            StoryType.IMAGE,
            "https://free-images.com/lg/4b9f/skyline_sunset_buildings_cityscape.jpg"
        ),
        StoryChild(StoryType.VIDEO, "asset:///sample8.mp4"),
        StoryChild(
            StoryType.IMAGE,
            "https://free-images.com/lg/3874/austria_alps_tirol_mountains.jpg"
        )
    )
    child1.forEachIndexed { index, storyChild -> storyChild.position = index }

    val child2 = listOf(
        StoryChild(StoryType.VIDEO, "asset:///sample6.mp4", duration = 14_000),
        StoryChild(StoryType.IMAGE, "https://free-images.com/or/2ef6/f14_tomcats_uss_theodore.jpg"),
        StoryChild(StoryType.VIDEO, "asset:///sample7.mp4", duration = 11_000),
        StoryChild(
            StoryType.IMAGE,
            "https://free-images.com/lg/531b/ljubljana_slovenia_ljubljanica_river.jpg"
        )
    )
    child2.forEachIndexed { index, storyChild -> storyChild.position = index }

    val childMap = mutableMapOf<Int, List<StoryChild>>()
    childMap[0] = child0
    childMap[1] = child1
    childMap[2] = child2

    return (0..2).mapIndexed { index, _ ->
        Story(
            page = index,
            items = childMap[index] ?: emptyList()
        )
    }

}