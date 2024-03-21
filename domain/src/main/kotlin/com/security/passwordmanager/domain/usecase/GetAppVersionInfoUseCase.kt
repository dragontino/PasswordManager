package com.security.passwordmanager.domain.usecase

import android.util.Log
import com.security.passwordmanager.domain.model.AppVersionInfo
import com.security.passwordmanager.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetAppVersionInfoUseCase(
    private val repository: SettingsRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "GetAppVersionInfoUseCase"
    }

    suspend fun getAppVersionInfo(resultAction: (Result<AppVersionInfo>) -> Unit) {
        withContext(dispatcher) {
            repository.getAppVersionInfo { result ->
                result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
                resultAction(result)
            }
        }
    }
}