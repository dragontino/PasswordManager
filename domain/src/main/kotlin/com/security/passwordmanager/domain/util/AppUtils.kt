package com.security.passwordmanager.domain.util

typealias Encrypt = (String) -> String


inline fun buildString(initString: String = "", builderAction: StringBuilder.() -> Unit) =
    StringBuilder(initString).apply(builderAction).toString()