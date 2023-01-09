package com.security.passwordmanager.presentation.view.login

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.BottomSheetFragment
import com.security.passwordmanager.presentation.view.navigation.FeedbackSheet
import com.security.passwordmanager.presentation.view.navigation.Screen
import com.security.passwordmanager.presentation.view.navigation.createRouteToNotesScreen
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.RaspberryLight
import com.security.passwordmanager.presentation.viewmodel.LoginViewModel
import com.security.passwordmanager.presentation.viewmodel.LoginViewModel.EntryState
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.LoginPasswordScreen(
    viewModel: LoginViewModel,
    settingsLiveData: LiveData<Settings>,
    fragmentManager: FragmentManager,
    navigateTo: (route: String) -> Unit,
    popBackStack: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val settings by settingsLiveData.observeAsState(initial = Settings())

    val bottomSheetFragment = BottomSheetFragment { fragment ->
        FeedbackSheet(context = context, beautifulDesign = settings.isUsingBeautifulFont) {
            fragment.dismiss()
        }
    }


    BackHandler {
        if (!viewModel.enterLogin) {
            viewModel.enterLogin = true
            viewModel.password = ""
            viewModel.passwordErrorMessage = ""
            viewModel.isPasswordVisible = false
            keyboardController?.show()
        } else {
            popBackStack()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = { bottomSheetFragment.show(fragmentManager) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HelpOutline,
                            contentDescription = "feedback",
                            modifier = Modifier.scale(1.3f)
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    actionIconContentColor = RaspberryLight
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(snackbarData = it, shape = MaterialTheme.shapes.small)
            }
        },
        containerColor = MaterialTheme.colorScheme.background.animate(),
        contentColor = MaterialTheme.colorScheme.onBackground.animate(),
        modifier = Modifier
            .animateEnterExit(
                enter = slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = LinearOutSlowInEasing
                    )
                ),
                exit = slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = FastOutLinearInEasing
                    )
                )
            )
            .fillMaxSize()
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp)
                .padding(contentPadding)
                .fillMaxSize()
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
                        append("by CuteCat")
                    }
                },
                color = MaterialTheme.colorScheme.onBackground.animate(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 24.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = when {
                    viewModel.enterLogin -> stringResource(R.string.enter_email)
                    viewModel.currentEntryState == EntryState.SignIn -> "Введите пароль"
                    else -> "Придумайте пароль и введите его в поля ввода"
                },
                color = MaterialTheme.colorScheme.onBackground.animate(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth()
            )



            LoginPasswordTextField(
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
                readOnly = !viewModel.enterLogin,
                keyboardActions = KeyboardActions(
                    onNext = {
                        viewModel.onEmailNext { isEmailValid ->
                            if (isEmailValid)
                                focusManager.moveFocus(FocusDirection.Next)
                            else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        context.getString(R.string.invalid_email)
                                    )
                                }
                            }
                        }
                    }
                ),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )


            Spacer(Modifier.height(16.dp))


            AnimatedVisibility(
                visible = !viewModel.enterLogin,
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
                    LoginPasswordTextField(
                        text = viewModel.password,
                        onTextChange = {
                            viewModel.password = it
                            viewModel.passwordErrorMessage =
                                if (!viewModel.isPasswordValid() && viewModel.currentEntryState == EntryState.Registration)
                                    context.getString(R.string.invalid_password)
                                else ""
                        },
                        label = stringResource(R.string.password),
                        error = viewModel.passwordErrorMessage.isNotBlank(),
                        errorMessage = viewModel.passwordErrorMessage,
                        keyboardType = KeyboardType.Password,
                        imeAction = when (viewModel.currentEntryState) {
                            EntryState.SignIn -> ImeAction.Done
                            EntryState.Registration -> ImeAction.Next
                        },
                        keyboardActions = KeyboardActions {
                            if (!viewModel.isPasswordValid()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        context.getString(R.string.invalid_password)
                                    )
                                }
                            }
                        },
                        visualTransformation = if (viewModel.isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
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

                    Spacer(Modifier.height(8.dp))


                    if (viewModel.currentEntryState == EntryState.Registration) {
                        LoginPasswordTextField(
                            text = viewModel.repeatedPassword,
                            onTextChange = viewModel::repeatedPassword::set,
                            label = stringResource(R.string.repeat_password),
                            error = viewModel.repeatedPassword != viewModel.password,
                            errorMessage = stringResource(R.string.passwords_is_not_equals),
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            keyboardActions = KeyboardActions {
                                if (viewModel.repeatedPassword != viewModel.password) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            context.getString(R.string.passwords_is_not_equals)
                                        )
                                    }
                                }
                            },
                            visualTransformation = if (viewModel.isRepeatedPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
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


                    Spacer(Modifier.height(32.dp))


                    TextButton(
                        onClick = {
                            viewModel.loginOrRegisterUser(context) { isSuccess, entryState ->
                                when {
                                    isSuccess -> navigateTo(
                                        createRouteToNotesScreen(
                                            dataType = DataType.All,
                                            title = context.getString(Screen.Notes.titleRes),
                                        )
                                    )
                                    else -> {
                                        viewModel.passwordErrorMessage = entryState.message
                                        scope.launch {
                                            snackbarHostState.showSnackbar(entryState.message)
                                        }
                                    }
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.animate(),
                            contentColor = MaterialTheme.colorScheme.onPrimary.animate()
                        ),
                        border = BorderStroke(
                            width = 1.1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.onBackground,
                                    MaterialTheme.colorScheme.background
                                ).map { it.animate() }
                            )
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(
                                if (viewModel.currentEntryState == EntryState.SignIn) {
                                    R.string.action_sign_in
                                } else {
                                    R.string.action_sign_up
                                }
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = viewModel.isLoading,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearOutSlowInEasing
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutLinearInEasing
                    )
                )
            ) {
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary.animate(),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }


            Spacer(Modifier.height(32.dp))
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
    trailingIcon: @Composable (() -> Unit) = {}
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onBackground
    ).map { it.animate() }

    val brush = when {
        error -> SolidColor(MaterialTheme.colorScheme.error.animate())
        readOnly -> SolidColor(MaterialTheme.colorScheme.onBackground.animate())
        else -> Brush.verticalGradient(colors)
    }


    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
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
            cursorBrush = Brush.verticalGradient(colors),
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            decorationBox = { innerTextField ->
                TextFieldDefaults.TextFieldDecorationBox(
                    value = text,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    trailingIcon = trailingIcon,
                    isError = error,
                    visualTransformation = visualTransformation,
                    placeholder = {
                        Text(
                            text = if (!isFocused) label else "",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        placeholderColor = MaterialTheme.colorScheme.onBackground
                            .copy(alpha = 0.86f)
                            .animate(),
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onBackground
                            .copy(alpha = 0.86f)
                            .animate(),
                        textColor = MaterialTheme.colorScheme.onBackground.animate(),
                        disabledTextColor = MaterialTheme.colorScheme.onBackground.animate(),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme.shapes.medium
                )
            },
            modifier = modifier
                .padding(top = 10.dp)
                .align(Alignment.TopCenter)
                .onFocusChanged { isFocused = it.isFocused }
                .border(
                    width = 1.3.dp,
                    shape = MaterialTheme.shapes.medium,
                    brush = brush
                )
                .fillMaxWidth(),
        )


        AnimatedVisibility(
            visible = text.isNotEmpty() || isFocused,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = LinearEasing
                ),
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 250,
                    easing = LinearEasing
                )
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 13.dp, top = 1.4.dp)
                .background(MaterialTheme.colorScheme.background.animate())
                .padding(horizontal = 3.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    error -> MaterialTheme.colorScheme.error
                    readOnly -> MaterialTheme.colorScheme.onBackground
                    else -> MaterialTheme.colorScheme.primary
                }.animate()
            )
        }


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
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, top = 64.dp)
        ) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error.animate(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}







@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
private fun LoginPasswordTextFieldPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        var text by remember { mutableStateOf("Hello!") }

        Column {
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

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                isError = true,
                label = {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                supportingText = {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}