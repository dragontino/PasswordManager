package com.security.passwordmanager.view.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.material.icons.rounded.Upgrade
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.domain.model.isValidPassword
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.AppVersionInfo
import com.security.passwordmanager.domain.model.ColorScheme
import com.security.passwordmanager.domain.model.Settings
import com.security.passwordmanager.model.Header
import com.security.passwordmanager.util.LoadingInBox
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.util.reversed
import com.security.passwordmanager.view.composables.DataTextFieldDefaults
import com.security.passwordmanager.view.composables.EditableDataTextField
import com.security.passwordmanager.view.composables.FeedbackBottomSheet
import com.security.passwordmanager.view.composables.TextButton
import com.security.passwordmanager.view.composables.Time
import com.security.passwordmanager.view.composables.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.view.composables.dialogs.ConfirmChangeDialog
import com.security.passwordmanager.view.composables.dialogs.DialogType
import com.security.passwordmanager.view.composables.dialogs.RenameDialog
import com.security.passwordmanager.view.composables.dialogs.TimePickerDialog
import com.security.passwordmanager.view.composables.managment.ScreenEvents
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffold
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffoldDefaults
import com.security.passwordmanager.view.composables.scaffold.ToolbarButton
import com.security.passwordmanager.view.composables.scaffold.ToolbarButtonDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingAppBarDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingToolbar
import com.security.passwordmanager.view.composables.sheets.ModalBottomSheet
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.IconTextItem
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.ImageTextItem
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.TextItem
import com.security.passwordmanager.view.navigation.LoginScreen
import com.security.passwordmanager.view.theme.HarrowFont
import com.security.passwordmanager.view.theme.Orange
import com.security.passwordmanager.view.theme.PacificFont
import com.security.passwordmanager.view.theme.PasswordManagerTheme
import com.security.passwordmanager.view.theme.ScreenContentAnimation
import com.security.passwordmanager.view.theme.ScreenToolbarAnimation
import com.security.passwordmanager.viewmodel.SettingsViewModel
import com.security.passwordmanager.viewmodel.ViewModelState
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AnimatedVisibilityScope.SettingsScreen(
    title: String,
    viewModel: SettingsViewModel,
    isDarkTheme: Boolean,
    navigateTo: (route: String) -> Unit,
    popBackStack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val contentState = rememberLazyListState()

    val snackbarHostState = remember(::SnackbarHostState)
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showSnackbar = remember {
        fun(message: String) {
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    BackHandler {
        viewModel.bottomSheetContent.value
            ?.let { viewModel.closeBottomSheet() }
            ?: viewModel.navigateTo(null)
    }


    var dialogType: DialogType? by rememberSaveable { mutableStateOf(null) }


    LaunchedEffect(key1 = true) {
        viewModel.eventsFlow.collect { event ->
            when (event) {
                is ScreenEvents.OpenDialog -> dialogType = event.type
                ScreenEvents.CloseDialog -> dialogType = null
                is ScreenEvents.Navigate -> (event.args as? String)
                    ?.let(navigateTo)
                    ?: popBackStack()
                is ScreenEvents.ShowSnackbar -> showSnackbar(event.message)
            }
        }
    }

    dialogType?.Dialog()



    CollapsingToolbarScaffold(
        contentState = contentState,
        contentModifier = Modifier
            .animateEnterExit(
                enter = ScreenContentAnimation.enter,
                exit = ScreenContentAnimation.exit
            )
            .padding(horizontal = 1.dp)
            .fillMaxSize(),
        topBar = {
            CollapsingToolbar(
                title = title,
                navigationButton = {
                    ToolbarButton(
                        icon = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "back screen",
                        colors = ToolbarButtonDefaults.colors(
                            contentColor = MaterialTheme.colorScheme.secondary,
                            transparentContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        onClick = popBackStack
                    )
                },
                modifier = Modifier.animateEnterExit(
                    enter = ScreenToolbarAnimation.enter,
                    exit = ScreenToolbarAnimation.exit
                )
            ) {
                ToolbarButton(
                    icon = Icons.Outlined.ManageAccounts,
                    contentDescription = "manage accounts",
                    iconModifier = Modifier.scale(1.2f),
                    enabled = viewModel.state != ViewModelState.Loading,
                    colors = ToolbarButtonDefaults.colors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        transparentContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = {
                        viewModel.openBottomSheet {
                            AccountInfoBottomSheet(
                                state = bottomSheetState,
                                isDarkTheme = isDarkTheme,
                                username = viewModel.username,
                                usingBeautifulHeading = viewModel.settings.beautifulFont,
                                changeUsername = {
                                    viewModel.closeBottomSheet()
                                    viewModel.openDialog(
                                        type = RenameDialog(
                                            text = viewModel::usernameInDialog,
                                            onTextChange = viewModel::usernameInDialog::set,
                                            onSave = {
                                                viewModel.saveUsernameFromDialog {
                                                    viewModel.showSnackbar(
                                                        context.getString(R.string.change_username_successful)
                                                    )
                                                }
                                            },
                                            onDismiss = { viewModel.usernameInDialog = viewModel.username },
                                            onClose = viewModel::closeDialog
                                        )
                                    )
                                },
                                changePassword = {
                                    viewModel.closeBottomSheet()
                                    viewModel.openBottomSheet {
                                        ChangingPasswordBottomSheet(
                                            state = bottomSheetState,
                                            useBeautifulFont = viewModel.settings.beautifulFont,
                                            openDialog = viewModel::openDialog,
                                            onDismiss = viewModel::closeBottomSheet,
                                            closeDialog = viewModel::closeDialog,
                                            onConfirm = { oldPassword, newPassword ->
                                                viewModel.closeBottomSheet()
                                                viewModel.changePassword(
                                                    oldPassword = oldPassword,
                                                    newPassword = newPassword
                                                ) {
                                                    viewModel.showSnackbar(
                                                        context.getString(R.string.changing_password_successful)
                                                    )
                                                }
                                            }
                                        )
                                    }
                                },
                                signOut = {
                                    viewModel.closeBottomSheet()
                                    viewModel.signOut()
                                    viewModel.navigateTo(LoginScreen.createUrl())
                                },
                                onClose = viewModel::closeBottomSheet
                            )
                        }
                    }
                )
            }
        },
        topBarScrollBehavior = CollapsingAppBarDefaults.onTopOfContentScrollBehavior(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    actionOnNewLine = true,
                    shape = MaterialTheme.shapes.medium,
                    actionColor = MaterialTheme.colorScheme.primary.animate(),
                    containerColor = MaterialTheme.colorScheme.onBackground.animate(),
                    contentColor = MaterialTheme.colorScheme.background.animate()
                )
            }
        },
        onRefresh = popBackStack,
        pullToRefreshIndicator = { pullToRefreshState ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowCircleLeft,
                    contentDescription = "return",
                    tint = MaterialTheme.colorScheme.onBackground.reversed,
                    modifier = Modifier
                        .graphicsLayer(
                            alpha = pullToRefreshState.progress,
                            scaleY = pullToRefreshState.progress,
                            scaleX = pullToRefreshState.progress
                        )
                        .fillMaxSize()
                )
            }
        },
        pullToRefreshEnabled = viewModel.settings.pullToRefresh,
        colors = CollapsingToolbarScaffoldDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background.animate(760),
            toolbarContainerColor = MaterialTheme.colorScheme.background,
            transparentToolbarContainerColor = MaterialTheme.colorScheme.background,
            toolbarTitleContentColor = MaterialTheme.colorScheme.onBackground,
            transparentToolbarTitleContentColor = MaterialTheme.colorScheme.onBackground
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            viewModel.bottomSheetContent.value?.invoke()

            SettingsContentScreen(
                state = contentState,
                viewModel = viewModel,
                isDarkTheme = isDarkTheme
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = viewModel.state == ViewModelState.Loading,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 350)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 350)
                )
            ) {
                LoadingInBox(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = .6f),
                    contentColor = MaterialTheme.colorScheme.secondary,
                    loadingModifier = Modifier.scale(2.2f),
                    modifier = Modifier.clickable(
                        interactionSource = remember(::MutableInteractionSource),
                        indication = null,
                        onClick = {}
                    )
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContentScreen(
    state: LazyListState,
    viewModel: SettingsViewModel,
    isDarkTheme: Boolean,
) {
    val context = LocalContext.current
    val showThemeBottomSheet = rememberSaveable { mutableStateOf(false) }

    if (showThemeBottomSheet.value) {
        SchemeBottomSheet(
            currentScheme = viewModel.settings.colorScheme,
            updateScheme = {
                viewModel.updateSettingsProperty(
                    property = Settings::colorScheme,
                    value = it
                )
            },
            onClose = { showThemeBottomSheet.value = false },
            showTimes = viewModel.settings.colorScheme == ColorScheme.Auto
        ) {
            HorizontalDivider()

            Row(modifier = Modifier.fillMaxWidth()) {
                Time(
                    time = viewModel.settings.sunriseTime,
                    title = stringResource(R.string.sunrise_time),
                    modifier = Modifier.weight(1f)
                ) { time, title ->
                    viewModel.openDialog(
                        TimePickerDialog(
                            time = time,
                            title = title,
                            onConfirm = {
                                viewModel.updateSettingsProperty(
                                    property = Settings::sunriseTime,
                                    value = it
                                )
                            },
                            onClose = viewModel::closeDialog
                        )
                    )

                }

                Time(
                    time = viewModel.settings.sunsetTime,
                    title = stringResource(R.string.sunset_time),
                    modifier = Modifier.weight(1f)
                ) { time, title ->
                    viewModel.openDialog(
                        TimePickerDialog(
                            time = time,
                            title = title,
                            onConfirm = {
                                viewModel.updateSettingsProperty(
                                    property = Settings::sunsetTime,
                                    value = it
                                )
                            },
                            onClose = viewModel::closeDialog
                        )
                    )
                }
            }
        }
    }

    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ShadowElevatedCard {
                SwitchItem(
                    isChecked = viewModel.settings.beautifulFont,
                    header = Header(
                        title = stringResource(R.string.beautiful_font)
                    ),
                    enabled = viewModel.state != ViewModelState.Loading
                ) {
                    viewModel.updateSettingsProperty(
                        property = Settings::beautifulFont,
                        value = it
                    )
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SwitchItem(
                        isChecked = viewModel.settings.dynamicColor,
                        header = Header(
                            title = stringResource(R.string.dynamic_color),
                            subtitle = stringResource(R.string.dynamic_color_desc)
                        ),
                        enabled = viewModel.state != ViewModelState.Loading
                    ) {
                        viewModel.updateSettingsProperty(
                            Settings::dynamicColor,
                            value = it
                        )
                    }
                }


                SwitchItem(
                    isChecked = viewModel.settings.pullToRefresh,
                    header = Header(
                        title = stringResource(R.string.pull_to_refresh),
                        subtitle = stringResource(R.string.pull_to_refresh_desc)
                    ),
                    enabled = viewModel.state != ViewModelState.Loading
                ) {
                    viewModel.updateSettingsProperty(
                        property = Settings::pullToRefresh,
                        value = it
                    )
                }


                SwitchItem(
                    isChecked = viewModel.settings.loadIcons,
                    header = Header(
                        title = stringResource(R.string.load_icons)
                    ),
                    enabled = viewModel.state != ViewModelState.Loading
                ) {
                    viewModel.updateSettingsProperty(
                        property = Settings::loadIcons,
                        value = it
                    )
                }
            }
        }


        item {
            ShadowElevatedCard(
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(enabled = viewModel.state != ViewModelState.Loading) {
                            showThemeBottomSheet.value = true
                        }
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(
                                stringResource(R.string.switchThemeText), " "
                            )

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.7.sp,
                                    shadow = Shadow(
                                        color = MaterialTheme.colorScheme.primary.animate(),
                                        blurRadius = 0.4f
                                    ),
                                    fontSynthesis = FontSynthesis.Weight
                                ),
                            ) {
                                append(
                                    text = viewModel.constructThemeText(
                                        currentTheme = viewModel.settings.colorScheme,
                                        isDark = isDarkTheme,
                                        context = context
                                    )
                                )
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        lineHeight = 22.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(10f)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.animate(),
                        modifier = Modifier
                            .scale(.6f)
                            .weight(1f)
                    )
                }
            }
        }

        item {
            ShadowElevatedCard {
                Text(
                    text = stringResource(R.string.check_app_updates),
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(enabled = viewModel.state != ViewModelState.Loading) {
                            viewModel.checkAppUpdates { isLatest, latestVersionInfo ->
                                when (latestVersionInfo) {
                                    null -> viewModel.showSnackbar(
                                        context.getString(R.string.check_app_updates_exception)
                                    )

                                    else -> viewModel.openBottomSheet {
                                        CheckAppUpdatesBottomSheet(
                                            currentVersionName = viewModel.getAppVersionName(),
                                            isCurrentVersionIsLast = isLatest,
                                            latestVersionInfo = latestVersionInfo,
                                            onClose = viewModel::closeBottomSheet,
                                            useBeautifulFont = viewModel.settings.beautifulFont
                                        ) { address ->
                                            Intent(Intent.ACTION_VIEW, Uri.parse(address)).let {
                                                context.startActivity(it)
                                            }
                                            viewModel.closeBottomSheet()
                                        }
                                    }
                                }

                            }
                        }
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.feedback),
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            viewModel.openBottomSheet {
                                FeedbackBottomSheet(
                                    beautifulDesign = viewModel.settings.beautifulFont,
                                    isDarkTheme = isDarkTheme,
                                    onClose = viewModel::closeBottomSheet
                                )
                            }
                        }
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}


