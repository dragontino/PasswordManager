package com.security.passwordmanager.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

data class Time(val hours: Int, val minutes: Int) {
    companion object {
        val defaultSunriseTime = Time(7, 0)

        val defaultSunsetTime = Time(23, 0)

        const val dateFormatPattern = "HH:mm"
    }

    override fun toString(): String = SimpleDateFormat(
        dateFormatPattern,
        Locale.getDefault()
    ).format(toCalendar().time)
}


fun Time.toCalendar(): Calendar {
    val (hours, minutes) = this
    return GregorianCalendar().apply {
        this[Calendar.HOUR_OF_DAY] = hours
        this[Calendar.MINUTE] = minutes
    }
}


fun Calendar.toTime(): Time {
    val hours = this[Calendar.HOUR_OF_DAY]
    val minutes = this[Calendar.MINUTE]
    return Time(hours, minutes)
}


fun Time(timeString: String): Time {
    val calendar = GregorianCalendar()
    val dateFormat = SimpleDateFormat(
        Time.dateFormatPattern,
        Locale.getDefault()
    )

    dateFormat.parse(timeString)?.let {
        calendar.time = it
    }

    return calendar.toTime()
}
