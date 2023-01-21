package com.security.passwordmanager.presentation.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.presentation.view.navigation.NavigationScreen
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import me.onebone.toolbar.ExperimentalToolbarApi


@ExperimentalToolbarApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PasswordManagerTheme {
                NavigationScreen(
                    viewModel = viewModel(
                        factory = (application as PasswordManagerApplication).viewModelFactory
                    ),
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
