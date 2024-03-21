package com.security.passwordmanager.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimePatterns {
    const val DatePattern = "dd/MM/yyyy"
}

fun String.parseToDate(pattern: String = DateTimePatterns.DatePattern): LocalDate {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return LocalDate.parse(this, formatter)
}


fun LocalDate.parseToString(pattern: String = DateTimePatterns.DatePattern): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}