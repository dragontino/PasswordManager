package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.settings.Settings
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

    private companion object {
        const val TAG = "SettingsViewModel"
    }

    enum class State {
        Loading,
        Ready
    }

    var viewModelState by mutableStateOf(State.Ready)

    var settings by mutableStateOf(Settings())
        private set

    var username by mutableStateOf(settingsRepository.username ?: "")
        private set

    var switchThemeTextLineCount by mutableStateOf(1)

    var showUsernameEditingDialog by mutableStateOf(false)

    var usernameInDialog by mutableStateOf(username)


    private val bottomSheetFragment = BottomSheetFragment()


    init {
        settingsRepository
            .fetchSettings()
            .onEach {
                if (it is Result.Success) {
                    settings = it.data
                } else if (it is Result.Error) {
                    it.exception.localizedMessage?.let { msg -> Log.e(TAG, msg) }
                    settings = Settings()

                    if (it.exception is NullPointerException) {
                        settingsRepository.addSettings(Settings())
                    }
                }
            }
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


    fun restoreLogin() {
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
                    usernameInDialog = username

                    it.exception.localizedMessage?.let { msg -> Log.e(TAG, msg) }

                    resultMessage(context.getString(R.string.change_username_exception))
                }
                is Result.Success -> {
                    viewModelState = State.Ready
                    username = usernameInDialog
                    resultMessage(context.getString(R.string.change_username_successful))
                }
            }
        }
    }

    fun dismissChangingUsernameInDialog() {
        showUsernameEditingDialog = false
        usernameInDialog = username
    }


    fun signOut() {
        settingsRepository.signOut()
    }


    /**
     * Метод, позволяющий обновить значение параметра с именем [name], изменив его на [value]
     *
     * Пример использования:
     * ```
     * viewModel.updateSettingsProperty(Settings::colorDesign.name, 1)
     * ```
     * @param name имя параметра, который нужно обновить
     * @param value новое значение параметра
     * @param error блок результата (не обязательный)
     */
    fun updateSettingsProperty(
        name: String,
        value: Any,
        error: (message: String?) -> Unit = {}
    ) {
        viewModelScope.launch {
            delay(100)
            settingsRepository.updateSettingsProperty(name, value) {
                viewModelState = when (it) {
                    Result.Loading -> State.Loading
                    is Result.Error -> {
                        error(it.exception.localizedMessage)
                        State.Ready
                    }
                    else -> State.Ready
                }
            }
        }
    }
}