package com.security.passwordmanager.domain.util


inline fun buildString(initString: String = "", builderAction: StringBuilder.() -> Unit) =
    StringBuilder(initString).apply(builderAction).toString()