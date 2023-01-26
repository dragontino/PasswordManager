package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.security.passwordmanager.R
import com.security.passwordmanager.checkNetworkConnection
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.data.repository.SettingsRepository
import com.security.passwordmanager.presentation.model.Time
import com.security.passwordmanager.presentation.model.Times
import com.security.passwordmanager.presentation.model.enums.Themes
import com.security.passwordmanager.presentation.view.BottomSheetFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val firebaseAuth: FirebaseAuth,
    private val preferences: AppPreferences,
) : ViewModel() {

    enum class State {
        Loading,
        Ready
    }

    var viewModelState by mutableStateOf(State.Ready)

    var settings by mutableStateOf(Settings())
        private set


    private var startTime: Time by mutableStateOf(preferences.startTime)

    private var endTime: Time by mutableStateOf(preferences.endTime)

    var times: Times by mutableStateOf(Times())
        private set


    val currentUsername: String get() = preferences.username.ifBlank { preferences.email }

    var bottomSheetContent: @Composable (ColumnScope.(BottomSheetFragment) -> Unit) by mutableStateOf({})

    var switchThemeTextLineCount by mutableStateOf(1)

    var showUsernameEditingDialog by mutableStateOf(false)

    var usernameInDialog by mutableStateOf(currentUsername)


    init {
        snapshotFlow { Times(startTime, endTime) }
            .onEach { times = it }
            .launchIn(viewModelScope)

        settingsRepository
            .getSettings(preferences.email)
            .onEach { settings = it }
            .launchIn(viewModelScope)
    }



    fun isDarkTheme(isDark: Boolean, context: Context): String =
        context.getString(
            if (isDark) R.string.dark_theme else R.string.light_theme
        ).lowercase()

    fun getThemeText(currentTheme: Themes, isDark: Boolean, context: Context) = buildString {
        append(context.getString(currentTheme.titleRes).lowercase())

        if (currentTheme == Themes.System || currentTheme == Themes.Auto) {
            append(" ", context.getString(R.string.now, isDarkTheme(isDark, context)))
        }
    }


    fun clearEmail() {
        preferences.email = ""
    }



    fun saveUsernameFromDialog(context: Context, result: (Result<Unit>) -> Unit) {
        changeUsername(usernameInDialog, context) {
            showUsernameEditingDialog = false
            when (it) {
                Result.Loading -> {}
                is Result.Error -> usernameInDialog = currentUsername
                is Result.Success -> preferences.username = usernameInDialog
            }
            result(it)
        }
    }

    fun dismissChangingUsernameInDialog() {
        showUsernameEditingDialog = false
        usernameInDialog = currentUsername
    }



    fun changeUsername(newUsername: String, context: Context, result: (Result<Unit>) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        when {
            currentUser == null -> {
                result(Result.Error(Exception(context.getString(R.string.change_username_exception))))
            }
            !context.checkNetworkConnection() -> {
                result(Result.Error(Exception(context.getString(R.string.check_internet_connection))))
            }
            else -> {
                result(Result.Loading)
                currentUser
                    .updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(newUsername)
                            .build()
                    )
                    .addOnCompleteListener {
                        if (it.isSuccessful) result(Result.Success(Unit))
                    }
                    .addOnFailureListener {
                        result(Result.Error(it))
                    }
            }
        }
    }


    fun signOut() {
        firebaseAuth.signOut()
    }


    fun updateTheme(newTheme: Themes, result: (Result<Themes>) -> Unit) {
        viewModelScope.launch {
            result(Result.Loading)
            delay(50)
            settingsRepository.updateTheme(preferences.email, newTheme)
            delay(50)
            if (settings.theme == newTheme) {
                result(Result.Success(newTheme))
            } else {
                result(Result.Error(Exception()))
            }
        }
    }

    fun updateUsingBeautifulFont(usingBeautifulFont: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateUsingBeautifulFont(preferences.email, usingBeautifulFont)
        }
    }


    fun updateAutofill(autofill: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAutofill(preferences.email, autofill)
        }
    }


    fun updateDynamicColor(useDynamicColor: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateUsingDynamicColor(preferences.email, useDynamicColor)
        }
    }


    fun updateTimes(newTimes: Times) {
        startTime = newTimes.startTime
        endTime = newTimes.endTime

        preferences.startTime = newTimes.startTime
        preferences.endTime = newTimes.endTime
    }
}