package com.security.passwordmanager.presentation.view

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.auth.FirebaseAuth
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.presentation.model.Times
import com.security.passwordmanager.presentation.model.enums.Themes
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.FeedbackSheet
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.IconTextItem
import com.security.passwordmanager.presentation.view.navigation.createRouteToLoginScreen
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.RaspberryLight
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
internal fun AnimatedVisibilityScope.SettingsScreen(
    title: String,
    viewModel: SettingsViewModel,
    fragmentManager: FragmentManager,
    isDarkTheme: Boolean,
    navigateTo: (route: String) -> Unit,
    popBackStack: () -> Unit
) {

    val times = viewModel.times.collectAsState()
    val context = LocalContext.current

    val settings by viewModel.settings.observeAsState(initial = Settings())
    val screenShape = MaterialTheme.shapes.large.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    )

    val bottomSheetFragment = BottomSheetFragment {
        viewModel.bottomSheetContent(this, it)
    }

    Scaffold(
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
                            FirebaseAuth.getInstance().signOut()
                            navigateTo(createRouteToLoginScreen())
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Logout,
                            contentDescription = "logout",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = RaspberryLight,
                    actionIconContentColor = RaspberryLight,
                    titleContentColor = MaterialTheme.colorScheme.onBackground.animate(),
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background.animate(),
        contentColor = MaterialTheme.colorScheme.onBackground.animate()
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 1.dp)
                .padding(contentPadding)
                .animateEnterExit(
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearEasing
                        )
                    ) + slideInVertically(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ) { it / 2 },
                    exit = fadeOut(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = 500,
                            easing = LinearEasing
                        )
                    ) + slideOutVertically(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutLinearInEasing
                        )
                    ) { it / 2 }
                )
                .clip(screenShape)
                .border(
                    width = 1.3.dp,
                    brush = Brush.verticalGradient(
                        0.01f to MaterialTheme.colorScheme.onBackground.animate(),
                        0.08f to MaterialTheme.colorScheme.background.animate()
                    ),
                    shape = screenShape
                )
                .fillMaxSize(),
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.switchThemeText))
                    append("\n")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.7.sp,
                            shadow = Shadow(color = RaspberryLight),
                            fontSynthesis = FontSynthesis.Weight
                        ),
                    ) {
                        append(
                            text = stringResource(
                                settings.theme.titleRes,
                                viewModel.isDarkTheme(isDarkTheme, context)
                            ).lowercase()
                        )
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.animate(),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .clickable {
                        viewModel.bottomSheetContent = { fragment ->
                            ThemeSheet(
                                currentTheme = settings.theme,
                                updateTheme = {
                                    viewModel.updateTheme(it)
                                    if (it != Themes.Auto) fragment.dismiss()
                                },
                                themeName = viewModel.isDarkTheme(isDarkTheme, context),
                                showAdditionalContent = settings.theme == Themes.Auto
                            ) {
                                Times(times = times.value) {
                                    viewModel.updateTimes(it)
                                }
                            }
                        }
                        bottomSheetFragment.show(fragmentManager)
                    }
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )

            Divider(color = DarkerGray)

            SwitchItem(
                isChecked = settings.isUsingBeautifulFont,
                title = stringResource(R.string.beautiful_font),
                subtitle = stringResource(R.string.beautiful_font_explain),
                onClick = viewModel::updateUsingBeautifulFont,
            )

            Divider(color = DarkerGray)

            SwitchItem(
                isChecked = settings.isShowingDataHints,
                title = stringResource(R.string.data_hints),
                subtitle = stringResource(R.string.data_hints_description),
                onClick = viewModel::updateDataHints,
            )

            Divider(color = DarkerGray)

            SwitchItem(
                isChecked = settings.isUsingBottomView,
                title = stringResource(R.string.using_bottom_view),
                subtitle = stringResource(R.string.using_bottom_view_explain),
                onClick = viewModel::updateUsingBottomView,
            )

            Divider(color = DarkerGray)

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
                                beautifulDesign = settings.isUsingBeautifulFont
                            ) {
                                fragment.dismiss()
                            }
                        }
                        bottomSheetFragment.show(fragmentManager)
                    }
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}



@Composable
private fun SwitchItem(
    isChecked: Boolean,
    title: String,
    subtitle: String,
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
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f).animate()
            )
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onClick,
            thumbContent = {
                Icon(
                    imageVector = if (isChecked) Icons.Rounded.Check else Icons.Rounded.Close,
                    contentDescription = "switch",
                    modifier = Modifier.scale(0.7f)
                )
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary.animate(),
                checkedTrackColor = MaterialTheme.colorScheme.primary.animate(),
                checkedIconColor = MaterialTheme.colorScheme.primary.animate(),
                uncheckedThumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f).animate(),
                uncheckedTrackColor = MaterialTheme.colorScheme.surface.animate(),
                uncheckedBorderColor = MaterialTheme.colorScheme.primary.animate(),
                uncheckedIconColor = MaterialTheme.colorScheme.background.animate()
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}


@Composable
private fun ThemeSheet(
    currentTheme: Themes,
    updateTheme: (newTheme: Themes) -> Unit,
    themeName: String,
    showAdditionalContent: Boolean,
    additionalContent: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val horizontalPadding = 16.dp
    val scope = rememberCoroutineScope()

    BottomSheetContent {
        Themes.values().forEach { theme ->
            IconTextItem(
                text = stringResource(theme.titleRes, themeName),
                icon = if (theme == currentTheme) Icons.Rounded.CheckCircle else null,
                iconTintColor = RaspberryLight,
                iconAlignment = Alignment.End,
                padding = horizontalPadding
            ) {
                scope.launch {
                    delay(50)
                    updateTheme(theme)
                }
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
            additionalContent()
        }
    }
}




@Preview
@Composable
fun SwitchItemPreview() {
    PasswordManagerTheme {
        SwitchItem(
            title = "Wrecked",
            subtitle = "Imagine Dragons",
            isChecked = true,
            onClick = {}
        )
    }
}