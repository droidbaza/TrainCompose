package com.droidbaza.traincompose.components

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.droidbaza.traincompose.R
import com.droidbaza.traincompose.components.Routes.ROUTE_BOOKS
import com.droidbaza.traincompose.components.Routes.ROUTE_BOOKS_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_HOME
import com.droidbaza.traincompose.components.Routes.ROUTE_HOME_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_INTRO
import com.droidbaza.traincompose.components.Routes.ROUTE_MOVIES
import com.droidbaza.traincompose.components.Routes.ROUTE_MOVIE_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_MUSIC
import com.droidbaza.traincompose.components.Routes.ROUTE_MUSIC_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_PROFILE
import com.droidbaza.traincompose.components.Routes.ROUTE_PROFILE_DETAILS
import com.droidbaza.traincompose.components.Routes.ROUTE_SPLASH
import com.droidbaza.traincompose.components.Routes.ROUTE_STORIES

object Routes {
    const val ROUTE_HOME = "home"
    const val ROUTE_HOME_DETAILS = "home details"
    const val ROUTE_MOVIES = "movies"
    const val ROUTE_MOVIE_DETAILS = "movies details"
    const val ROUTE_BOOKS = "books"
    const val ROUTE_BOOKS_DETAILS = "books details"
    const val ROUTE_PROFILE = "profile"
    const val ROUTE_MUSIC = "music"
    const val ROUTE_MUSIC_DETAILS = "music details"
    const val ROUTE_PROFILE_DETAILS = "profile details"
    const val ROUTE_INTRO = "intro"
    const val ROUTE_SPLASH = "splash"
    const val ROUTE_STORIES = "splash"
}

sealed class Destiny(val route: String, var tag: String = route,val title: String="", @DrawableRes val icon: Int = 0) {
    //bottom bar destinies
    object Home : Destiny(route = ROUTE_HOME, title = "Home", icon = R.drawable.ic_home)
    object Music : Destiny(route = ROUTE_MUSIC, title = "Music", icon = R.drawable.ic_search)
    object Movies : Destiny(route = ROUTE_MOVIES, title = "Movies", icon = R.drawable.ic_star)
    object Books : Destiny(route = ROUTE_BOOKS, title = "Books", icon = R.drawable.ic_star)
    object Profile : Destiny(route = ROUTE_PROFILE, title = "Profile", icon = R.drawable.ic_profile)

    // details screen destinies
    object HomeDetails : Destiny(route = ROUTE_HOME_DETAILS)
    object MusicDetails : Destiny(route = ROUTE_MUSIC_DETAILS)
    object MoviesDetails : Destiny(route = ROUTE_MOVIE_DETAILS)
    object BooksDetails : Destiny(route = ROUTE_BOOKS_DETAILS)
    object ProfileDetails : Destiny(route = ROUTE_PROFILE_DETAILS)
    object Intro : Destiny(route = ROUTE_INTRO)
    object Splash : Destiny(route = ROUTE_SPLASH)
    object Stories : Destiny(route = ROUTE_STORIES)
}
interface Router{
    val goMoviesDetails: (arg: Any?) -> Unit
    val goHomeDetails: (arg: Any?) -> Unit
    val goBooksDetails: (arg: Any?) -> Unit
    val goStories: () -> Unit
    val goProfileDetails: (arg: Any?) -> Unit
    val goMusicDetails: (arg: Any?) -> Unit
    val goSplash:()->Unit
    val goHome:()->Unit
    val goBack: () -> Unit
    val goIntro: () -> Unit
    fun < T : Any> getArgs(tag: String): T?
}
class RouterImpl(val navHostController: NavHostController, startDestination: String = ROUTE_HOME):Router {

    override val goMoviesDetails: (arg: Any?) -> Unit = {
        checkArgsAndNavigate(it, Destiny.MoviesDetails)
    }

    override val goHomeDetails: (arg: Any?) -> Unit = {
        checkArgsAndNavigate(it, Destiny.HomeDetails)
    }

    override val goBooksDetails: (arg: Any?) -> Unit = {
        checkArgsAndNavigate(it, Destiny.BooksDetails)
    }

    override val goStories: () -> Unit = {
        navigate(Destiny.Stories.route)
    }

    override val goProfileDetails: (arg: Any?) -> Unit = {
        checkArgsAndNavigate(it, Destiny.ProfileDetails)
    }

    override val goMusicDetails: (arg: Any?) -> Unit = {
        checkArgsAndNavigate(it, Destiny.MusicDetails)
    }

    override val goIntro: () -> Unit = {
        navigate(Destiny.Intro.route, true)
    }

    override val goSplash:()->Unit={
        navigate(Destiny.Splash.route,true)
    }
    override val goHome:()->Unit={
        navigate(Destiny.Home.route,removeFromHistory = true,singleTop = true)
    }

    override val goBack: () -> Unit = {
        navHostController.apply {
            if (isLifecycleResumed()) {
                navigateUp()
                navigate(startDestination){
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    private fun navigate(route: String,removeFromHistory:Boolean = false,singleTop:Boolean = false) {
        navHostController.apply {
            if (isLifecycleResumed()) {
                navigate(route) {
                    if(removeFromHistory){
                        if(singleTop){
                            popUpTo(Destiny.Home.route)
                        }else{
                            popUpTo(0){
                                saveState = false
                            }
                        }

                    }else{
                        restoreState = true
                    }
                    launchSingleTop = singleTop
                }
            }
        }
    }

    private fun checkArgsAndNavigate(it: Any?, destiny: Destiny) {
        if (it != null) {
            navHostController.putArgs(Pair(destiny.tag, it))
        }
        navigate(destiny.route)
    }

    override fun < T : Any> getArgs(tag: String): T? {
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