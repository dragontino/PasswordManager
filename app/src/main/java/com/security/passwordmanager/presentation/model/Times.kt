@file:JvmName("TimesKt")

package com.security.passwordmanager.presentation.model

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.composablelements.TimePickerDialog
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*


data class Time(val hours: Int = 0, val minutes: Int = 0) {
    override fun toString(): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(toCalendar().time)
    }
}



data class Times(
    val startTime: Time = Time(),
    val endTime: Time = Time()
)



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



@Composable
fun ColumnScope.Times(
    times: Times,
    updateTimes: (Times) -> Unit
) {
    Divider()

    Text(
        text = stringResource(R.string.changing_theme_time),
        color = MaterialTheme.colorScheme.onBackground.animate(),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(top = 4.dp)
            .alpha(0.8f)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    )

    Row(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(),
    ) {
        Time(
            time = times.startTime,
            titleRes = R.string.start_time,
            modifier = Modifier.weight(1f)
        ) {
            updateTimes(times.copy(startTime = it))
        }
        Time(
            time = times.endTime,
            titleRes = R.string.end_time,
            modifier = Modifier.weight(1f)
        ) {
            updateTimes(times.copy(endTime = it))
        }
    }
}


@Composable
private fun RowScope.Time(
    time: Time,
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    onTimeChange: (Time) -> Unit
) {
    val dialogState = rememberMaterialDialogState()

    // TODO: 31.10.2022 переделать на mt timePicker 
    TimePickerDialog(dialogState, time, stringResource(titleRes), onTimeChange)

    OutlinedButton(
        onClick = {
            dialogState.show()
        },
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.primaryContainer
                )
            )
        ),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground.animate(),
            disabledContentColor = MaterialTheme.colorScheme
                .onBackground
                .copy(alpha = 0.7f)
                .animate()
        ),
        modifier = modifier
            .padding(16.dp)
            .align(Alignment.CenterVertically)
    ) {
        Text(
            text = time.toString(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}



@Preview
@Composable
private fun TimesPreview() {
    var times by remember { mutableStateOf(Times(Time(14, 29), Time(23, 1))) }
    PasswordManagerTheme(isDarkTheme = true) {
        Column {
            Times(times) {
                times = it
            }
        }
    }
}
