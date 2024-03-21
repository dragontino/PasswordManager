package com.security.passwordmanager.domain.model

interface ExceptionMessage {
    fun getMessage(exception: PasswordManagerException): String

    fun getMessage(throwable: Throwable): String? = when (throwable) {
        is PasswordManagerException -> getMessage(throwable)
        else -> null
    }
}