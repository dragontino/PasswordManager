package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.theme.RaspberryLight
import com.security.passwordmanager.presentation.view.theme.bottomSheetBorderThickness

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
                onClick = {
                    onConfirm()
                    onClose()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = RaspberryLight,
                    containerColor = Color.Transparent
                )
            ) {
                Text(text = "Сохранить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onClose()
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = RaspberryLight
                )
            ) {
                Text(text = "Не сохранять", style = MaterialTheme.typography.bodyMedium)
            }
        },
        text = {
            Text(
                text = "У вас есть несохранённые данные. Сохранить?",
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
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = RaspberryLight,
                    containerColor = Color.Transparent
                )
            ) {
                Text("Удалить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = RaspberryLight
                )
            ) {
                Text("Сохранить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        title = {
            Text(
                text = "Подтверждение удаления",
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
