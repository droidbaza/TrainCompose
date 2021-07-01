package com.droidbaza.traincompose.components.stories

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.droidbaza.storypager.*
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@Composable
fun StoryScreen(finish: () -> Unit) {
    val childs = listOf(
        StoryChild(StoryType.VIDEO, "asset:///food.mp4"),
        StoryChild(StoryType.IMAGE, "asset:///food.mp4"),
        StoryChild(StoryType.IMAGE, "asset:///castle.mp4"),
        StoryChild(StoryType.VIDEO, "asset:///food.mp4"),
        StoryChild(StoryType.IMAGE, "asset:///food.mp4"),
        StoryChild(StoryType.VIDEO, "asset:///food.mp4"),
        StoryChild(StoryType.IMAGE, "asset:///food.mp4"),
        StoryChild(StoryType.IMAGE, "asset:///castle.mp4"),
        StoryChild(StoryType.VIDEO, "asset:///food.mp4"),
    )
    childs.forEachIndexed { index, storyChild -> storyChild.position = index }
    val items = remember {
        (0..100).mapIndexed { index, _ ->
            Story(
                page = index,
                items = childs
            )
        }
    }
    val context = LocalContext.current

    StoriesPager(
        items = items,
        finish = finish,
        storyOrientation = StoryOrientation.VERTICAL
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            when (it.position) {
                2 -> {

                    Button(
                        onClick = {
                            Toast.makeText(context, "2 position", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .background(color = Color.Blue)
                            .align(Alignment.Center)
                            .wrapContentWidth()
                    ) {
                        Text(text = "2 position")
                    }
                }
                3 -> {
                    Button(
                        onClick = {
                            Toast.makeText(context, "3 position", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .background(color = Color.Green)
                            .align(Alignment.Center)
                    ) {
                        Text(text = "3 position")
                    }
                }
                5 -> {
                    Button(
                        onClick = {
                            Toast.makeText(context, "5 position", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .background(color = Color.Yellow)
                            .align(Alignment.Center)
                    ) {
                        Text(text = "2 position")
                    }
                }
                else -> {
                    Text(
                        text = "default page",
                        modifier = Modifier
                            .background(color = Color.Blue)
                            .align(Alignment.Center)
                    )
                }

            }
        }


    }
}