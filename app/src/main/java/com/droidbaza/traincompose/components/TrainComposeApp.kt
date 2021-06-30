package com.droidbaza.traincompose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.droidbaza.traincompose.components.*
import com.droidbaza.traincompose.components.Destiny.*
import com.droidbaza.traincompose.components.map.MapScreen
import com.droidbaza.traincompose.components.map.rememberMapViewWithLifecycle
import com.droidbaza.traincompose.components.stories.MyInstagramScreen
import com.droidbaza.traincompose.components.stories.StoriesScreen
import com.droidbaza.traincompose.components.viewmodels.MoviesViewModel
import com.droidbaza.traincompose.ui.theme.TrainComposeTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@Composable
fun MainScreen(finish: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    TrainComposeTheme(darkTheme = true) {
        Scaffold(
            bottomBar = {
                if (currentRoute != null) {
                    if (currentRoute == "intro" || currentRoute == "splash") {
                        return@Scaffold
                    } else {
                        BottomNavigationBar(navController)
                    }
                }
            }
        ) {
            AppNavigation(navController = navController, finish)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val tabs = listOf(
       Home,
       Music,
        Movies,
        Books,
        Profile
    )
    BottomNavigation(
        backgroundColor = Color.Black,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Home.route

        tabs.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = false,
                selected = currentRoute.contains(item.route),
                onClick = {
                    if (!currentRoute.contains(item.route)) {
                        navController.navigate(item.route) {
                            restoreState = true
                            launchSingleTop = true
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route = route) {
                                    saveState = true
                                }
                            }
                        }
                    } else {
                        navController.navigateUp()
                        navController.popBackStack(item.route, false)
                    }
                }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun AppNavigation(navController: NavHostController, finish: () -> Unit) {
    val startDestination = Home.route
    val router = remember(navController) { Router(navController, startDestination) }
    val mapView = rememberMapViewWithLifecycle()
    val moviesViewModel = hiltViewModel<MoviesViewModel>()

    NavHost(navController, startDestination = startDestination) {
        composable(Intro.route) {
            IntroScreen(
                goBack = router.goHome
            )
        }
        composable(Splash.route) {
            SplashScreen(
                goBack = router.goIntro
            )
        }
        composable(Stories.route) {
            StoriesScreen(
               finish = router.goBack
            )
        }

        composable(Home.route) {
            HomeScreen(
                goDetails = router.goHomeDetails,
                goBack = finish,
                goStories = router.goStories
            )
        }
        composable(Music.route) {
            MusicScreen(
                goBack = router.goBack,
                goDetails = router.goMusicDetails
            )
        }
        composable(Movies.route) {
            MoviesScreen(
                moviesViewModel,
                goBack = router.goBack,
                goDetails = router.goMoviesDetails
            )
        }
        composable(Books.route) {
            MapScreen(mapView = mapView)
        }
        composable(Profile.route) {
            ProfileScreen(
                goBack = router.goBack,
                goDetails = router.goProfileDetails
            )
        }
        composable(HomeDetails.route) {
            HomeScreenDetails(
                id = router.getArgs(HomeDetails.tag) ?: 0,
                goDetails = router.goHomeDetails
            )
        }
        composable(ProfileDetails.route) {
            ProfileScreenDetails(
                id = router.getArgs(ProfileDetails.tag) ?: 0,
                goDetails = router.goProfileDetails
            )

        }
        composable(MusicDetails.route) {
            MusicScreenDetails(
                id = router.getArgs(MusicDetails.tag) ?: 0,
                goDetails = router.goMusicDetails
            )
        }
        composable(BooksDetails.route) {
            BooksScreenDetails(
                id = router.getArgs(BooksDetails.tag) ?: 0,
                goDetails = router.goBooksDetails
            )
        }
        composable(MoviesDetails.route) {
            MoviesScreenDetails(
                movie = router.getArgs(MoviesDetails.tag),
                goDetails = router.goMoviesDetails
            )
        }
    }
}


@ExperimentalAnimationApi
@Composable
fun ExampleAnimation(content: @Composable () -> Unit) {
    val state = remember { MutableTransitionState(initialState = false) }
        .apply { targetState = true }
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(0.3f),
        exit = fadeOut(0.3f)
    ) {
        content()
    }
}