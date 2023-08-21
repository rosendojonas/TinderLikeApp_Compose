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
import com.jonasrosendo.tinder.model.FirebaseUserData
import com.jonasrosendo.tinder.ui.screens.Gender
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
        //auth.signOut()
        val currentUser = auth.currentUser
        _signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            retrieveUserData(uid)
        }
    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields!")
            return
        }

        showLoading()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signedIn.value = true
                    hideLoading()
                    auth.currentUser?.uid?.let {
                        retrieveUserData(it)
                    }
                } else {
                    handleException(task.exception, customMessage = "Login failed.")
                }
            }
            .addOnFailureListener {
                handleException(it, customMessage = "Login failed.")
            }
    }

    fun onSignup(username: String, email: String, password: String) {
        if (username.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields!")
            return
        }

        showLoading()

        signupUser(email = email, password = password, username = username)
    }

    private fun signupUser(email: String, password: String, username: String) {
        getUserByUsername(username)
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

    fun onLogout() {
        auth.signOut()
        _signedIn.value = false
        _userData.value = null
        _popupNotification.value = Event("Logged out.")
    }

    private fun getFirebaseUserById(uid: String): DocumentReference {
        return fireStore.collection(COLLECTION_USER)
            .document(uid)
    }

    private fun getUserByUsername(username: String): Task<QuerySnapshot> {
        return fireStore.collection(COLLECTION_USER)
            .whereEqualTo("username", username)
            .get()
    }

    private fun showLoading() {
        _loading.value = true
    }

    private fun hideLoading() {
        _loading.value = false
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
            genderPreference = gender?.toString() ?: _userData.value?.genderPreference
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