package com.jonasrosendo.tinder.ui.features.login

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jonasrosendo.tinder.core.BaseViewModel
import com.jonasrosendo.tinder.data.Event
import com.jonasrosendo.tinder.data.FirebaseHelper.getFirebaseUserById
import com.jonasrosendo.tinder.data.FirebaseHelper.isAuthenticated
import com.jonasrosendo.tinder.model.FirebaseUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class LoginViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val fireStore: FirebaseFirestore,
    val storage: FirebaseStorage
) : BaseViewModel<LoginViewState, LoginViewEvent, LoginViewAction>() {

    override val initialViewState: LoginViewState
        get() = LoginViewState.Default

    override fun processAction(action: LoginViewAction) {
        when (action) {
            is LoginViewAction.Login -> login(action.email, action.password)
        }
    }

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _popupNotification = MutableStateFlow<Event<String>?>(null)
    val popupNotification: StateFlow<Event<String>?> = _popupNotification

    private val _signedIn = MutableStateFlow(false)
    val signedIn: StateFlow<Boolean> = _signedIn

    private val _userData = MutableStateFlow<FirebaseUserData?>(null)
    val userData: StateFlow<FirebaseUserData?> = _userData

    fun login(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields!")
            return
        }

        showLoading()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signedIn.value = auth.isAuthenticated()
                    hideLoading()
                    auth.currentUser?.uid?.let {
                        retrieveUserData(it)
                    }
                    val state = LoginViewState.Success
                    updateViewState(state)
                } else {
                    handleException(task.exception, customMessage = "Login failed.")
                }
            }
            .addOnFailureListener {
                handleException(it, customMessage = "Login failed.")
            }
    }

    private fun retrieveUserData(uid: String) {
        showLoading()
        fireStore.getFirebaseUserById(uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, "Cannot retrieve user data.")
                }

                if (value != null) {
                    println(value.data)
                    val user = value.toObject<FirebaseUserData>()
                    _userData.value = user
                    hideLoading()
                }
            }
    }

    private fun showLoading() {
        _loading.value = true
    }

    private fun hideLoading() {
        _loading.value = false
    }


    private fun handleException(
        exception: java.lang.Exception? = null,
        customMessage: String = ""
    ) {
        Log.e("TinderClone", "Tinder exception", exception)
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMessage else "$customMessage: $errorMessage"
        _popupNotification.value = Event(message)
        hideLoading()
    }
}