package com.security.passwordmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.AppPreferences
import com.security.passwordmanager.settings.Settings
import com.security.passwordmanager.settings.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MySettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val preferences: AppPreferences
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

    init {
        viewModelScope.launch {
            while (isActive) {
                val settings = getSettings()
                _state.value = State.Data(settings)
                delay(50)
            }
        }
    }


    suspend fun getSettings() = settingsRepository.getSuspendSettings(preferences.email)
}