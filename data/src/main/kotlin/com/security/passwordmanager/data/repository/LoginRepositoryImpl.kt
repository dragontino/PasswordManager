package com.security.passwordmanager.data.repository

import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.util.checkNetworkConnection
import com.security.passwordmanager.domain.model.ChangePasswordException
import com.security.passwordmanager.domain.model.ChangeUsernameException
import com.security.passwordmanager.domain.model.InternetConnectionLostException
import com.security.passwordmanager.domain.model.PasswordResetException
import com.security.passwordmanager.domain.model.SignInCredentials
import com.security.passwordmanager.domain.model.SignInException
import com.security.passwordmanager.domain.model.SignUpCredentials
import com.security.passwordmanager.domain.model.SignUpException
import com.security.passwordmanager.domain.model.UserEmailCheckException
import com.security.passwordmanager.domain.model.UserNotLoggedException
import com.security.passwordmanager.domain.repository.LoginRepository

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepositoryImpl(
    auth: FirebaseAuth,
    database: FirebaseDatabase,
    cryptoManager: CryptoManager,
    private val preferences: AppPreferences
) : FirebaseEncryptedRepository(auth, database, cryptoManager), LoginRepository {

    override val userEmail: String get() = preferences.email

    override suspend fun signUp(
        credentials: SignUpCredentials,
        resultAction: (Result<String>) -> Unit
    ) {
        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }
        credentials.check().exceptionOrNull()?.let {
            return resultAction(Result.failure(it))
        }

        auth.createUserWithEmailAndPassword(credentials.email, credentials.password)
            .addOnSuccessListener {
                auth.sendSignInLinkToEmail(credentials.email, ActionCodeSettings.zzb())
                preferences.email = it.user?.email ?: credentials.email

                if (credentials.username.isNotBlank()) {
                    auth.currentUser?.setDisplayName(credentials.username) { result ->
                        val newUsername = result.getOrNull()?.let { credentials.username } ?: ""
                        resultAction(Result.success(newUsername))
                    }
                } else {
                    resultAction(Result.success(credentials.username))
                }
            }
            .addOnFailureListener {
                val exception = it.mapOrNull() ?: SignUpException(it.localizedMessage)
                resultAction(Result.failure(exception))
            }
    }


    override suspend fun signIn(
        credentials: SignInCredentials,
        resultAction: (Result<String>) -> Unit
    ) {
        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }
        credentials.check().exceptionOrNull()?.let {
            return resultAction(Result.failure(it))
        }


        auth.signInWithEmailAndPassword(credentials.email, credentials.password)
            .addOnSuccessListener { authResult ->
                authResult.user
                    ?.let {
                        preferences.email = it.email ?: credentials.email
                        resultAction(Result.success(it.username))
                    }
                    ?: resultAction(Result.failure(SignInException(null)))
            }
            .addOnFailureListener {
                resultAction(
                    Result.failure(it.mapOrNull() ?: SignInException(it.localizedMessage))
                )
            }
    }


    override suspend fun checkUserExistsByEmail(
        email: String,
        isExists: (Result<Boolean>) -> Unit
    ) {
        if (!context.checkNetworkConnection()) {
            return isExists(Result.failure(InternetConnectionLostException))
        }

        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                isExists(Result.success(!result?.signInMethods.isNullOrEmpty()))
            }
            .addOnFailureListener {
                isExists(Result.failure(it.mapOrNull() ?: UserEmailCheckException(email)))
            }
    }


    override suspend fun restorePassword(email: String, resultAction: (Result<Unit>) -> Unit) {
        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                resultAction(Result.success(Unit))
            }
            .addOnFailureListener {
                resultAction(Result.failure(it.mapOrNull() ?: PasswordResetException))
            }
    }


    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        resultAction: (Result<Unit>) -> Unit
    ) {
        val currentUser = auth.currentUser
        val email = currentUser?.email

        when {
            currentUser == null || email == null -> {
                return resultAction(Result.failure(UserNotLoggedException))
            }
            !context.checkNetworkConnection() -> {
                return resultAction(Result.failure(InternetConnectionLostException))
            }
            else -> {
                val authCredential = EmailAuthProvider.getCredential(email, oldPassword)

                currentUser.reauthenticate(authCredential)
                    .addOnSuccessListener {
                        currentUser.updatePassword(newPassword)
                            .addOnSuccessListener {
                                resultAction(Result.success(Unit))
                            }
                            .addOnFailureListener {
                                resultAction(
                                    Result.failure(it.mapOrNull() ?: ChangePasswordException)
                                )
                            }
                    }
                    .addOnFailureListener {
                        resultAction(
                            Result.failure(it.mapOrNull() ?: ChangePasswordException)
                        )
                    }
            }
        }
    }


    override suspend fun signOut() {
        restoreEmail()
        auth.signOut()
    }


    override fun restoreEmail() {
        preferences.email = ""
    }


    private fun FirebaseUser.setDisplayName(displayName: String, result: (Result<Unit>) -> Unit) {
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()

        updateProfile(request)
            .addOnSuccessListener {
                result(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception.mapOrNull() ?: ChangeUsernameException))
            }
    }


    private val FirebaseUser.username: String get() = displayName ?: email ?: ""
}