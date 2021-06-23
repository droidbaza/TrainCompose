package com.droidbaza.traincompose.components.main

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.droidbaza.traincompose.components.BottomBar
import com.droidbaza.traincompose.components.Screen
import com.droidbaza.traincompose.components.favouries.FavouritesScreen
import com.droidbaza.traincompose.components.home.HomeScreen
import com.droidbaza.traincompose.components.home.HomeViewModel
import com.droidbaza.traincompose.components.map.MapViewModel
import com.droidbaza.traincompose.components.map.rememberMapViewWithLifecycle
import com.droidbaza.traincompose.components.onboardings.LoginScreen
import com.droidbaza.traincompose.components.onboardings.OnBoardingScreen
import com.droidbaza.traincompose.components.profile.ProfileScreen
import com.droidbaza.traincompose.components.profile.ProfileViewModel
import com.droidbaza.traincompose.ui.theme.TrainComposeTheme
import com.google.android.libraries.maps.MapView

@ExperimentalAnimationApi
@Composable
fun MainScreen(viewModel: MainViewModel, onBackPressed: () -> Unit) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val mapViewModel: MapViewModel = hiltViewModel()
    val mapView: MapView = rememberMapViewWithLifecycle()

    TrainComposeTheme {
        val skipIntro = viewModel.newsState.collectAsState()
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val route = navBackStackEntry?.destination?.route
        var start = "intro"
        Log.d("ROTTTT", "$route")
        Scaffold(
            bottomBar = {
                if (skipIntro.value) {
                    if (route == "intro" || route == "login") return@Scaffold
                    BottomBar(
                        navController
                    )
                }
            }
        )
        {
            NavHost(navController, startDestination = start) {
                if (!skipIntro.value) {
                    composable("intro") {

                        OnBoardingScreen {
                            viewModel.login()
                            navController.navigate("main") {
                                popUpTo(0)
                                start = "main"
                                launchSingleTop = true
                            }
                        }


                    }
                }

                navigation(route = "main", startDestination = Screen.Home.route) {

                    mainCore(
                        onBackPressed,
                        homeViewModel,
                        profileViewModel,
                        skipIntro,
                        navController
                    )
                }
                composable("login") {

                    LoginScreen {
                        navController.navigate("main")
                    }


                }
            }
        }

    }
}

fun NavGraphBuilder.mainCore(
    onBackPressed: () -> Unit,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    onboardingComplete: State<Boolean>, // https://issuetracker.google.com/174783110
    navController: NavHostController
) {
    composable(Screen.Home.route) { from ->
        // Show onboarding instead if not shown yet.
        LaunchedEffect(onboardingComplete) {
            if (!onboardingComplete.value) {
                // navController.navigate("intro")
            }
        }
        if (onboardingComplete.value) { // Avoid glitch when showing onboarding
            HomeScreen(homeViewModel)
        }
    }
    composable(Screen.Search.route) {
        //  MapScreen(mapViewModel, mapView)
    }
    composable(Screen.Favourites.route) {
        FavouritesScreen {
            navController.navigate("login")
        }
    }

    composable(Screen.Profile.route) {
        ProfileScreen(profileViewModel)
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