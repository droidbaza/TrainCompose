package com.droidbaza.storypager

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class StoryLifecycle(val state: MutableState<Boolean> = mutableStateOf(true))

@Composable
internal fun rememberStoryLifecycle(): StoryLifecycle {
    val storyLifecycle = remember { StoryLifecycle() }
    val observer = rememberStoryLifecycleObserver(storyLifecycle = storyLifecycle)
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
internal fun rememberStoryLifecycleObserver(storyLifecycle: StoryLifecycle): LifecycleEventObserver =
    remember(storyLifecycle) {
        LifecycleEventObserver { _, event ->
            storyLifecycle.state.value = event == Lifecycle.Event.ON_RESUME
        }
    }