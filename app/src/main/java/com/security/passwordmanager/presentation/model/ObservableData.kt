package com.security.passwordmanager.presentation.model

import com.security.passwordmanager.data.model.Data

sealed class ObservableData(private val id: Int, private val email: String) {

    fun toData() = toData(id, email)

    open val haveErrors: Boolean
        get() = errors.any { it.isNotBlank() }

    protected abstract val errors: Array<String>

    protected abstract fun toData(id: Int, email: String): Data
}