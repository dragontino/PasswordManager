package com.security.passwordmanager.presentation.view.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.LoadingInBox
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.presentation.model.Time
import com.security.passwordmanager.presentation.model.enums.ColorDesign
import com.security.passwordmanager.presentation.view.composablelements.FeedbackSheetContent
import com.security.passwordmanager.presentation.view.composablelements.RenameDialog
import com.security.passwordmanager.presentation.view.composablelements.ScrollableTopBar
import com.security.passwordmanager.presentation.view.composablelements.ScrollableTopBarScaffold
import com.security.passwordmanager.presentation.view.composablelements.ScrollableTopBarScaffoldDefaults
import com.security.passwordmanager.presentation.view.composablelements.ToolbarButton
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.ModalSheetDefaults
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.IconTextItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.TextItem
import com.security.passwordmanager.presentation.view.navigation.createRouteToLoginScreen
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.screenBorderThickness
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState


@ExperimentalMaterialApi
@ExperimentalToolbarApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
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

    val scaffoldState = rememberCollapsingToolbarScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = ModalSheetDefaults.AnimationSpec,
        skipHalfExpanded = true
    )

    val showSnackbar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    val openBottomSheet = {
        scope.launch {
            delay(50)
            bottomSheetState.show()
        }
    }

    val closeBottomSheet = {
        scope.launch { bottomSheetState.hide() }
    }



    if (viewModel.showUsernameEditingDialog) {
        RenameDialog(
            text = viewModel.usernameInDialog,
            onTextChange = viewModel::usernameInDialog::set,
            onSave = {
                viewModel.saveUsernameFromDialog(context) { message ->
                    showSnackbar(message)
                }
            },
            onDismiss = viewModel::dismissChangingUsernameInDialog
        )
    }



    BackHandler {
        when {
            bottomSheetState.isVisible -> closeBottomSheet()
            else -> popBackStack()
        }
    }



    ModalBottomSheetLayout(
        sheetContent = viewModel.bottomSheetContent,
        sheetState = bottomSheetState,
        sheetShape = ModalSheetDefaults.Shape,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface.animate()
    ) {
        ScrollableTopBarScaffold(
            state = scaffoldState,
            scrollStrategy = ScrollStrategy.EnterAlways,
            topBar = {
                ScrollableTopBar(
                    title = title,
                    navigationButton = {
                        ToolbarButton(
                            icon = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "back screen",
                            onClick = popBackStack
                        )
                    },
                ) {
                    ToolbarButton(
                        icon = Icons.Outlined.ManageAccounts,
                        contentDescription = "manage accounts",
                        iconModifier = Modifier.scale(1.2f)
                    ) {
                        viewModel.bottomSheetContent = {
                            AccountInfoSheetContent(
                                username = viewModel.username,
                                usingBeautifulHeading = viewModel.settings.beautifulFont,
                                changeUsername = {
                                    closeBottomSheet()
                                    viewModel.showUsernameEditingDialog = true
                                },
                                signOut = {
                                    closeBottomSheet()
                                    viewModel.signOut()
                                    viewModel.restoreLogin()
                                    navigateTo(createRouteToLoginScreen())
                                },
                                onClose = { closeBottomSheet() }
                            )
                        }
                        openBottomSheet()
                    }
                }
            },
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
            contentBorder = BorderStroke(
                width = screenBorderThickness,
                brush = Brush.verticalGradient(
                    0.01f to MaterialTheme.colorScheme.onBackground.animate(),
                    0.08f to MaterialTheme.colorScheme.background.animate()
                ),
            ),
            onRefresh = popBackStack,
            isPullRefreshEnabled = viewModel.settings.pullToRefresh,
            pullRefreshIndicator = {
                Icon(
                    imageVector = Icons.Outlined.ArrowCircleLeft,
                    contentDescription = "return",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .scale(it)
                )
            },
            colors = ScrollableTopBarScaffoldDefaults.colors(
                topBarColor = MaterialTheme.colorScheme.background.animate(durationMillis = 400),
                topBarTitleContentColor = MaterialTheme.colorScheme.onBackground.animate(400),
                topBarNavigationIconContentColor = MaterialTheme.colorScheme.secondary.animate(400),
                topBarActionIconContentColor = MaterialTheme.colorScheme.secondary.animate(400),
                containerColor = MaterialTheme.colorScheme.background.animate()
            ),
            contentModifier = Modifier
                .padding(horizontal = 1.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Crossfade(
                targetState = viewModel.viewModelState,
                animationSpec = tween(durationMillis = 700)
            ) {
                when (it) {
                    SettingsViewModel.State.Loading -> {
                        LoadingInBox(Modifier.padding(40.dp))
                    }

                    SettingsViewModel.State.Ready -> {
                        SettingsContentScreen(
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            showSnackbar = { msg -> showSnackbar(msg) },
                            openBottomSheet = { content ->
                                viewModel.bottomSheetContent = content
                                openBottomSheet()
                            },
                            hideBottomSheet = { closeBottomSheet() }
                        )
                    }
                }
            }
        }
    }
}





