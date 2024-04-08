package com.security.passwordmanager.domain.model

import androidx.annotation.StringRes
import com.security.passwordmanager.domain.R

enum class ColorScheme(@StringRes val titleRes: Int) {
    Light(R.string.light_scheme),
    Dark(R.string.dark_scheme),
    System(R.string.system_scheme),
    Auto(R.string.auto_scheme);
}