package com.security.passwordmanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.security.passwordmanager.domain.model.Settings
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import com.security.passwordmanager.view.navigation.AppScreen
import com.security.passwordmanager.view.navigation.HomeScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel
@Inject constructor(private val settingsUseCase: SettingsUseCase) : ViewModel() {

    var settings by mutableStateOf(Settings())
        private set

    var isDarkStatusBarIconsInScreen by mutableStateOf(true)

    init {
        settingsUseCase.fetchSettings()
            .onEach { settings = it }
            .launchIn(viewModelScope)
    }


    fun updateSettings() {
        viewModelScope.launch {
            settingsUseCase.loadSettings {
                settings = it
            }
        }
    }


    fun navigateTo(
        navController: NavController,
        route: String,
        parentScreen: AppScreen? = HomeScreen.AllNotes
    ) {
        navController.navigate(route) {
            when (parentScreen) {
                null -> popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                else -> popUpTo(parentScreen.destinationRoute)
            }
            launchSingleTop = true
        }
    }
}