@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
private fun SettingsContentScreen(
    viewModel: SettingsViewModel,
    isDarkTheme: Boolean,
    openBottomSheet: (content: @Composable ColumnScope.() -> Unit) -> Unit,
    hideBottomSheet: () -> Unit,
    showSnackbar: (message: String) -> Unit
) {
    val context = LocalContext.current

    fun showError(message: String?) {
        if (message != null) showSnackbar(message)
    }


    val themeSheetContent: @Composable (ColumnScope.() -> Unit) = {
        ThemeSheetContent(
            currentDesign = viewModel.settings.colorDesign,
            updateDesign = {
                viewModel.updateSettingsProperty(
                    name = Settings::colorDesign.name,
                    value = it,
                    error = ::showError
                )
            },
            onClose = hideBottomSheet,
            showAdditionalContent = viewModel.settings.colorDesign == ColorDesign.Auto
        ) {
            Divider()

            Row(modifier = Modifier.fillMaxWidth()) {
                Time(
                    time = viewModel.settings.sunriseTime,
                    title = stringResource(R.string.sunrise_time),
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.updateSettingsProperty(
                        name = Settings::sunriseTime.name,
                        value = it,
                        error = ::showError
                    )
                }

                Time(
                    time = viewModel.settings.sunsetTime,
                    title = stringResource(R.string.sunset_time),
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.updateSettingsProperty(
                        name = Settings::sunsetTime.name,
                        value = it,
                        error = ::showError
                    )
                }
            }
        }
    }




    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.switchThemeText) + " ")
//                if (viewModel.switchThemeTextLineCount > 1) append("\n")

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
                        text = viewModel.getThemeText(
                            currentTheme = viewModel.settings.colorDesign,
                            isDark = isDarkTheme,
                            context = context
                        )
                    )
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            textAlign = TextAlign.Start,
            onTextLayout = {
//                println("lineCount = ${it.lineCount}")
                viewModel.switchThemeTextLineCount = it.lineCount
            },
            modifier = Modifier
                .clickable { openBottomSheet(themeSheetContent) }
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxWidth()
        )

        Divider()

        SwitchItem(
            isChecked = viewModel.settings.beautifulFont,
            title = stringResource(R.string.beautiful_font),
        ) {
            viewModel.updateSettingsProperty(
                name = Settings::beautifulFont.name,
                value = it,
                error = ::showError
            )
        }

        Divider()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SwitchItem(
                isChecked = viewModel.settings.dynamicColor,
                title = stringResource(R.string.dynamic_color),
                subtitle = stringResource(R.string.dynamic_color_desc)
            ) {
                viewModel.updateSettingsProperty(
                    Settings::dynamicColor.name,
                    value = it,
                    error = ::showError
                )
            }

            Divider()
        }

        SwitchItem(
            isChecked = viewModel.settings.pullToRefresh,
            title = stringResource(R.string.pull_to_refresh),
            subtitle = stringResource(R.string.pull_to_refresh_desc)
        ) {
            viewModel.updateSettingsProperty(
                name = Settings::pullToRefresh.name,
                value = it,
                error = ::showError
            )
        }

        Divider()

        Text(
            text = stringResource(R.string.feedback),
            color = MaterialTheme.colorScheme.onBackground.animate(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .clickable {
                    openBottomSheet {
                        FeedbackSheetContent(
                            beautifulDesign = viewModel.settings.beautifulFont,
                            onClose = hideBottomSheet
                        )
                    }
                }
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}






@Composable
private fun SwitchItem(
    isChecked: Boolean,
    title: String,
    subtitle: String = "",
    onClick: (isChecked: Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background.animate())
            .toggleable(
                value = isChecked,
                role = Role.Switch,
                onValueChange = onClick
            )
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(2f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.animate()
            )

            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
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


@ExperimentalMaterial3Api
@Composable
private fun ColumnScope.ThemeSheetContent(
    currentDesign: ColorDesign,
    updateDesign: (newDesign: ColorDesign) -> Unit,
    onClose: () -> Unit,
    showAdditionalContent: Boolean,
    additionalContent: @Composable (ColumnScope.() -> Unit),
) = BottomSheetContent {
    ColorDesign.values().forEach { design ->
        TextItem(
            text = stringResource(design.titleRes),
            selected = design == currentDesign,
            onClick = {
                if (design != ColorDesign.Auto) onClose()
                updateDesign(design)
            }
        )
    }

    AnimatedVisibility(
        visible = showAdditionalContent,
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
            additionalContent()
        }
    }
}




@ExperimentalMaterial3Api
@Composable
private fun ColumnScope.AccountInfoSheetContent(
    username: String,
    usingBeautifulHeading: Boolean,
    changeUsername: () -> Unit,
    signOut: () -> Unit,
    onClose: () -> Unit
) = BottomSheetContent(
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
        iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        onClose()
        changeUsername()
    }

    IconTextItem(
        text = stringResource(R.string.logout),
        icon = Icons.Rounded.Logout,
        iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        onClose()
        signOut()
    }
}





@Preview
@Composable
private fun SwitchItemPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            SwitchItem(
                title = "Wrecked",
                subtitle = "Imagine Dragons",
                isChecked = true,
                onClick = {}
            )
            
            Spacer(modifier = Modifier.size(16.dp))
            
            SwitchItem(
                isChecked = false,
                title = "Wrecked",
                subtitle = "Imagine Dragons",
                onClick = {}
            )

            Spacer(modifier = Modifier.size(16.dp))

            PasswordManagerTheme(isDarkTheme = true) {
                SwitchItem(
                    title = "Wrecked",
                    subtitle = "Imagine Dragons",
                    isChecked = true,
                    onClick = {}
                )

                Spacer(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .height(16.dp)
                        .fillMaxWidth(),
                )

                SwitchItem(
                    isChecked = false,
                    title = "Wrecked",
                    subtitle = "Imagine Dragons",
                    onClick = {}
                )
            }
        }
    }
}