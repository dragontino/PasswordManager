package com.security.passwordmanager.data

import android.app.Application
import android.content.Context
import androidx.annotation.StyleRes
import com.security.passwordmanager.R
import com.security.passwordmanager.data.EnumPreferences.*
import com.security.passwordmanager.getString
import com.security.passwordmanager.presentation.model.Time

class AppPreferences(application: Application) {
    private val preferences =
        application.getSharedPreferences(APP_PREFERENCES.title, Context.MODE_PRIVATE)

    var startTime: Time
        get() {
            val hours = preferences.getInt(APP_PREFERENCES_START_HOURS.title, 7)
            val minutes = preferences.getInt(APP_PREFERENCES_START_MINUTES.title, 0)
            return Time(hours, minutes)
        }
        set(value) {
            preferences.edit().apply {
                putInt(APP_PREFERENCES_START_HOURS.title, value.hours)
                putInt(APP_PREFERENCES_START_MINUTES.title, value.minutes)
            }.apply()
        }

    var endTime: Time
        get() {
            val hours = preferences.getInt(APP_PREFERENCES_END_HOURS.title, 23)
            val minutes = preferences.getInt(APP_PREFERENCES_END_MINUTES.title, 0)
            return Time(hours, minutes)
        }
        set(value) {
            preferences.edit().apply {
                putInt(APP_PREFERENCES_END_HOURS.title, value.hours)
                putInt(APP_PREFERENCES_END_MINUTES.title, value.minutes)
            }.apply()
        }

    var isPasswordRemembered: Boolean
        get() = preferences.getBoolean(APP_PREFERENCES_IS_PASSWORD_REMEMBERED.title, false)
        set(value) = preferences.edit()
            .putBoolean(APP_PREFERENCES_IS_PASSWORD_REMEMBERED.title, value).apply()

    var email: String
        get() = preferences.getString(APP_PREFERENCES_EMAIL.title) ?: ""
        set(value) = preferences.edit()
            .putString(APP_PREFERENCES_EMAIL.title, value).apply()

    var theme: Int
        get(): @StyleRes Int = preferences
            .getInt(APP_PREFERENCES_THEME.title, R.style.Theme_PasswordManager)
        set(@StyleRes value) = preferences.edit()
            .putInt(APP_PREFERENCES_THEME.title, value).apply()
}


enum class EnumPreferences(val title: String) {
    APP_PREFERENCES("my_settings"),
    APP_PREFERENCES_START_HOURS("start_hours"),
    APP_PREFERENCES_START_MINUTES("start_minutes"),
    APP_PREFERENCES_END_HOURS("end_hours"),
    APP_PREFERENCES_END_MINUTES("end_minutes"),
    APP_PREFERENCES_IS_PASSWORD_REMEMBERED("is_password_remembered"),
    APP_PREFERENCES_EMAIL("login"),
    APP_PREFERENCES_THEME("appTheme")
}