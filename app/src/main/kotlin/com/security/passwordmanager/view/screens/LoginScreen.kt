package com.security.passwordmanager.view.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Start
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.util.LoadingInBox
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.util.getActivity
import com.security.passwordmanager.view.composables.DataTextFieldDefaults
import com.security.passwordmanager.view.composables.EditableDataTextField
import com.security.passwordmanager.view.composables.FeedbackBottomSheet
import com.security.passwordmanager.view.composables.TextButton
import com.security.passwordmanager.view.composables.managment.ScreenEvents
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffold
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffoldDefaults
import com.security.passwordmanager.view.composables.scaffold.ToolbarButton
import com.security.passwordmanager.view.composables.scaffold.ToolbarButtonDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingAppBarDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingToolbar
import com.security.passwordmanager.view.composables.scaffold.toolbar.rememberAppBarState
import com.security.passwordmanager.view.navigation.HomeScreen
import com.security.passwordmanager.view.theme.AlettaFont
import com.security.passwordmanager.view.theme.ScreenContentAnimation
import com.security.passwordmanager.view.theme.ScreenToolbarAnimation
import com.security.passwordmanager.viewmodel.LoginViewModel
import com.security.passwordmanager.viewmodel.LoginViewModel.EntryState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
internal fun AnimatedVisibilityScope.LoginScreen(
    viewModel: LoginViewModel,
    isDarkTheme: Boolean,
    navigateTo: (route: String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember(::SnackbarHostState)


    BackHandler {
        if (viewModel.entryState.value != EntryState.Identification
            && !viewModel.hasEmailInPreferences()
        ) {
            viewModel.changeLogin()
            keyboardController?.show()
            focusManager.moveFocus(FocusDirection.Previous)
        } else {
            context.getActivity()?.finish()
        }
    }


    val showSnackbar = remember {
        fun(message: String) {
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    LaunchedEffect(viewModel.isPreLoading) {
        if (viewModel.isPreLoading) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 600,
                    delayMillis = 250,
                    easing = LinearEasing
                )
            ) { value, _ ->
                viewModel.loadingProgress.floatValue = value
            }
            viewModel.onPreLoadingFinish()
        }
    }


    LaunchedEffect(key1 = true) {
        viewModel.eventsFlow.collect { event ->
            when (event) {
                is ScreenEvents.Navigate -> navigateTo(event.args.toString())
                is ScreenEvents.ShowSnackbar -> showSnackbar(event.message)
                else -> {}
            }
        }
    }


    Crossfade(
        targetState = viewModel.viewModelState.value,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background.animate())
            .fillMaxSize(),
        label = "loading"
    ) {
        when (it) {
            LoginViewModel.State.PreLoading -> LoadingInBox(progress = viewModel.loadingProgress.floatValue)
            else -> LoginContentScreen(
                snackbarHostState = snackbarHostState,
                viewModel = viewModel,
                isDarkTheme = isDarkTheme
            )
        }
    }
}



