package com.droidbaza.traincompose.components

import androidx.annotation.DrawableRes
import com.droidbaza.traincompose.R

sealed class Screen(val route: String, val title: String, @DrawableRes val icon: Int) {
    object Home : Screen(route = "home", title = "Home", icon = R.drawable.ic_home)
    object Search : Screen(route = "search", title = "Search", icon = R.drawable.ic_search)
    object Favourites : Screen(route = "favourites", title = "Favourites", icon = R.drawable.ic_star)
    object Profile : Screen(route = "profile", title = "Profile", icon = R.drawable.ic_profile)
}

