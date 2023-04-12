package com.security.passwordmanager.presentation.view.screens

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import com.security.passwordmanager.LoadingInBox
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.presentation.model.Time
import com.security.passwordmanager.presentation.model.enums.ColorDesign
import com.security.passwordmanager.presentation.view.BottomSheetState
import com.security.passwordmanager.presentation.view.composablelements.*
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.IconTextItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.TextItem
import com.security.passwordmanager.presentation.view.navigation.createRouteToLoginScreen
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.screenBorderThickness
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState


@ExperimentalToolbarApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.SettingsScreen(
    title: String,
    viewModel: SettingsViewModel,
    fragmentManager: FragmentManager,
    isDarkTheme: Boolean,
    navigateTo: (route: String) -> Unit,
    popBackStack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberCollapsingToolbarScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }

    val showSnackbar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }


    val accountInfoSheetState = createAccountInfoSheet(
        username = viewModel.username,
        usingBeautifulHeading = viewModel.settings.beautifulFont,
        changeUsername = {
            viewModel.showUsernameEditingDialog = true
        },
        signOut = {
            viewModel.signOut()
            viewModel.restoreLogin()
            navigateTo(createRouteToLoginScreen())
        }
    )



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
                actions = {
                    ToolbarButton(
                        icon = Icons.Outlined.ManageAccounts,
                        contentDescription = "manage accounts",
                        iconModifier = Modifier.scale(1.2f),
                        onClick = {
                            viewModel.showBottomSheet(
                                fragmentManager = fragmentManager,
                                bottomSheetState = accountInfoSheetState
                            )
                        }
                    )
                },
            )
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
                    LoadingInBox(Modifier.padding(32.dp))
                }
                SettingsViewModel.State.Ready -> {
                    SettingsContentScreen(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        showFragment = {
                            viewModel.showBottomSheet(
                                fragmentManager,
                                bottomSheetState = this
                            )
                        },
                        showSnackbar = { msg -> showSnackbar(msg) }
                    )
                }
            }
        }
    }
}





@Composable
private fun SettingsContentScreen(
    viewModel: SettingsViewModel,
    isDarkTheme: Boolean,
    showFragment: BottomSheetState.() -> Unit,
    showSnackbar: (message: String) -> Unit
) {
    val context = LocalContext.current

    fun showError(message: String?) {
        if (message != null) showSnackbar(message)
    }


    val themeSheet = createThemeSheet(
        currentDesign = viewModel.settings.colorDesign,
        updateDesign = {
            viewModel.updateSettingsProperty(
                name = Settings::colorDesign.name,
                value = it,
                error = ::showError
            )
        },
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


    val feedbackSheet = feedbackBottomState(
        beautifulDesign = viewModel.settings.beautifulFont
    )




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
                        shadow = Shadow(color = MaterialTheme.colorScheme.secondary.animate()),
                        fontSynthesis = FontSynthesis.Weight
                    ),
                ) {
                    append(
                        text = viewModel.getThemeText(
                            currentTheme = viewModel.settings.colorDesign,
                            isDark = isDarkTheme,
                            context
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
                .clickable { themeSheet.showFragment() }
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
                .clickable { feedbackSheet.showFragment() }
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
            .clickable { onClick(!isChecked) }
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
            onCheckedChange = onClick,
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


@Composable
private fun createThemeSheet(
    currentDesign: ColorDesign,
    updateDesign: (newDesign: ColorDesign) -> Unit,
    showAdditionalContent: Boolean,
    additionalContent: @Composable (ColumnScope.() -> Unit),
) = BottomSheetState { fragment ->

    ColorDesign.values().forEach { design ->
        TextItem(
            text = stringResource(design.titleRes),
            selected = design == currentDesign,
            onClick = {
                if (design != ColorDesign.Auto) fragment.dismiss()
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




@Composable
private fun createAccountInfoSheet(
    username: String,
    usingBeautifulHeading: Boolean,
    changeUsername: () -> Unit,
    signOut: () -> Unit
) =
    BottomSheetState(
        title = buildAnnotatedString {
            if (username.isBlank()) return@buildAnnotatedString

            append(stringResource(R.string.current_user))
            append(" ")

            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(color = MaterialTheme.colorScheme.secondary.animate()),
                    letterSpacing = 3.sp,
                ),
            ) {
                append(username)
            }
        },
        beautifulDesign = usingBeautifulHeading,
    ) { fragment ->
        IconTextItem(
            text = stringResource(R.string.edit_username),
            icon = Icons.Filled.DriveFileRenameOutline,
            iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = {
                fragment.dismiss()
                changeUsername()
            }
        )

        IconTextItem(
            text = stringResource(R.string.logout),
            icon = Icons.Rounded.Logout,
            iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = {
                fragment.dismiss()
                signOut()
            }
        )
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