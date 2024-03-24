package com.security.passwordmanager.domain.model.settings

import com.security.passwordmanager.domain.model.ColorScheme
import com.security.passwordmanager.domain.model.Time

data class EncryptedSettings(
    val colorScheme: String = ColorScheme.System.toString(),
    val sunriseTime: String = Time.defaultSunriseTime.toString(),
    val sunsetTime: String = Time.defaultSunsetTime.toString(),
    val beautifulFont: String = "true",
    val autofill: String = "true",
    val dynamicColor: String = "false",
    val pullToRefresh: String = "true",
    val loadIcons: String = "true",
)
