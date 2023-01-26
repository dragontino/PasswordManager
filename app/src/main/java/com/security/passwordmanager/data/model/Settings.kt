package com.security.passwordmanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.security.passwordmanager.presentation.model.enums.Themes

@Entity(tableName = "SettingsTable")
data class Settings(
    @PrimaryKey var id: Int = 0,
    var email: String = "",
    var theme: Themes = Themes.System,
    var isUsingBeautifulFont: Boolean = true,
    var isUsingAutofill: Boolean = true,
    @ColumnInfo(name = "usingDynamicColor")
    var dynamicColor: Boolean = false,
)