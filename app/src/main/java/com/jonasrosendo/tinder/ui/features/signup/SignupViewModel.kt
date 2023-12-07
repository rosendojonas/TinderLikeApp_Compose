package com.jonasrosendo.tinder.ui.features.signup

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jonasrosendo.tinder.core.BaseViewModel
import com.jonasrosendo.tinder.data.COLLECTION_USER
import com.jonasrosendo.tinder.data.Event
import com.jonasrosendo.tinder.data.FirebaseHelper.getFirebaseUserById
import com.jonasrosendo.tinder.data.FirebaseHelper.getUserByUsername
import com.jonasrosendo.tinder.model.FirebaseUserData
import com.jonasrosendo.tinder.ui.features.profile.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SignupViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val fireStore: FirebaseFirestore,
    val storage: FirebaseStorage
) : BaseViewModel<SignupViewState, SignupViewEvent, SignupViewAction>() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _popupNotification = MutableStateFlow<Event<String>?>(null)
    val popupNotification: StateFlow<Event<String>?> = _popupNotification

    private val _signedIn = MutableStateFlow(false)
    val signedIn: StateFlow<Boolean> = _signedIn

    private val _userData = MutableStateFlow<FirebaseUserData?>(null)
    val userData: StateFlow<FirebaseUserData?> = _userData

    override val initialViewState: SignupViewState
        get() = SignupViewState.Default

    override fun processAction(action: SignupViewAction) {
        when (action) {
            is SignupViewAction.Signup -> signup(action.username, action.email, action.password)
        }
    }

    private fun signup(username: String, email: String, password: String) {
        if (username.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields!")
            return
        }

        showLoading()

        signupUser(email = email, password = password, username = username)
    }

    private fun signupUser(email: String, password: String, username: String) {
        fireStore.getUserByUsername(username)
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    createAccount(
                        email = email,
                        password = password,
                        username = username
                    )
                } else {
                    handleException(customMessage = "Username already exists")
                }

                hideLoading()

            }.addOnFailureListener { exception ->
                handleException(exception)
            }
    }

    private fun createAccount(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // create user profile in database
                    createOrUpdateProfile(
                        username = username
                    )
                } else {
                    handleException(task.exception, "Signup failed")
                }
            }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        username: String? = null,
        imageUrl: String? = null,
        bio: String? = null,
        gender: Gender? = null,
        genderPreference: Gender? = null
    ) {
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
            showLoading()
            fireStore.collection(COLLECTION_USER)
                .document(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                hideLoading()
                            }
                            .addOnFailureListener { exception ->
                                handleException(exception, "Cannot update user!")
                            }
                    } else {
                        fireStore.collection(COLLECTION_USER)
                            .document(userId)
                            .set(userData)
                        hideLoading()
                        retrieveUserData(userId)
                    }
                }
                .addOnFailureListener { exception ->
                    handleException(exception, "Cannot create user!")
                }
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