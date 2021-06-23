package com.droidbaza.traincompose.components.onboardings


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.droidbaza.traincompose.R
import com.droidbaza.traincompose.ui.theme.Purple500

@Composable
fun OnBoardingScreen(onClickSkip: () -> Unit) {
    Box {
        BgCard2(onClickSkip)
        MainCard2()
    }
}

@Composable
fun BgCard2(onClickSkip: () -> Unit) {
    val signupText = with(AnnotatedString.Builder()) {
        append("Don't have an account? ")
        withStyle(SpanStyle(color = Color.Red)) {
            append("Sign up here!")
        }
    }
    Surface(color = Purple500, modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-30).dp)
        ) {
            Row {
                Box(
                    Modifier
                        .background(Color.Gray, shape = CircleShape)
                        .size(10.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Box(
                    Modifier
                        .background(Color.White, shape = CircleShape)
                        .size(10.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Box(
                    Modifier
                        .background(Color.Gray, shape = CircleShape)
                        .size(10.dp)
                )

            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextButton(onClick = onClickSkip) {
                    Text(text = "Skip", color = Color.White)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {}) {
                        Text(text = "Next", color = Color.White)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Image(
                            painterResource(id = R.drawable.ic_next),
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MainCard2() {
    Surface(
        color = Color.White, modifier = Modifier
            .height(600.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(60.dp).copy(topStart = ZeroCornerSize, topEnd = ZeroCornerSize)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painterResource(id = R.drawable.ic_cleaning), "")
            Spacer(modifier = Modifier.padding(32.dp))
            /* AmbientContentAlpha(emphasis = EmphasisAmbient.current.high) {
                 Text(text = "Cleaning on Demand",style = MaterialTheme.typography.h6)
             }*/

            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            /* ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                 Text(text = "Book an appointment in less than 60 seconds and get on the schedule as early as tomorrow.",
                         style = MaterialTheme.typography.caption, textAlign = TextAlign.Center)


             }*/

        }
    }
}