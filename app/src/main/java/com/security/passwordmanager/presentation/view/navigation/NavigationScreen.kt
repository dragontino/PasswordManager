package com.security.passwordmanager.presentation.view.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.screens.LoginScreen
import com.security.passwordmanager.presentation.view.screens.NotesScreen
import com.security.passwordmanager.presentation.view.screens.SettingsScreen
import com.security.passwordmanager.presentation.view.screens.WebsiteScreen
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.NavigationViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import com.security.passwordmanager.presentation.viewmodel.WebsiteViewModel
import kotlinx.coroutines.launch
import me.onebone.toolbar.ExperimentalToolbarApi

@ExperimentalToolbarApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun NavigationScreen(
    viewModel: NavigationViewModel,
    fragmentManager: FragmentManager,
    isDarkTheme: Boolean
) {

    val dataViewModel = viewModel<DataViewModel>(factory = viewModel.factory)
    val websiteViewModel = viewModel<WebsiteViewModel>(factory = viewModel.factory)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberAnimatedNavController()

    val openDrawer = {
        scope.launch {
            drawerState.animateTo(DrawerValue.Open, BottomAnimationSpec)
        }
    }

    fun onDrawerItemClick(route: String) {
        scope.launch {
            drawerState.animateTo(DrawerValue.Closed, BottomAnimationSpec)
        }
        viewModel.navigateTo(navController, route)
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
                    fragmentManager = fragmentManager,
                    settings = viewModel<SettingsViewModel>(factory = viewModel.factory).settings,
                    navigateTo = { route ->
                        viewModel.navigateTo(navController, route, true)
                    }
                )
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
                    viewModel = dataViewModel,
                    fragmentManager = fragmentManager,
                    openDrawer = { openDrawer() },
                    navigateTo = { route -> viewModel.navigateTo(navController, route) },
                    isDarkStatusBarIcons = { isDarkStatusBarIcons ->
                        viewModel.isDarkStatusBarIcons = isDarkStatusBarIcons
                    }
                )
            }

            composable(AppScreens.Settings.fullRoute) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                SettingsScreen(
                    title = stringResource(AppScreens.Settings.titleRes),
                    viewModel = viewModel(factory = viewModel.factory),
                    fragmentManager = fragmentManager,
                    isDarkTheme = isDarkTheme,
                    navigateTo = { viewModel.navigateTo(navController, it, clearStack = true) },
                    popBackStack = navController::popBackStack
                )
            }

            composable(
                route = AppScreens.Website.fullRoute,
                arguments = listOf(
                    navArgument(AppScreens.Website.Args.DataKey.name) {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument(AppScreens.Website.Args.StartPosition.name) {
                        type = NavType.IntType
                    }
                )
            ) {
                viewModel.isDarkStatusBarIcons = !isDarkTheme

                val address = it
                    .arguments
                    .getString(AppScreens.Website.Args.DataKey.name)

                WebsiteScreen(
                    address = AppScreens.Website.replaceCharsInAddress(address),
                    viewModel = websiteViewModel,
                    dataViewModel = dataViewModel,
                    settingsViewModel = viewModel(factory = viewModel.factory),
                    startPosition = it.arguments.getInt(
                        key = AppScreens.Website.Args.StartPosition.name,
                        defaultValue = 0,
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