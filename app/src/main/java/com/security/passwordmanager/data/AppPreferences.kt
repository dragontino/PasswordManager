package com.security.passwordmanager.data

import android.app.Application
import android.content.Context
import androidx.annotation.StyleRes
import com.security.passwordmanager.R
import com.security.passwordmanager.data.AppPreferences.EnumPreferences.*
import com.security.passwordmanager.presentation.model.Time

class AppPreferences(application: Application) {
    private val preferences =
        application.getSharedPreferences(PREFERENCES_NAME.title, Context.MODE_PRIVATE)


    var startTime: Time
        get() {
            val hours = preferences.getInt(START_HOURS.title, 7)
            val minutes = preferences.getInt(START_MINUTES.title, 0)
            return Time(hours, minutes)
        }
        set(value) = preferences.edit().apply {
            putInt(START_HOURS.title, value.hours)
            putInt(START_MINUTES.title, value.minutes)
        }.apply()


    var endTime: Time
        get() {
            val hours = preferences.getInt(END_HOURS.title, 23)
            val minutes = preferences.getInt(END_MINUTES.title, 0)
            return Time(hours, minutes)
        }
        set(value) = preferences.edit().apply {
            putInt(END_HOURS.title, value.hours)
            putInt(END_MINUTES.title, value.minutes)
        }.apply()


    var email: String
        get() = preferences.getString(EMAIL.title, "") ?: ""
        set(value) = preferences.edit()
            .putString(EMAIL.title, value)
            .apply()


    var username: String
        get() = preferences.getString(USERNAME.title, "") ?: ""
        set(value) = preferences.edit()
            .putString(USERNAME.title, value)
            .apply()


    var theme: Int
        get(): @StyleRes Int = preferences
            .getInt(THEME.title, R.style.Theme_PasswordManager)
        set(@StyleRes value) = preferences.edit()
            .putInt(THEME.title, value).apply()


    private enum class EnumPreferences(val title: String) {
        PREFERENCES_NAME("application_settings"),
        START_HOURS("start_hours"),
        START_MINUTES("start_minutes"),
        END_HOURS("end_hours"),
        END_MINUTES("end_minutes"),
        EMAIL("email"),
        USERNAME("username"),
        THEME("appTheme")
    }
}