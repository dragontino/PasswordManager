package com.security.passwordmanager.domain.usecase

import android.util.Log
import com.security.passwordmanager.domain.model.TitleNotFoundException
import com.security.passwordmanager.domain.repository.EntityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetWebsiteDomainNameUseCase(
    private val repository: EntityRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "GetWebsiteDomainNameUseCase"
    }

    suspend fun getWebsiteDomainName(websiteUrl: String): Result<String> =
        withContext(dispatcher) {
            val titleRegex = "<title>(.*?)</title>".toRegex(RegexOption.IGNORE_CASE)
            val bodyResult = repository.getWebsiteBody(url = websiteUrl)

            bodyResult.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
            val body = bodyResult.getOrNull() ?: return@withContext bodyResult

            titleRegex
                .find(body)
                ?.groupValues
                ?.firstOrNull()
                ?.removeSurrounding(
                    prefix = "<title>",
                    suffix = "</title>"
                )
                ?.let { Result.success(it) }
                ?: Result.failure(TitleNotFoundException(websiteUrl))
        }
}