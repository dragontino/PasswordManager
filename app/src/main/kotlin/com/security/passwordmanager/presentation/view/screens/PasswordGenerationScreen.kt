package com.security.passwordmanager.presentation.view.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.ManageHistory
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.security.passwordmanager.LoadingInBox
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.composables.CollapsingToolbar
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffold
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffoldDefaults
import com.security.passwordmanager.presentation.view.composables.TextButton
import com.security.passwordmanager.presentation.view.composables.ToolbarButton
import com.security.passwordmanager.presentation.view.composables.ToolbarButtonDefaults
import com.security.passwordmanager.presentation.view.composables.rememberScrollableScaffoldState
import com.security.passwordmanager.presentation.view.managment.ToolbarType
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.ScreenContentAnimation
import com.security.passwordmanager.presentation.view.theme.ScreenToolbarAnimation
import com.security.passwordmanager.presentation.viewmodel.PasswordGenerationViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedVisibilityScope.PasswordGenerationScreen(
    title: String,
    viewModel: PasswordGenerationViewModel,
    popBackStack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scaffoldState = rememberScrollableScaffoldState(
        contentState = scrollState,
        toolbarType = ToolbarType.PinnedToolbar
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val showSnackbar = { text: String ->
        scope.launch { snackbarHostState.showSnackbar(text) }
    }


    ScrollableToolbarScaffold(
        state = scaffoldState,
        contentModifier = Modifier
            .windowInsetsPadding(WindowInsets.ime.only(WindowInsetsSides.Bottom))
            .animateEnterExit(
                enter = ScreenContentAnimation.enter,
                exit = ScreenContentAnimation.exit
            )
            .fillMaxSize()
            .verticalScroll(scrollState),
        topBar = {
            CollapsingToolbar(
                title = title,
                navigationButton = {
                    ToolbarButton(
                        icon = Icons.Rounded.ArrowBackIosNew,
                        colors = ToolbarButtonDefaults.colors(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        onClick = popBackStack
                    )
                },
                actions = {
                    ToolbarButton(
                        icon = Icons.Rounded.ManageHistory,
                        colors = ToolbarButtonDefaults.colors(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        iconModifier = Modifier.scale(1.3f)
                    ) {
                        viewModel.showHistory()
                    }
                },
                modifier = Modifier.animateEnterExit(
                    enter = ScreenToolbarAnimation.enter,
                    exit = ScreenToolbarAnimation.exit
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    shape = MaterialTheme.shapes.large,
                    containerColor = MaterialTheme.colorScheme.onBackground.animate(),
                    contentColor = MaterialTheme.colorScheme.background.animate()
                )
            }
        },
        contentShape = MaterialTheme.shapes.small,
        colors = ScrollableToolbarScaffoldDefaults.colors(
            toolbarContainerColor = MaterialTheme.colorScheme.background,
            toolbarTitleContentColor = MaterialTheme.colorScheme.onBackground,
            toolbarNavigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            toolbarActionIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        PasswordGeneration(
            viewModel = viewModel,
            showSnackbar = { showSnackbar(it) }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGenerationDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
        ),
    ) {
        Row {

        }
    }
}


@Composable
fun PasswordGeneration(
    viewModel: PasswordGenerationViewModel,
    showSnackbar: (text: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    if (viewModel.showDialog) {
        PasswordLengthSelectionDialog(
            passwordLength = viewModel.passwordLength,
            onSave = {
                viewModel.passwordLength = it
                viewModel.showDialog = false
            },
            onCancel = { viewModel.showDialog = false }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        TextField(
            value = TextFieldValue(viewModel.colorizedPassword),
            onValueChange = {},
            readOnly = true,
            textStyle = TextStyle(
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
            ),
            placeholder = {
                if (viewModel.state == PasswordGenerationViewModel.State.Loading) {
                    LoadingInBox(
                        modifier = Modifier.background(Color.Transparent),
                        loadingModifier = Modifier.scale(.7f)
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = viewModel::regeneratePassword) {
                    Icon(
                        imageVector = Icons.Rounded.Loop,
                        contentDescription = "regenerate password",
                        modifier = Modifier.scale(1.2f)
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTrailingIconColor = MaterialTheme.colorScheme.secondary.animate(),
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary.animate()
            ),
            modifier = Modifier.fillMaxWidth()
        )

        ElevatedButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(viewModel.password))
                showSnackbar(context.getString(R.string.copy_text_successful))
            },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary.animate(),
                contentColor = MaterialTheme.colorScheme.onPrimary.animate()
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(R.string.copy_info),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Divider()

        Text(
            text = "Длина пароля: ${viewModel.passwordLength}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier
                .padding(start = 6.dp, top = 16.dp, end = 16.dp)
                .clickable { viewModel.showDialog = true }
                .fillMaxWidth()
        )

        for (
        pair in arrayOf(
            viewModel::useUppercase to "Прописные буквы (A—Z)",
            viewModel::useLowercase to "Строчные буквы (a—z)",
            viewModel::useDigits to "Цифры (0—9)"
        )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = pair.first.get(),
                    onCheckedChange = pair.first::set,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.secondary.animate(),
                        uncheckedColor = MaterialTheme.colorScheme.onBackground.animate(),
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary.animate()
                    )
                )

                Text(
                    text = pair.second,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.animate()
                )
            }
        }


        Text(
            text = "Специальные символы",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkerGray
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = viewModel.useSpecialChars,
                onCheckedChange = viewModel::useSpecialChars::set,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary.animate(),
                    uncheckedColor = MaterialTheme.colorScheme.onBackground.animate(),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary.animate()
                )
            )


            TextField(
                value = viewModel.specialCharacters,
                onValueChange = viewModel::updateSpecialCharacters,
                trailingIcon = if (!viewModel.mayResetSpecialSymbols) null
                else {
                    {
                        IconButton(onClick = viewModel::resetSpecialCharacters) {
                            Icon(
                                imageVector = Icons.Rounded.RestartAlt,
                                contentDescription = "reset special symbols"
                            )
                        }
                    }
                },
                placeholder = {
                    Text(
                        text = "Специальные символы",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground.animate(),
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground.animate(),
                    focusedIndicatorColor = MaterialTheme.colorScheme.secondary.animate(),
                    focusedTrailingIconColor = MaterialTheme.colorScheme.onBackground.animate(),
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onBackground.animate()
                )
            )
        }
    }
}


@Composable
private fun PasswordLengthSelectionDialog(
    passwordLength: Int,
    onSave: (newLength: Int) -> Unit,
    onCancel: () -> Unit
) {
    var selectedLength by rememberSaveable { mutableStateOf(passwordLength) }
    val maxLength = PasswordGenerationViewModel.maxPasswordLength

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(
                text = stringResource(R.string.save),
                textColor = MaterialTheme.colorScheme.primary.animate()
            ) {
                onSave(selectedLength)
            }
        },
        dismissButton = {
            TextButton(
                text = stringResource(R.string.cancel_action),
                textColor = MaterialTheme.colorScheme.primary.animate(),
                onClick = onCancel
            )
        },
        title = {
            Text(
                text = stringResource(R.string.password_length_selection),
                style = MaterialTheme.typography.labelMedium
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                TextField(
                    value = selectedLength.toString(),
                    onValueChange = {
                        val length = it.toIntOrNull()
                        if (length != null && length in 4..maxLength) {
                            selectedLength = length
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = LocalContentColor.current,
                        unfocusedTextColor = LocalContentColor.current,
                        focusedIndicatorColor = DarkerGray,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )


                Slider(
                    value = selectedLength.toFloat(),
                    onValueChange = { selectedLength = it.roundToInt() },
                    valueRange = 0f..maxLength.toFloat(),
                    steps = maxLength,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary.animate(),
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent,
                        activeTrackColor = MaterialTheme.colorScheme.primaryContainer.animate(),
                        inactiveTrackColor = MaterialTheme.colorScheme.onBackground.animate()
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.background.animate(),
        titleContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        tonalElevation = 8.dp,
        properties = DialogProperties(
            dismissOnBackPress = false
        )
    )
}