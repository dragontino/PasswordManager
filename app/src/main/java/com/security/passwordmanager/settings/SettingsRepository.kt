package com.security.passwordmanager.settings

import com.security.passwordmanager.model.Settings
import com.security.passwordmanager.model.Themes

class SettingsRepository(private val settingsDao: SettingsDao) {

    fun updateTheme(email: String, theme: Themes) = settingsDao.updateTheme(email, theme.themeName)

    fun updateUsingBeautifulFont(email: String, usingBeautifulFont: Boolean) =
        settingsDao.updateUsingBeautifulFont(email, usingBeautifulFont)

    fun updateDataHints(email: String, usingDataHints: Boolean) =
        settingsDao.updateDataHints(email, usingDataHints)

    fun updateUsingBottomView(email: String, usingBottomView: Boolean) =
        settingsDao.updateUsingBottomView(email, usingBottomView)

    fun getSettings(email: String) = settingsDao.getSettings(email) ?: Settings()

    suspend fun getSuspendSettings(email: String) =
        settingsDao.getSuspendSettings(email) ?: Settings()
}