package com.security.passwordmanager.presentation.view.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.security.passwordmanager.animate
import com.security.passwordmanager.getEnum
import com.security.passwordmanager.getInt
import com.security.passwordmanager.getString
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.screens.LoginScreen
import com.security.passwordmanager.presentation.view.screens.PasswordGenerationScreen
import com.security.passwordmanager.presentation.view.screens.SettingsScreen
import com.security.passwordmanager.presentation.view.screens.WebsiteEditingScreen
import com.security.passwordmanager.presentation.view.screens.datascreens.AllNotesScreen
import com.security.passwordmanager.presentation.view.screens.datascreens.SearchScreen
import com.security.passwordmanager.presentation.view.screens.datascreens.WebsitesScreen
import com.security.passwordmanager.presentation.view.theme.AnyScreenAnimation
import com.security.passwordmanager.presentation.viewmodel.NavigationViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun NavigationScreen(
    viewModel: NavigationViewModel,
    settingsViewModel: SettingsViewModel,
    isDarkTheme: Boolean
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberAnimatedNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf {
            currentBackStackEntry?.destination?.route ?: AppScreens.Login.destinationRoute
        }
    }


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
                    screen = AppScreens.AllNotes,
                    selected = currentRoute == AppScreens.AllNotes.destinationRoute
                ) { screen ->
                    onDrawerItemClick(route = screen.createUrl())
                }

                /*
                ScreenTypeItem(
                    screen = AppScreens.WebsiteNotes,
                    selected = currentRoute == AppScreens.WebsiteNotes.destinationRoute
                ) {
                    onDrawerItemClick(route = AppScreens.WebsiteNotes.createUrl())
                }

                ScreenTypeItem(
                    screen = AppScreens.BankNotes,
                    selected = currentRoute == AppScreens.BankNotes.destinationRoute
                ) {
                    onDrawerItemClick(
                        route = AppScreens.BankNotes.createUrl()
                    )
                }
                */

                Divider(Modifier.padding(top = 8.dp, bottom = 4.dp))


                ScreenTypeItem(
                    screen = AppScreens.PasswordGeneration,
                    selected = currentRoute == AppScreens.PasswordGeneration.destinationRoute
                ) {
                    onDrawerItemClick(AppScreens.PasswordGeneration.createUrl())
                }


                Divider(Modifier.padding(top = 8.dp, bottom = 4.dp))

                ScreenTypeItem(
                    screen = AppScreens.Settings,
                    selected = currentRoute == AppScreens.Settings.destinationRoute
                ) {
                    onDrawerItemClick(AppScreens.Settings.createUrl())
                }
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = AppScreens.Login.destinationRoute,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.animate())
                .fillMaxSize(),
            enterTransition = { AnyScreenAnimation.enter },
            exitTransition = { AnyScreenAnimation.exit }
        ) {
            composable(
                route = AppScreens.Login.destinationRoute,
            ) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                LoginScreen(
                    viewModel = viewModel(factory = viewModel.factory),
                    settings = settingsViewModel.settings,
                    isDarkTheme = isDarkTheme
                ) { route ->
                    viewModel.navigateTo(navController, route, true)
                }
            }



            composable(route = AppScreens.AllNotes.destinationRoute) {
                AllNotesScreen(
                    title = AppScreens.AllNotes.title(),
                    isDarkTheme = isDarkTheme,
                    viewModel = viewModel(factory = viewModel.factory),
                    settings = settingsViewModel.settings,
                    openDrawer = { openDrawer() },
                    navigateTo = { route -> viewModel.navigateTo(navController, route) }
                ) { isDarkStatusBarIcons ->
                    viewModel.isDarkStatusBarIcons = isDarkStatusBarIcons
                }
            }



            composable(route = AppScreens.WebsiteNotes.destinationRoute) {
                WebsitesScreen(
                    title = AppScreens.WebsiteNotes.title(),
                    isDarkTheme = isDarkTheme,
                    viewModel = viewModel(factory = viewModel.factory),
                    settings = settingsViewModel.settings,
                    navigateTo = { route -> viewModel.navigateTo(navController, route) }
                ) { isDarkStatusBarIcons ->
                    viewModel.isDarkStatusBarIcons = isDarkStatusBarIcons
                }
            }



            composable(route = AppScreens.BankNotes.destinationRoute) {
                WebsitesScreen(
                    title = AppScreens.WebsiteNotes.title(),
                    isDarkTheme = isDarkTheme,
                    viewModel = viewModel(factory = viewModel.factory),
                    settings = settingsViewModel.settings,
                    navigateTo = { route -> viewModel.navigateTo(navController, route) }
                ) { isDarkStatusBarIcons ->
                    viewModel.isDarkStatusBarIcons = isDarkStatusBarIcons
                }
            }



            composable(
                route = AppScreens.Search.destinationRoute,
                arguments = listOf(
                    navArgument(AppScreens.Search.Args.DataType.name) {
                        type = NavType.EnumType(DataType::class.java)
                    }
                )
            ) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                SearchScreen(
                    viewModel = viewModel(factory = viewModel.factory),
                    dataType = it.arguments.getEnum(
                        key = AppScreens.Search.Args.DataType.name,
                        defaultValue = DataType.All
                    ),
                    settings = settingsViewModel.settings,
                    navigateTo = { route -> viewModel.navigateTo(navController, route) },
                    popBackStack = navController::popBackStack
                )
            }



            composable(AppScreens.Settings.destinationRoute) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                SettingsScreen(
                    title = AppScreens.Settings.title(),
                    viewModel = viewModel(factory = viewModel.factory),
                    isDarkTheme = isDarkTheme,
                    navigateTo = { viewModel.navigateTo(navController, it, clearStack = true) },
                    popBackStack = navController::popBackStack
                )
            }



            composable(
                route = AppScreens.WebsiteEdit.destinationRoute,
                arguments = listOf(
                    navArgument(AppScreens.WebsiteEdit.Args.DataId.name) {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument(AppScreens.WebsiteEdit.Args.StartPosition.name) {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) {
                WebsiteEditingScreen(
                    id = it.arguments.getString(AppScreens.WebsiteEdit.Args.DataId.name),
                    viewModel = viewModel(factory = viewModel.factory),
                    settingsViewModel = settingsViewModel,
                    startPosition = it.arguments.getInt(
                        key = AppScreens.WebsiteEdit.Args.StartPosition.name,
                        defaultValue = 0
                    ),
                    popBackStack = navController::popBackStack,
                    isDarkTheme = isDarkTheme,
                ) { isDarkStatusBarIcons ->
                    viewModel.isDarkStatusBarIcons = isDarkStatusBarIcons
                }
            }



            composable(
                route = AppScreens.BankEdit.destinationRoute,
                arguments = listOf(
                    navArgument(AppScreens.BankEdit.Args.DataId.name) {
                        type = NavType.StringType
                    },
                    navArgument(AppScreens.BankEdit.Args.StartPosition.name) {
                        type = NavType.IntType
                    }
                )
            ) {
                viewModel.isDarkStatusBarIcons = false

                // TODO: 11.11.2022 интегрировать BankScreen
            }



            composable(route = AppScreens.PasswordGeneration.destinationRoute) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme
                PasswordGenerationScreen(
                    title = AppScreens.PasswordGeneration.title(),
                    viewModel = viewModel(factory = viewModel.factory),
                    popBackStack = navController::popBackStack
                )
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