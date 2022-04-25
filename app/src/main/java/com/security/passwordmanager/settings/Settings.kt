package com.security.passwordmanager.settings

import android.content.Intent
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SettingsTable")
class Settings(
    @PrimaryKey var id: Int = 1,
    var email: String = "",
    var theme: String? = ThemeDef.SYSTEM_THEME.themeName,
) {
    var isUsingBeautifulFont = true
    var isShowingDataHints = true
    var isUsingBottomView = true
}

fun Intent.getStringExtra(name : String, defaultValue : String) =
    getStringExtra(name) ?: defaultValue