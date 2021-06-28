package com.droidbaza.traincompose.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.droidbaza.data.model.Movie
import com.droidbaza.traincompose.components.home.LazyMovieItems
import com.droidbaza.traincompose.components.viewmodels.MoviesViewModel
import kotlin.random.Random

@Composable
fun HomeScreen(goDetails: (id: Int) -> Unit, goBack: () -> Unit) {
    BackHandler {
        goBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Home View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goDetails(Random.nextInt(0, 10000))
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Composable
fun HomeScreenDetails(id: Int, goDetails: (id: Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Home View$id",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goDetails(Random.nextInt(0, 10000))
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}


@Composable
fun MusicScreen(goDetails: (id: Int) -> Unit, goBack: () -> Unit) {
    BackHandler {
        goBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Music View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goDetails(Random.nextInt(1, 50))
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}


@Composable
fun IntroScreen(goBack: () -> Unit) {
    BackHandler {
        goBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Intro screen",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goBack()
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Composable
fun SplashScreen(goBack: () -> Unit) {
    BackHandler {
        goBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Splash screen",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goBack()
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Composable
fun MusicScreenDetails(id: Int, goDetails: (id: Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Music Details$id",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goDetails(Random.nextInt(0, 10000))
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}


@Composable
fun MoviesScreen(viewModel: MoviesViewModel,goDetails: (Movie) -> Unit, goBack: () -> Unit) {
    val itemsState = viewModel.itemsState.collectAsState()
    val error = viewModel.errorState.collectAsState()
    val loading = viewModel.loading.collectAsState()

    BackHandler {
        goBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Movies View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
        LazyMovieItems(
            items = itemsState.value,
            error = error.value,
            loading = loading.value,
            goDetails = goDetails,
            nextPage = { viewModel.onNextPage() },
            onRefresh = { viewModel.onRefresh(false) }
        )
    }
}

@Composable
fun MoviesScreenDetails(movie: Movie?, goDetails: (Movie) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Movies Details${movie?.id}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    if (movie != null) {
                        goDetails(movie)
                    }
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}


@Composable
fun BooksScreen(goDetails: (id: Int) -> Unit, goBack: () -> Unit) {
    BackHandler {
        goBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Books View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goDetails(Random.nextInt(101, 200))
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Composable
fun BooksScreenDetails(id: Int, goDetails: (id: Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Books Details$id",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goDetails(Random.nextInt(0, 10000))
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Composable
fun ProfileScreen(goDetails: (id: Int) -> Unit, goBack: () -> Unit) {
    BackHandler {
        goBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Profile View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goDetails(Random.nextInt(200, 300))
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}


@Composable
fun ProfileScreenDetails(id: Int, goDetails: (id: Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Profile Details$id",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    goDetails(Random.nextInt(0, 10000))
                },
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}
