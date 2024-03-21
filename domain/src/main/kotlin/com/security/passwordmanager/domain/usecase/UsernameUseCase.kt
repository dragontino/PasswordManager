package com.security.passwordmanager.domain.usecase

import android.util.Log
import com.security.passwordmanager.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UsernameUseCase(
    private val repository: SettingsRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "ChangeUsernameUseCase"
    }

    suspend fun changeUsername(newUsername: String, resultAction: (Result<Unit>) -> Unit) {
        withContext(dispatcher) {
            repository.changeUsername(newUsername) { result ->
                result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
                resultAction(result)
            }
        }
    }

    fun getUsername(): Result<String> {
        val result = repository.getUsername()
        result.exceptionOrNull()?.let {
            Log.e(TAG, it.localizedMessage, it)
        }
        return result
    }
}