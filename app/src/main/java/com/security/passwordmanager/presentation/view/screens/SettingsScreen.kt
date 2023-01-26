package com.security.passwordmanager.presentation.view.screens

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.presentation.model.Times
import com.security.passwordmanager.presentation.model.enums.Themes
import com.security.passwordmanager.presentation.view.BottomSheetFragment
import com.security.passwordmanager.presentation.view.composablelements.RenameDialog
import com.security.passwordmanager.presentation.view.composablelements.ToolbarScaffold
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.FeedbackSheet
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.IconTextItem
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

    val screenShape = MaterialTheme.shapes.large.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    )

    val bottomSheetFragment = BottomSheetFragment {
        viewModel.bottomSheetContent(this, it)
    }


    val showSnackbar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }



    if (viewModel.showUsernameEditingDialog) {
        RenameDialog(
            text = viewModel.usernameInDialog,
            onTextChange = viewModel::usernameInDialog::set,
            onSave = {
                viewModel.saveUsernameFromDialog(context) { result ->
                    when (result) {
                        Result.Loading ->
                            viewModel.viewModelState = SettingsViewModel.State.Loading
                        is Result.Error -> {
                            showSnackbar(context.getString(R.string.change_username_exception))
                            viewModel.viewModelState = SettingsViewModel.State.Ready
                        }
                        is Result.Success -> {
                            showSnackbar(context.getString(R.string.change_username_successful))
                            viewModel.viewModelState = SettingsViewModel.State.Ready
                        }
                    }
                }
            },
            onDismiss = viewModel::dismissChangingUsernameInDialog
        )
    }



    ToolbarScaffold(
        state = scaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlways,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = popBackStack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "back",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.bottomSheetContent = {
                                AccountInfoSheet(
                                    username = viewModel.currentUsername,
                                    usingBeautifulHeading = viewModel.settings.isUsingBeautifulFont,
                                    signOut = {
                                        it.dismiss()
                                        viewModel.signOut()
                                        viewModel.clearEmail()
                                        navigateTo(createRouteToLoginScreen())
                                    },
                                    changeUsername = {
                                        viewModel.showUsernameEditingDialog = true
                                        it.dismiss()
                                    }
                                )
                            }
                            bottomSheetFragment.show(fragmentManager)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ManageAccounts,
                            contentDescription = "manage accounts",
                            modifier = Modifier.scale(1.3f)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.animate(durationMillis = 400),
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary.animate(),
                    actionIconContentColor = MaterialTheme.colorScheme.secondary.animate(),
                    titleContentColor = MaterialTheme.colorScheme.onBackground.animate(400),
                ),
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
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 1.dp)
                .animateEnterExit(
                    enter = EnterContentAnimation,
                    exit = ExitContentAnimation
                )
                .clip(screenShape)
                .background(
                    color = MaterialTheme.colorScheme.background.animate(),
                    shape = screenShape
                )
                .border(
                    width = screenBorderThickness,
                    brush = Brush.verticalGradient(
                        0.01f to MaterialTheme.colorScheme.onBackground.animate(),
                        0.08f to MaterialTheme.colorScheme.background.animate()
                    ),
                    shape = screenShape
                )
        ) {
            Crossfade(
                targetState = viewModel.viewModelState,
                animationSpec = tween(durationMillis = 700)
            ) {
                when (it) {
                    SettingsViewModel.State.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(64.dp)
                                .fillMaxSize()
                                .align(Alignment.Center)
                        ) {
                            Loading()
                        }
                    }
                    SettingsViewModel.State.Ready -> {
                        Column {
                            SettingsContentScreen(
                                viewModel = viewModel,
                                isDarkTheme = isDarkTheme,
                                showBottomFragment = { bottomSheetFragment.show(fragmentManager) },
                                showSnackbar = { msg -> showSnackbar(msg) }
                            )
                        }
                    }
                }
            }
        }
    }
}





