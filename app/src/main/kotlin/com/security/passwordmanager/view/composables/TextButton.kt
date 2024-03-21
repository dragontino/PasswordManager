package com.security.passwordmanager.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.theme.PasswordManagerTheme

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.secondary,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = textColor.animate(),
            disabledContainerColor = Color.Transparent,
            disabledContentColor = textColor.copy(alpha = .5f).animate()
        ),
        shape = MaterialTheme.shapes.medium,
        enabled = enabled,
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = style
        )
    }
}


@Preview
@Composable
private fun TextButtonPreview() {
    var borderWidthFloat by remember { mutableDoubleStateOf(1.1) }

    PasswordManagerTheme(isDarkTheme = true) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.medium
                )
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            TextButton(
                text = "Resize border",
                modifier = Modifier.border(
                    width = borderWidthFloat.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onBackground
                        )
                    ),
                    shape = MaterialTheme.shapes.small
                )
            ) {
                borderWidthFloat += 0.1
            }

            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}