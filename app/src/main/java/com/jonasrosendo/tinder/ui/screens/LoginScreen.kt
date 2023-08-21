package com.jonasrosendo.tinder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jonasrosendo.tinder.R
import com.jonasrosendo.tinder.TinderViewModel
import com.jonasrosendo.tinder.navigation.RouteNavigation
import com.jonasrosendo.tinder.navigation.navigateToAndClearStack
import com.jonasrosendo.tinder.utils.AuthenticationButton
import com.jonasrosendo.tinder.utils.CommonProgressSpinner

@Composable
fun LoginScreen(
    viewModel: TinderViewModel,
    navController: NavController
) {

    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }

    val emailOnValueChanged = { newValue: TextFieldValue ->
        emailState.value = newValue
    }

    val passwordOnValueChanged = { newValue: TextFieldValue ->
        passwordState.value = newValue
    }

    val isLoading = remember { mutableStateOf(false) }

    val isLogged = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.fire
                ),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )

            Text(
                text = "Login",
                modifier = Modifier
                    .padding(8.dp),
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = emailOnValueChanged,
                modifier = Modifier
                    .padding(8.dp),
                label = { Text(text = "E-mail") }
            )

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = passwordOnValueChanged,
                modifier = Modifier
                    .padding(8.dp),
                label = { Text(text = "Password") }
            )

            AuthenticationButton(
                label = "LOGIN"
            ) {
                viewModel.onLogin(
                    emailState.value.text.trim(),
                    passwordState.value.text.trim()
                )
            }

            Text(
                text = "Don't have an account? Go to signup ->",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navController.navigateToAndClearStack(RouteNavigation.Signup.route)
                    }
            )
        }

        LaunchedEffect(key1 = Unit) {
            viewModel.loading.collect { loading ->
                isLoading.value = loading
            }
        }

        LaunchedEffect(key1 = Unit) {
            viewModel.signedIn.collect {
                isLogged.value = it
            }
        }

        if (isLoading.value) {
            CommonProgressSpinner()
        }

        if (isLogged.value) {
            navController.navigateToAndClearStack(RouteNavigation.Profile.route)
        }
    }
}