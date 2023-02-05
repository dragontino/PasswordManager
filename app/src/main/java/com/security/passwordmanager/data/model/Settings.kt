package com.security.passwordmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.security.passwordmanager.presentation.model.Time
import com.security.passwordmanager.presentation.model.TimeConverter
import com.security.passwordmanager.presentation.model.enums.ColorDesign

@TypeConverters(TimeConverter::class)
@Entity(tableName = "SettingsTable")
data class Settings(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val email: String = "",
    var colorDesign: ColorDesign = ColorDesign.System,
    var sunriseTime: Time = Time.defaultSunriseTime,
    var sunsetTime: Time = Time.defaultSunsetTime,
    var isUsingBeautifulFont: Boolean = true,
    var isUsingAutofill: Boolean = true,
    var dynamicColor: Boolean = false,
    var disablePullToRefresh: Boolean = false,
)