package com.security.passwordmanager.data.model

import com.security.passwordmanager.domain.model.ColorScheme
import com.security.passwordmanager.domain.model.EncryptionHelper
import com.security.passwordmanager.domain.model.Settings
import com.security.passwordmanager.domain.model.Time

data class SettingsDB(
    val colorScheme: String = ColorScheme.System.toString(),
    val sunriseTime: String = Time.defaultSunriseTime.toString(),
    val sunsetTime: String = Time.defaultSunsetTime.toString(),
    val beautifulFont: String = "true",
    val autofill: String = "true",
    val dynamicColor: String = "false",
    val pullToRefresh: String = "true",
    val loadIcons: String = "true",
) {
    constructor(settings: Settings) : this(
        colorScheme = settings.colorScheme.toString(),
        sunriseTime = settings.sunriseTime.toString(),
        sunsetTime = settings.sunsetTime.toString(),
        beautifulFont = settings.beautifulFont.toString(),
        autofill = settings.autofill.toString(),
        dynamicColor = settings.dynamicColor.toString(),
        pullToRefresh = settings.pullToRefresh.toString(),
        loadIcons = settings.loadIcons.toString(),
    )

    fun mapToSettings(): Settings {
        return try {
            Settings(
                colorScheme = ColorScheme.valueOf(colorScheme),
                sunriseTime = Time(sunriseTime),
                sunsetTime = Time(sunsetTime),
                beautifulFont = beautifulFont.toBoolean(),
                autofill = autofill.toBoolean(),
                dynamicColor = dynamicColor.toBoolean(),
                pullToRefresh = pullToRefresh.toBoolean(),
                loadIcons = loadIcons.toBoolean(),
            )
        } catch (e: IllegalArgumentException) {
            return Settings()
        }
    }


    fun encrypt(encryption: EncryptionHelper): SettingsDB? {
        return copy(
            colorScheme = encryption.encrypt(colorScheme, ::colorScheme.name) ?: return null,
            sunriseTime = encryption.encrypt(sunriseTime, ::sunriseTime.name) ?: return null,
            sunsetTime = encryption.encrypt(sunsetTime, ::sunsetTime.name) ?: return null,
            beautifulFont = encryption.encrypt(beautifulFont, ::beautifulFont.name) ?: return null,
            autofill = encryption.encrypt(autofill, ::autofill.name) ?: return null,
            dynamicColor = encryption.encrypt(dynamicColor, ::dynamicColor.name) ?: return null,
            pullToRefresh = encryption.encrypt(pullToRefresh, ::pullToRefresh.name) ?: return null,
            loadIcons = encryption.encrypt(loadIcons, ::loadIcons.name) ?: return null,
        )
    }

    fun decrypt(decryption: EncryptionHelper): SettingsDB? {
        return copy(
            colorScheme = decryption.decrypt(colorScheme, ::colorScheme.name) ?: return null,
            sunriseTime = decryption.decrypt(sunriseTime, ::sunriseTime.name) ?: return null,
            sunsetTime = decryption.decrypt(sunsetTime, ::sunsetTime.name) ?: return null,
            beautifulFont = decryption.decrypt(beautifulFont, ::beautifulFont.name) ?: return null,
            autofill = decryption.decrypt(autofill, ::autofill.name) ?: return null,
            dynamicColor = decryption.decrypt(dynamicColor, ::dynamicColor.name) ?: return null,
            pullToRefresh = decryption.decrypt(pullToRefresh, ::pullToRefresh.name) ?: return null,
            loadIcons = decryption.decrypt(loadIcons, ::loadIcons.name) ?: return null,
        )
    }
}
