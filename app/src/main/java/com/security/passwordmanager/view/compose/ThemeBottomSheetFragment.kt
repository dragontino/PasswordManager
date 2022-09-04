package com.security.passwordmanager.view.compose

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
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
import com.security.passwordmanager.settings.ThemeDef
import com.security.passwordmanager.viewmodel.SettingsViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

interface Theme {
    fun updateTheme()
}


class ThemeBottomSheetFragment(
    context: Context,
    private val theme: Theme,
    settingsViewModel: SettingsViewModel
) : BottomSheetFragment(settingsViewModel) {

    init {
        context.resources.getStringArray(R.array.themes).forEachIndexed { index, string ->

            addView(
                image = Icons.Default.RadioButtonChecked,
                text = string,
                imageBound = ImageBounds.RIGHT,
                showImage = index == settingsViewModel.indexTheme
            ) {
                settingsViewModel.theme = ThemeDef.values()[index]
                if (settingsViewModel.theme != ThemeDef.AUTO_THEME) dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: 29.08.2022 переделать
        if (settingsViewModel.theme == ThemeDef.AUTO_THEME)
            addView { Times(settingsViewModel.getTimes()) }
    }


    @Composable
    fun Times(times: Times) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.activity_horizontal_margin))
                .background(colorResource(R.color.app_background_color))
                .fillMaxWidth()
        ) {
            Divider(color = colorResource(android.R.color.darker_gray))

            Text(
                text = stringResource(R.string.changing_theme_time),
                fontSize = 18.sp,
                color = colorResource(R.color.text_color),
                fontFamily = MaterialTheme.typography.subtitle1.fontFamily,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.8f)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Time(times.startTime, true, 1f)
                Time(times.endTime, false, 1f)
            }
        }
    }


    @Composable
    private fun RowScope.Time(time: Time, isStart: Boolean, weight: Float) {

        val startTimePicker = timePicker(time = time, titleRes = R.string.start_time) {
            settingsViewModel.startTime = it.toCalendar()
        }

        val endTimePicker = timePicker(time = time, titleRes = R.string.end_time) {
            settingsViewModel.endTime = it.toCalendar()
        }

        OutlinedButton(
            onClick = {
                if (isStart) startTimePicker.show()
                else endTimePicker.show()
                theme.updateTheme()
            },
            border = BorderStroke(1.dp, colorResource(R.color.raspberry)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.text_view_corner)),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = colorResource(android.R.color.transparent),
                contentColor = colorResource(R.color.text_color),
                disabledContentColor = colorResource(R.color.text_color).copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.activity_horizontal_margin))
                .align(Alignment.CenterVertically)
                .weight(weight)
        ) {
            Text(
                text = time.toString(),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
    }


    @Composable
    private fun timePicker(time: Time, @StringRes titleRes: Int, onTimeChange: (Time) -> Unit): TimePicker {
        val dialogState = rememberMaterialDialogState()

        MaterialDialog(
            dialogState = dialogState,
            backgroundColor = colorResource(R.color.app_background_color),
            shape = RoundedCornerShape(dimensionResource(R.dimen.data_element_corner)),
            border = BorderStroke(1.dp, colorResource(R.color.text_color)),
            autoDismiss = true,
            buttons = {
                positiveButton(
                    text = stringResource(R.string.button_ok),
                    textStyle = TextStyle(color = colorResource(R.color.raspberry))
                )
                negativeButton(
                    text = stringResource(R.string.button_cancel),
                    textStyle = TextStyle(color = colorResource(R.color.raspberry))
                )
            }
        ) {
            timepicker(
                initialTime = time.toLocalTime(),
                title = stringResource(titleRes),
                is24HourClock = true,
                colors = TimePickerDefaults.colors(
                    headerTextColor = colorResource(R.color.text_color),
                    activeBackgroundColor = colorResource(R.color.app_background_color),
                    inactiveBackgroundColor = colorResource(R.color.app_background_color),
                    selectorColor = colorResource(R.color.raspberry),
                    selectorTextColor = colorResource(R.color.text_color),
                    activeTextColor = Color.White,
                    inactiveTextColor = colorResource(R.color.text_color)
                ),
            ) { localTime -> onTimeChange(localTime.toTime()) }
        }

        return TimePicker(dialogState)
    }



    private class TimePicker(private val dialogState: MaterialDialogState) {
        fun show() = dialogState.show()
    }
}



@Preview
@Composable
private fun TimePicker() {
    val dialogState = rememberMaterialDialogState()
    val title = "Время рассвета"

    val time = remember { mutableStateOf(Time(14, 29)) }

    MaterialDialog(
        dialogState = dialogState,
        backgroundColor = colorResource(R.color.app_background_color),
        shape = RoundedCornerShape(dimensionResource(R.dimen.data_element_corner)),
        border = BorderStroke(1.dp, colorResource(R.color.text_color)),
        autoDismiss = true,
        buttons = {
            positiveButton(
                text = stringResource(R.string.button_ok),
                textStyle = TextStyle(color = colorResource(R.color.raspberry))
            )
            negativeButton(
                text = stringResource(R.string.button_cancel),
                textStyle = TextStyle(color = colorResource(R.color.raspberry))
            )
        }
    ) {
        timepicker(
            initialTime = time.value.toLocalTime(),
            title = title,
            is24HourClock = true,
            colors = TimePickerDefaults.colors(
                headerTextColor = colorResource(R.color.text_color),
                activeBackgroundColor = colorResource(R.color.app_background_color),
                inactiveBackgroundColor = colorResource(R.color.app_background_color),
                inactivePeriodBackground = colorResource(R.color.data_background_color),
                selectorColor = colorResource(R.color.raspberry),
                selectorTextColor = colorResource(R.color.text_color),
                activeTextColor = Color.White,
                inactiveTextColor = colorResource(R.color.text_color)
            )
        ) { localTime ->
            time.value = localTime.toTime()
        }
    }

    Button(onClick = { dialogState.show() }) {
        Text(text = time.value.toString())
    }


}