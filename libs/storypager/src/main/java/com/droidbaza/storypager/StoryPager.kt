package com.droidbaza.storypager

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

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
            items.forEachIndexed { index, story ->
                if (!story.active.value && index == page) {
                    story.active.value = true
                } else {
                    if (story.active.value) {
                        story.active.value = false
                    }
                }
            }
            selectedStory.value = items[page]
        }
    }
}

@Composable
private fun StoryPage(
    story: Story,
    nextPage: () -> Unit,
    backPage: () -> Unit,
    content: @Composable (StoryChild) -> Unit,
) {
    val stepCount = story.childCount
    var pressTime = 0L
    var position by remember {
        mutableStateOf(story.position)
    }
    var isPaused by remember {
        mutableStateOf(true)
    }
    var isReset by remember {
        mutableStateOf(false)
    }
    var isSkip by remember {
        mutableStateOf(false)
    }
    if (!story.active.value) {
        if (!isPaused) {
            isPaused = true
        }
    } else {
        position = story.position
        isPaused = false
        isReset = false
        isSkip = false
    }
    BoxWithConstraints(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        val imageModifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val time = (System.currentTimeMillis() - pressTime)
                        if (time < 300) {
                            val targetPosition =
                                if (offset.x < constraints.maxWidth / 2) {
                                    max(-1, position - 1)
                                } else {
                                    min(stepCount, position + 1)
                                }
                            when {
                                targetPosition < 0 -> {
                                    isReset = true
                                    isSkip = false
                                }
                                targetPosition == stepCount -> {
                                    isSkip = true
                                    isReset = false
                                }
                                else -> {
                                    position = targetPosition
                                    isPaused = false
                                    isReset = false
                                    isSkip = false
                                }
                            }
                        }
                    },
                    onPress = {
                        pressTime = System.currentTimeMillis()
                        try {
                            isPaused = true
                            awaitRelease()
                        } finally {
                            isPaused = false
                            pressTime = System.currentTimeMillis()
                        }
                    },
                )
            }
        val child = story.items[position]

        StoryContent(
            storyChild = child,
            isPaused = isPaused,
            modifier = imageModifier,
            isReady = {
            },
            content = content
        )

        StoryProgress(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            size = stepCount,
            durationInMillis = child.duration,
            backgroundColor = Color.LightGray,
            activeColor = Color.Blue,
            isReset = isReset,
            isSkip = isSkip,
            position = position,
            positionSave = { story.position = it },
            positionChange = { position = it },
            nextPage = nextPage,
            backPage = backPage,
            isPaused = isPaused
        )
    }
}

@Composable
private fun StoryProgress(
    modifier: Modifier = Modifier,
    size: Int,
    durationInMillis: Int,
    backgroundColor: Color,
    activeColor: Color,
    isReset: Boolean = false,
    isSkip: Boolean = false,
    isPaused: Boolean = false,
    position: Int,
    positionSave: (Int) -> Unit,
    positionChange: (Int) -> Unit,
    nextPage: () -> Unit,
    backPage: () -> Unit,
) {
    var statePosition by remember(position) {
        mutableStateOf(position)
    }
    val progress = remember(position) {
        Animatable(0f)
    }
    Row(modifier) {
        for (i in 0 until size) {
            val progressValue = when {
                i == statePosition -> progress.value
                i > statePosition -> 0f
                else -> 1f
            }
            LinearProgressIndicator(
                color = activeColor,
                backgroundColor = backgroundColor,
                progress = progressValue,
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
            )
        }
    }

    LaunchedEffect(isPaused, position, isReset, isSkip) {
        when {
            isPaused -> {
                positionSave(statePosition)
                progress.stop()
            }
            isReset -> {
                progress.animateTo(0f)
                statePosition = 0
                positionSave(statePosition)
                backPage()
            }
            isSkip -> {
                statePosition = size - 1
                positionSave(statePosition)
                progress.animateTo(1f)
                progress.stop()
                nextPage()
            }
            else -> {
                if (progress.value == 1f) {
                    progress.animateTo(0f)
                }
                for (i in position until size) {
                    val durationRemain = ((1f - progress.value) * durationInMillis).toInt()
                    progress.animateTo(
                        1f,
                        animationSpec = tween(
                            durationMillis = durationRemain,
                            easing = LinearEasing
                        )
                    )
                    if (statePosition + 1 <= size - 1) {
                        progress.snapTo(0f)
                        statePosition += 1
                        positionSave(statePosition)
                        positionChange(statePosition)
                    } else {
                        if (progress.value == 1f) {
                            statePosition = size - 1
                            positionSave(statePosition)
                            nextPage()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryContent(
    storyChild: StoryChild,
    isPaused: Boolean,
    isReady: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (StoryChild) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .fillMaxHeight()
    ) {
        when (storyChild.storyType) {
            StoryType.VIDEO -> {
                StoryVideo(storyChild.source, isPaused, isReady)
            }
            StoryType.IMAGE -> {
                StoryImage(storyChild.source, isPaused, isReady)
            }
        }
        content(storyChild)
    }
}

@Composable
private fun StoryImage(sourceUrl: String, isPlay: Boolean = false, changePlay: (Boolean) -> Unit) {

    /* Image(
         painter = painterResource(id = R.drawable),
         contentDescription = "",
         modifier = Modifier
             .background(color = Color.Red)
             .fillMaxSize()
     )*/
}

@Composable
private fun StoryVideo(sourceUrl: String, isPlay: Boolean = false, changePlay: (Boolean) -> Unit) {
    val context = LocalContext.current
    val playUrl = Uri.parse(sourceUrl)
    val currentUrl: MutableState<Uri?> = remember {
        mutableStateOf(null)
    }
    val dataSourceFactory: DataSource.Factory = remember {
        DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )
    }
    val source = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(playUrl)

    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    if (currentUrl.value != playUrl) {
        currentUrl.value = playUrl
        exoPlayer.stop(true)
        exoPlayer.prepare(source)
    }
    if (isPlay) {
        exoPlayer.pause()
    } else {
        exoPlayer.play()
    }
    exoPlayer.addListener(object : Player.Listener {
        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            changePlay(false)
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            super.onIsLoadingChanged(isLoading)
            if (!isLoading) {
                changePlay(true)
            } else {
                changePlay(false)
            }
        }
    })
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

    DisposableEffect(AndroidView(factory = {
        PlayerView(context).apply {
            hideController()
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    })) {
        onDispose {
            exoPlayer.release()
        }
    }
}

private class StoryLifecycle(val state: MutableState<Boolean> = mutableStateOf(true))

@Composable
private fun rememberStoryLifecycle(): StoryLifecycle {
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
private fun rememberStoryLifecycleObserver(storyLifecycle: StoryLifecycle): LifecycleEventObserver =
    remember(storyLifecycle) {
        LifecycleEventObserver { _, event ->
            storyLifecycle.state.value = event == Lifecycle.Event.ON_RESUME
        }
    }

