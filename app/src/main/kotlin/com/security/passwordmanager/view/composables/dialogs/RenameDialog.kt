package com.security.passwordmanager.view.composables.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.composables.TextButton
import com.security.passwordmanager.view.theme.PasswordManagerTheme

class RenameDialog(
    private val text: () -> String,
    private val onTextChange: (text: String) -> Unit,
    private val onSave: () -> Unit,
    private val onDismiss: () -> Unit,
    private val onClose: () -> Unit
) : DialogType {
    @Composable
    override fun Dialog() {
        val focusRequester = remember(::FocusRequester)
        val textState = rememberUpdatedState(text)
        val onTextChangeState = rememberUpdatedState(onTextChange)
        val onSaveState = rememberUpdatedState(onSave)
        val onDismissState = rememberUpdatedState(onDismiss)
        val onCloseState = rememberUpdatedState(onClose)

        AlertDialog(
            onDismissRequest = onDismissState.value,
            confirmButton = {
                TextButton(
                    text = stringResource(R.string.save),
                    textColor = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.onBackground,
                            blurRadius = .01f
                        )
                    ),
                    onClick = {
                        onSaveState.value.invoke()
                        onCloseState.value.invoke()
                    }
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(R.string.cancel),
                    textColor = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.onBackground,
                            blurRadius = .1f
                        )
                    ),
                    onClick = {
                        onDismissState.value.invoke()
                        onCloseState.value.invoke()
                    }
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
                        value = textState.value.invoke(),
                        onValueChange = onTextChangeState.value::invoke,
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
}



@Preview
@Composable
private fun RenameDialogPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        var text by remember { mutableStateOf("") }

        RenameDialog(
            text = { text },
            onTextChange = { text = it },
            onSave = {},
            onDismiss = {},
            onClose = {}
        ).Dialog()
    }
}