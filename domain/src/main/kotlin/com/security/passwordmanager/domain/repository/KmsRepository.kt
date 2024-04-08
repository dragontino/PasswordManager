package com.security.passwordmanager.domain.repository

interface KmsRepository {
    suspend fun initializeKmsClient(): Result<Unit>
}