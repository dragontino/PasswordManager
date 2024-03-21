package com.security.passwordmanager.domain.usecase

import android.net.Uri
import android.util.Log
import com.security.passwordmanager.domain.model.IconSite
import com.security.passwordmanager.domain.repository.EntityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetWebsiteLogoUseCase(
    private val repository: EntityRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "GetLogoUseCase"
    }


    /**
     * Находит лого сайта, который расположен на заданном [websiteUrl]
     * @param websiteUrl адрес сайта
     * @return лого сайта
     */
    suspend fun getWebsiteLogo(websiteUrl: String): Result<String> =
         withContext(dispatcher) {
             val iconSiteResult = repository.getWebsiteIcons(websiteUrl)
             iconSiteResult.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
             return@withContext iconSiteResult
                 .getOrNull()
                 ?.getBestIconUrl()
                 ?.let { Result.success(it) }
                 ?: iconSiteResult.map { "" }
         }


    private fun IconSite.getBestIconUrl(): String? {
        val bestIcon = icons
            .find { "favicon" in it.url }
            ?: icons.find { "apple" in it.url }
            ?: icons.firstOrNull()

        return bestIcon?.url?.getAbsolutePath(root = url)
    }

    private fun String.getAbsolutePath(root: String): String = when {
        Uri.parse(this).isAbsolute -> this
        else -> "$root$this"
    }
}