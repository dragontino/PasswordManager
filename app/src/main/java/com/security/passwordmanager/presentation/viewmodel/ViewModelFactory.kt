package com.security.passwordmanager.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.LoginDataSource
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.data.repository.LoginRepository
import com.security.passwordmanager.data.repository.SettingsRepository
import com.security.passwordmanager.data.room.MainDatabase

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                return SettingsViewModel(
                    settingsRepository = SettingsRepository(
                        settingsDao = MainDatabase.getDatabase(application).settingsDao()
                    ),
                    preferences = AppPreferences(application)
                ) as T
            }
            modelClass.isAssignableFrom(DataViewModel::class.java) -> {
                return DataViewModel(
                    dataRepository = DataRepository(
                        dataDao = MainDatabase.getDatabase(application).dataDao()
                    ),
                    preferences = AppPreferences(application),
                    cryptoManager = CryptoManager()
                ) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(
                    loginRepository = LoginRepository(
                        dataSource = LoginDataSource()
                    ),
                    preferences = AppPreferences(application)
                ) as T
            }
            modelClass.isAssignableFrom(WebsiteViewModel::class.java) -> return WebsiteViewModel() as T
            modelClass.isAssignableFrom(NotesViewModel::class.java) -> return NotesViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}