@file:JvmName("TimesKt")

package com.security.passwordmanager.view.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
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



@Composable
fun Times(
    times: Times,
    updateTimes: (Times) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Divider(color = colorResource(android.R.color.darker_gray))

        Text(
            text = stringResource(R.string.changing_theme_time),
            fontSize = 18.sp,
            color = MaterialTheme.colors.onBackground,
            fontFamily = MaterialTheme.typography.subtitle1.fontFamily,
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

    TimePicker(dialogState, time, titleRes, onTimeChange)

    OutlinedButton(
        onClick = {
            dialogState.show()
        },
        border = BorderStroke(1.dp, colorResource(R.color.raspberry)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.text_view_corner)),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onBackground,
            disabledContentColor = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
        ),
        modifier = modifier
            .padding(dimensionResource(R.dimen.activity_horizontal_margin))
            .align(Alignment.CenterVertically)
    ) {
        Text(
            text = time.toString(),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
    }
}


@Composable
private fun TimePicker(
    dialogState: MaterialDialogState,
    time: Time,
    @StringRes titleRes: Int,
    onTimeChange: (Time) -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(dimensionResource(R.dimen.data_element_corner)),
        border = BorderStroke(1.dp, MaterialTheme.colors.onBackground),
        autoDismiss = true,
        buttons = {
            positiveButton(
                res = R.string.button_save,
                textStyle = TextStyle(
                    color = colorResource(R.color.raspberry),
                    fontFamily = MaterialTheme.typography.caption.fontFamily,
                    fontSize = 14.sp
                )
            )
            negativeButton(
                res = R.string.button_cancel,
                textStyle = TextStyle(
                    color = colorResource(R.color.raspberry),
                    fontFamily = MaterialTheme.typography.caption.fontFamily,
                    fontSize = 14.sp
                )
            )
        }
    ) {
        timepicker(
            initialTime = time.toLocalTime(),
            title = stringResource(titleRes),
            is24HourClock = true,
            colors = TimePickerDefaults.colors(
                headerTextColor = MaterialTheme.colors.onBackground,
                activeBackgroundColor = MaterialTheme.colors.background,
                inactiveBackgroundColor = MaterialTheme.colors.background,
                inactivePeriodBackground = MaterialTheme.colors.background,
                selectorColor = colorResource(R.color.raspberry),
                selectorTextColor = MaterialTheme.colors.onBackground,
                activeTextColor = Color.White,
                inactiveTextColor = MaterialTheme.colors.onBackground
            ),
        ) { localTime -> onTimeChange(localTime.toTime()) }
    }
}



@Preview
@Composable
private fun TimesPreview() {
    val times = remember { mutableStateOf(Times(Time(14, 29), Time(23, 1))) }

    Times(times.value) {
        times.value = it
    }
}
