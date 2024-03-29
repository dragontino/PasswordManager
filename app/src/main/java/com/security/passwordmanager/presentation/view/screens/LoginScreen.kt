package com.security.passwordmanager.presentation.view.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.composablelements.EditableDataTextField
import com.security.passwordmanager.presentation.view.composablelements.FeedbackSheetContent
import com.security.passwordmanager.presentation.view.composablelements.TextButton
import com.security.passwordmanager.presentation.view.navigation.AppScreens
import com.security.passwordmanager.presentation.view.navigation.ModalSheetDefaults
import com.security.passwordmanager.presentation.view.navigation.createRouteToNotesScreen
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.LoginViewModel
import com.security.passwordmanager.presentation.viewmodel.LoginViewModel.EntryState
import com.security.passwordmanager.presentation.viewmodel.LoginViewModel.ViewModelState
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.LoginScreen(
    viewModel: LoginViewModel,
    settings: Settings,
    navigateTo: (route: String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current


    BackHandler {
        if (viewModel.entryState != EntryState.Undefined && !viewModel.hasEmailInPreferences()) {
            viewModel.changeLogin()
            keyboardController?.show()
            focusManager.moveFocus(FocusDirection.Previous)
        } else {
            context.getActivity()?.finish()
        }
    }


    Crossfade(
        targetState = viewModel.viewModelState,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background.animate())
            .fillMaxSize()
    ) {
        when (it) {
            ViewModelState.PreLoading -> LoadingInBox(progress = viewModel.loadingProgress)
            else -> LoginContentScreen(
                viewModel = viewModel,
                settings = settings,
                navigateTo = navigateTo
            )
        }
    }
}







