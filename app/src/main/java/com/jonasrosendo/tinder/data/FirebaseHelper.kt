package com.jonasrosendo.tinder.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

object FirebaseHelper {

    fun FirebaseAuth.isAuthenticated(): Boolean = currentUser != null

    fun FirebaseFirestore.getFirebaseUserById(uid: String): DocumentReference {
        return collection(COLLECTION_USER)
            .document(uid)
    }

    fun FirebaseFirestore.getUserByUsername(username: String): Task<QuerySnapshot> {
        return collection(COLLECTION_USER)
            .whereEqualTo("username", username)
            .get()
    }

    fun FirebaseAuth.logout() { signOut() }
}