package com.security.passwordmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.AppPreferences
import com.security.passwordmanager.model.Settings
import com.security.passwordmanager.model.Themes
import com.security.passwordmanager.model.toThemes
import com.security.passwordmanager.settings.SettingsRepository
import com.security.passwordmanager.view.compose.Time
import com.security.passwordmanager.view.compose.Times
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MySettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val preferences: AppPreferences,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Data(val settings: Settings) : State()
    }


    companion object {
        @Volatile
        private var INSTANCE: MySettingsViewModel? = null

        fun getInstance(
            owner: ViewModelStoreOwner,
            factory: SettingsViewModelFactory
        ): MySettingsViewModel {
            val temp = INSTANCE
            if (temp != null)
                return temp

            synchronized(this) {
                val instance = ViewModelProvider(owner, factory)[MySettingsViewModel::class.java]

                INSTANCE = instance
                return instance
            }
        }
    }


    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()


    private val _theme = MutableStateFlow(Themes.SYSTEM_THEME)
    val theme = _theme.asStateFlow()


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
    val times: StateFlow<Times> get() = _times.asStateFlow()


    init {
        viewModelScope.launch {
            val settings = getSettings()
            _state.value = State.Data(settings)
            _theme.value = settings.theme.toThemes()
        }
    }


    // TODO: сделать stateFlow в репозитории
    fun updateTheme(newTheme: Themes) {
        _theme.value = newTheme
        settingsRepository.updateTheme(preferences.email, newTheme)
    }


    fun updateTimes(newTimes: Times) {
        _times.value = newTimes
        startTime = newTimes.startTime
        endTime = newTimes.endTime
    }


    private suspend fun getSettings() = settingsRepository.getSuspendSettings(preferences.email)
}