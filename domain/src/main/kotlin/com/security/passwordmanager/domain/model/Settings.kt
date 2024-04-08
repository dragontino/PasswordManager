package com.security.passwordmanager.domain.model

data class Settings(
    val colorScheme: ColorScheme = ColorScheme.System,
    val sunriseTime: Time = Time.defaultSunriseTime,
    val sunsetTime: Time = Time.defaultSunsetTime,
    val beautifulFont: Boolean = true,
    val autofill: Boolean = false,
    val dynamicColor: Boolean = false,
    val pullToRefresh: Boolean = true,
    val loadIcons: Boolean = true,
    val autoBlockingTimeMinutes: Int? = 1
)