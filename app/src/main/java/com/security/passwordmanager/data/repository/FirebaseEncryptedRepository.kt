package com.security.passwordmanager.data.repository

import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference
import com.security.passwordmanager.R
import com.security.passwordmanager.data.CryptoManager

open class FirebaseEncryptedRepository(
    protected val auth: FirebaseAuth,
    private val database: DatabaseReference,
    private val cryptoManager: CryptoManager
) {

    protected fun getDatabaseReference(uid: String) = database.child(uid)
    protected val currentUser get() = auth.currentUser
    protected val context: Context get() = auth.app.applicationContext


    protected fun <R : Any> R.encrypt(
        uid: String,
        parseToString: (R) -> String = { it.toString() }
    ): String {
        val passcode = cryptoManager.createPasscodeFrom(uid)
        return cryptoManager.newEncrypt(
            defaultString = parseToString(this),
            passcode = passcode
        )
    }


    protected fun <R : Any> String.decrypt(uid: String, parseResult: (String) -> R): R {
        val passcode = cryptoManager.createPasscodeFrom(uid)
        return cryptoManager
            .newDecrypt(this, passcode)
            .let(parseResult)
    }


    protected fun String.decrypt(uid: String): String {
        return decrypt(uid) { it }
    }


    protected fun Exception.getRightMessages() = when (this) {
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
        is FirebaseException ->
            FirebaseException(context.getString(R.string.failed_to_connect_to_server), this)
        else -> this
    }
}