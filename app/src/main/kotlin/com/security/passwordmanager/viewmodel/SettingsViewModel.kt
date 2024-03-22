package com.security.passwordmanager.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.BuildConfig
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.AppVersionInfo
import com.security.passwordmanager.domain.model.ColorDesign
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.settings.Settings
import com.security.passwordmanager.domain.usecase.GetAppVersionInfoUseCase
import com.security.passwordmanager.domain.usecase.LoginUseCase
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import com.security.passwordmanager.domain.usecase.UsernameUseCase
import com.security.passwordmanager.model.ColorDesignMapper.title
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KProperty1

class SettingsViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val usernameUseCase: UsernameUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val appVersionInfoUseCase: GetAppVersionInfoUseCase,
    private val exceptionMessage: ExceptionMessage
) : EventsViewModel() {

    var state by mutableStateOf(ViewModelState.Ready)

    private val _bottomSheetContent = mutableStateOf<@Composable (() -> Unit)?>(null)
    internal val bottomSheetContent = derivedStateOf { _bottomSheetContent.value }

    var settings by mutableStateOf(Settings())
        private set

    val username: String get() {
        val result = usernameUseCase.getUsername()
        result.exceptionOrNull()
            ?.let(exceptionMessage::getMessage)
            ?.let(::showSnackbar)
        return result.getOrNull() ?: ""
    }

    var usernameInDialog by mutableStateOf(username)


    init {
        settingsUseCase.fetchSettings()
            .onEach { settings = it }
            .launchIn(viewModelScope)
    }


    internal fun openBottomSheet(content: @Composable () -> Unit) {
        _bottomSheetContent.value = content
    }

    internal fun closeBottomSheet() {
        _bottomSheetContent.value = null
    }


    fun constructThemeText(
        currentTheme: ColorDesign,
        isDark: Boolean,
        context: Context
    ) = buildString {
        append(currentTheme.title(context).lowercase())

        when (currentTheme) {
            ColorDesign.System, ColorDesign.Auto -> {
                val textToAppend = when {
                    isDark -> context.getString(R.string.dark_theme)
                    else -> context.getString(R.string.light_theme)
                }

                append(" ", context.getString(R.string.now, textToAppend).lowercase())
            }

            else -> return@buildString
        }
    }



    fun saveUsernameFromDialog(success: () -> Unit) {
        viewModelScope.launch {
            state = ViewModelState.Loading
            usernameUseCase.changeUsername(usernameInDialog) { result ->
                state = ViewModelState.Ready
                usernameInDialog = username

                result.exceptionOrNull()
                    ?.let(exceptionMessage::getMessage)
                    ?.let(::showSnackbar)

                if (result.isSuccess) success()
            }
        }
    }


    fun signOut() {
        viewModelScope.launch {
            loginUseCase.signOut()
        }
    }


    /**
     * Функция, позволяющая обновить значение параметра с именем [property], изменив его на [value]
     *
     * Пример использования:
     * ```
     * viewModel.updateSettingsProperty(Settings::colorDesign, 1)
     * ```
     * @param property имя параметра, который нужно обновить
     * @param value новое значение параметра
     */
    fun updateSettingsProperty(property: KProperty1<Settings, Any>, value: Any) {
        viewModelScope.launch {
            state = ViewModelState.Loading
            delay(100)
            settingsUseCase.updateSettingsProperty(property.name, value) { result ->
                state = ViewModelState.Ready
                result.exceptionOrNull()
                    ?.let(exceptionMessage::getMessage)
                    ?.let(::showSnackbar)
            }
        }
    }


    fun getAppVersionName(): String =
        BuildConfig.VERSION_NAME


    fun checkAppUpdates(result: (isLatest: Boolean, latestVersionInfo: AppVersionInfo?) -> Unit) {
        viewModelScope.launch {
            state = ViewModelState.Loading
            delay(150)
            val currentVersion = getAppVersionName()

            appVersionInfoUseCase.getAppVersionInfo { result ->
                val latestVersion = result.getOrNull()?.name ?: currentVersion
                result(latestVersion == currentVersion, result.getOrNull())
                state = ViewModelState.Ready
            }
        }
    }



    fun changePassword(
        oldPassword: String,
        newPassword: String,
        success: () -> Unit
    ) {
        viewModelScope.launch {
            state = ViewModelState.Loading
            delay(150)
            loginUseCase.changePassword(oldPassword, newPassword) { result ->
                state = ViewModelState.Ready
                result.exceptionOrNull()
                    ?.let(exceptionMessage::getMessage)
                    ?.let(::showSnackbar)
                if (result.isSuccess) success()
            }
        }
    }
}