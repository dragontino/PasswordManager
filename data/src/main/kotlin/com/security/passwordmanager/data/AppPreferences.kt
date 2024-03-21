package com.security.passwordmanager.data

import android.content.Context

class AppPreferences(context: Context) {
    private val preferences =
        context.getSharedPreferences(PrefNames.DefaultPreferencesName, Context.MODE_PRIVATE)


    var email: String
        get() = preferences.getString(PrefNames.Email, "") ?: ""
        set(value) = preferences.edit()
            .putString(PrefNames.Email, value)
            .apply()


    private object PrefNames {
        const val DefaultPreferencesName = "application_settings"
        const val Email = "email"
    }
}