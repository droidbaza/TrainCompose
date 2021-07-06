package com.droidbaza.tiktokpager

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class JetLifecycle(val state: MutableState<Boolean> = mutableStateOf(true))

@Composable
fun rememberJetLifecycle(): JetLifecycle {
    val jetLifecycle = remember { JetLifecycle() }
    val observer = rememberJetLifecycleObserver(storyLifecycle = jetLifecycle)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    return jetLifecycle
}

@Composable
fun rememberJetLifecycleObserver(storyLifecycle: JetLifecycle): LifecycleEventObserver =
    remember(storyLifecycle) {
        LifecycleEventObserver { _, event ->
            storyLifecycle.state.value = event == Lifecycle.Event.ON_RESUME
        }
    }