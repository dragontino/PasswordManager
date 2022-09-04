package com.security.passwordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.AppPreferences
import com.security.passwordmanager.data.MainDatabase
import com.security.passwordmanager.settings.SettingsRepository

class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MySettingsViewModel::class.java)) {
            return MySettingsViewModel(
                settingsRepository = SettingsRepository(
                    settingsDao = MainDatabase.getDatabase(application).settingsDao()
                ),
                preferences = AppPreferences(application)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}