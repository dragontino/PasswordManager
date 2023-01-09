package com.security.passwordmanager.presentation.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import com.security.passwordmanager.*
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.ModalNavigationDrawerContent
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.navigation.Screen
import com.security.passwordmanager.presentation.view.theme.BottomSheetShape
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
class NavigationActivity : AppCompatActivity() {

    companion object {
        fun getIntent(context: Context) =
            createIntent<NavigationActivity>(context) {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = (application as PasswordManagerApplication).viewModelFactory
        val settingsViewModel = SettingsViewModel.getInstance(this, viewModelFactory)
        val dataViewModel = DataViewModel.getInstance(this, viewModelFactory)

        setContent {
            PasswordManagerTheme {
                NavigationScreen(
                    dataViewModel = dataViewModel,
                    settingsViewModel = settingsViewModel,
                    openUrl = { address ->
                        val urlString = when {
                            "www." in address -> "https://$address"
                            "https://www." in address || "http://www." in address -> address
                            else -> "https://www.$address"
                        }

                        if (urlString.isValidUrl()) {
                            val intent = Intent(Intent.ACTION_VIEW, urlString.toUri())
                            startActivity(intent)
                        } else
                            showToast(this, "Адрес $address — некорректный!")
                    }
                )
            }
        }
    }
}


@ExperimentalMaterial3Api
@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@Composable
private fun NavigationScreen(
    dataViewModel: DataViewModel,
    settingsViewModel: SettingsViewModel = PasswordManagerApplication()
        .viewModelFactory
        .create(SettingsViewModel::class.java),
    openUrl: (address: String) -> Unit = {}
) {
    val bottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = BottomAnimationSpec
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current.getActivity()

    val onDrawerDestinationClicked = { route: String ->
        scope.launch {
            drawerState.animateTo(DrawerValue.Closed, BottomAnimationSpec)
        }

        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
    }

    val closeBottomSheet = {
        scope.launch { bottomSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            BottomSheetContent {
                ScreenTypeItem(Screen.Website) {
                    closeBottomSheet()
                    activity?.startActivity(WebsiteActivity.getIntent(activity, null))
                }

                ScreenTypeItem(Screen.BankCard) {
                    closeBottomSheet()
                    activity?.startActivity(BankCardActivity.getIntent(activity, null))
                }
            }
        },
        sheetShape = BottomSheetShape,
        sheetBackgroundColor = MaterialTheme.colorScheme.background.animate()
    ) {
        ModalNavigationDrawer(
            drawerContent = {
                ModalNavigationDrawerContent {
                    ScreenTypeItem(Screen.Notes) {
                        onDrawerDestinationClicked(Screen.Notes.fullRoute)
                    }
                }
            },
            gesturesEnabled = drawerState.isOpen,
            drawerState = drawerState
        ) {

        }
    }
}


@ExperimentalMaterialApi
@Preview
@Composable
private fun NavigationScreenPreview() {
    PasswordManagerTheme {
//        NavigationScreen(SettingsViewModel())
    }
}