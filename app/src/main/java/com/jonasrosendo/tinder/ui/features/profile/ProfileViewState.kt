package com.jonasrosendo.tinder.ui.features.profile

import com.jonasrosendo.tinder.model.FirebaseUserData

sealed class ProfileViewState {
    object Default : ProfileViewState()
    data class DataRetrieved(val userData: FirebaseUserData?) : ProfileViewState()
    object Loading: ProfileViewState()
}
