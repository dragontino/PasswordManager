package com.security.passwordmanager.presentation.view.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.security.passwordmanager.*
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.NotesScreen
import com.security.passwordmanager.presentation.view.SettingsScreen
import com.security.passwordmanager.presentation.view.WebsiteScreen
import com.security.passwordmanager.presentation.view.login.LoginPasswordScreen
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.NotesViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import com.security.passwordmanager.presentation.viewmodel.WebsiteViewModel
import kotlinx.coroutines.launch

@ExperimentalLayoutApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun NavigationScreen(fragmentManager: FragmentManager, isDarkTheme: Boolean) {
    val factory = (
            LocalContext
                .current
                .getActivity()
                ?.application as PasswordManagerApplication?
            )
        ?.viewModelFactory

    val dataViewModel = viewModel<DataViewModel>(factory = factory)
    val settingsViewModel = viewModel<SettingsViewModel>(factory = factory)
    val notesViewModel = viewModel<NotesViewModel>(factory = factory)
    val websiteViewModel = viewModel<WebsiteViewModel>()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberAnimatedNavController()

    var isDarkStatusBarIcons by rememberSaveable { mutableStateOf(false) }

    val openDrawer = {
        scope.launch {
            drawerState.animateTo(DrawerValue.Open, BottomAnimationSpec)
        }
    }

    val navigateTo = { route: String ->
        navController.navigate(route) {
            popUpTo(Screen.Notes.fullRoute)
            launchSingleTop = true
        }
    }

    fun onDrawerItemClick(route: String) {
        scope.launch {
            drawerState.animateTo(DrawerValue.Closed, BottomAnimationSpec)
        }
        navigateTo(route)
    }


    TransparentSystemBars(isDarkStatusBarIcons, !isDarkTheme)


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalNavigationDrawerContent {
                ScreenTypeItem(Screen.Notes) { text ->
                    onDrawerItemClick(
                        createRouteToNotesScreen(DataType.All, title = text)
                    )
                }

//                IconTextItem(
//                    icon = Screen.Website.icon,
//                    iconTintColor = RaspberryLight,
//                    text = stringResource(R.string.websites)
//                ) {
//                    onDrawerItemClick(createRouteToNotesScreen(DataType.Website, title = it))
//                }

//                IconTextItem(
//                    icon = Screen.BankCard.icon,
//                    iconTintColor = RaspberryLight,
//                    text = stringResource(R.string.bank_cards),
//                ) {
//                    onDrawerItemClick(createRouteToNotesScreen(DataType.BankCard, title = it))
//                }

                Divider(
                    color = DarkerGray,
                    modifier = Modifier.padding(top = 3.dp, bottom = 2.dp)
                )

                ScreenTypeItem(Screen.Settings) {
                    onDrawerItemClick(createRouteToSettingsScreen())
                }
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = Screen.Login.fullRoute,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.animate())
                .fillMaxSize(),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = LinearOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = FastOutLinearInEasing
                    )
                )
            }
        ) {
            composable(
                route = Screen.Login.fullRoute
            ) {
                isDarkStatusBarIcons = !isDarkTheme

                LoginPasswordScreen(
                    viewModel = viewModel(factory = factory),
                    fragmentManager = fragmentManager,
                    settingsLiveData = settingsViewModel.settings,
                    navigateTo = navigateTo,
                    popBackStack = navController::popBackStack
                )
            }


            composable(
                route = Screen.Notes.fullRoute,
                arguments = listOf(
                    navArgument(Screen.Notes.Args.NotesScreenType.name) {
                        type = NavType.EnumType(DataType::class.java)
                    },
                    navArgument(Screen.Notes.Args.Title.name) {
                        type = NavType.StringType
                    }
                )
            ) {
                isDarkStatusBarIcons = false

                NotesScreen(
                    title = it.arguments.getString(
                        key = Screen.Notes.Args.Title.name,
                        defaultValue = stringResource(Screen.Notes.titleRes),
                    ),
                    dataType = it.arguments.getEnum(
                        Screen.Notes.Args.NotesScreenType.name,
                        DataType.All
                    ),
                    dataViewModel = dataViewModel,
                    viewModel = notesViewModel,
                    fragmentManager = fragmentManager,
                    openDrawer = { openDrawer() },
                    navigateTo = navigateTo
                )
            }

            composable(Screen.Settings.fullRoute) {
                isDarkStatusBarIcons = !isDarkTheme

                SettingsScreen(
                    title = stringResource(Screen.Settings.titleRes),
                    viewModel = settingsViewModel,
                    fragmentManager = fragmentManager,
                    isDarkTheme = isDarkTheme,
                    navigateTo = navigateTo,
                    popBackStack = navController::popBackStack
                )
            }

            composable(
                route = Screen.Website.fullRoute,
                arguments = listOf(
                    navArgument(Screen.Website.Args.DataKey.name) {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument(Screen.Website.Args.StartPosition.name) {
                        type = NavType.IntType
                    }
                )
            ) {
                isDarkStatusBarIcons = false

                WebsiteScreen(
                    address = it.arguments.getString(Screen.Website.Args.DataKey.name),
                    viewModel = websiteViewModel,
                    dataViewModel = dataViewModel,
                    settingsViewModel = settingsViewModel,
                    startPosition = it.arguments.getInt(
                        Screen.Website.Args.StartPosition.name,
                        defaultValue = 0,
                    ),
                    popBackStack = navController::popBackStack
                )
            }

            composable(
                route = Screen.BankCard.fullRoute,
                arguments = listOf(
                    navArgument(Screen.BankCard.Args.DataKey.name) {
                        type = NavType.StringType
                    },
                    navArgument(Screen.BankCard.Args.StartPosition.name) {
                        type = NavType.IntType
                    }
                )
            ) {
                isDarkStatusBarIcons = false

                // TODO: 11.11.2022 интегрировать BankCardScreen
            }
        }
    }
}


@Composable
private fun TransparentSystemBars(
    isDarkStatusBarIcons: Boolean,
    isDarkNavigationBarIcons: Boolean,
) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = isDarkStatusBarIcons
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            navigationBarContrastEnforced = false,
            darkIcons = isDarkNavigationBarIcons
        )
    }
}




@ExperimentalLayoutApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
private fun NavigationScreenPreview() {
    PasswordManagerTheme {
        NavigationScreen(
            fragmentManager = AppCompatActivity().supportFragmentManager,
            isDarkTheme = it
        )
    }
}