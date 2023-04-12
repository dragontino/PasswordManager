package com.security.passwordmanager.data.model.settings

import com.security.passwordmanager.presentation.model.Time
import com.security.passwordmanager.presentation.model.enums.ColorDesign

data class Settings(
    var colorDesign: ColorDesign = ColorDesign.System,
    var sunriseTime: Time = Time.defaultSunriseTime,
    var sunsetTime: Time = Time.defaultSunsetTime,
    var beautifulFont: Boolean = true,
    var autofill: Boolean = false,
    var dynamicColor: Boolean = false,
    var pullToRefresh: Boolean = true,
)