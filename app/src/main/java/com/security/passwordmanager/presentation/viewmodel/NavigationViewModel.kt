package com.security.passwordmanager.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.presentation.view.navigation.Screen

class NavigationViewModel(private val application: PasswordManagerApplication) : ViewModel() {

    val factory by lazy { application.viewModelFactory }

    var isDarkStatusBarIcons by mutableStateOf(false)




    fun navigateTo(navController: NavController, route: String, clearStack: Boolean = false) {
        navController.navigate(route) {
            if (clearStack) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            } else {
                popUpTo(Screen.Notes.fullRoute)
            }
            launchSingleTop = true
        }
    }
}