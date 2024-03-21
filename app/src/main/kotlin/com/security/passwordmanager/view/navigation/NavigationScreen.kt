package com.security.passwordmanager.view.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.security.passwordmanager.app.App
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.util.getEnum
import com.security.passwordmanager.util.getInt
import com.security.passwordmanager.util.getString
import com.security.passwordmanager.view.composables.sheets.ModalNavigationDrawerContent
import com.security.passwordmanager.view.composables.sheets.ModalSheetDefaults
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.view.screens.LoginScreen
import com.security.passwordmanager.view.screens.PasswordGenerationScreen
import com.security.passwordmanager.view.screens.SettingsScreen
import com.security.passwordmanager.view.screens.WebsiteEditingScreen
import com.security.passwordmanager.view.screens.datascreens.AllNotesScreen
import com.security.passwordmanager.view.screens.datascreens.SearchScreen
import com.security.passwordmanager.view.screens.datascreens.WebsitesScreen
import com.security.passwordmanager.view.theme.AnyScreenAnimation
import com.security.passwordmanager.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@Composable
internal fun NavigationScreen(
    application: App,
    viewModel: MainViewModel,
    isDarkTheme: Boolean
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentBackStackEntryState = navController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf {
            currentBackStackEntryState.value?.destination?.route ?: LoginScreen.destinationRoute
        }
    }


    val openDrawer = remember {
        fun() {
            scope.launch {
                delay(50)
                drawerState.animateTo(DrawerValue.Open, ModalSheetDefaults.AnimationSpec)
            }
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


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalNavigationDrawerContent(
                appName = application.name,
                appVersion = application.version
            ) {
                ScreenTypeItem(
                    screen = HomeScreen.AllNotes,
                    selected = currentRoute == HomeScreen.AllNotes.destinationRoute
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

                HorizontalDivider(Modifier.padding(top = 8.dp, bottom = 4.dp))


                ScreenTypeItem(
                    screen = PasswordGenerationScreen,
                    selected = currentRoute == PasswordGenerationScreen.destinationRoute
                ) { screen ->
                    onDrawerItemClick(route = screen.createUrl())
                }


                HorizontalDivider(Modifier.padding(top = 8.dp, bottom = 4.dp))

                ScreenTypeItem(
                    screen = SettingsScreen,
                    selected = currentRoute == SettingsScreen.destinationRoute
                ) { screen ->
                    onDrawerItemClick(route = screen.createUrl())
                }
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        NavHost(
            navController = navController,
            startDestination = LoginScreen.destinationRoute,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.animate())
                .fillMaxSize(),
            enterTransition = { AnyScreenAnimation.enter },
            exitTransition = { AnyScreenAnimation.exit }
        ) {
            // TODO: 03.03.2024 сделать отдельные экраны для регистрации и авторизации и вынести в отдельный navigation
            composable(LoginScreen.destinationRoute) {
                viewModel.isDarkStatusBarIconsInScreen = !isDarkTheme
                LoginScreen(
                    viewModel = viewModel { application.component.loginViewModel() },
                    isDarkTheme = isDarkTheme
                ) { route ->
                    viewModel.navigateTo(navController, route, parentScreen = null)
                }
            }

            navigation(
                route = HomeScreen.rootRoute,
                startDestination = HomeScreen.AllNotes.destinationRoute
            ) {
                composable(route = HomeScreen.AllNotes.destinationRoute) {
                    AllNotesScreen(
                        title = HomeScreen.AllNotes.title(),
                        isDarkTheme = isDarkTheme,
                        viewModel = viewModel { application.component.allNotesViewModel() },
                        openDrawer = openDrawer,
                        navigateTo = { route -> viewModel.navigateTo(navController, route) }
                    ) { isDarkStatusBarIcons ->
                        viewModel.isDarkStatusBarIconsInScreen = isDarkStatusBarIcons
                    }
                }


                composable(route = HomeScreen.WebsiteNotes.destinationRoute) {
                    WebsitesScreen(
                        title = HomeScreen.WebsiteNotes.title(),
                        isDarkTheme = isDarkTheme,
                        viewModel = viewModel { application.component.allNotesViewModel() },
                        navigateTo = { route -> viewModel.navigateTo(navController, route) }
                    ) { isDarkStatusBarIcons ->
                        viewModel.isDarkStatusBarIconsInScreen = isDarkStatusBarIcons
                    }
                }



                composable(route = HomeScreen.BankNotes.destinationRoute) {
                    WebsitesScreen(
                        title = HomeScreen.WebsiteNotes.title(),
                        isDarkTheme = isDarkTheme,
                        viewModel = viewModel { application.component.allNotesViewModel() },
                        navigateTo = { route -> viewModel.navigateTo(navController, route) }
                    ) { isDarkStatusBarIcons ->
                        viewModel.isDarkStatusBarIconsInScreen = isDarkStatusBarIcons
                    }
                }



                composable(
                    route = HomeScreen.Search.destinationRoute,
                    arguments = listOf(
                        navArgument(HomeScreen.Search.Args.EntityType) {
                            type = NavType.EnumType(EntityType::class.java)
                        }
                    )
                ) {
                    viewModel.isDarkStatusBarIconsInScreen = !isDarkTheme

                    val searchViewModelFactory = remember {
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return application.component.searchViewModel().create(
                                    type = it.arguments.getEnum(
                                        key = HomeScreen.Search.Args.EntityType,
                                        defaultValue = EntityType.All
                                    )
                                ) as T
                            }
                        }
                    }

                    SearchScreen(
                        viewModel = viewModel(factory = searchViewModelFactory),
                        navigateTo = { route -> viewModel.navigateTo(navController, route) },
                        popBackStack = { navController.popBackStack() }
                    )
                }
            }


            composable(SettingsScreen.destinationRoute) {
                viewModel.isDarkStatusBarIconsInScreen = !isDarkTheme

                SettingsScreen(
                    title = SettingsScreen.title(),
                    viewModel = viewModel { application.component.settingsViewModel() },
                    isDarkTheme = isDarkTheme,
                    navigateTo = { viewModel.navigateTo(navController, it, parentScreen = null) },
                    popBackStack = { navController.popBackStack() }
                )
            }


            navigation(
                route = EditScreen.rootRoute,
                startDestination = EditScreen.Website.destinationRoute
            ) {
                composable(
                    route = EditScreen.Website.destinationRoute,
                    arguments = listOf(
                        navArgument(EditScreen.Website.Args.EntityId) {
                            type = NavType.StringType
                            nullable = true
                        },
                        navArgument(EditScreen.Website.Args.StartPosition) {
                            type = NavType.IntType
                            defaultValue = 0
                        }
                    )
                ) {
                    val vmFactory = remember {
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return application.component.websiteViewModel().create(
                                    id = it.arguments.getString(EditScreen.Website.Args.EntityId)
                                ) as T
                            }
                        }
                    }

                    WebsiteEditingScreen(
                        viewModel = viewModel(factory = vmFactory),
                        startPosition = it.arguments.getInt(
                            key = EditScreen.Website.Args.StartPosition,
                            defaultValue = 0
                        ),
                        popBackStack = { navController.popBackStack() },
                        isDarkTheme = isDarkTheme,
                    ) { isDarkStatusBarIcons ->
                        viewModel.isDarkStatusBarIconsInScreen = isDarkStatusBarIcons
                    }
                }



                composable(
                    route = EditScreen.BankEdit.destinationRoute,
                    arguments = listOf(
                        navArgument(EditScreen.BankEdit.Args.EntityId) {
                            type = NavType.StringType
                        },
                        navArgument(EditScreen.BankEdit.Args.StartPosition) {
                            type = NavType.IntType
                        }
                    )
                ) {
                    viewModel.isDarkStatusBarIconsInScreen = false

                    // TODO: 11.11.2022 интегрировать BankScreen
                }
            }



            composable(route = PasswordGenerationScreen.destinationRoute) {
                viewModel.isDarkStatusBarIconsInScreen = !isDarkTheme
                PasswordGenerationScreen(
                    title = PasswordGenerationScreen.title(),
                    viewModel = viewModel { application.component.passwordGenerationViewModel() },
                    popBackStack = { navController.popBackStack() }
                )
            }
        }
    }
}