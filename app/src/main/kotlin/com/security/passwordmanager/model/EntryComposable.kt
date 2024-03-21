package com.security.passwordmanager.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.security.passwordmanager.domain.model.SignInCredentials
import com.security.passwordmanager.domain.model.SignUpCredentials

class EntryComposable(
    username: String = "",
    email: String = "",
    password: String = ""
) {
    var username by mutableStateOf(username)
    var email by mutableStateOf(email)
    var password by mutableStateOf(password)
    var repeatedPassword by mutableStateOf(password)

    var isPasswordVisible by mutableStateOf(false)
    var isRepeatedPasswordVisible by mutableStateOf(false)

    var emailErrorMessage by mutableStateOf("")
    var passwordErrorMessage by mutableStateOf("")

    fun changePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

    fun changeRepeatedPasswordVisibility() {
        isRepeatedPasswordVisible = !isRepeatedPasswordVisible
    }

    fun clear() {
        email = ""
        password = ""
        isPasswordVisible = false
        emailErrorMessage = ""
        passwordErrorMessage = ""
    }

    fun asSignInCredentials() = SignInCredentials(email, password)

    fun asSignUpCredentials() = SignUpCredentials(email, password, username)
}