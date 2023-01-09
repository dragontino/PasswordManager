@file:JvmName("TimesKt")

package com.security.passwordmanager.presentation.model

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.RaspberryLight
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*


data class Time(val hours: Int, val minutes: Int) {
    companion object {
        val Undefined = Time(0, 0)
    }

    override fun toString(): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(toCalendar().time)
    }
}



data class Times(val startTime: Time, val endTime: Time) {
    companion object {
        val Undefined = Times(Time.Undefined, Time.Undefined)
    }
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



@Composable
fun Times(
    times: Times,
    updateTimes: (Times) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background.animate())
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Divider(color = colorResource(android.R.color.darker_gray))

        Text(
            text = stringResource(R.string.changing_theme_time),
            color = MaterialTheme.colorScheme.onBackground.animate(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(top = 4.dp)
                .alpha(0.8f)
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth()) {
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
    TimePicker(dialogState, time, stringResource(titleRes), onTimeChange)

    OutlinedButton(
        onClick = {
            dialogState.show()
        },
        border = BorderStroke(1.dp, RaspberryLight),
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


@Composable
private fun TimePicker(
    dialogState: MaterialDialogState,
    time: Time,
    title: String,
    onTimeChange: (Time) -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        backgroundColor = MaterialTheme.colorScheme.background.animate(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.animate()),
        autoDismiss = true,
        buttons = {
            positiveButton(
                res = R.string.button_save,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = RaspberryLight
                )
            )
            negativeButton(
                res = R.string.button_cancel,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = RaspberryLight
                )
            )
        }
    ) {
        timepicker(
            initialTime = time.toLocalTime(),
            title = title,
            is24HourClock = true,
            colors = TimePickerDefaults.colors(
                headerTextColor = MaterialTheme.colorScheme.onBackground.animate(),
                activeBackgroundColor = MaterialTheme.colorScheme.background.animate(),
                inactiveBackgroundColor = MaterialTheme.colorScheme.background.animate(),
                inactivePeriodBackground = MaterialTheme.colorScheme.background.animate(),
                selectorColor = RaspberryLight,
                selectorTextColor = MaterialTheme.colorScheme.onBackground.animate(),
                activeTextColor = Color.White,
                inactiveTextColor = MaterialTheme.colorScheme.onBackground.animate()
            ),
        ) { localTime -> onTimeChange(localTime.toTime()) }
    }
}



@Preview
@Composable
private fun TimesPreview() {
    var times by remember { mutableStateOf(Times(Time(14, 29), Time(23, 1))) }
    PasswordManagerTheme {
        Times(times) {
            times = it
        }
    }
}


@Preview
@Composable
private fun TimePickerPreview() {
    PasswordManagerTheme {
        TimePicker(
            dialogState = rememberMaterialDialogState(true),
            time = Time(18, 12),
            title = "Тестовое время",
            onTimeChange = {}
        )
    }
}
