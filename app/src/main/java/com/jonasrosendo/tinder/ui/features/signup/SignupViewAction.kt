package com.jonasrosendo.tinder.ui.features.signup

sealed class SignupViewAction {
    data class Signup(val username: String, val email: String, val password: String) :
        SignupViewAction()
}