@Composable
private fun ShadowElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    containerColor: Color = MaterialTheme.colorScheme.surfaceTint,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    shadowElevation: Dp = 2.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 4.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    content: @Composable (ColumnScope.() -> Unit)
) {
    Surface(
        shape = shape,
        color = containerColor.animate(durationMillis = 300),
        contentColor = contentColor.animate(durationMillis = 300),
        shadowElevation = shadowElevation,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}



@Composable
private fun SwitchItem(
    isChecked: Boolean,
    header: Header,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (isChecked: Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .toggleable(
                value = isChecked,
                role = Role.Switch,
                enabled = enabled,
                onValueChange = onClick
            )
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(2f)) {
            Text(
                text = header.title as String,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.animate()
            )

            if (header.subtitle.isNotBlank()) {
                Text(
                    text = header.subtitle as String,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f).animate()
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary.animate(),
                checkedTrackColor = MaterialTheme.colorScheme.primary.animate(),
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer.animate(),
                uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer.animate(),
                uncheckedBorderColor = MaterialTheme.colorScheme.secondary.animate(),
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchemeBottomSheet(
    currentScheme: ColorScheme,
    updateScheme: (newScheme: ColorScheme) -> Unit,
    onClose: () -> Unit,
    showTimes: Boolean,
    times: @Composable (ColumnScope.() -> Unit),
) {
    val context = LocalContext.current

    ModalBottomSheet(onClose = onClose) {
        ColorScheme.entries.forEach { design ->
            TextItem(
                text = context.getString(design.titleRes),
                selected = design == currentScheme,
                onClick = {
                    if (design != ColorScheme.Auto) onClose()
                    updateScheme(design)
                }
            )
        }

        AnimatedVisibility(
            visible = showTimes,
            enter = expandVertically(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            ),
            exit = shrinkVertically(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            ) { it / 2 }
        ) {
            Column {
                Spacer(modifier = Modifier.size(8.dp))
                times()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountInfoBottomSheet(
    state: SheetState,
    isDarkTheme: Boolean,
    username: String,
    usingBeautifulHeading: Boolean,
    changeUsername: () -> Unit,
    changePassword: () -> Unit,
    signOut: () -> Unit,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        state = state,
        onClose = onClose,
        title = buildAnnotatedString {
            if (username.isBlank()) return@buildAnnotatedString

            append(stringResource(R.string.current_user))
            append(" ")

            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.primary.animate(),
                        blurRadius = 0.6f
                    ),
                    fontFamily = FontFamily(HarrowFont),
                    letterSpacing = 3.sp,
                ),
            ) {
                append(username)
            }
        },
        beautifulDesign = usingBeautifulHeading,
    ) {
        IconTextItem(
            text = stringResource(R.string.edit_username),
            icon = Icons.Filled.DriveFileRenameOutline,
            iconTintColor = Orange,
            modifier = Modifier.padding(vertical = 16.dp),
            iconModifier = Modifier.width(48.dp)
        ) {
            onClose()
            changeUsername()
        }

        ImageTextItem(
            text = stringResource(R.string.change_password),
            image = when {
                isDarkTheme -> R.drawable.change_password_logo_white
                else -> R.drawable.change_password_logo
            },
            imageModifier = Modifier.width(48.dp)
        ) {
            onClose()
            changePassword()
        }

        IconTextItem(
            text = stringResource(R.string.logout),
            icon = Icons.AutoMirrored.Rounded.Logout,
            iconTintColor = Orange,
            modifier = Modifier.padding(vertical = 16.dp),
            iconModifier = Modifier.width(48.dp)
        ) {
            onClose()
            signOut()
        }
    }
}




@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CheckAppUpdatesBottomSheet(
    currentVersionName: String,
    isCurrentVersionIsLast: Boolean,
    latestVersionInfo: AppVersionInfo,
    useBeautifulFont: Boolean,
    onClose: () -> Unit,
    openAddress: (String) -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurface.animate()

    ModalBottomSheet(onClose = onClose) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight()
        ) {
            Icon(
                imageVector = when {
                    isCurrentVersionIsLast -> Icons.Rounded.DoneOutline
                    else -> Icons.Rounded.Upgrade
                },
                contentDescription = "icon",
                tint = contentColor,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 1.7.dp,
                        color = contentColor,
                        shape = CircleShape
                    )
                    .size(130.dp)
                    .padding(16.dp)
            )

            // TODO: 21.03.2024 переделать
            ClickableText(
                text = buildAnnotatedString {
                    when {
                        isCurrentVersionIsLast -> {
                            append(stringResource(R.string.current_app_version_is_latest))
                        }
                        else -> {
                            append(
                                stringResource(
                                    R.string.update_available,
                                    AnnotatedString(
                                        text = latestVersionInfo.name,
                                        spanStyle = SpanStyle(
                                            fontFamily = when {
                                                useBeautifulFont -> FontFamily(PacificFont)
                                                else -> MaterialTheme.typography.titleMedium.fontFamily
                                            }
                                        )
                                    ),
                                ),
                                "\n",
                                stringResource(R.string.downloading_update_instructions),
                                "\n"
                            )

                            pushUrlAnnotation(
                                UrlAnnotation(latestVersionInfo.url)
                            )

                            withStyle(
                                SpanStyle(
                                    color = Color.Blue.copy(alpha = .7f),
                                    fontSize = 15.sp,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(latestVersionInfo.url)
                            }

                            pop()
                        }
                    }
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    color = contentColor,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center
                ),
            ) {
                openAddress(latestVersionInfo.url)
            }

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = buildString {
                    append(stringResource(R.string.current_version_number, currentVersionName))
                    if (!isCurrentVersionIsLast) {
                        append(
                            "\n",
                            stringResource(R.string.latest_version_number, latestVersionInfo.name)
                        )
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                lineHeight = 24.sp
            )
        }
    }
}


// TODO: 01.09.2023 поменять на полноценный экран

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangingPasswordBottomSheet(
    state: SheetState,
    useBeautifulFont: Boolean,
    openDialog: (type: DialogType) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (oldPassword: String,  newPassword: String) -> Unit,
    closeDialog: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var repeatedPassword by rememberSaveable { mutableStateOf("") }

    var showOldPassword by rememberSaveable { mutableStateOf(false) }
    var showNewPassword by rememberSaveable { mutableStateOf(false) }
    var showRepeatedPassword by rememberSaveable { mutableStateOf(false) }


    val confirmChangeDialogType = ConfirmChangeDialog(
        text = stringResource(R.string.changing_password_confirmation),
        confirmButtonText = stringResource(R.string.change_password),
        onConfirm = {
            closeDialog()
            onConfirm(oldPassword, newPassword)
        },
        onDismiss = closeDialog
    )

    ModalBottomSheet(
        state = state,
        onClose = onDismiss,
        title = stringResource(R.string.changing_password),
        beautifulDesign = useBeautifulFont
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            EditableDataTextField(
                text = oldPassword,
                onTextChange = { oldPassword = it },
                label = stringResource(R.string.old_password),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                visualTransformation = DataTextFieldDefaults
                    .passwordVisualTransformation(isVisible = showOldPassword),
                trailingActions = {
                    VisibilityIconButton(
                        visible = showOldPassword,
                        changeVisibility = { showOldPassword = it }
                    )
                }
            )

            EditableDataTextField(
                text = newPassword,
                onTextChange = { newPassword = it },
                label = stringResource(R.string.new_password),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                visualTransformation = DataTextFieldDefaults
                    .passwordVisualTransformation(isVisible = showNewPassword),
                error = newPassword.isValidPassword(),
                errorMessage = stringResource(R.string.password_length_exception),
                trailingActions = {
                    VisibilityIconButton(
                        visible = showNewPassword,
                        changeVisibility = { showNewPassword = it })
                }
            )

            EditableDataTextField(
                text = repeatedPassword,
                onTextChange = { repeatedPassword = it },
                label = stringResource(R.string.new_repeated_password),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeActionClick = { focusManager.clearFocus() },
                visualTransformation = DataTextFieldDefaults
                    .passwordVisualTransformation(isVisible = showRepeatedPassword),
                error = newPassword != repeatedPassword && repeatedPassword.isNotBlank(),
                errorMessage = stringResource(R.string.passwords_is_not_equals),
                trailingActions = {
                    VisibilityIconButton(
                        visible = showRepeatedPassword,
                        changeVisibility = { showRepeatedPassword = it }
                    )
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    text = stringResource(R.string.cancel),
                    textColor = MaterialTheme.colorScheme.secondary,
                    onClick = onDismiss
                )

                TextButton(
                    text = stringResource(R.string.change_password),
                    textColor = MaterialTheme.colorScheme.secondary,
                    enabled = newPassword == repeatedPassword
                            && newPassword.isValidPassword()
                ) {
                    if (newPassword == repeatedPassword && newPassword.isNotBlank() && newPassword.length > 6) {
                        openDialog(confirmChangeDialogType)
                    }
                }
            }
        }
    }
}




@Preview
@Composable
private fun SwitchItemPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShadowElevatedCard(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SwitchItem(
                    header = Header(
                        title = "Wrecked",
                        subtitle = "Imagine Dragons"
                    ),
                    isChecked = true,
                    onClick = {}
                )

                SwitchItem(
                    isChecked = false,
                    header = Header(
                        title = "Wrecked",
                        subtitle = "Imagine Dragons"
                    ),
                    onClick = {}
                )
            }

            PasswordManagerTheme(isDarkTheme = true) {
                ShadowElevatedCard(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SwitchItem(
                        header = Header(
                            title = "Wrecked",
                            subtitle = "Imagine Dragons"
                        ),
                        isChecked = true,
                        onClick = {}
                    )

                    SwitchItem(
                        isChecked = false,
                        header = Header(
                            title = "Wrecked",
                            subtitle = "Imagine Dragons"
                        ),
                        onClick = {}
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AccountInfoSheetPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        AccountInfoBottomSheet(
            state = rememberModalBottomSheetState(),
            isDarkTheme = true,
            username = "Test User",
            usingBeautifulHeading = true,
            changeUsername = {},
            changePassword = {},
            signOut = {},
            onClose = {}
        )
    }
}



@Preview
@Composable
private fun CheckAppUpdatesBottomSheetPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        Column(
            Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            CheckAppUpdatesBottomSheet(
                currentVersionName = "6.3.0",
                isCurrentVersionIsLast = false,
                latestVersionInfo = AppVersionInfo(name = "7.0.0", url = "test.url"),
                useBeautifulFont = true,
                openAddress = {},
                onClose = {}
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ChangingPasswordBottomSheetPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
            ChangingPasswordBottomSheet(
                state = rememberModalBottomSheetState(),
                useBeautifulFont = true,
                openDialog = {},
                onDismiss = {},
                onConfirm = { _, _ -> },
                closeDialog = {}
            )
        }
    }
}