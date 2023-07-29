package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme

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
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground.animate(),
            disabledTextColor = MaterialTheme.colorScheme.onBackground.animate(),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLabelColor = MaterialTheme.colorScheme.onBackground.animate(),
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.animate(),
            disabledLabelColor = MaterialTheme.colorScheme.onBackground.animate(),
        ),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    )
}


@ExperimentalMaterial3Api
@Composable
internal fun EditableDataTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    error: Boolean = false,
    errorMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeActionClick: KeyboardActionScope.(text: String) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingActions: @Composable (RowScope.() -> Unit) = {},
    shape: Shape = MaterialTheme.shapes.medium,
    borderColors: Collection<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.onBackground
    )
) {
    val focusManager = LocalFocusManager.current
    var showSupportingText by rememberSaveable { mutableStateOf(true) }

    val keyboard = Keyboard(
        type = keyboardType,
        imeAction = imeAction,
        autoCorrect = false
    ) {
        onImeActionClick(text)
        when (imeAction) {
            ImeAction.Next -> focusManager.moveFocus(FocusDirection.Down)
            ImeAction.Done -> focusManager.clearFocus()
        }
    }


    val borderBrush = when {
        error -> SolidColor(MaterialTheme.colorScheme.error.animate())
        readOnly || !enabled -> SolidColor(MaterialTheme.colorScheme.onBackground.animate())
        else -> Brush.horizontalGradient(
            colors = borderColors.map { it.animate() }
        )
    }
    val cursorBrush = when {
        error || readOnly || !enabled -> borderBrush
        else -> Brush.verticalGradient(
            colors = borderColors.map { it.animate() },
            tileMode = TileMode.Mirror
        )
    }

    val interactionSource = remember { MutableInteractionSource() }

    val textColor = MaterialTheme.colorScheme.onBackground
    val unfocusedTextColor = textColor.copy(alpha = 0.7f)
    val containerColor = Color.Transparent



    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        readOnly = readOnly,
        singleLine = true,
        enabled = enabled,
        cursorBrush = cursorBrush,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = textColor.animate()
        ),
        keyboardOptions = keyboard.options,
        keyboardActions = keyboard.actions,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = text,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = true,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                isError = error,
                label = {
                    Text(
                        text = label,
                        style = when {
                            text.isEmpty() -> MaterialTheme.typography.bodyMedium
                            else -> MaterialTheme.typography.bodySmall
                        },
                        maxLines = 1,
                        color = when {
                            error -> MaterialTheme.colorScheme.error
                            readOnly -> textColor
                            text.isEmpty() -> unfocusedTextColor
                            else -> MaterialTheme.colorScheme.primary
                        }.animate()
                    )
                },
                supportingText = {
                    AnimatedVisibility(
                        visible = error && errorMessage.isNotEmpty() && showSupportingText,
                        enter = expandVertically(
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = LinearEasing
                            )
                        ),
                        exit = shrinkVertically(
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = LinearEasing
                            )
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                leadingIcon = leadingIcon,
                trailingIcon = {
                    if (error) {
                        IconButton(
                            onClick = { showSupportingText = !showSupportingText },
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
                contentPadding = TextFieldDefaults.contentPaddingWithLabel(
                    top = 16.dp,
                    bottom = 16.dp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor.animate(),
                    unfocusedTextColor = unfocusedTextColor.animate(),
                    disabledTextColor = textColor.animate(),
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    errorContainerColor = containerColor,
//                    focusedBorderColor = MaterialTheme.colorScheme.primary.animate(),
//                    unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer
//                        .copy(alpha = 0.8f)
//                        .animate(),
//                    disabledBorderColor = MaterialTheme.colorScheme.primary
//                        .copy(alpha = 0.8f)
//                        .animate(),
//                    focusedLabelColor = MaterialTheme.colorScheme.secondary.animate(),
//                    unfocusedLabelColor = when {
//                        text.isNotEmpty() -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
//                        else -> MaterialTheme.colorScheme.onSurfaceVariant
//                    }.animate(),
//                    errorLabelColor = when {
//                        text.isNotEmpty() -> MaterialTheme.colorScheme.error
//                        else -> MaterialTheme.colorScheme.onSurfaceVariant
//                    }.animate(),
                    focusedLeadingIconColor = textColor.animate(),
                    unfocusedLeadingIconColor = unfocusedTextColor.animate(),
                    disabledLeadingIconColor = textColor.animate(),
                    errorLeadingIconColor = MaterialTheme.colorScheme.error.animate(),
                    focusedSupportingTextColor = MaterialTheme.colorScheme.error.animate(),
                    unfocusedSupportingTextColor = MaterialTheme.colorScheme.error.animate(),
                    disabledSupportingTextColor = MaterialTheme.colorScheme.error.animate(),
                ),
                container = {
                    val borderStroke = animateBorderStrokeAsState(
                        borderBrush = borderBrush,
                        enabled = enabled,
                        interactionSource = interactionSource,
                        focusedBorderThickness = 2.5.dp,
                        unfocusedBorderThickness = 1.dp
                    )

                    Box(
                        modifier = Modifier
                            .border(borderStroke.value, shape)
                            .background(
                                color = containerColor,
                                shape = shape
                            )
                    )
                }
            )
        },
//        keyboardActions = KeyboardActions(
//            onNext = {
//                onImeActionClick(text)
//                focusManager.moveFocus(FocusDirection.Next)
//            },
//            onDone = {
//                onImeActionClick(text)
//                focusManager.clearFocus()
//            }
//        ),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
//            .onFocusChanged {
//                if (it.isFocused) whenFocused()
//                isFocused = it.isFocused
//            }
            .fillMaxWidth()
    )
}





