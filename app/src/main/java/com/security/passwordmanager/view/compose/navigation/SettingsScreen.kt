package com.security.passwordmanager.view.compose.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.security.passwordmanager.BottomAnimationSpec
import com.security.passwordmanager.BottomSheetShape
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.model.ScreenType
import com.security.passwordmanager.model.Themes
import com.security.passwordmanager.view.compose.ThemeBottomSheetContent
import com.security.passwordmanager.view.compose.Times
import com.security.passwordmanager.viewmodel.MySettingsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SettingsScreen(navController: NavController, settingsViewModel: MySettingsViewModel) {
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        animationSpec = BottomAnimationSpec
    )

    val theme = settingsViewModel.theme.collectAsState()
    val times = settingsViewModel.times.collectAsState()
    val scope = rememberCoroutineScope()

    val openBottomSheet = {
        scope.launch { bottomSheetState.show() }
    }
    val closeBottomSheet = {
        scope.launch { bottomSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            ThemeBottomSheetContent(
                currentTheme = theme.value,
                updateTheme = {
                    settingsViewModel.updateTheme(it)
                    if (it != Themes.AUTO_THEME)
                        closeBottomSheet()
                }
            ) {
                if (theme.value == Themes.AUTO_THEME)
                    Times(times = times.value) {
                        settingsViewModel.updateTimes(it)
                    }
            }
        },
        sheetShape = BottomSheetShape,
        sheetBackgroundColor = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(ScreenType.Settings.pluralTitleRes),
                navigationIcon = Icons.Filled.ArrowBack,
                onNavigate = navController::popBackStack
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { openBottomSheet() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(ScreenType.Settings.pluralTitleRes))
                }
            }
        }
    }
}



@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        navController = rememberNavController(),
        settingsViewModel = PasswordManagerApplication()
            .settingsViewModelFactory
            .create(MySettingsViewModel::class.java)
    )
}