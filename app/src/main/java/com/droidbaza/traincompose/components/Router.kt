package com.droidbaza.traincompose.components

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.droidbaza.traincompose.R
import com.droidbaza.traincompose.components.Routes.ARGS_HOME_DETAILS
import com.droidbaza.traincompose.components.Routes.ARGS_MOVIE_DETAILS
import com.droidbaza.traincompose.components.Routes.ARGS_MUSIC_DETAILS
import com.droidbaza.traincompose.components.Routes.ARGS_PROFILE_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_BOOKS
import com.droidbaza.traincompose.components.Routes.ROUTE_HOME
import com.droidbaza.traincompose.components.Routes.ROUTE_HOME_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_MOVIES
import com.droidbaza.traincompose.components.Routes.ROUTE_MOVIE_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_MUSIC
import com.droidbaza.traincompose.components.Routes.ROUTE_MUSIC_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_PROFILE
import com.droidbaza.traincompose.components.Routes.ROUTE_PROFILE_DETAILS

object Routes {

    const val ROUTE_HOME = "home"
    const val ARGS_HOME = "args_home"

    const val ROUTE_HOME_DETAILS = "main_details"
    const val ARGS_HOME_DETAILS = "args_home_details"

    const val ROUTE_MOVIES = "movies"
    const val ARGS_MOVIES = "args_movies"

    const val ROUTE_MOVIE_DETAILS = "movies_details"
    const val ARGS_MOVIE_DETAILS = "args_movies_details"

    const val ROUTE_BOOKS = "books"
    const val ARGS_BOOKS = "args_books"

    const val ROUTE_BOOKS_DETAILS = "books_details"
    const val ARGS_BOOKS_DETAILS = "args_books_details"

    const val ROUTE_PROFILE = "profile"
    const val ARGS_PROFILE = "args_profile"

    const val ROUTE_MUSIC = "music"
    const val ARGS_MUSIC = "args_music"

    const val ROUTE_MUSIC_DETAILS = "music_details"
    const val ARGS_MUSIC_DETAILS = "args_music_details"

    const val ROUTE_PROFILE_DETAILS = "profile_details"
    const val ARGS_PROFILE_DETAILS = "args_profile_details"

}

sealed class Screen(val route: String, val title: String, @DrawableRes val icon: Int) {
    object Home : Screen(route = ROUTE_HOME, title = "Home", icon = R.drawable.ic_home)
    object Music : Screen(route = ROUTE_MUSIC, title = "Search", icon = R.drawable.ic_search)
    object Movies : Screen(route = ROUTE_MOVIES, title = "Favourites", icon = R.drawable.ic_star)
    object Books : Screen(route = ROUTE_BOOKS, title = "Favourites", icon = R.drawable.ic_star)
    object Profile : Screen(route = ROUTE_PROFILE, title = "Profile", icon = R.drawable.ic_profile)
}


class Router(val navHostController: NavHostController, startDestination: String = ROUTE_HOME) {

    val goMoviesDetails: (arg: Any?) -> Unit = {
        putArgs(it, ARGS_MOVIE_DETAILS)
        navigate(ROUTE_MOVIE_DETAILS)
    }

    val goHomeDetails: (arg: Any?) -> Unit = {
        putArgs(it, ARGS_HOME_DETAILS)
        navigate(ROUTE_HOME_DETAILS)
    }

    val goBooksDetails: (arg: Any?) -> Unit = {
        putArgs(it, ARGS_MOVIE_DETAILS)
        navigate(ROUTE_MOVIE_DETAILS)
    }

    val goProfileDetails: (arg: Any?) -> Unit = {
        putArgs(it, ARGS_PROFILE_DETAILS)
        navigate(ROUTE_PROFILE_DETAILS)
    }

    val goMusicDetails: (arg: Any?) -> Unit = {
        putArgs(it, ARGS_MUSIC_DETAILS)
        navigate(ROUTE_MUSIC_DETAILS)
    }

    val goBack: () -> Unit = {
        navHostController.apply {
            if (isLifecycleResumed()) {
                navigateUp()
                navigate(startDestination) {
                    restoreState = false
                    launchSingleTop = true
                }
            }
        }
    }

    private fun navigate(route: String) {
        navHostController.apply {
            if (isLifecycleResumed()) {
                navigate(route) {
                    restoreState = true
                }
            }
        }
    }

    private fun putArgs(it: Any?, key: String) {
        if (it != null) {
            navHostController.putArgs(Pair(key, it))
        }
    }

    inline fun <reified T : Any> getArgs(tag: String): T? {
        return try {
            navHostController.previousBackStackEntry?.arguments?.get(tag) as T?
        } catch (ex: Exception) {
            null
        }
    }

    fun <T : Any> NavHostController.putArgs(args: Pair<String, T>) {
        val key = args.first
        val value = args.second
        currentBackStackEntry?.arguments?.apply {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Short -> putShort(key, value)
                is Long -> putLong(key, value)
                is Byte -> putByte(key, value)
                is ByteArray -> putByteArray(key, value)
                is Char -> putChar(key, value)
                is CharArray -> putCharArray(key, value)
                is CharSequence -> putCharSequence(key, value)
                is Float -> putFloat(key, value)
                is Bundle -> putBundle(key, value)
                // is Serializable -> putSerializable(key, value)
                is Parcelable -> putParcelable(key, value)
                else -> throw IllegalStateException("Type ${value.javaClass.canonicalName} is not supported now")
            }
        }
    }

    private fun NavHostController.isLifecycleResumed() =
        this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

}