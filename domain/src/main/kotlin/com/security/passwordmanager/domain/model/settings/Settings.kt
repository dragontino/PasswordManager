package com.security.passwordmanager.domain.model.settings

import com.security.passwordmanager.domain.model.ColorDesign
import com.security.passwordmanager.domain.model.Time

data class Settings(
    val colorDesign: ColorDesign = ColorDesign.System,
    val sunriseTime: Time = Time.defaultSunriseTime,
    val sunsetTime: Time = Time.defaultSunsetTime,
    val beautifulFont: Boolean = true,
    val autofill: Boolean = false,
    val dynamicColor: Boolean = false,
    val pullToRefresh: Boolean = true,
    val loadIcons: Boolean = true,
    val autoBlockingTimeMinutes: Int? = 1
)