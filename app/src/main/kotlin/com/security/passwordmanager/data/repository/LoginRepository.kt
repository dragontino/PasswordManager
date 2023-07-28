package com.security.passwordmanager.data.repository

import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.Result

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    auth: FirebaseAuth,
    database: DatabaseReference,
    cryptoManager: CryptoManager
) : FirebaseEncryptedRepository(auth, database, cryptoManager) {

    fun register(
        email: String,
        password: String,
        displayName: String,
        resultAction: (Result<FirebaseUser>) -> Unit
    ) {
        resultAction(Result.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.sendSignInLinkToEmail(email, ActionCodeSettings.zzb())
                val currentUser = auth.currentUser

                if (displayName.isNotBlank()) {
                    currentUser?.setDisplayName(displayName) {
                        resultAction(it.map { currentUser })
                    }
                }
            }
            .addOnFailureListener {
                resultAction(Result.Error(it.getRightMessages()))
            }
    }


    fun login(
        email: String,
        password: String,
        resultAction: (Result<FirebaseUser>) -> Unit
    ) {
        resultAction(Result.Loading)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                currentUser?.let {
                    resultAction(Result.Success(it))
                }
            }
            .addOnFailureListener {
                val exception = it.getRightMessages()
                resultAction(Result.Error(exception))
            }
    }


    fun checkEmailExists(email: String, isExists: (Boolean) -> Unit) {
        auth
            .fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                isExists(!result?.signInMethods.isNullOrEmpty())
            }
    }


    fun restorePassword(email: String, resultAction: (Result<Unit>) -> Unit) {
        resultAction(Result.Loading)
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                resultAction(Result.Success(Unit))
            }
            .addOnFailureListener {
                resultAction(Result.Error(it.getRightMessages()))
            }
    }


    private fun FirebaseUser.setDisplayName(displayName: String, result: (Result<Unit>) -> Unit) {
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()

        updateProfile(request)
            .addOnSuccessListener {
                result(Result.Success(Unit))
            }
            .addOnFailureListener { exception ->
                result(Result.Error(exception.getRightMessages()))
            }
    }
}