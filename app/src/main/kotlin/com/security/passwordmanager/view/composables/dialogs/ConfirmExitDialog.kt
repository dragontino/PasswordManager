package com.security.passwordmanager.view.composables.dialogs

import androidx.compose.foundation.border
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.composables.TextButton
import com.security.passwordmanager.view.theme.bottomSheetBorderThickness

class ConfirmExitDialog(
    private val onConfirm: () -> Unit,
    private val onDismiss: () -> Unit,
    private val onClose: () -> Unit,
) : DialogType {
    @Composable
    override fun Dialog() {
        val onConfirmState = rememberUpdatedState(onConfirm)
        val onDismissState = rememberUpdatedState(onDismiss)
        val onCloseState = rememberUpdatedState(onClose)

        AlertDialog(
            onDismissRequest = onClose,
            confirmButton = {
                TextButton(
                    text = stringResource(R.string.save),
                    textColor = MaterialTheme.colorScheme.primary
                ) {
                    onConfirmState.value.invoke()
                    onCloseState.value.invoke()
                }
            },
            dismissButton = {
                TextButton(
                    text = stringResource(R.string.do_not_save),
                    textColor = MaterialTheme.colorScheme.primary
                ) {
                    onDismissState.value.invoke()
                    onCloseState.value.invoke()
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
}