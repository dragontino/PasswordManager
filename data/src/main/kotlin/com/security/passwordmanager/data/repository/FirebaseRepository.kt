package com.security.passwordmanager.data.repository

import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.domain.model.ConnectionFailedException
import com.security.passwordmanager.domain.model.IncorrectEmailException
import com.security.passwordmanager.domain.model.IncorrectPasswordException
import com.security.passwordmanager.domain.model.PasswordManagerException
import com.security.passwordmanager.domain.model.TooManyRequestsException
import com.security.passwordmanager.domain.model.UserDisabledException

internal interface FirebaseRepository {
    val auth: FirebaseAuth
    val database: FirebaseDatabase

    fun getDatabaseReference(root: String) = database.getReference(root)
    val currentUser get() = auth.currentUser
    val context: Context get() = database.app.applicationContext

    fun Exception.mapOrNull(): PasswordManagerException? = when (this) {
        is FirebaseAuthInvalidCredentialsException -> IncorrectPasswordException(errorCode, message)
        is FirebaseAuthInvalidUserException -> {
            if (errorCode == "ERROR_USER_DISABLED") {
                UserDisabledException
            } else {
                IncorrectEmailException
            }
        }
        is FirebaseTooManyRequestsException -> TooManyRequestsException
        is FirebaseException -> ConnectionFailedException(message ?: "")
        else -> null
    }


    fun Exception.map() = mapOrNull() ?: this
}