package com.security.passwordmanager.domain.repository

import com.security.passwordmanager.domain.model.AppVersionInfo
import com.security.passwordmanager.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun addSettings(settings: Settings, resultAction: (Result<Unit>) -> Unit = {})

    suspend fun updateSettingsProperty(
        name: String,
        value: Any,
        resultAction: (Result<Unit>) -> Unit
    )

    fun fetchSettings(): Flow<Result<Settings>>

    suspend fun getSettings(resultAction: (Result<Settings>) -> Unit)

    fun getUsername(): Result<String>

    suspend fun changeUsername(newUsername: String, resultAction: (Result<Unit>) -> Unit)

    suspend fun getAppVersionInfo(resultAction: (Result<AppVersionInfo>) -> Unit)
}