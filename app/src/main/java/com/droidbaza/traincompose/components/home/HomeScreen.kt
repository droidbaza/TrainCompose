package com.droidbaza.traincompose.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.droidbaza.data.model.Movie
import com.google.accompanist.coil.rememberCoilPainter


inline fun <T : Any> List<T>.isReadyForNext(
    currentIndex: Int,
    readyItemPosition: Int = 10,
    loadNext: () -> Unit
) {
    if (lastIndex - readyItemPosition == currentIndex) {
        loadNext()
    }
}

@Composable
fun LazyMovieItems(
    items: List<Movie>,
    error: Int?,
    loading: Boolean,
    goDetails: (Movie) -> Unit,
    nextPage: () -> Unit,
    onRefresh: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center),
                color = Color.Blue,
                strokeWidth = 2.dp
            )
        }

        LazyColumn(
            modifier = Modifier.padding(bottom = 56.dp),
        ) {
            itemsIndexed(
                items,
                itemContent = { i: Int, movie: Movie ->
                    MovieCard(
                        movie = movie,
                        onClick = goDetails
                    )
                    items.isReadyForNext(
                        currentIndex = i,
                        loadNext = nextPage
                    )
                })
        }

        if (error != null) {
            Snackbar(
                action = {
                    Button(onClick = onRefresh) {
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
fun MovieCard(movie: Movie, onClick: (Movie) -> Unit) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable { onClick(movie) },
        backgroundColor = Color.LightGray
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


/*fun LazyListState.withSnap(coroutineScope: CoroutineScope) {
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
}*/



