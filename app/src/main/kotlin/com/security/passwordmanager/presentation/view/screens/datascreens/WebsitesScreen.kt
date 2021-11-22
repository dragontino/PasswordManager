package com.security.passwordmanager.presentation.view.screens.datascreens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.presentation.view.navigation.AppScreens
import com.security.passwordmanager.presentation.viewmodel.AllNotesViewModel

@Composable
fun AnimatedVisibilityScope.WebsitesScreen(
    title: String,
    isDarkTheme: Boolean,
    viewModel: AllNotesViewModel,
    settings: Settings,
    navigateTo: (route: String) -> Unit,
    isDarkStatusBarIcons: (Boolean) -> Unit
) {
    navigateTo(AppScreens.WebsiteEdit.createUrl())
}