internal class Keyboard private constructor(
    val options: KeyboardOptions = KeyboardOptions(),
    private val onImeActionClick: (KeyboardActionScope.() -> Unit)? = null
) { constructor(
        type: KeyboardType = KeyboardType.Text,
        imeAction: ImeAction = ImeAction.Next,
        capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
        autoCorrect: Boolean = true,
        onImeActionClick: (KeyboardActionScope.() -> Unit)? = null
    ) : this(
        options = KeyboardOptions(
            capitalization,
            autoCorrect,
            type,
            imeAction
        ),
        onImeActionClick = onImeActionClick
    )


    val actions = KeyboardActions(
        onDone = { onImeClick(ImeAction.Done) },
        onGo = { onImeClick(ImeAction.Go) },
        onNext = { onImeClick(ImeAction.Next) },
        onPrevious = { onImeClick(ImeAction.Previous) },
        onSearch = { onImeClick(ImeAction.Search) },
        onSend = { onImeClick(ImeAction.Send) }
    )



    private fun KeyboardActionScope.onImeClick(imeAction: ImeAction) =
        when (options.imeAction) {
            imeAction -> onImeActionClick?.invoke(this) ?: defaultKeyboardAction(imeAction)
            else -> defaultKeyboardAction(imeAction)
        }
}





object TrailingActions {
    @Composable
    fun RowScope.VisibilityIconButton(
        visible: Boolean,
        modifier: Modifier = Modifier,
        changeVisibility: (visible: Boolean) -> Unit,
    ) {
        TrailingIconButton(
            icon = when {
                visible -> Icons.Outlined.VisibilityOff
                else -> Icons.Outlined.Visibility
            },
            modifier = modifier,
            contentDescription = stringResource(R.string.show_password)
        ) {
           changeVisibility(!visible)
        }
    }

    @Composable
    fun RowScope.CopyIconButton(
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) =
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







@Composable
private fun animateBorderStrokeAsState(
    borderBrush: Brush,
    enabled: Boolean,
    interactionSource: InteractionSource,
    focusedBorderThickness: Dp = OutlinedTextFieldDefaults.FocusedBorderThickness,
    unfocusedBorderThickness: Dp = OutlinedTextFieldDefaults.UnfocusedBorderThickness
): State<BorderStroke> {

    val focused by interactionSource.collectIsFocusedAsState()
    val targetThickness = if (focused) focusedBorderThickness else unfocusedBorderThickness

    val animatedThickness = when {
        enabled -> targetThickness.animate(durationMillis = 150)
        else -> rememberUpdatedState(unfocusedBorderThickness).value
    }

    return rememberUpdatedState(
        BorderStroke(animatedThickness, borderBrush)
    )
}









@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
private fun DataTextFieldPreview() {
    var visible by remember { mutableStateOf(false) }
    PasswordManagerTheme(isDarkTheme = false, dynamicColor = false) {
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
                label = "Imagine Dragons",
                error = false,
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