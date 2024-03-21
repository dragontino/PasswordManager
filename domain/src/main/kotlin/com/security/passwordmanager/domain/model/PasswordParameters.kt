package com.security.passwordmanager.domain.model

data class PasswordParameters(
    val length: Int,
    val useUppercase: Boolean,
    val useLowercase: Boolean,
    val useDigits: Boolean,
    val specialCharacters: String?
)
