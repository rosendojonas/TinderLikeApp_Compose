package com.jonasrosendo.tinder

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jonasrosendo.tinder.data.COLLECTION_USER
import com.jonasrosendo.tinder.data.Event
import com.jonasrosendo.tinder.data.FirebaseHelper.logout
import com.jonasrosendo.tinder.model.FirebaseUserData
import com.jonasrosendo.tinder.ui.features.profile.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class TinderViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val fireStore: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _popupNotification = MutableStateFlow<Event<String>?>(null)
    val popupNotification: StateFlow<Event<String>?> = _popupNotification

    private val _signedIn = MutableStateFlow(false)
    val signedIn: StateFlow<Boolean> = _signedIn

    private val _userData = MutableStateFlow<FirebaseUserData?>(null)
    val userData: StateFlow<FirebaseUserData?> = _userData

    init {
        val currentUser = auth.currentUser
        _signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            retrieveUserData(uid)
        }
    }

    private fun retrieveUserData(uid: String) {
        showLoading()
        getFirebaseUserById(uid)
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

    private fun getFirebaseUserById(uid: String): DocumentReference {
        return fireStore.collection(COLLECTION_USER)
            .document(uid)
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