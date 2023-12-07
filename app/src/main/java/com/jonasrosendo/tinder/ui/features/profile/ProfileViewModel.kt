package com.jonasrosendo.tinder.ui.features.profile

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jonasrosendo.tinder.core.BaseViewModel
import com.jonasrosendo.tinder.data.COLLECTION_USER
import com.jonasrosendo.tinder.data.Event
import com.jonasrosendo.tinder.data.FirebaseHelper.getFirebaseUserById
import com.jonasrosendo.tinder.data.FirebaseHelper.isAuthenticated
import com.jonasrosendo.tinder.data.FirebaseHelper.logout
import com.jonasrosendo.tinder.model.FirebaseUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    val storage: FirebaseStorage
) : BaseViewModel<ProfileViewState, ProfileViewEvent, ProfileViewAction>() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _popupNotification = MutableStateFlow<Event<String>?>(null)
    val popupNotification: StateFlow<Event<String>?> = _popupNotification

    private val _signedIn = MutableStateFlow(false)
    val signedIn: StateFlow<Boolean> = _signedIn

    private val _userData = MutableStateFlow<FirebaseUserData?>(null)
    val userData: StateFlow<FirebaseUserData?> = _userData

    override val initialViewState: ProfileViewState
        get() = ProfileViewState.Default

    override fun processAction(action: ProfileViewAction) {
        when (action) {
            is ProfileViewAction.SignOut -> logout()
            ProfileViewAction.GetUserData -> getUserData()
            is ProfileViewAction.SaveProfile -> saveProfile(
                action.name, action.username, action.bio, action.gender, action.genderPreference
            )
        }
    }

    private fun saveProfile(
        name: String,
        username: String,
        bio: String,
        gender: Gender,
        genderPreference: Gender
    ) {
        save(name, username, bio, gender, genderPreference)
    }

    private fun getUserData() {
        updateViewState(ProfileViewState.Loading)
        val uid = auth.currentUser?.uid

        if (auth.isAuthenticated()) {
            retrieveUserData(uid!!)
        }

        updateViewState(ProfileViewState.DataRetrieved(userData.value))
    }

    private fun updateProfile(
        name: String? = null,
        username: String? = null,
        imageUrl: String? = null,
        bio: String? = null,
        gender: Gender? = null,
        genderPreference: Gender? = null
    ) {
        sendEvent(ProfileViewEvent.Saving)

        val uid = auth.currentUser?.uid

        val userData = FirebaseUserData(
            userId = uid,
            name = name ?: _userData.value?.name,
            username = username ?: _userData.value?.username,
            imageUrl = imageUrl ?: _userData.value?.imageUrl,
            bio = bio ?: _userData.value?.bio,
            gender = gender?.toString() ?: _userData.value?.gender,
            genderPreference = genderPreference?.toString() ?: _userData.value?.genderPreference
        )

        uid?.let { userId ->
            fireStore.collection(COLLECTION_USER)
                .document(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                sendEvent(ProfileViewEvent.Saved)
                                retrieveUserData(userId)
                            }
                            .addOnFailureListener { exception ->
                                handleException(exception, "Cannot update user!")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    handleException(exception, "Cannot create user!")
                }
        }
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {

            val result = it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener(onSuccess)
        }.addOnFailureListener {
            handleException(it)
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            updateProfile(imageUrl = it.toString())
        }
    }

    fun save(
        name: String,
        username: String,
        bio: String,
        gender: Gender,
        genderPreference: Gender
    ) {
        updateProfile(
            name = name,
            username = username,
            bio = bio,
            gender = gender,
            genderPreference = genderPreference
        )
    }

    private fun retrieveUserData(uid: String) {
        fireStore.getFirebaseUserById(uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, "Cannot retrieve user data.")
                }

                if (value != null) {
                    println(value.data)
                    val user = value.toObject<FirebaseUserData>()
                    _userData.value = user
                }
            }
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
    }

    fun logout() {
        auth.logout()
        _signedIn.value = false
        _userData.value = null
        _popupNotification.value = Event("Logged out.")
    }
}