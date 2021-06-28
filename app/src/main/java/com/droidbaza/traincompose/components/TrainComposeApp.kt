package com.droidbaza.traincompose.components.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.droidbaza.traincompose.components.*


@Composable
fun MainScreen(finish: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Music,
        Screen.Movies,
        Screen.Books,
        Screen.Profile
    )
    BottomNavigation(
        backgroundColor = Color.Black,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Routes.ROUTE_HOME

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
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
                        navController.popBackStack(item.route, false)
                    }
                }
            )
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, finish: () -> Unit) {
    val startDestination = Routes.ROUTE_HOME
    val router = remember(navController) { Router(navController, startDestination) }

    NavHost(navController, startDestination = startDestination) {
        composable("intro") { IntroScreen(goBack = { navController.popBackStack() }) }
        composable("splash") { SplashScreen(goBack = { navController.navigate("core") }) }
        composable(Routes.ROUTE_HOME) {
            HomeScreen(
                goDetails = router.goHomeDetails,
                goBack = finish
            )
        }
        composable(Routes.ROUTE_MUSIC) {
            MusicScreen(
                goBack = router.goBack,
                goDetails = router.goMusicDetails
            )
        }
        composable(Routes.ROUTE_MOVIES) {
            MoviesScreen(
                goBack = router.goBack,
                goDetails = router.goMoviesDetails
            )
        }
        composable(Routes.ROUTE_BOOKS) {
            BooksScreen(
                goBack = router.goBack,
                goDetails = router.goBooksDetails
            )
        }
        composable(Routes.ROUTE_PROFILE) {
            ProfileScreen(
                goBack = router.goBack,
                goDetails = router.goProfileDetails
            )
        }
        composable(Routes.ROUTE_HOME_DETAILS) {
            HomeScreenDetails(
                id = router.getArgs(Routes.ARGS_HOME_DETAILS) ?: 0,
                goDetails = router.goHomeDetails
            )
        }
        composable(Routes.ROUTE_PROFILE_DETAILS) {
            ProfileScreenDetails(
                id = router.getArgs(Routes.ARGS_PROFILE_DETAILS) ?: 0,
                goDetails = router.goProfileDetails
            )

        }
        composable(Routes.ROUTE_MUSIC_DETAILS) {
            MusicScreenDetails(
                id = router.getArgs(Routes.ARGS_MUSIC_DETAILS) ?: 0,
                goDetails = router.goMusicDetails
            )
        }
        composable(Routes.ROUTE_BOOKS_DETAILS) {
            BooksScreenDetails(
                id = router.getArgs(Routes.ARGS_BOOKS_DETAILS) ?: 0,
                goDetails = router.goBooksDetails
            )
        }
        composable(Routes.ROUTE_MOVIE_DETAILS) {
            MoviesScreenDetails(
                movie = router.getArgs(Routes.ARGS_MOVIE_DETAILS),
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