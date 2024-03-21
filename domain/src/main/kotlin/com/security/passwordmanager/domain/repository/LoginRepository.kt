package com.security.passwordmanager.domain.repository

import com.security.passwordmanager.domain.model.SignInCredentials
import com.security.passwordmanager.domain.model.SignUpCredentials

interface LoginRepository {

    val userEmail: String
    suspend fun signUp(credentials: SignUpCredentials, resultAction: (Result<String>) -> Unit)

    suspend fun signIn(credentials: SignInCredentials, resultAction: (Result<String>) -> Unit)

    suspend fun signOut()

    suspend fun checkUserExistsByEmail(email: String, isExists: (Result<Boolean>) -> Unit)

    // TODO: 17.10.2023 переделать
    suspend fun restorePassword(email: String, resultAction: (Result<Unit>) -> Unit)

    fun restoreEmail()

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        resultAction: (Result<Unit>) -> Unit
    )
}