package com.jonasrosendo.tinder.ui.features.profile

import android.net.Uri

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

    data class UploadProfileImage(val uri: Uri) : ProfileViewAction()
}