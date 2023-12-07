package com.jonasrosendo.tinder.ui.features.profile

sealed class ProfileViewEvent {
    object Saving: ProfileViewEvent()
    object Saved: ProfileViewEvent()
}
