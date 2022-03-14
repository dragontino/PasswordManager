package com.security.passwordmanager.settings

class SettingsRepository(private val settingsDao: SettingsDao) {

    fun updateTheme(theme: String) = settingsDao.updateTheme(theme)

    fun updateUsingBeautifulFont(usingBeautifulFont: Boolean) =
        settingsDao.updateUsingBeautifulFont(usingBeautifulFont)

    fun updateDataHints(usingDataHints: Boolean) =
        settingsDao.updateDataHints(usingDataHints)

    fun updateUsingBottomView(usingBottomView: Boolean) =
        settingsDao.updateUsingBottomView(usingBottomView)

    fun getSettings() = settingsDao.getSettings() ?: Settings()

}