@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedVisibilityScope.LoginContentScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: LoginViewModel,
    isDarkTheme: Boolean,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    val contentState = rememberScrollState()

    if (isSheetOpen) {
        FeedbackBottomSheet(
            state = bottomSheetState,
            beautifulDesign = viewModel.settings.value.beautifulFont,
            isDarkTheme = isDarkTheme,
            onClose = { isSheetOpen = false }
        )
    }

    CollapsingToolbarScaffold(
        contentState = contentState,
        contentModifier = Modifier
            .animateEnterExit(
                enter = ScreenContentAnimation.enter,
                exit = ScreenContentAnimation.exit
            )
            .fillMaxSize()
            .verticalScroll(contentState),
        topBar = {
            CollapsingToolbar(
                title = when (viewModel.entryState.value) {
                    EntryState.Identification -> stringResource(R.string.identification)
                    EntryState.Authentication -> stringResource(R.string.authentication)
                    EntryState.Registration -> stringResource(R.string.registration)
                },
                textStyle = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                actions = {
                    ToolbarButton(
                        icon = Icons.AutoMirrored.Rounded.HelpOutline,
                        contentDescription = "feedback",
                        iconModifier = Modifier.scale(1.3f),
                        colors = ToolbarButtonDefaults.colors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = { isSheetOpen = true }
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
        topBarScrollBehavior = CollapsingAppBarDefaults.pinnedScrollBehavior(
            state = rememberAppBarState(maxAlpha = 0f)
        ),
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
                            when (viewModel.entryState.value) {
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
                        targetState = viewModel.viewModelState.value,
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = FastOutSlowInEasing
                        ),
                        label = "entry"
                    ) { state ->
                        when (state) {
                            LoginViewModel.State.Ready -> {
                                Icon(
                                    imageVector = when (viewModel.entryState.value) {
                                        EntryState.Identification -> Icons.Rounded.Start
                                        EntryState.Authentication -> Icons.AutoMirrored.Rounded.Login
                                        EntryState.Registration -> Icons.Rounded.AppRegistration
                                    },
                                    contentDescription = null
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
                    if (viewModel.viewModelState.value != LoginViewModel.State.Loading) {
                        when (viewModel.entryState.value) {
                            EntryState.Identification -> viewModel.onEmailNext()
                            else -> {
                                viewModel.signWithEmail {
                                    viewModel.showSnackbar(context.getString(R.string.success))
                                    delay(600)
                                    viewModel.navigateTo(HomeScreen.AllNotes.createUrl())
                                }
                            }
                        }
                    }
                },
                shape = MaterialTheme.shapes.small,
                expanded = viewModel.viewModelState.value == LoginViewModel.State.Ready,
                elevation = FloatingActionButtonDefaults.loweredElevation(),
                containerColor = MaterialTheme.colorScheme.primary.animate(),
                contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        pullToRefreshEnabled = false,
        colors = CollapsingToolbarScaffoldDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
            toolbarColors = CollapsingAppBarDefaults.colors(
                containerColor = Color.Transparent,
                transparentContainerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                transparentTitleContentColor = MaterialTheme.colorScheme.onBackground
            ),
        )
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = "app icon",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(160.dp)
        )

        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.app_name))
                append("\n")

                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily(AlettaFont),
                        fontSize = 40.sp
                    )
                ) {
                    append(stringResource(R.string.main_subtitle))
                }
            },
            color = MaterialTheme.colorScheme.onBackground.animate(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 24.dp)
                .fillMaxWidth()
        )



        EditableDataTextField(
            text = viewModel.entry.email,
            onTextChange = {
                viewModel.entry.email = it
                viewModel.entry.emailErrorMessage = ""
            },
            label = "Email",
            error = viewModel.entry.emailErrorMessage.isNotBlank(),
            errorMessage = viewModel.entry.emailErrorMessage,
            readOnly = viewModel.entryState.value != EntryState.Identification,
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
            visible = viewModel.entryState.value != EntryState.Identification,
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

                if (viewModel.entryState.value == EntryState.Registration) {
                    EditableDataTextField(
                        text = viewModel.entry.username,
                        onTextChange = viewModel.entry::username::set,
                        label = stringResource(R.string.username_placeholder)
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                EditableDataTextField(
                    text = viewModel.entry.password,
                    onTextChange = {
                        viewModel.entry.password = it
                        viewModel.entry.passwordErrorMessage = ""
                    },
                    label = stringResource(R.string.password),
                    error = viewModel.entry.passwordErrorMessage.isNotBlank(),
                    errorMessage = viewModel.entry.passwordErrorMessage,
                    keyboardType = KeyboardType.Password,
                    imeAction = when (viewModel.entryState.value) {
                        EntryState.Authentication -> ImeAction.Done
                        EntryState.Registration -> ImeAction.Next
                        EntryState.Identification -> ImeAction.None
                    },
                    onImeActionClick = {
                        when (viewModel.entryState.value) {
                            EntryState.Authentication -> keyboardController?.hide()
                            else -> defaultKeyboardAction(ImeAction.Default)
                        }
                    },
                    visualTransformation = DataTextFieldDefaults.passwordVisualTransformation(
                        isVisible = viewModel.entry.isPasswordVisible
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Password,
                            contentDescription = "password"
                        )
                    },
                    trailingActions = {
                        IconButton(onClick = viewModel.entry::changePasswordVisibility) {
                            Icon(
                                imageVector = if (viewModel.entry.isPasswordVisible) {
                                    Icons.Outlined.VisibilityOff
                                } else {
                                    Icons.Outlined.Visibility
                                },
                                contentDescription = "show/hide password"
                            )
                        }
                    }
                )



                if (viewModel.entryState.value == EntryState.Registration) {
                    Spacer(modifier = Modifier.height(8.dp))

                    EditableDataTextField(
                        text = viewModel.entry.repeatedPassword,
                        onTextChange = viewModel.entry::repeatedPassword::set,
                        label = stringResource(R.string.repeat_password),
                        error = viewModel.entry.repeatedPassword != viewModel.entry.password,
                        errorMessage = stringResource(R.string.passwords_is_not_equals),
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        onImeActionClick = {
                            if (viewModel.entry.repeatedPassword != viewModel.entry.password) {
                                viewModel.showSnackbar(context.getString(R.string.passwords_is_not_equals))
                            }
                        },
                        visualTransformation = when {
                            viewModel.entry.isRepeatedPasswordVisible -> VisualTransformation.None
                            else -> PasswordVisualTransformation()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Password,
                                contentDescription = "password"
                            )
                        },
                        trailingActions = {
                            IconButton(onClick = viewModel.entry::changeRepeatedPasswordVisibility) {
                                Icon(
                                    imageVector = if (viewModel.entry.isRepeatedPasswordVisible) {
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
                BottomButtons(viewModel)
            }
        }
        Spacer(modifier = Modifier.size(72.dp))
    }
}


@Composable
private fun BottomButtons(viewModel: LoginViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (viewModel.entryState.value == EntryState.Authentication) {

            TextButton(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
            ) {
                viewModel.restorePassword {
                    viewModel.showSnackbar(context.getString(R.string.password_reset))
                }
            }
        }


        if (viewModel.entryState.value != EntryState.Identification) {
            Spacer(modifier = Modifier.width(16.dp))

            TextButton(
                text = context.getString(R.string.change_login),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
            ) {
                viewModel.changeLogin()
                keyboardController?.show()
            }
        }
    }
}