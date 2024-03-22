package com.security.passwordmanager.view

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.app.App
import com.security.passwordmanager.view.navigation.NavigationScreen
import com.security.passwordmanager.view.theme.PasswordManagerTheme
import com.security.passwordmanager.viewmodel.MainViewModel


class MainActivity : ComponentActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return (application as App).component.mainViewModel() as T
            }
        }

        mainViewModel = viewModels<MainViewModel> { factory }.value
        setContent {
            PasswordManagerTheme(mainViewModel.settings) { isDarkTheme ->
                val style = when {
                    mainViewModel.isDarkStatusBarIconsInScreen -> SystemBarStyle.light(
                        scrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT
                    )

                    else -> SystemBarStyle.dark(Color.TRANSPARENT)
                }

                enableEdgeToEdge(
                    statusBarStyle = style,
                    navigationBarStyle = style
                )

                NavigationScreen(
                    application = application as App,
                    viewModel = mainViewModel,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }


    override fun onStart() {
        super.onStart()
        mainViewModel.updateSettings()
    }
}
