package com.droidbaza.storypager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


enum class StoryType {
    IMAGE, VIDEO
}
enum class StoryOrientation {
    HORIZONTAL, VERTICAL
}

class Story(
    val page: Int,
    val items: List<StoryChild>,
    var position: Int = 0,
    val childCount: Int = items.size,
    var active: MutableState<Boolean> = mutableStateOf(false)
)

class StoryChild(
     val storyType: StoryType,
     val source: String,
     var duration: Int = 15_000,
     var position: Int = 0,
     var parentPage: Int = 0
)