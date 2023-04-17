package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.model.Time
import com.security.passwordmanager.presentation.model.toLocalTime
import com.security.passwordmanager.presentation.model.toTime
import com.security.passwordmanager.presentation.view.navigation.ModalSheetDefaults
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.bottomSheetBorderThickness
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


@ExperimentalMaterial3Api
@Composable
fun TimePickerDialog(
    dialogState: MaterialDialogState,
    time: Time,
    title: String,
    onTimeChange: (Time) -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        backgroundColor = MaterialTheme.colorScheme.background.animate(),
        shape = MaterialTheme.shapes.large,
        border = ModalSheetDefaults.borderStroke(),
        autoDismiss = true,
        buttons = {
            positiveButton(
                res = R.string.save,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary.animate()
                )
            )
            negativeButton(
                res = R.string.cancel,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary.animate()
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
                selectorColor = MaterialTheme.colorScheme.primary.animate(),
                selectorTextColor = MaterialTheme.colorScheme.onPrimary.animate(),
                activeTextColor = MaterialTheme.colorScheme.secondary.animate(),
                inactiveTextColor = MaterialTheme.colorScheme.onBackground.animate()
            ),
        ) { localTime -> onTimeChange(localTime.toTime()) }
    }
}



@Composable
fun ExitDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onClose: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            DialogButton(text = stringResource(R.string.save)) {
                onConfirm()
                onClose()
            }
        },
        dismissButton = {
            DialogButton(text = stringResource(R.string.do_not_save)) {
                onDismiss()
                onClose()
            }
        },
        text = {
            Text(
                text = stringResource(R.string.unsaved_data),
                style = MaterialTheme.typography.labelMedium.copy(
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



@Composable
fun DeleteDialog(text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            DialogButton(
                text = stringResource(R.string.delete_data),
                onClick = onConfirm
            )
        },
        dismissButton = {
            DialogButton(
                text = stringResource(R.string.save),
                onClick = onDismiss
            )
        },
        title = {
            Text(
                text = stringResource(R.string.confirm_delete),
                style = MaterialTheme.typography.labelLarge
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(lineHeight = 35.sp),
            )
        },
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.background.animate(),
        titleContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        modifier = Modifier.border(
            width = bottomSheetBorderThickness,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            shape = MaterialTheme.shapes.medium
        )
    )
}



@ExperimentalMaterial3Api
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
            DialogButton(
                text = stringResource(R.string.save),
                onClick = onSave
            )
        },
        dismissButton = {
            DialogButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss
            )
        },
        title = {
            Text(
                text = stringResource(R.string.username),
                style = MaterialTheme.typography.titleLarge
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



@Composable
private fun DialogButton(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = textColor.animate(),
            containerColor = Color.Transparent
        ),
        modifier = modifier
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}






@ExperimentalMaterial3Api
@Preview
@Composable
private fun TimePickerDialogPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        TimePickerDialog(
            dialogState = rememberMaterialDialogState(true),
            time = Time(18, 12),
            title = "Тестовое время",
            onTimeChange = {}
        )
    }
}



@ExperimentalMaterial3Api
@Preview
@Composable
private fun RenameDialogPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        var text by remember { mutableStateOf("Imagine Dragons") }

        RenameDialog(
            text = text,
            onTextChange = { text = it },
            onSave = {},
            onDismiss = {}
        )
    }
}
