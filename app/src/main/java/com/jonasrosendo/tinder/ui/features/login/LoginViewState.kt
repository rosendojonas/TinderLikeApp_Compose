package com.jonasrosendo.tinder.ui.features.login

sealed class LoginViewState {
    object Default : LoginViewState()
    object Loading : LoginViewState()
    object Success : LoginViewState()
}