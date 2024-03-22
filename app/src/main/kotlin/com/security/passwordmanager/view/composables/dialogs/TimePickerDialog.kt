package com.security.passwordmanager.view.composables.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.Time
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.composables.TextButton
import com.security.passwordmanager.view.composables.toTime
import com.security.passwordmanager.view.theme.PasswordManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
class TimePickerDialog(
    private val time: Time,
    private val title: String,
    private val onConfirm: (newTime: Time) -> Unit,
    private val onClose: () -> Unit
) : DialogType {
    @Composable
    override fun Dialog() {
        val timeState = rememberUpdatedState(time)
        val titleState = rememberUpdatedState(title)
        val onConfirmState = rememberUpdatedState(onConfirm)
        val onCloseState = rememberUpdatedState(onClose)


        val configuration = LocalConfiguration.current

        val timePickerState = rememberTimePickerState(
            initialHour = timeState.value.hours,
            initialMinute = timeState.value.minutes,
            is24Hour = true
        )

        var dialogState by rememberSaveable {
            mutableStateOf(
                when {
                    configuration.screenHeightDp >= 590 -> TimeDialogState.TimePicker
                    else -> TimeDialogState.TimeInput
                }
            )
        }

        val shape = MaterialTheme.shapes.medium
        val colors = TimePickerDefaults.colors(
            clockDialColor = MaterialTheme.colorScheme.background.animate(),
            clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
            clockDialUnselectedContentColor = MaterialTheme.colorScheme.onBackground.animate(),
            selectorColor = MaterialTheme.colorScheme.secondary.animate(),
            containerColor = MaterialTheme.colorScheme.surface.animate(),
            periodSelectorBorderColor = MaterialTheme.colorScheme.onBackground.animate(),
            periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.tertiary.animate(),
            periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onTertiary.animate(),
            periodSelectorUnselectedContainerColor = Color.Transparent,
            periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface.animate(),
            timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer.animate(),
            timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer.animate(),
            timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant.animate(),
            timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.animate()
        )

        BasicAlertDialog(
            onDismissRequest = onCloseState.value,
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .clip(shape)
                .border(
                    width = 1.6.dp,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.animate(),
                            MaterialTheme.colorScheme.primaryContainer.animate(),
                            MaterialTheme.colorScheme.surface.animate()
                        ),
                        tileMode = TileMode.Mirror
                    ),
                    shape = shape
                )
                .background(
                    color = MaterialTheme.colorScheme.surface.animate(),
                    shape = shape
                ),
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = titleState.value,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground.animate()
                )
                Spacer(modifier = Modifier.height(20.dp))

                when (dialogState) {
                    TimeDialogState.TimePicker -> TimePicker(timePickerState, colors = colors)
                    TimeDialogState.TimeInput -> TimeInput(timePickerState, colors = colors)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(Modifier.fillMaxWidth()) {
                    if (configuration.screenHeightDp >= 590) {
                        IconButton(
                            onClick = { dialogState = dialogState.switch() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onBackground.animate()
                            ),
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = when (dialogState) {
                                    TimeDialogState.TimePicker -> Icons.Outlined.Keyboard
                                    else -> Icons.Rounded.Schedule
                                },
                                contentDescription = "keyboard",
                                modifier = Modifier.scale(1.2f)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        TextButton(
                            text = stringResource(R.string.cancel),
                            textColor = MaterialTheme.colorScheme.onBackground,
                            onClick = onCloseState.value
                        )

                        Spacer(Modifier.width(8.dp))

                        TextButton(
                            text = stringResource(android.R.string.ok),
                            textColor = MaterialTheme.colorScheme.onBackground,
                            onClick = {
                                onConfirmState.value.invoke(timePickerState.toTime())
                                onCloseState.value.invoke()
                            }
                        )
                    }
                }
            }
        }
    }


    private enum class TimeDialogState {
        TimePicker,
        TimeInput;

        fun switch(): TimeDialogState = when (this) {
            TimePicker -> TimeInput
            TimeInput -> TimePicker
        }
    }
}


@Preview
@Composable
private fun TimePickerDialogPreview() {
    var time by remember { mutableStateOf(Time(18, 12)) }

    PasswordManagerTheme(isDarkTheme = true) {
        Column {
            TimePickerDialog(
                time = time,
                title = "Test time",
                onConfirm = { time = it },
                onClose = {}
            ).Dialog()
        }
    }
}