package com.security.passwordmanager.settings

class SettingsRepository(private val settingsDao: SettingsDao) {

    fun updateTheme(theme: String) = settingsDao.addSettings(Settings(theme))

    fun getTheme() = settingsDao.getSettings()?.theme ?: ThemeDef.SYSTEM_THEME.themeName

}