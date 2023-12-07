package com.jonasrosendo.tinder.ui.features.login

sealed class LoginViewAction {
    data class Login(val email: String, val password: String) : LoginViewAction()
}