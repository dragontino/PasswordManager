package com.security.passwordmanager.data.repository

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Result

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val auth: FirebaseAuth, private val context: Context) {

    fun register(
        email: String,
        password: String,
        displayName: String,
        result: (Result<FirebaseUser>) -> Unit
    ) {
        result(Result.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    result(Result.Error(task.exception?.getRightMessages() ?: NullPointerException()))
                    return@addOnCompleteListener
                }

                auth.currentUser?.uid?.let {
                    FirebaseDatabase
                        .getInstance()
                        .getReference("Users")
                        .child(it)
                        .setValue(email)
                        .addOnCompleteListener { task1 ->
                            val currentUser = auth.currentUser
                            if (task1.isSuccessful && currentUser != null) {

                                currentUser
                                    .setDisplayName(displayName)
                                    .addOnCompleteListener { task2 ->
                                        if (task2.isSuccessful)
                                            result(Result.Success(currentUser))
                                    }
                                    .addOnFailureListener { exception ->
                                        result(Result.Error(exception.getRightMessages()))
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            result(Result.Error(exception.getRightMessages()))
                        }
                }
            }
            .addOnFailureListener {
                result(Result.Error(it.getRightMessages()))
            }
    }






    fun login(
        email: String,
        password: String,
        result: (Result<FirebaseUser>) -> Unit
    ) {
        result(Result.Loading)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && auth.currentUser != null) {
                    result(Result.Success(auth.currentUser!!))
                }
            }
            .addOnFailureListener {
                val exception = it.getRightMessages()
                println("exception = $exception")
                result(Result.Error(exception))
            }
    }


    fun checkEmailExists(email: String, isExists: (Boolean) -> Unit) = auth
        .fetchSignInMethodsForEmail(email)
        .addOnCompleteListener { task: Task<SignInMethodQueryResult?> ->
            isExists(!task.result?.signInMethods.isNullOrEmpty())
        }


    fun restorePassword(email: String, result: (Result<Void>) -> Unit) {
        result(Result.Loading)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result(Result.Success(task.result))
                else result(Result.Error(task.exception ?: NullPointerException()))
            }
            .addOnFailureListener {
                result(Result.Error(it))
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



    private fun FirebaseUser.setDisplayName(displayName: String) = updateProfile(
        UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
    )
}