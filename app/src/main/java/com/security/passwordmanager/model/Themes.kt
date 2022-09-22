package com.security.passwordmanager.model

import androidx.annotation.StringRes
import com.security.passwordmanager.R

enum class Themes(val themeName : String, @StringRes val titleRes: Int) {
    LIGHT_THEME("Light", R.string.light_theme),
    DARK_THEME("Dark", R.string.dark_theme),
    SYSTEM_THEME("System", R.string.system_theme),
    AUTO_THEME("Auto", R.string.auto_theme);
}


internal fun String?.toThemes(): Themes = Themes.values()
    .find { it.themeName == this }
    ?: Themes.LIGHT_THEME