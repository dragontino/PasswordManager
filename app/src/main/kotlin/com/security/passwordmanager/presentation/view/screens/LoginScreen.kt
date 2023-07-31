package com.security.passwordmanager.presentation.view.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.presentation.view.composables.CollapsingToolbar
import com.security.passwordmanager.presentation.view.composables.CollapsingToolbarDefaults
import com.security.passwordmanager.presentation.view.composables.DataTextFieldDefaults
import com.security.passwordmanager.presentation.view.composables.EditableDataTextField
import com.security.passwordmanager.presentation.view.composables.FeedbackSheetContent
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffold
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffoldDefaults
import com.security.passwordmanager.presentation.view.composables.TextButton
import com.security.passwordmanager.presentation.view.composables.ToolbarButton
import com.security.passwordmanager.presentation.view.composables.ToolbarButtonDefaults
import com.security.passwordmanager.presentation.view.composables.managment.ScrollableScaffoldState
import com.security.passwordmanager.presentation.view.composables.managment.ToolbarType
import com.security.passwordmanager.presentation.view.composables.managment.rememberScrollableScaffoldState
import com.security.passwordmanager.presentation.view.navigation.AppScreens
import com.security.passwordmanager.presentation.view.navigation.ModalSheetDefaults
import com.security.passwordmanager.presentation.view.theme.ScreenContentAnimation
import com.security.passwordmanager.presentation.view.theme.ScreenToolbarAnimation
import com.security.passwordmanager.presentation.viewmodel.LoginViewModel
import com.security.passwordmanager.presentation.viewmodel.LoginViewModel.EntryState
import com.security.passwordmanager.presentation.viewmodel.LoginViewModel.ViewModelState
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AnimatedVisibilityScope.LoginScreen(
    viewModel: LoginViewModel,
    settings: Settings,
    isDarkTheme: Boolean,
    navigateTo: (route: String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val scaffoldState = rememberScrollableScaffoldState(
        scrollState = rememberScrollState(),
        toolbarType = ToolbarType.PinnedToolbar,
        maxToolbarAlpha = 0f
    )


    BackHandler {
        if (viewModel.entryState != EntryState.Identification && !viewModel.hasEmailInPreferences()) {
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
            .fillMaxSize(),
        label = "loading"
    ) {
        when (it) {
            ViewModelState.PreLoading -> LoadingInBox(progress = viewModel.loadingProgress)
            else -> LoginContentScreen(
                scaffoldState = scaffoldState,
                viewModel = viewModel,
                settings = settings,
                isDarkTheme = isDarkTheme,
                navigateTo = navigateTo
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@ExperimentalComposeUiApi
@Composable
private fun AnimatedVisibilityScope.LoginContentScreen(
    scaffoldState: ScrollableScaffoldState.ScrollContent,
    viewModel: LoginViewModel,
    settings: Settings,
    isDarkTheme: Boolean,
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
            FeedbackSheetContent(
                beautifulDesign = settings.beautifulFont,
                isDarkTheme = isDarkTheme
            ) {
                scope.launch { sheetState.hide() }
            }
        },
        sheetState = sheetState,
        sheetShape = ModalSheetDefaults.Shape,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface.animate()
    ) {
        ScrollableToolbarScaffold(
            state = scaffoldState,
            contentModifier = Modifier
                .imePadding()
                .animateEnterExit(
                    enter = ScreenContentAnimation.enter,
                    exit = ScreenContentAnimation.exit
                )
                .fillMaxSize(),
            topBar = {
                CollapsingToolbar(
                    title = when (viewModel.entryState) {
                        EntryState.Identification -> stringResource(R.string.identification)
                        EntryState.Authentication -> stringResource(R.string.authentication)
                        EntryState.Registration -> stringResource(R.string.registration)
                    },
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    actions = {
                        ToolbarButton(
                            icon = Icons.Rounded.HelpOutline,
                            contentDescription = "feedback",
                            iconModifier = Modifier.scale(1.3f),
                            colors = ToolbarButtonDefaults.colors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                scope.launch { sheetState.show() }
                            }
                        )
                    },
                    modifier = Modifier
                        .animateEnterExit(
                            enter = ScreenToolbarAnimation.enter,
                            exit = ScreenToolbarAnimation.exit
                        )
                        .fillMaxWidth()
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(snackbarData = it, shape = MaterialTheme.shapes.small)
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = stringResource(
                                when (viewModel.entryState) {
                                    EntryState.Identification -> R.string.go_next
                                    EntryState.Authentication -> R.string.action_sign_in
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
                                durationMillis = 350,
                                easing = FastOutSlowInEasing
                            ),
                            label = "entry"
                        ) { state ->
                            when (state) {
                                ViewModelState.Ready -> {
                                    Icon(
                                        imageVector = when (viewModel.entryState) {
                                            EntryState.Identification -> Icons.Rounded.Start
                                            EntryState.Authentication -> Icons.Rounded.Login
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
                            EntryState.Identification -> viewModel.onEmailNext { isEmailValid ->
                                if (!isEmailValid)
                                    showSnackbar(context.getString(R.string.invalid_email))
                            }

                            else -> {
                                viewModel.signWithEmail(context) { result, entryState ->
                                    when (result) {
                                        is Result.Success -> navigateTo(
                                            AppScreens.AllNotes.createUrl()
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
                    /*modifier = Modifier.padding(
                        WindowInsets.tappableElement
                            .only(WindowInsetsSides.Vertical)
                            .asPaddingValues()
                    )*/
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
            isPullRefreshEnabled = false,
            colors = ScrollableToolbarScaffoldDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background,
                toolbarColors = CollapsingToolbarDefaults.colors(
                    containerColor = Color.Transparent,
                    transparentContainerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    transparentTitleContentColor = MaterialTheme.colorScheme.onBackground
                ),
            )
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scaffoldState.contentState)
                    .padding(top = 16.dp)
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
                    onTextChange = viewModel::email::set,
                    label = "Email",
                    error = viewModel.emailErrorMessage.isNotBlank(),
                    errorMessage = viewModel.emailErrorMessage,
                    readOnly = viewModel.entryState != EntryState.Identification,
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
                    visible = viewModel.entryState != EntryState.Identification,
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
                                EntryState.Authentication -> ImeAction.Done
                                EntryState.Registration -> ImeAction.Next
                                EntryState.Identification -> ImeAction.None
                            },
                            onImeActionClick = {
                                when (viewModel.entryState) {
                                    EntryState.Authentication -> keyboardController?.hide()
                                    EntryState.Registration -> {
                                        if (!viewModel.isPasswordValid()) {
                                            showSnackbar(context.getString(R.string.invalid_password))
                                        }
                                    }

                                    EntryState.Identification -> defaultKeyboardAction(ImeAction.Default)
                                }
                            },
                            visualTransformation = DataTextFieldDefaults
                                .passwordVisualTransformation(
                                    isVisible = viewModel.isPasswordVisible
                                ),
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

                        Spacer(modifier = Modifier.height(4.dp))

                        BottomButtons(
                            viewModel = viewModel,
                            showSnackbar = {
                                showSnackbar(it)
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.size(72.dp))
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
        if (viewModel.entryState == EntryState.Authentication) {

            TextButton(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
            ) {
                viewModel.restorePassword { success ->
                    if (success) showSnackbar(context.getString(R.string.password_reset))
                    else showSnackbar(context.getString(R.string.password_not_reset))
                }
            }
        }


        if (viewModel.entryState != EntryState.Identification) {
            Spacer(modifier = Modifier.width(16.dp))

            TextButton(
                text = context.getString(R.string.change_login),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
            ) {
                viewModel.changeLogin()
                viewModel.email = ""
                keyboardController?.show()
            }
        }
    }
}