package com.security.passwordmanager.presentation.view

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.presentation.view.navigation.NavigationScreen
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.NavigationViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel


class MainActivity : AppCompatActivity() {

    private var settingsViewModel: SettingsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val factory = (application as PasswordManagerApplication).viewModelFactory

        val navigationViewModel by viewModels<NavigationViewModel> { factory }
        val settingsViewModel by viewModels<SettingsViewModel> { factory }

        this.settingsViewModel = settingsViewModel


        setContent {
            PasswordManagerTheme(settings = settingsViewModel.settings) {
                NavigationScreen(
                    viewModel = navigationViewModel,
                    settingsViewModel = settingsViewModel,
                    isDarkTheme = it
                )
            }
        }
    }


    override fun onStart() {
        super.onStart()
        settingsViewModel?.updateSettingsOnce()
    }
}
