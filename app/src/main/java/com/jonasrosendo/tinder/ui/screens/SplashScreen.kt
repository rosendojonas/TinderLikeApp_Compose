package com.jonasrosendo.tinder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jonasrosendo.tinder.R
import com.jonasrosendo.tinder.TinderViewModel
import com.jonasrosendo.tinder.navigation.RouteNavigation
import com.jonasrosendo.tinder.navigation.navigateToAndClearStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Composable
fun SplashScreen(
    viewModel: TinderViewModel,
    navController: NavController
) {

    val isLogged = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.fire),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = "Tinder Clone",
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        LaunchedEffect(key1 = Unit) {
            runBlocking {
                delay(1000L)
            }

            viewModel.signedIn.collect {
                isLogged.value = it
            }
        }

        if (isLogged.value) {
            navController.navigateToAndClearStack(RouteNavigation.Swipe.route)
        } else {
            navController.navigateToAndClearStack(RouteNavigation.Login.route)
        }
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.fire),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = "Tinder Clone",
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            CircularProgressIndicator(color = Color.Black)
        }
    }
}