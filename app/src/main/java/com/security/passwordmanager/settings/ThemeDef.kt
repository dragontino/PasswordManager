package com.security.passwordmanager.settings

enum class ThemeDef(val themeName : String) {
    LIGHT_THEME("Light"),
    DARK_THEME("Dark"),
    SYSTEM_THEME("System"),
    AUTO_THEME("Auto");

    companion object {
        // TODO: 28.01.2022 убрать это говно
        fun getTheme(themeName: String) = when(themeName) {
            "Light" -> LIGHT_THEME
            "Dark" -> DARK_THEME
            "Auto" -> AUTO_THEME
            else -> SYSTEM_THEME
        }

        fun getIndexTheme(theme : ThemeDef) = when (theme) {
            LIGHT_THEME -> 0
            DARK_THEME -> 1
            SYSTEM_THEME -> 2
            AUTO_THEME -> 3
        }

        fun getTheme(themeIndex : Int) = when (themeIndex) {
            0 -> LIGHT_THEME
            1 -> DARK_THEME
            2 -> SYSTEM_THEME
            3 -> AUTO_THEME
            else -> SYSTEM_THEME
        }
    }
}