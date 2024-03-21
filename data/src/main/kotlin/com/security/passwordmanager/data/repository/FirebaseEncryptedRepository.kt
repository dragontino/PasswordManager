package com.security.passwordmanager.data.repository

import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.domain.model.ConnectionFailedException
import com.security.passwordmanager.domain.model.IncorrectEmailException
import com.security.passwordmanager.domain.model.IncorrectPasswordException
import com.security.passwordmanager.domain.model.TooManyRequestsException
import com.security.passwordmanager.domain.model.UserDisabledException

abstract class FirebaseEncryptedRepository(
    protected val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val cryptoManager: CryptoManager
) {

    protected fun getDatabaseReference(uid: String) = database.getReference(uid)
    protected val currentUser get() = auth.currentUser
    protected val context: Context get() = database.app.applicationContext


    protected fun <R : Any> R.encrypt(
        uid: String,
        parseToString: (R) -> String = { it.toString() }
    ): String {
        val passcode = cryptoManager.createPasscodeFrom(uid)
        return cryptoManager.encrypt(
            defaultString = parseToString(this),
            passcode = passcode
        )
    }


    protected fun <R : Any> String.decrypt(uid: String, parseResult: (String) -> R): R {
        val passcode = cryptoManager.createPasscodeFrom(uid)
        return cryptoManager
            .decrypt(this, passcode)
            .let(parseResult)
    }


    protected fun String.decrypt(uid: String): String {
        return decrypt(uid) { it }
    }


    protected fun Exception.mapOrNull(): Exception? = when (this) {
        is FirebaseAuthInvalidCredentialsException -> IncorrectPasswordException(errorCode, message)
        is FirebaseAuthInvalidUserException -> {
            if (errorCode == "ERROR_USER_DISABLED") {
                UserDisabledException
            } else {
                IncorrectEmailException
            }
        }
        is FirebaseTooManyRequestsException -> TooManyRequestsException
        is FirebaseException -> ConnectionFailedException(message)
        else -> null
    }


    protected fun Exception.map() = mapOrNull() ?: this
}
