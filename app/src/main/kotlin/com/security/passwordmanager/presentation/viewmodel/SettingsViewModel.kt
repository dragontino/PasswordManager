package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.BuildConfig
import com.security.passwordmanager.R
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.AppVersionInfo
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.data.repository.SettingsRepository
import com.security.passwordmanager.presentation.model.enums.ColorDesign
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
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

    var username by mutableStateOf(repository.username ?: "")
        private set

    var usernameInDialog by mutableStateOf(username)





    var bottomSheetContent: @Composable (ColumnScope.() -> Unit) by mutableStateOf({})

    var dialogContent: @Composable (() -> Unit) by mutableStateOf({})
        private set

    var showDialog by mutableStateOf(false)
        private set


    init {
        repository
            .fetchSettings()
            .onEach {
                if (it is Result.Success) {
                    settings = it.data
                } else if (it is Result.Error) {
                    it.exception.localizedMessage?.let { msg -> Log.e(TAG, msg) }
                    settings = Settings()

                    if (it.exception is NullPointerException) {
                        repository.addSettings(Settings())
                    }
                }
            }
            .launchIn(viewModelScope)
    }


    fun constructThemeText(
        currentTheme: ColorDesign,
        isDark: Boolean,
        context: Context
    ) = buildString {
        append(context.getString(currentTheme.titleRes).lowercase())

        when (currentTheme) {
            ColorDesign.System, ColorDesign.Auto -> {
                val textToAppend = if (isDark) {
                    context.getString(R.string.dark_theme)
                } else {
                    context.getString(R.string.light_theme)
                }

                append(" ", context.getString(R.string.now, textToAppend).lowercase())
            }

            else -> return@buildString
        }
    }


    fun restoreLogin() {
        preferences.email = ""
    }



    fun updateSettingsOnce() {
        viewModelScope.launch {
            repository.getSettings {
                when (it) {
                    is Result.Loading -> viewModelState = State.Loading
                    is Result.Success -> {
                        settings = it.data
                        viewModelState = State.Ready
                    }

                    is Result.Error -> {
                        it.exception.localizedMessage?.let { msg -> Log.e(TAG, msg) }
                        viewModelState = State.Ready
                    }
                }
            }
        }
    }



    fun openDialog(content: @Composable () -> Unit) {
        dialogContent = content
        showDialog = true
    }


    fun closeDialog() {
        showDialog = false
        dialogContent = {}
    }



    fun saveUsernameFromDialog(context: Context, resultMessage: (resultMessage: String) -> Unit) {
        viewModelScope.launch {
            repository.changeUsername(usernameInDialog) {
                showDialog = false
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
    }


    fun signOut() {
        repository.signOut()
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
            viewModelState = State.Loading
            delay(100)
            repository.updateSettingsProperty(name, value) {
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


    fun getAppVersionName(): String =
        BuildConfig.VERSION_NAME


    fun checkAppUpdates(result: (isLatest: Boolean, latestVersionInfo: AppVersionInfo?) -> Unit) {
        viewModelScope.launch {
            viewModelState = State.Loading
            delay(150)
            val currentVersion = getAppVersionName()

            repository.getCurrentAppVersionInfo { result ->
                viewModelState = when (result) {
                    is Result.Loading -> State.Loading
                    is Result.Error -> {
                        Log.e(TAG, result.exception.localizedMessage, result.exception)
                        result(true, null)
                        State.Ready
                    }

                    is Result.Success -> {
                        val latestVersion = result.data.name
                        result(currentVersion == latestVersion, result.data)
                        State.Ready
                    }
                }
            }
        }
    }



    fun changePassword(
        oldPassword: String,
        newPassword: String,
        context: Context,
        resultAction: (message: String?) -> Unit
    ) {
        viewModelScope.launch {
            viewModelState = State.Loading
            delay(150)
            repository.changePassword(oldPassword, newPassword) { result ->
                viewModelState = when (result) {
                    Result.Loading -> State.Loading
                    is Result.Error -> {
                        Log.e(TAG, result.exception.localizedMessage, result.exception)
                        resultAction(result.exception.localizedMessage)
                        State.Ready
                    }

                    is Result.Success -> {
                        resultAction(context.getString(R.string.changing_password_success))
                        State.Ready
                    }
                }
            }
        }
    }
}