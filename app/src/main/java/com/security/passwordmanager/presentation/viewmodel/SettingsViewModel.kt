package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.data.repository.SettingsRepository
import com.security.passwordmanager.presentation.model.enums.ColorDesign
import com.security.passwordmanager.presentation.view.BottomSheetFragment
import com.security.passwordmanager.presentation.view.BottomSheetState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val preferences: AppPreferences,
) : ViewModel() {

    enum class State {
        Loading,
        Ready
    }

    var viewModelState by mutableStateOf(State.Ready)

    var settings by mutableStateOf(Settings())
        private set

    val currentUsername: String get() = preferences.username.ifBlank { preferences.email }

    var switchThemeTextLineCount by mutableStateOf(1)

    var showUsernameEditingDialog by mutableStateOf(false)

    var usernameInDialog by mutableStateOf(currentUsername)


    private val bottomSheetFragment = BottomSheetFragment()


    init {
        settingsRepository
            .getSettings(preferences.email)
            .onEach { settings = it }
            .launchIn(viewModelScope)
    }


    fun getThemeText(
        currentTheme: ColorDesign,
        isDark: Boolean,
        context: Context
    ) = buildString {
        append(context.getString(currentTheme.titleRes).lowercase())

        if (currentTheme == ColorDesign.System || currentTheme == ColorDesign.Auto) {
            val textToAppend = if (isDark) {
                context.getString(R.string.dark_theme)
            } else {
                context.getString(R.string.light_theme)
            }

            append(" ", context.getString(R.string.now, textToAppend).lowercase())
        }
    }


    fun showBottomSheet(
        fragmentManager: FragmentManager,
        bottomSheetState: BottomSheetState = BottomSheetState()
    ) {
        bottomSheetFragment
            .copy(state = bottomSheetState)
            .show(fragmentManager)
    }


    fun clearEmail() {
        preferences.email = ""
    }



    fun saveUsernameFromDialog(context: Context, resultMessage: (resultMessage: String) -> Unit) {
        settingsRepository.changeUsername(usernameInDialog, context) {
            showUsernameEditingDialog = false
            when (it) {
                Result.Loading ->
                    viewModelState = State.Loading
                is Result.Error -> {
                    viewModelState = State.Ready
                    usernameInDialog = currentUsername
                    resultMessage(context.getString(R.string.change_username_exception))
                }
                is Result.Success -> {
                    viewModelState = State.Ready
                    preferences.username = usernameInDialog
                    resultMessage(context.getString(R.string.change_username_successful))
                }
            }
        }
    }

    fun dismissChangingUsernameInDialog() {
        showUsernameEditingDialog = false
        usernameInDialog = currentUsername
    }


    fun signOut() {
        settingsRepository.signOut()
    }


    fun updateSettings(
        contentToUpdate: Settings.() -> Unit,
        result: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            viewModelState = State.Loading
            delay(100)
            val newSettings = settings.copy().apply(contentToUpdate)
            println("defaultSettings = $settings\nnewSettings = $newSettings")
            settingsRepository.updateSettings(newSettings)
            delay(100)
            viewModelState = State.Ready
            result(settings == newSettings)
        }
    }
}