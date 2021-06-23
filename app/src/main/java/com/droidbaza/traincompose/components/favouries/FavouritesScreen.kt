package com.droidbaza.traincompose.components.favouries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.droidbaza.traincompose.components.Screen

@Composable
fun FavouritesScreen(onClick:()->Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Text(
            text = Screen.Favourites.title,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 20.sp,
        )
        Button(
            onClick = onClick,
            Modifier.background(Color.Blue)
            // backgroundColor = orangish,
            // shape = shapes.medium,
            //  contentColor = Color.White,
            //  modifier = modifier,
            // contentPadding = InnerPadding(16.dp)
        ) {
            Text(text = "Log In")
        }
    }
}