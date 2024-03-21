package com.security.passwordmanager.domain.usecase

import android.util.Log
import com.security.passwordmanager.domain.model.PasswordParameters
import com.security.passwordmanager.domain.repository.PasswordGenerationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GeneratePasswordUseCase(
    private val repository: PasswordGenerationRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "GeneratePasswordUseCase"
    }

    suspend fun generateStrongPassword(params: PasswordParameters): Result<String> =
        withContext(dispatcher) {
            val result = repository.generateStrongPassword(params)
            result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
            return@withContext result
        }
}