@Composable
private fun ColumnScope.SettingsContentScreen(
    viewModel: SettingsViewModel,
    isDarkTheme: Boolean,
    showBottomFragment: () -> Unit,
    showSnackbar: (message: String) -> Unit
) {
    val context = LocalContext.current

    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.switchThemeText) + " ")
            if (viewModel.switchThemeTextLineCount > 1) append("\n")

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
                        currentTheme = viewModel.settings.theme,
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
            println("lineCount = ${it.lineCount}")
            viewModel.switchThemeTextLineCount = it.lineCount
        },
        modifier = Modifier
            .clickable {
                viewModel.bottomSheetContent = { fragment ->
                    ThemeSheet(
                        currentTheme = viewModel.settings.theme,
                        updateTheme = {
                            viewModel.updateTheme(it) { result ->
                                when (result) {
                                    Result.Loading ->
                                        viewModel.viewModelState = SettingsViewModel.State.Loading
                                    is Result.Error -> {
                                        showSnackbar(
                                            context.getString(R.string.change_theme_exception)
                                        )
                                        viewModel.viewModelState = SettingsViewModel.State.Ready
                                    }
                                    is Result.Success -> {
                                        viewModel.viewModelState = SettingsViewModel.State.Ready
                                        if (result.data != Themes.Auto)
                                            fragment.dismiss()
                                    }
                                }
                            }
                            if (it != Themes.Auto) fragment.dismiss()
                        },
                        showAdditionalContent = viewModel.settings.theme == Themes.Auto
                    ) {
                        Times(times = viewModel.times) {
                            viewModel.updateTimes(it)
                        }
                    }
                }
                showBottomFragment()
            }
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth()
    )

    Divider()

    SwitchItem(
        isChecked = viewModel.settings.isUsingBeautifulFont,
        title = stringResource(R.string.beautiful_font),
        onClick = viewModel::updateUsingBeautifulFont,
    )

    Divider()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        SwitchItem(
            isChecked = viewModel.settings.dynamicColor,
            title = stringResource(R.string.dynamic_color),
            subtitle = stringResource(R.string.dynamic_color_description),
            onClick = viewModel::updateDynamicColor
        )

        Divider()
    }

    Text(
        text = stringResource(R.string.feedback),
        color = MaterialTheme.colorScheme.onBackground.animate(),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .clickable {
                viewModel.bottomSheetContent = { fragment ->
                    FeedbackSheet(
                        context = context,
                        beautifulDesign = viewModel.settings.isUsingBeautifulFont
                    ) {
                        fragment.dismiss()
                    }
                }
                showBottomFragment()
            }
            .align(Alignment.CenterHorizontally)
            .padding(16.dp)
            .fillMaxWidth()
    )
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
private fun ThemeSheet(
    currentTheme: Themes,
    updateTheme: (newTheme: Themes) -> Unit,
    showAdditionalContent: Boolean,
    additionalContent: @Composable ColumnScope.() -> Unit,
) {
    val horizontalPadding = 16.dp

    BottomSheetContent {
        Themes.values().forEach { theme ->
            IconTextItem(
                text = stringResource(theme.titleRes),
                icon = if (theme == currentTheme) Icons.Rounded.CheckCircle else null,
                selected = theme == currentTheme,
                iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
                iconAlignment = Alignment.End,
                padding = horizontalPadding
            ) {
                updateTheme(theme)
            }
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
}



@Composable
private fun AccountInfoSheet(
    username: String,
    usingBeautifulHeading: Boolean,
    changeUsername: () -> Unit,
    signOut: () -> Unit
) {
    BottomSheetContent(
        title = buildAnnotatedString {
            if (username.isBlank()) return@buildAnnotatedString

            append(stringResource(R.string.current_user))
            append(" ")

            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(color = MaterialTheme.colorScheme.secondary.animate()),
                    letterSpacing = 3.sp
                )
            ) {
                append(username)
            }
        },
        beautifulDesign = usingBeautifulHeading
    ) {
        IconTextItem(
            text = stringResource(R.string.edit_username),
            icon = Icons.Filled.DriveFileRenameOutline,
            iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
            onClick = { changeUsername() },
            modifier = Modifier.padding(vertical = 16.dp)
        )

        IconTextItem(
            text = stringResource(R.string.logout),
            icon = Icons.Rounded.Logout,
            iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = { signOut() }
        )
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