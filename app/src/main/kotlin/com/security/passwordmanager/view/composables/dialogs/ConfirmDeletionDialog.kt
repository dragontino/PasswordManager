package com.security.passwordmanager.view.composables.dialogs

import androidx.compose.foundation.border
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.composables.TextButton
import com.security.passwordmanager.view.theme.bottomSheetBorderThickness

class ConfirmDeletionDialog(
    private val text: String,
    private val onConfirm: () -> Unit,
    private val onDismiss: () -> Unit
) : DialogType {
    @Composable
    override fun Dialog() {
        val onConfirmState = rememberUpdatedState(onConfirm)
        val onDismissState = rememberUpdatedState(onDismiss)

        AlertDialog(
            onDismissRequest = onDismissState.value,
            confirmButton = {
                TextButton(
                    text = stringResource(R.string.delete_data),
                    textColor = MaterialTheme.colorScheme.primary,
                    onClick = onConfirmState.value
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(R.string.save),
                    textColor = MaterialTheme.colorScheme.primary,
                    onClick = onDismissState.value
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

}