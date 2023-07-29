package com.security.passwordmanager.presentation.view.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.security.passwordmanager.EnterScreenAnimation
import com.security.passwordmanager.ExitScreenAnimation
import com.security.passwordmanager.animate
import com.security.passwordmanager.getEnum
import com.security.passwordmanager.getInt
import com.security.passwordmanager.getString
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.screens.LoginScreen
import com.security.passwordmanager.presentation.view.screens.NotesScreen
import com.security.passwordmanager.presentation.view.screens.SettingsScreen
import com.security.passwordmanager.presentation.view.screens.WebsiteScreen
import com.security.passwordmanager.presentation.viewmodel.NavigationViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.ExperimentalToolbarApi

@ExperimentalMaterialApi
@ExperimentalToolbarApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun NavigationScreen(
    viewModel: NavigationViewModel,
    settingsViewModel: SettingsViewModel,
    isDarkTheme: Boolean
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberAnimatedNavController()

    val openDrawer = {
        scope.launch {
            delay(50)
            drawerState.animateTo(DrawerValue.Open, ModalSheetDefaults.AnimationSpec)
        }
    }

    fun onDrawerItemClick(route: String) {
        scope.launch {
            drawerState.animateTo(DrawerValue.Closed, ModalSheetDefaults.AnimationSpec)
        }
        scope.launch {
            delay(100)
            viewModel.navigateTo(navController, route)
        }
    }


    TransparentSystemBars(
        isDarkStatusBarIcons = viewModel.isDarkStatusBarIcons,
        isDarkNavigationBarIcons = !isDarkTheme
    )


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalNavigationDrawerContent {
                ScreenTypeItem(
                    AppScreens.Notes,
                    selected = navController.currentDestination?.route == AppScreens.Notes.fullRoute
                ) { text ->
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

                Divider(Modifier.padding(top = 8.dp, bottom = 4.dp))

                ScreenTypeItem(AppScreens.Settings) {
                    onDrawerItemClick(createRouteToSettingsScreen())
                }
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = AppScreens.Login.fullRoute,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.animate())
                .fillMaxSize(),
            enterTransition = { EnterScreenAnimation },
            exitTransition = { ExitScreenAnimation }
        ) {
            composable(
                route = AppScreens.Login.fullRoute,
            ) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                LoginScreen(
                    viewModel = viewModel(factory = viewModel.factory),
                    settings = settingsViewModel.settings
                ) { route ->
                    viewModel.navigateTo(navController, route, true)
                }
            }


            composable(
                route = AppScreens.Notes.fullRoute,
                arguments = listOf(
                    navArgument(AppScreens.Notes.Args.NotesScreenType.name) {
                        type = NavType.EnumType(DataType::class.java)
                    },
                    navArgument(AppScreens.Notes.Args.Title.name) {
                        type = NavType.StringType
                    }
                )
            ) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                NotesScreen(
                    title = it.arguments.getString(
                        key = AppScreens.Notes.Args.Title.name,
                        defaultValue = stringResource(AppScreens.Notes.titleRes),
                    ),
                    isDarkTheme = isDarkTheme,
                    dataType = it.arguments.getEnum(
                        AppScreens.Notes.Args.NotesScreenType.name,
                        DataType.All
                    ),
                    viewModel = viewModel(factory = viewModel.factory),
                    settings = settingsViewModel.settings,
                    openDrawer = { openDrawer() },
                    navigateTo = { route -> viewModel.navigateTo(navController, route) }
                ) { isDarkStatusBarIcons ->
                    viewModel.isDarkStatusBarIcons = isDarkStatusBarIcons
                }
            }

            composable(AppScreens.Settings.fullRoute) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                SettingsScreen(
                    title = stringResource(AppScreens.Settings.titleRes),
                    viewModel = viewModel(factory = viewModel.factory),
                    isDarkTheme = isDarkTheme,
                    navigateTo = { viewModel.navigateTo(navController, it, clearStack = true) },
                    popBackStack = navController::popBackStack
                )
            }

            composable(
                route = AppScreens.Website.fullRoute,
                arguments = listOf(
                    navArgument(AppScreens.Website.Args.DataId.name) {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument(AppScreens.Website.Args.StartPosition.name) {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                WebsiteScreen(
                    id = it.arguments.getString(AppScreens.Website.Args.DataId.name),
                    viewModel = viewModel(factory = viewModel.factory),
                    settingsViewModel = settingsViewModel,
                    startPosition = it.arguments.getInt(
                        key = AppScreens.Website.Args.StartPosition.name,
                        defaultValue = 0
                    ),
                    popBackStack = navController::popBackStack
                )
            }

            composable(
                route = AppScreens.BankCard.fullRoute,
                arguments = listOf(
                    navArgument(AppScreens.BankCard.Args.DataKey.name) {
                        type = NavType.StringType
                    },
                    navArgument(AppScreens.BankCard.Args.StartPosition.name) {
                        type = NavType.IntType
                    }
                )
            ) {
                viewModel.isDarkStatusBarIcons = false

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