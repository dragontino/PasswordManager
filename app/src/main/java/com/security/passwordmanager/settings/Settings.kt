package com.security.passwordmanager.settings

import android.content.Intent
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "SettingsTable")
class Settings(@PrimaryKey var id: Int, var theme: String?) {
    @Ignore
    constructor(theme: String?) : this(1, theme)
}

fun Intent.getStringExtra(name : String, defaultValue : String) =
    getStringExtra(name) ?: defaultValue