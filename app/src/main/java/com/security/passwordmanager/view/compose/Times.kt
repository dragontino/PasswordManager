package com.security.passwordmanager.view.compose

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

data class Time(val hours: Int, val minutes: Int) {
    override fun toString(): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(toCalendar().time)
    }
}



data class Times(val startTime: Time, val endTime: Time) {
    constructor(startTime: Calendar, endTime: Calendar)
            : this(startTime.toTime(), endTime.toTime())
}



fun Time.toCalendar(): Calendar {
    val (hours, minutes) = this
    val calendar = GregorianCalendar()

    calendar[Calendar.HOUR_OF_DAY] = hours
    calendar[Calendar.MINUTE] = minutes
    return calendar
}



fun Calendar.toTime(): Time {
    val hours = this[Calendar.HOUR_OF_DAY]
    val minutes = this[Calendar.MINUTE]
    return Time(hours, minutes)
}



fun Time.toLocalTime(): LocalTime =
    LocalTime.of(hours, minutes)



fun LocalTime.toTime(): Time =
    Time(hours = hour, minutes = minute)