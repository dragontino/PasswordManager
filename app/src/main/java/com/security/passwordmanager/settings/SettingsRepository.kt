package com.security.passwordmanager.settings

class SettingsRepository(private val settingsDao: SettingsDao) {

    fun updateTheme(email: String, theme: ThemeDef) = settingsDao.updateTheme(email, theme.themeName)

    fun updateUsingBeautifulFont(email: String, usingBeautifulFont: Boolean) =
        settingsDao.updateUsingBeautifulFont(email, usingBeautifulFont)

    fun updateDataHints(email: String, usingDataHints: Boolean) =
        settingsDao.updateDataHints(email, usingDataHints)

    fun updateUsingBottomView(email: String, usingBottomView: Boolean) =
        settingsDao.updateUsingBottomView(email, usingBottomView)

    fun getSettings(email: String) = settingsDao.getSettings(email) ?: Settings()

}