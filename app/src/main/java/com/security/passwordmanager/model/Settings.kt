package com.security.passwordmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SettingsTable")
data class Settings(
    @PrimaryKey var id: Int = 1,
    var email: String = "",
    var theme: String? = Themes.SYSTEM_THEME.themeName,
    var isUsingBeautifulFont: Boolean = true,
    var isShowingDataHints: Boolean = true,
    var isUsingBottomView: Boolean = true,
)