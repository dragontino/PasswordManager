package com.security.passwordmanager.domain.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Time(val hours: Int, val minutes: Int) : Comparable<Time> {
    companion object {
        val defaultSunriseTime = Time(7, 0)

        val defaultSunsetTime = Time(23, 0)

        const val TIME_FORMAT_PATTERN = "HH:mm"
    }

    override fun compareTo(other: Time): Int {
        return this.asLocalTime().compareTo(other.asLocalTime())
    }

    override fun toString(): String = this.asLocalTime()
        .format(DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN))
}


fun Time.asLocalTime(): LocalTime {
    return LocalTime.of(hours, minutes)
}


fun LocalTime.asTime(): Time {
    return Time(hours = hour, minutes = minute)
}


fun Time(timeString: String): Time {
    val formatter = DateTimeFormatter.ofPattern(Time.TIME_FORMAT_PATTERN)
    return LocalTime.parse(timeString, formatter).asTime()
}
