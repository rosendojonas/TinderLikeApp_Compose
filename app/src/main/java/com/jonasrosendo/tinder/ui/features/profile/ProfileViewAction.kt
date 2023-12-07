package com.jonasrosendo.tinder.ui.features.profile

sealed class ProfileViewAction {
    object SignOut : ProfileViewAction()
    object GetUserData : ProfileViewAction()
    data class SaveProfile(
        val name: String,
        val username: String,
        val bio: String,
        val gender: Gender,
        val genderPreference: Gender
    ) : ProfileViewAction()
}