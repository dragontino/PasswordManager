package com.security.passwordmanager.view.composables

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.security.passwordmanager.R
import com.security.passwordmanager.domain.model.Time
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.theme.bottomSheetBorderThickness


@Deprecated("Use TimePickerDialog from dialogs package")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    time: Time,
    title: String,
    onConfirm: (newTime: Time) -> Unit,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current

    val timePickerState = rememberTimePickerState(
        initialHour = time.hours,
        initialMinute = time.minutes,
        is24Hour = true
    )

    var dialogState by rememberSaveable {
        if (configuration.screenHeightDp >= 590) {
            mutableStateOf(TimeDialogState.TimePicker)
        } else {
            mutableStateOf(TimeDialogState.TimeInput)
        }
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

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
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
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
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
                        onClick = onDismiss
                    )

                    Spacer(Modifier.width(8.dp))

                    TextButton(
                        text = stringResource(android.R.string.ok),
                        textColor = MaterialTheme.colorScheme.onBackground,
                        onClick = { onConfirm(timePickerState.toTime()) }
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


@Deprecated("Use ConfirmExitDialog from DialogType")
@Composable
fun ExitDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onClose: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                text = stringResource(R.string.save),
                textColor = MaterialTheme.colorScheme.primary
            ) {
                onConfirm()
                onClose()
            }
        },
        dismissButton = {
            TextButton(
                text = stringResource(R.string.do_not_save),
                textColor = MaterialTheme.colorScheme.primary
            ) {
                onDismiss()
                onClose()
            }
        },
        text = {
            Text(
                text = stringResource(R.string.unsaved_data),
                style = MaterialTheme.typography.titleMedium.copy(
                    textAlign = TextAlign.Center,
                    lineHeight = 35.sp
                )
            )
        },
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.background.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        modifier = Modifier.border(
            width = bottomSheetBorderThickness,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            shape = MaterialTheme.shapes.medium
        )
    )
}


@Deprecated("Use confirmDeletionDialog from dialogs package")
@Composable
fun DeleteDialog(text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                text = stringResource(R.string.delete_data),
                textColor = MaterialTheme.colorScheme.primary,
                onClick = onConfirm
            )
        },
        dismissButton = {
            TextButton(
                text = stringResource(R.string.save),
                textColor = MaterialTheme.colorScheme.primary,
                onClick = onDismiss
            )
        },
        title = {
            Text(
                text = stringResource(R.string.confirm_delete),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(lineHeight = 35.sp),
            )
        },
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.surface.animate(),
        titleContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        modifier = Modifier.border(
            width = bottomSheetBorderThickness,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            shape = MaterialTheme.shapes.medium
        )
    )
}


@Deprecated("Use RenameDialog from dialogs package")
@Composable
fun RenameDialog(
    text: String,
    onTextChange: (text: String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                text = stringResource(R.string.save),
                textColor = MaterialTheme.colorScheme.primary, onClick = onSave
            )
        },
        dismissButton = {
            TextButton(
                text = stringResource(R.string.cancel_action),
                textColor = MaterialTheme.colorScheme.primary, onClick = onDismiss
            )
        },
        title = {
            Text(
                text = stringResource(R.string.username),
                style = MaterialTheme.typography.headlineLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                val containerColor = MaterialTheme.colorScheme.background.animate()
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground.animate(),
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        disabledContainerColor = containerColor,
                        focusedBorderColor = MaterialTheme.colorScheme.primary.animate(),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.animate(),
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = .8f
                        ),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = .4f
                        )
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.changing_username_placeholder),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    keyboardActions = KeyboardActions {
                        defaultKeyboardAction(ImeAction.Done)
                        focusRequester.freeFocus()
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    shape = MaterialTheme.shapes.small,
                    singleLine = true,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                )
            }
        },
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.background.animate(),
        titleContentColor = MaterialTheme.colorScheme.onBackground.animate()
    )
}


@Deprecated("Use ConfirmChangeDialog from dialogs package")
@Composable
fun ConfirmChangeDialog(
    text: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.confirm_action),
                style = MaterialTheme.typography.titleLarge
            )
        },
        confirmButton = {
            TextButton(
                text = confirmButtonText,
                textColor = MaterialTheme.colorScheme.primary,
                onClick = onConfirm
            )
        },
        dismissButton = {
            TextButton(
                text = stringResource(R.string.cancel_action),
                textColor = MaterialTheme.colorScheme.primary,
                onClick = onDismiss
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(lineHeight = 35.sp),
            )
        },
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.surface.animate(),
        titleContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        modifier = Modifier.border(
            width = bottomSheetBorderThickness,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            shape = MaterialTheme.shapes.medium
        )
    )
}
