package com.droidbaza.traincompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import com.droidbaza.traincompose.components.MainScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  //  private val ready: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var backPressed = 0L

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*lifecycleScope.launchWhenCreated {
            delay(2000)
            ready.value = true
        }*/
        setContent {
           /* val result = ready.collectAsState()
            if (result.value) {

            }*/
            MainScreen(finish = finish)
        }
    }

    private val finish: () -> Unit = {
        if (backPressed + 3000 > System.currentTimeMillis()) {
            finishAndRemoveTask()
        } else {
            Toast.makeText(
                this,
                "Нажмите еще раз для выхода",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressed = System.currentTimeMillis()
    }
}




