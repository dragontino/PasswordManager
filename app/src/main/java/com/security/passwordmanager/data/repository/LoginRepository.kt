package com.security.passwordmanager.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.data.Result

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val auth: FirebaseAuth) {

    fun register(
        email: String,
        password: String,
        result: (Result<FirebaseUser>) -> Unit
    ) {
        result(Result.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    result(Result.Error(task.exception ?: NullPointerException()))
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
                                result(Result.Success(currentUser))
                            } else {
                                result(Result.Error(task1.exception ?: NullPointerException()))
                            }
                        }
                }
            }
            .addOnFailureListener {
                result(Result.Error(it))
            }
    }


    fun login(
        email: String,
        password: String,
        result: (Result<FirebaseUser>) -> Unit
    ) {
        result(Result.Loading)
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful && auth.currentUser != null) {
                    result(Result.Success(auth.currentUser!!))
                }
                else {
                    result(Result.Error(task.exception ?: NullPointerException()))
                }
            }
            .addOnFailureListener {
                result(Result.Error(it))
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
}