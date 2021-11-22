package com.security.passwordmanager.data

import java.io.Serializable
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


private object Patterns {
    const val formatPattern = "ddMMWyyHHmmsS"

    val formatter: DateTimeFormatter
        get() =
            DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault())
}


data class UID internal constructor(private val id: String) : Comparable<UID>, Serializable {

    companion object {
        fun generateFromDate(clock: Clock = Clock.systemDefaultZone()): UID {
            val now = LocalDateTime.now(clock)
            return UID(id = now.format(Patterns.formatter))
        }
    }


    private fun toLocalDateTime(): LocalDateTime =
        LocalDateTime.parse(id, Patterns.formatter)


    override fun compareTo(other: UID) =
        toLocalDateTime().compareTo(other.toLocalDateTime())


    override fun toString() = id
}


fun UID(clock: Clock = Clock.systemDefaultZone()) =
    UID.generateFromDate(clock)


fun String.toUID() = UID(this)