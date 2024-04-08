package com.security.passwordmanager.model

import android.content.Context
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.PasswordManagerException

class ExceptionMessageImpl(private val context: Context) : ExceptionMessage {
    override fun getMessage(exception: PasswordManagerException): String {
        return exception.getMessage(context.resources)
    }
}