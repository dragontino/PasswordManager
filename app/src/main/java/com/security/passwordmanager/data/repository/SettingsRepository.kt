package com.security.passwordmanager.data.repository

import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.data.room.SettingsDao
import com.security.passwordmanager.presentation.model.enums.Themes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val settingsDao: SettingsDao) {

    suspend fun addSettings(settings: Settings) {
        settingsDao.addSettings(settings)
    }

    suspend fun updateTheme(email: String, theme: Themes) = settingsDao.updateTheme(email, theme)

    suspend fun updateUsingBeautifulFont(email: String, usingBeautifulFont: Boolean) =
        settingsDao.updateUsingBeautifulFont(email, usingBeautifulFont)

    suspend fun updateDataHints(email: String, usingDataHints: Boolean) =
        settingsDao.updateDataHints(email, usingDataHints)

    suspend fun updateUsingBottomView(email: String, usingBottomView: Boolean) =
        settingsDao.updateUsingBottomView(email, usingBottomView)



    fun getSettings(email: String): Flow<Settings> =
        settingsDao
            .getSettings(email)
            .map {
                it ?: Settings(email = email)
                    .also { newSettings -> addSettings(newSettings) }
            }
}