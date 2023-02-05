package com.security.passwordmanager.presentation.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.presentation.view.navigation.NavigationScreen
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.NavigationViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import me.onebone.toolbar.ExperimentalToolbarApi


@ExperimentalMaterialApi
@ExperimentalToolbarApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val factory = (application as PasswordManagerApplication).viewModelFactory
        val settingsViewModel by viewModels<SettingsViewModel> { factory }
        val navigationViewModel by viewModels<NavigationViewModel> { factory }

        setContent {
            PasswordManagerTheme(settings = settingsViewModel.settings) {
                NavigationScreen(
                    viewModel = navigationViewModel,
                    fragmentManager = supportFragmentManager,
                    isDarkTheme = it
                )
            }
        }
    }


    override fun onDestroy() {
        FirebaseAuth.getInstance().signOut()
        super.onDestroy()
    }
}
