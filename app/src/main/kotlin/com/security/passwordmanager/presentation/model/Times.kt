@file:JvmName("TimesKt")

package com.security.passwordmanager.presentation.model

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.composables.TimePickerDialog
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import java.text.SimpleDateFormat
import java.util.*


data class Time(val hours: Int = 0, val minutes: Int = 0) {
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


fun TimePickerState.toTime(): Time =
    Time(hours = hour, minutes = minute)


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


@Composable
fun RowScope.Time(
    time: Time,
    title: String,
    modifier: Modifier = Modifier,
    onTimeChange: (Time) -> Unit
) {
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    if (showTimePicker) {
        TimePickerDialog(
            time = time,
            title = title,
            onConfirm = { newTime ->
                onTimeChange(newTime)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .align(Alignment.CenterVertically)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            style = MaterialTheme.typography.titleSmall,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(8.dp))

        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable { showTimePicker = true }
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primaryContainer
                        ).map { it.animate() }
                    ),
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            val timeString = time.toString()

            repeat(timeString.length) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .border(
                            width = 0.2.dp,
                            color = MaterialTheme.colorScheme.onBackground
                                .copy(alpha = 0.3f)
                                .animate()
                        )
                        .padding(7.dp)
                ) {
                    Text(
                        text = timeString[index].toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.animate()
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun TimesPreview() {
    PasswordManagerTheme(isDarkTheme = false, dynamicColor = false) {
        var sunriseTime by remember { mutableStateOf(Time(14, 29)) }
        var sunsetTime by remember { mutableStateOf(Time(23, 1)) }

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
        ) {
            Time(
                time = sunriseTime,
                title = stringResource(R.string.sunrise_time),
                modifier = Modifier.weight(1f),
                onTimeChange = { sunriseTime = it }
            )
            Time(
                time = sunsetTime,
                title = stringResource(R.string.sunset_time),
                modifier = Modifier.weight(1f),
                onTimeChange = { sunsetTime = it }
            )
        }
    }
}
