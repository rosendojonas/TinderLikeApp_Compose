package com.jonasrosendo.tinder.ui.features.login

sealed class LoginViewEvent {
    data class Toast(val message: String) : LoginViewEvent()
}