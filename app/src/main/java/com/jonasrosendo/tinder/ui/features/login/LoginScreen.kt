package com.jonasrosendo.tinder.ui.features.login

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
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jonasrosendo.tinder.R
import com.jonasrosendo.tinder.navigation.RouteNavigation
import com.jonasrosendo.tinder.navigation.navigateToAndClearStack
import com.jonasrosendo.tinder.shared_ui.ShowToast
import com.jonasrosendo.tinder.utils.AuthenticationButton
import com.jonasrosendo.tinder.utils.CommonProgressSpinner

@Composable
fun LoginScreen(
    navController: NavController
) {
    val viewModel = hiltViewModel<LoginViewModel>()

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val emailOnValueChanged = { newValue: String ->
        emailState.value = newValue
    }

    val passwordOnValueChanged = { newValue: String ->
        passwordState.value = newValue
    }

    val isLoading = remember { mutableStateOf(false) }

    val isLogged = remember { mutableStateOf(false) }

    Content(
        viewModel = viewModel,
        navController = navController,
        isLogged = isLogged.value,
        isLoading = isLoading.value,
        email = emailState.value,
        password = passwordState.value,
        onEmailChange = emailOnValueChanged,
        onPasswordChange = passwordOnValueChanged
    )

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

    if (isLogged.value) {
        navController.navigateToAndClearStack(RouteNavigation.Swipe.route)
    }
}

@Composable
private fun Content(
    viewModel: LoginViewModel,
    navController: NavController,
    isLoading: Boolean,
    isLogged: Boolean,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    val state: LoginViewState? = viewModel.viewState.collectAsState(initial = null).value

    return when (state) {
        is LoginViewState.Default -> DefaultState(
            viewModel = viewModel,
            navController = navController,
            isLoading = isLoading,
            isLogged = isLogged,
            email = email,
            password = password,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange
        )

        else -> {}
    }
}

@Composable
fun DefaultState(
    viewModel: LoginViewModel,
    navController: NavController,
    isLoading: Boolean,
    isLogged: Boolean,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
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
                text = stringResource(R.string.login),
                modifier = Modifier
                    .padding(8.dp),
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier
                    .padding(8.dp),
                label = { Text(text = stringResource(R.string.e_mail)) }
            )

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier
                    .padding(8.dp),
                label = { Text(text = stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation()
            )

            AuthenticationButton(
                label = stringResource(R.string.login).uppercase()
            ) {
                viewModel.applyAction(
                    LoginViewAction.Login(
                        email.trim(),
                        password.trim()
                    )
                )
            }

            Text(
                text = stringResource(R.string.don_t_have_an_account_go_to_signup),
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navController.navigateToAndClearStack(RouteNavigation.Signup.route)
                    }
            )
        }
    }

    if (isLoading) {
        CommonProgressSpinner()
    }

    if (isLogged) {
        navController.navigateToAndClearStack(RouteNavigation.Swipe.route)
    }
}

@Composable
private fun EventsProcessor(
    viewModel: LoginViewModel
) {
    val event: LoginViewEvent? = viewModel.viewEvents.collectAsState(initial = null).value

    return when (event) {
        is LoginViewEvent.Toast -> ShowToast(
            message = event.message
        )

        else -> {}
    }
}

