package com.security.passwordmanager.settings

enum class EnumPreferences(val title: String) {
    APP_PREFERENCES("my_settings"),
    APP_PREFERENCES_START_HOURS("start_hours"),
    APP_PREFERENCES_START_MINUTES("start_minutes"),
    APP_PREFERENCES_END_HOURS("end_hours"),
    APP_PREFERENCES_END_MINUTES("end_minutes"),
    APP_PREFERENCES_IS_PASSWORD_REMEMBERED("is_password_remembered"),
    APP_PREFERENCES_LOGIN("login")
}