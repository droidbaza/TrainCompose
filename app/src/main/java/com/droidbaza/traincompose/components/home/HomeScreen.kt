package com.droidbaza.traincompose.components.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidbaza.traincompose.data.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "Pagination with Compose") })
            }) {
            LazyNewsItems(viewModel = viewModel)
        }
    }
}

@Composable
fun LazyNewsItems(viewModel: HomeViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val state = viewModel.newsState.collectAsState()
    val lastIndex = state.value.articles.lastIndex
    val parentState = rememberLazyListState()
   // parentState.withSnap(coroutineScope)


    LazyColumn(state = parentState) {
        itemsIndexed(state.value.articles,
            itemContent = { i: Int, newsItem: NewsItem ->
                if (lastIndex == i) {
                    viewModel.getMoreNews()
                }
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "${newsItem.title}",
                    Modifier.padding(4.dp),
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onSurface,
                )
                Spacer(modifier = Modifier.size(4.dp))
                val childState = rememberLazyListState()
               // childState.withSnap(coroutineScope)
                LazyRow(state = childState) {
                    itemsIndexed(state.value.articles,
                        itemContent = { i: Int, newsItem: NewsItem ->
                            if (lastIndex == i) {
                                viewModel.getMoreNews()
                            }
                            NewsCard(newsItem) {
                                viewModel.onSelectedNews(newsItem)
                            }
                        })

                }
                // childState.snap(coroutineScope)

            })
    }
    //  parentState.snap(coroutineScope)
}

fun LazyListState.withSnap(coroutineScope: CoroutineScope) {
    if (!isScrollInProgress && firstVisibleItemScrollOffset != 0) {
        val current = firstVisibleItemIndex
        val last = layoutInfo.totalItemsCount - 1
        val currentOffset = firstVisibleItemScrollOffset
        val maxItemOffset = 1000 / (layoutInfo.visibleItemsInfo.size + 1)
        val snapPosition = if (currentOffset > maxItemOffset && current + 1 <= last) {
            current + 1
        } else {
            current
        }
        coroutineScope.launch {
            animateScrollToItem(snapPosition)
        }
    }
}



