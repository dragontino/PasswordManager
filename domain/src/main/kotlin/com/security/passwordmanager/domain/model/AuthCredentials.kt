package com.security.passwordmanager.domain.model

import android.util.Patterns

interface AuthCredentials {
    val email: String
    val password: String

    fun check(): Result<Unit> = when {
        !email.isValidEmail() -> Result.failure(InvalidEmailException)
        password.isBlank() -> Result.failure(EmptyPasswordException)
        password.length < CredentialDefaults.minLength -> Result.failure(PasswordLengthException)
        else -> Result.success(Unit)
    }
}


data class SignUpCredentials(
    override val email: String,
    override val password: String,
    val username: String
) : AuthCredentials

data class SignInCredentials(
    override val email: String,
    override val password: String
) : AuthCredentials


fun String.isValidEmail() =
    isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun String.isValidPassword() =
    isNotBlank() && length >= CredentialDefaults.minLength


private object CredentialDefaults {
    const val minLength = 7
}