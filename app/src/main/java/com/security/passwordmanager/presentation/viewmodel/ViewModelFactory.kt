package com.security.passwordmanager.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.FirebaseAuth
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
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
                    preferences = AppPreferences(application),
                    firebaseAuth = FirebaseAuth.getInstance()
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
                    repository = LoginRepository(
                        auth = FirebaseAuth.getInstance(),
                        context = application
                    ),
                    preferences = AppPreferences(application)
                ) as T
            }
            modelClass.isAssignableFrom(WebsiteViewModel::class.java) -> {
                return WebsiteViewModel() as T
            }
            modelClass.isAssignableFrom(NotesViewModel::class.java) -> {
                return NotesViewModel() as T
            }
            modelClass.isAssignableFrom(NavigationViewModel::class.java) -> {
                return NavigationViewModel(application as PasswordManagerApplication) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}