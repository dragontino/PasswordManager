package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.RaspberryLight

@ExperimentalMaterial3Api
@Composable
fun DataTextField(
    text: String,
    heading: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingActions: @Composable (RowScope.() -> Unit) = {}
) {
    TextField(
        value = text,
        onValueChange = {},
        textStyle = MaterialTheme.typography.bodyMedium,
        label = {
            Text(
                text = heading,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                ),
            )
        },
        visualTransformation = visualTransformation,
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                trailingActions()
            }
        },
        readOnly = true,
        enabled = false,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground.animate(),
            disabledTextColor = MaterialTheme.colorScheme.onBackground.animate(),
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLabelColor = MaterialTheme.colorScheme.onBackground.animate(),
            disabledLabelColor = MaterialTheme.colorScheme.onBackground.animate(),
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.animate()
        ),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    )
}


@ExperimentalMaterial3Api
@Composable
fun EditableDataTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingActions: @Composable (RowScope.() -> Unit) = {},
    isError: Boolean = false,
    errorMessage: String = "",
    whenFocused: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var isFocused by rememberSaveable { mutableStateOf(false) }
    var showSupportedText by rememberSaveable { mutableStateOf(true) }

    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        label = {
            Text(
                text = hint,
                style = if (!isFocused && text.isEmpty())
                    MaterialTheme.typography.bodyMedium
                else MaterialTheme.typography.bodySmall,
            )
        },
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (isError) {
                IconButton(
                    onClick = { showSupportedText = !showSupportedText },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.error.animate()
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Error, contentDescription = "error")
                }

            } else {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    trailingActions()
                }
            }
        },
        supportingText = {
            AnimatedVisibility(
                visible = isError && showSupportedText,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error.animate(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Next) },
            onDone = { focusManager.clearFocus() }
        ),
        shape = MaterialTheme.shapes.small,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground.animate(),
            containerColor = if (isFocused) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.background.animate()
            },
            focusedBorderColor = RaspberryLight,
            unfocusedBorderColor = RaspberryLight.copy(alpha = 0.6f),
            focusedLabelColor = RaspberryLight,
            unfocusedLabelColor = if (text.isNotEmpty()) {
                RaspberryLight.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.animate()
            },
            errorLabelColor = if (text.isNotEmpty()) {
                MaterialTheme.colorScheme.error.animate()
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.animate()
            }
        ),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .onFocusChanged {
                if (it.isFocused) whenFocused()
                isFocused = it.isFocused
            }
            .fillMaxWidth()
    )
}


object TrailingActions {
    @Composable
    fun RowScope.VisibilityIconButton(
        visible: Boolean,
        modifier: Modifier = Modifier,
        changeVisibility: (visible: Boolean) -> Unit,
    ) {
        TrailingIconButton(
            icon = if (visible) Icons.Outlined.VisibilityOff
            else Icons.Outlined.Visibility,
            modifier = modifier,
            contentDescription = stringResource(R.string.show_password)
        ) {
           changeVisibility(!visible)
        }
    }

    @Composable
    fun RowScope.CopyIconButton(modifier: Modifier = Modifier, onClick: () -> Unit) =
        TrailingIconButton(
            icon = Icons.Outlined.ContentCopy,
            contentDescription = stringResource(R.string.copy_info),
            modifier = modifier,
            onClick = onClick
        )


    @Composable
    private fun RowScope.TrailingIconButton(
        icon: ImageVector,
        modifier: Modifier = Modifier,
        contentDescription: String? = null,
        onClick: () -> Unit
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = modifier
                .clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
                .align(Alignment.CenterVertically)
                .padding(horizontal = 2.dp, vertical = 4.dp)
                .scale(0.9f)
        )
    }
}


@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
private fun DataTextFieldPreview() {
    var visible by remember { mutableStateOf(false) }
    PasswordManagerTheme(isDarkTheme = true) {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface.animate())) {
            DataTextField(
                text = "Wrecked",
                heading = "Imagine Dragons",
                visualTransformation = if (visible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation()
            ) {
                VisibilityIconButton(visible) { visible = it }
                Spacer(modifier = Modifier.width(8.dp))
                CopyIconButton {}
            }

            EditableDataTextField(
                text = "Wrecked",
                onTextChange = {},
                hint = "Imagine Dragons",
                isError = true,
                errorMessage = "Error Message",
                trailingActions = {
                    VisibilityIconButton(visible) { visible = it }
                    Spacer(modifier = Modifier.width(8.dp))
                    CopyIconButton {}
                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        }
    }
}