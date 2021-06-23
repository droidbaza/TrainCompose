package com.droidbaza.traincompose.components.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.droidbaza.data.model.Movie
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun HomeScreen(homeViewModel: HomeViewModel,state: LazyListState) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "Pagination with Compose") })
            }) {
            LazyNewsItems(homeViewModel,state)
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun LazyNewsItems(homeViewModel: HomeViewModel,state: LazyListState) {
    val movies = homeViewModel.newsState.collectAsState()
    val lastIndex = movies.value.items.lastIndex
    val error = homeViewModel.liveError.collectAsState()

    // parentState.withSnap(coroutineScope)
    Log.d("VALUES", " state ${movies.value.items.size}")
    Box(modifier = Modifier.fillMaxHeight()) {
        LazyColumn(state = state, modifier = Modifier.padding(bottom = 56.dp)) {
            itemsIndexed(
                movies.value.items,
                itemContent = { i: Int, movie: Movie ->
                    MovieCard(movie = movie)
                    if (lastIndex - 10 == i) {
                        homeViewModel.onNextPage {
                        }
                    }
                    //val childState = rememberLazyListState()
                    // childState.withSnap(coroutineScope)
                    /*LazyRow(state = childState) {
                        itemsIndexed(state.value.articles,
                            itemContent = { i: Int, newsItem: NewsItem ->
                                if (lastIndex == i) {
                                    viewModel.getMoreNews()
                                }
                                NewsCard(newsItem) {
                                    viewModel.onSelectedNews(newsItem)
                                }
                            })

                    }*/
                    // childState.snap(coroutineScope)

                })
        }

        if (error.value != null) {
            Snackbar(
                action = {
                    Button(onClick = { homeViewModel.onRefresh(false) }) {
                        Text("Retry")
                    }
                },
                modifier = Modifier
                    .padding(bottom = 56.dp)
                    .align(Alignment.BottomCenter)
            ) { Text(text = "Error loading") }

        }
    }


    //  parentState.snap(coroutineScope)
}

@Composable
fun MovieCard(movie: Movie) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberCoilPainter(request = movie.posterPath),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "${movie.name}",
                Modifier.padding(4.dp),
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSurface,
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = movie.pageMeta,
                Modifier.padding(4.dp),
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSurface,
            )
        }
    }
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