@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
private fun AnimatedVisibilityScope.LoginContentScreen(
    viewModel: LoginViewModel,
    settings: Settings,
    navigateTo: (route: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = ModalSheetDefaults.AnimationSpec,
        skipHalfExpanded = true
    )
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val showSnackbar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }



    ModalBottomSheetLayout(
        sheetContent = {
            FeedbackSheetContent(beautifulDesign = settings.beautifulFont) {
                scope.launch { sheetState.hide() }
            }
        },
        sheetState = sheetState,
        sheetShape = ModalSheetDefaults.Shape,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface.animate()
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = buildString {
                                if (viewModel.entryState != EntryState.Registration) {
                                    append(stringResource(R.string.authorization))
                                }

                                if (viewModel.entryState == EntryState.Undefined) {
                                    append("/")
                                }

                                if (viewModel.entryState != EntryState.SignIn) {
                                    append(stringResource(R.string.registration))
                                }
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                scope.launch { sheetState.show() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.HelpOutline,
                                contentDescription = "feedback",
                                modifier = Modifier.scale(1.3f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        actionIconContentColor = MaterialTheme.colorScheme.primary.animate(),
                        titleContentColor = MaterialTheme.colorScheme.onBackground.animate()
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = stringResource(
                                when (viewModel.entryState) {
                                    EntryState.Undefined -> R.string.go_next
                                    EntryState.SignIn -> R.string.action_sign_in
                                    EntryState.Registration -> R.string.action_sign_up
                                }
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    icon = {
                        Crossfade(
                            targetState = viewModel.viewModelState,
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = FastOutSlowInEasing
                            )
                        ) { state ->
                            when (state) {
                                ViewModelState.Ready -> {
                                    Icon(
                                        imageVector = when (viewModel.entryState) {
                                            EntryState.Undefined -> Icons.Rounded.Start
                                            EntryState.SignIn -> Icons.Rounded.Login
                                            EntryState.Registration -> Icons.Rounded.AppRegistration
                                        },
                                        contentDescription = "button"
                                    )
                                }

                                else -> {
                                    CircularProgressIndicator(
                                        strokeWidth = 3.5.dp,
                                        color = MaterialTheme.colorScheme.onPrimary.animate(),
                                        modifier = Modifier.scale(0.6f)
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        when (viewModel.entryState) {
                            EntryState.Undefined -> viewModel.onEmailNext { isEmailValid ->
                                if (!isEmailValid)
                                    showSnackbar(context.getString(R.string.invalid_email))
                            }

                            else -> {
                                viewModel.signWithEmail(context) { result, entryState ->
                                    when (result) {
                                        is Result.Success -> navigateTo(
                                            createRouteToNotesScreen(
                                                dataType = DataType.All,
                                                title = context.getString(AppScreens.Notes.titleRes),
                                            )
                                        )

                                        is Result.Error -> {
                                            val exceptionMessage =
                                                result.exception.localizedMessage ?: ""

                                            if (exceptionMessage.isNotBlank()) {
                                                showSnackbar(exceptionMessage)
                                            }
                                            viewModel.passwordErrorMessage = entryState.message
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    expanded = viewModel.viewModelState == ViewModelState.Ready,
                    elevation = FloatingActionButtonDefaults.loweredElevation(),
                    containerColor = MaterialTheme.colorScheme.primary.animate(),
                    contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    modifier = Modifier.padding(
                        WindowInsets.tappableElement
                            .only(WindowInsetsSides.Vertical)
                            .asPaddingValues()
                    )
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(snackbarData = it, shape = MaterialTheme.shapes.small)
                }
            },
            containerColor = MaterialTheme.colorScheme.background.animate(),
            contentColor = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier
                .animateEnterExit(
                    enter = EnterContentAnimation,
                    exit = ExitContentAnimation
                )
                .fillMaxSize()
        ) { contentPadding ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp)
                    .padding(contentPadding)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = "app icon",
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(160.dp)
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(fontSize = 35.sp, fontWeight = FontWeight.Bold)
                        ) {
                            append(stringResource(R.string.app_label))
                        }

                        append("\n")

                        withStyle(
                            MaterialTheme.typography.titleLarge.toSpanStyle()
                        ) {
                            append(stringResource(R.string.main_subtitle))
                        }
                    },
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 24.dp)
                        .fillMaxWidth()
                )



                EditableDataTextField(
                    text = viewModel.email,
                    onTextChange = {
                        viewModel.email = it
                        viewModel.emailErrorMessage =
                            if (!viewModel.isEmailValid())
                                context.getString(R.string.invalid_email)
                            else ""
                    },
                    label = "Email",
                    error = viewModel.emailErrorMessage.isNotBlank(),
                    errorMessage = viewModel.emailErrorMessage,
                    readOnly = viewModel.entryState != EntryState.Undefined,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = "Email"
                        )
                    },
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )



                AnimatedVisibility(
                    visible = viewModel.entryState != EntryState.Undefined,
                    enter = expandVertically(
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = FastOutSlowInEasing
                        )
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = FastOutSlowInEasing
                        )
                    )
                ) {
                    Column {
                        Spacer(Modifier.height(16.dp))

                        if (viewModel.entryState == EntryState.Registration) {
                            EditableDataTextField(
                                text = viewModel.username,
                                onTextChange = viewModel::username::set,
                                label = stringResource(R.string.username_placeholder)
                            )
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        EditableDataTextField(
                            text = viewModel.password,
                            onTextChange = {
                                viewModel.password = it
                                viewModel.passwordErrorMessage =
                                    if (
                                        !viewModel.isPasswordValid() &&
                                        viewModel.entryState == EntryState.Registration
                                    )
                                        context.getString(R.string.invalid_password)
                                    else ""
                            },
                            label = stringResource(R.string.password),
                            error = viewModel.passwordErrorMessage.isNotBlank(),
                            errorMessage = viewModel.passwordErrorMessage,
                            keyboardType = KeyboardType.Password,
                            imeAction = when (viewModel.entryState) {
                                EntryState.SignIn -> ImeAction.Done
                                EntryState.Registration -> ImeAction.Next
                                EntryState.Undefined -> ImeAction.None
                            },
                            onImeActionClick = {
                                when (viewModel.entryState) {
                                    EntryState.SignIn -> keyboardController?.hide()
                                    EntryState.Registration -> {
                                        if (!viewModel.isPasswordValid()) {
                                            showSnackbar(context.getString(R.string.invalid_password))
                                        }
                                    }
                                    EntryState.Undefined -> defaultKeyboardAction(ImeAction.Default)
                                }
                            },
                            visualTransformation = when {
                                viewModel.isPasswordVisible -> VisualTransformation.None
                                else -> PasswordVisualTransformation()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Password,
                                    contentDescription = "password"
                                )
                            },
                            trailingActions = {
                                IconButton(
                                    onClick = {
                                        viewModel.isPasswordVisible =
                                            !viewModel.isPasswordVisible
                                    },
                                ) {
                                    Icon(
                                        imageVector = if (viewModel.isPasswordVisible) {
                                            Icons.Outlined.VisibilityOff
                                        } else {
                                            Icons.Outlined.Visibility
                                        },
                                        contentDescription = "show/hide password"
                                    )
                                }
                            }
                        )



                        if (viewModel.entryState == EntryState.Registration) {
                            Spacer(modifier = Modifier.height(8.dp))

                            EditableDataTextField(
                                text = viewModel.repeatedPassword,
                                onTextChange = viewModel::repeatedPassword::set,
                                label = stringResource(R.string.repeat_password),
                                error = viewModel.repeatedPassword != viewModel.password,
                                errorMessage = stringResource(R.string.passwords_is_not_equals),
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                                onImeActionClick = {
                                    if (viewModel.repeatedPassword != viewModel.password) {
                                        showSnackbar(context.getString(R.string.passwords_is_not_equals))
                                    }
                                },
                                visualTransformation = when {
                                    viewModel.isRepeatedPasswordVisible -> VisualTransformation.None
                                    else -> PasswordVisualTransformation()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Password,
                                        contentDescription = "password"
                                    )
                                },
                                trailingActions = {
                                    IconButton(
                                        onClick = {
                                            viewModel.isRepeatedPasswordVisible =
                                                !viewModel.isRepeatedPasswordVisible
                                        },
                                    ) {
                                        Icon(
                                            imageVector = if (viewModel.isRepeatedPasswordVisible) {
                                                Icons.Outlined.VisibilityOff
                                            } else {
                                                Icons.Outlined.Visibility
                                            },
                                            contentDescription = "show/hide password"
                                        )
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        BottomButtons(
                            viewModel = viewModel,
                            showSnackbar = {
                                showSnackbar(it)
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}




@ExperimentalComposeUiApi
@Composable
private fun BottomButtons(
    viewModel: LoginViewModel,
    showSnackbar: (message: String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (viewModel.entryState == EntryState.SignIn) {

            TextButton(
                text = stringResource(R.string.forgot_password),
                fontSize = 15.sp
            ) {
                viewModel.restorePassword { success ->
                    if (success) showSnackbar(context.getString(R.string.password_reset))
                    else showSnackbar(context.getString(R.string.password_not_reset))
                }
            }
        }


        if (viewModel.entryState != EntryState.Undefined) {
            Spacer(modifier = Modifier.width(16.dp))

            TextButton(
                text = context.getString(R.string.change_login),
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                viewModel.changeLogin()
                viewModel.email = ""
                keyboardController?.show()
            }
        }
    }
}






@ExperimentalMaterial3Api
@Composable
private fun LoginPasswordTextField(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    error: Boolean = false,
    errorMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    val borderColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onBackground
    ).map { it.animate() }

    val brush = when {
        error -> SolidColor(MaterialTheme.colorScheme.error.animate())
        readOnly -> SolidColor(MaterialTheme.colorScheme.onBackground.animate())
        else -> Brush.verticalGradient(borderColors, tileMode = TileMode.Repeated)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val shape = MaterialTheme.shapes.medium

    val textColor = MaterialTheme.colorScheme.onBackground
    val unfocusedTextColor = textColor.copy(alpha = 0.7f)
    val containerColor = Color.Transparent

    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textColor.animate(),
        unfocusedTextColor = unfocusedTextColor.animate(),
        disabledTextColor = textColor.animate(),
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        disabledContainerColor = containerColor,
        errorContainerColor = containerColor,
        focusedLeadingIconColor = textColor.animate(),
        unfocusedLeadingIconColor = unfocusedTextColor.animate(),
        disabledLeadingIconColor = textColor.animate(),
        errorLeadingIconColor = MaterialTheme.colorScheme.error.animate(),
        focusedTrailingIconColor = textColor.animate(),
        unfocusedTrailingIconColor = unfocusedTextColor.animate(),
        disabledTrailingIconColor = textColor.animate()
    )


    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        readOnly = readOnly,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
            autoCorrect = false
        ),
        cursorBrush = brush,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onBackground.animate()
        ),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = text,
                innerTextField = innerTextField,
                enabled = true,
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
                        visible = errorMessage.isNotEmpty() && error,
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
                        ),
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error.animate(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                colors = colors,
                contentPadding = TextFieldDefaults.contentPaddingWithLabel(
                    top = 18.dp,
                    bottom = 18.dp
                ),
                container = {
                    val borderStroke = animateBorderStrokeAsState(
                        borderBrush = brush,
                        enabled = true,
                        interactionSource = interactionSource
                    )

                    Box(
                        modifier = Modifier
                            .border(borderStroke.value, shape)
                            .background(
                                color = containerColor,
                                shape = shape
                            )
                    )
                },
            )
        },
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .fillMaxWidth(),
    )
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
private fun LoginPasswordTextFieldPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        var text by remember { mutableStateOf("Hello!") }

        LoginPasswordTextField(
            text = text,
            onTextChange = { text = it },
            label = "Email",
            readOnly = true,
            error = true,
            errorMessage = "Error",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions()
        )
    }
}