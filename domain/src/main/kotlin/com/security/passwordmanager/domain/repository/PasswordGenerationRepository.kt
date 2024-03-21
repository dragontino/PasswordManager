package com.security.passwordmanager.domain.repository

import com.security.passwordmanager.domain.model.PasswordParameters

interface PasswordGenerationRepository {
    suspend fun generateStrongPassword(params: PasswordParameters): Result<String>
}