package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.data.repository.SettingsRepository
import com.security.passwordmanager.presentation.model.Time
import com.security.passwordmanager.presentation.model.Times
import com.security.passwordmanager.presentation.model.enums.Themes
import com.security.passwordmanager.presentation.view.BottomSheetFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val preferences: AppPreferences,
) : ViewModel() {

    enum class State {
        Loading,
        Ready
    }


    companion object {
        @Volatile
        private var INSTANCE: SettingsViewModel? = null

        fun getInstance(
            owner: ViewModelStoreOwner,
            factory: ViewModelFactory
        ): SettingsViewModel {
            val temp = INSTANCE
            if (temp != null)
                return temp

            synchronized(this) {
                val instance = ViewModelProvider(owner, factory)[SettingsViewModel::class.java]

                INSTANCE = instance
                return instance
            }
        }
    }


    var state by mutableStateOf(State.Loading)
        private set


    val settings: LiveData<Settings> by lazy {
        when {
            preferences.email.isBlank() -> liveData { Settings() }
            else -> settingsRepository.getSettings(preferences.email).asLiveData()
        }
    }


    private var startTime: Time
        get() = preferences.startTime
        set(value) {
            preferences.startTime = value
        }


    private var endTime: Time
        get() = preferences.endTime
        set(value) {
            preferences.endTime = value
        }


    private val _times: MutableStateFlow<Times> = MutableStateFlow(Times(startTime, endTime))
    val times: StateFlow<Times> = _times.asStateFlow()


    var bottomSheetContent: @Composable (ColumnScope.(BottomSheetFragment) -> Unit) by mutableStateOf({})


    init {
        state = State.Loading
        viewModelScope.launch {
            delay(200)
//            _settings = settingsRepository.getSettings(preferences.email)
//            _theme.value = _settings.value.theme.toThemes()
            state = State.Ready
        }
    }


    fun isDarkTheme(isDark: Boolean, context: Context): String =
        context.getString(
            if (isDark) R.string.dark_theme else R.string.light_theme
        ).lowercase()


    // TODO: сделать stateFlow в репозитории
    fun updateTheme(newTheme: Themes) {
        viewModelScope.launch {
            settingsRepository.updateTheme(preferences.email, newTheme)
        }
    }

    fun updateUsingBeautifulFont(usingBeautifulFont: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateUsingBeautifulFont(preferences.email, usingBeautifulFont)
        }
    }


    fun updateDataHints(usingDataHints: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDataHints(preferences.email, usingDataHints)
        }
    }


    fun updateUsingBottomView(usingBottomView: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateUsingBottomView(preferences.email, usingBottomView)
        }
    }


    fun updateTimes(newTimes: Times) {
        _times.value = newTimes
        startTime = newTimes.startTime
        endTime = newTimes.endTime
    }

    // TODO: исправить
    fun updateSettings(update: Settings.() -> Unit) {
        viewModelScope.launch {
            settingsRepository.addSettings(settings.value?.apply(update) ?: Settings())
        }
    }
}