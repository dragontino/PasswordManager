package com.security.passwordmanager.domain.usecase

import android.util.Log
import com.security.passwordmanager.domain.model.SettingsNotFoundException
import com.security.passwordmanager.domain.model.settings.Settings
import com.security.passwordmanager.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class SettingsUseCase(
    private val repository: SettingsRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "LoadSettingsUseCase"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchSettings(): Flow<Settings> = repository.fetchSettings().mapLatest { result ->
        if (result.exceptionOrNull() is SettingsNotFoundException) {
            repository.addSettings(Settings())
            delay(200)
            return@mapLatest Settings()
        }
        else {
            result.exceptionOrNull()?.localizedMessage?.let { Log.e(TAG, it) }
            return@mapLatest result.getOrNull() ?: Settings()
        }
    }.flowOn(dispatcher)


    suspend fun loadSettings(resultAction: (Settings) -> Unit) {
        withContext(dispatcher) {
            repository.getSettings { result ->
                result.exceptionOrNull()?.localizedMessage?.let { Log.e(TAG, it) }
                result.getOrNull()?.let(resultAction)
            }
        }
    }


    suspend fun updateSettingsProperty(
        name: String,
        value: Any,
        resultAction: (Result<Unit>) -> Unit
    ) {
        withContext(dispatcher) {
            repository.updateSettingsProperty(name, value) { result ->
                result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
                resultAction(result)
            }
        }
    }
}