package com.security.passwordmanager.view

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.app.App
import com.security.passwordmanager.util.LoadingInBox
import com.security.passwordmanager.util.reversed
import com.security.passwordmanager.view.navigation.NavigationScreen
import com.security.passwordmanager.view.theme.PasswordManagerTheme
import com.security.passwordmanager.viewmodel.MainViewModel
import kotlinx.coroutines.launch


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


                val isApplicationLoading = (application as App).isLoadingFlow.collectAsState()
                when {
                    isApplicationLoading.value -> ApplicationLoadingScreen()
                    else -> ApplicationContentScreen(isDarkTheme)
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        mainViewModel.updateSettings()
    }


    @Composable
    private fun ApplicationLoadingScreen() {
        LoadingInBox()
    }


    @Composable
    private fun ApplicationContentScreen(isDarkTheme: Boolean) {
        val snackbarHostState = remember(::SnackbarHostState)
        val scope = rememberCoroutineScope()

        val showSnackbar = remember {
            fun (message: String) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }
        }

        val message = (application as App).messageFlow.collectAsState()

        LaunchedEffect(message.value) {
            message.value.takeIf { it.isNotBlank() }?.let(showSnackbar)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            NavigationScreen(
                application = application as App,
                viewModel = mainViewModel,
                isDarkTheme = isDarkTheme
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Snackbar(
                    snackbarData = it,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = MaterialTheme.colorScheme.background.reversed,
                    contentColor = MaterialTheme.colorScheme.onBackground.reversed,
                    dismissActionContentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
