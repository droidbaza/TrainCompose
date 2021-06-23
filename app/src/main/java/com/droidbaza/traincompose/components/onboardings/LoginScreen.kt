package com.droidbaza.traincompose.components.onboardings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.droidbaza.traincompose.R
import com.droidbaza.traincompose.ui.theme.Purple200

@Composable
fun LoginScreen(onClick: () -> Unit) {
    Box {
        BgCard()
        MainCard(onClick)
    }
}

@Composable
fun BgCard() {
    val signupText = buildAnnotatedString {
        append("Don't have an account? ")
        withStyle(SpanStyle(color = Color.Red)) {
            append("Sign up here!")
        }
    }
    Surface(color = Purple200, modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-30).dp)
        ) {
            Row() {
                Image(
                    painterResource(id = R.drawable.ic_fb),
                    alignment = Alignment.Center,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Image(
                    painterResource(R.drawable.ic_google), alignment = Alignment.Center,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Image(
                    painterResource(R.drawable.ic_twitter), alignment = Alignment.Center,
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Text(text = signupText, color = Color.White)
        }
    }
}


@Composable
fun MainCard(onClick:()->Unit) {
    val emailState = remember { mutableStateOf(TextFieldValue("mtechviral@gmail.com")) }
    val passState = remember { mutableStateOf(TextFieldValue("")) }
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(60.dp).copy(topStart = ZeroCornerSize, topEnd = ZeroCornerSize)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            Image(
                painterResource(id = R.drawable.ic_vaccum), alignment = Alignment.Center,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.padding(16.dp))
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text(text = "Email address") },
                // leadingIcon = { Icon(Icons.Filled.Email) },
                modifier = modifier
            )

            Spacer(modifier = Modifier.padding(6.dp))

            OutlinedTextField(
                value = passState.value,
                onValueChange = { passState.value = it },
                label = { Text(text = "Password") },
                // leadingIcon = { Icon(Icons.Filled.Lock) },
                modifier = modifier,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            /* ProvideEmphasis(emphasis = EmphasisAmbient.current.disabled) {
                 Text(text = "Forgot password?", textAlign = TextAlign.End, modifier = modifier)
             }*/
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
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
}