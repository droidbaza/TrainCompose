package com.droidbaza.traincompose.components.stories

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


enum class StoryType {
    IMAGE, VIDEO
}

data class Story(
    val page: Int,
    val items: List<StoryChild>,
    var position: Int = 0,
    val childCount: Int = items.size,
    var active: MutableState<Boolean> = mutableStateOf(false)
)

data class StoryChild(
    val storyType: StoryType,
    val source: String,
    var duration: Int = 15_000,
    var position: Int = 0,
    var parentPage: Int = 0
)