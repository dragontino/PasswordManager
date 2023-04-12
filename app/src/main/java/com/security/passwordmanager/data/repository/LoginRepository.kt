package com.security.passwordmanager.data.repository

import android.content.Context
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Result

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    private val auth: FirebaseAuth,
    private val context: Context
) {

    fun register(
        email: String,
        password: String,
        displayName: String,
        resultAction: (Result<FirebaseUser>) -> Unit
    ) {
        resultAction(Result.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val currentUser = auth.currentUser

                currentUser?.setDisplayName(displayName) {
                    resultAction(it.map { currentUser })
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
                auth.currentUser?.let {
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
                resultAction(Result.Error(it))
            }
    }



    private fun Exception.getRightMessages() = when (this) {
        is FirebaseAuthInvalidCredentialsException ->
            FirebaseAuthInvalidCredentialsException(
                errorCode,
                context.getString(R.string.incorrect_password)
            )
        is FirebaseAuthInvalidUserException -> {
            val msg = if (errorCode == "ERROR_USER_DISABLED") {
                context.getString(R.string.user_disabled_exception)
            } else {
                context.getString(R.string.incorrect_email)
            }

            FirebaseAuthInvalidUserException(errorCode, msg)
        }
        is FirebaseTooManyRequestsException ->
            FirebaseTooManyRequestsException(context.getString(R.string.too_many_requests_exception))
        else -> this
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