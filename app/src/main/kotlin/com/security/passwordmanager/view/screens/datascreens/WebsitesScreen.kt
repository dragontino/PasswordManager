package com.security.passwordmanager.view.screens.datascreens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import com.security.passwordmanager.view.navigation.EditScreen
import com.security.passwordmanager.viewmodel.AllNotesViewModel

@Composable
fun AnimatedVisibilityScope.WebsitesScreen(
    title: String,
    isDarkTheme: Boolean,
    viewModel: AllNotesViewModel,
    navigateTo: (route: String) -> Unit,
    isDarkStatusBarIcons: (Boolean) -> Unit
) {
    navigateTo(EditScreen.Website.createUrl())
}