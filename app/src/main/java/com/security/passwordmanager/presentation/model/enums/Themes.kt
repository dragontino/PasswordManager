package com.security.passwordmanager.presentation.model.enums

import androidx.annotation.StringRes
import com.security.passwordmanager.R

enum class Themes(@StringRes val titleRes: Int) {
    Light(R.string.light_theme),
    Dark(R.string.dark_theme),
    System(R.string.system_theme),
    Auto(R.string.auto_theme);
}