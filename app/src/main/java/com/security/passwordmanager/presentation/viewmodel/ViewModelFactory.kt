package com.security.passwordmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.data.repository.LoginRepository
import com.security.passwordmanager.data.repository.SettingsRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val application: PasswordManagerApplication
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        val preferences = AppPreferences(application)
        val auth = Firebase.auth
        val database = Firebase.database.reference
        val cryptoManager = CryptoManager()

        when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                return SettingsViewModel(
                    settingsRepository = SettingsRepository(
                        auth = auth,
                        database = database,
                        cryptoManager = cryptoManager
                    ),
                    preferences = AppPreferences(application)
                ) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(
                    repository = LoginRepository(
                        auth = auth,
                        database = database,
                        cryptoManager = cryptoManager
                    ),
                    preferences = preferences
                ) as T
            }
            modelClass.isAssignableFrom(WebsiteViewModel::class.java) -> {
                return WebsiteViewModel(
                    repository = DataRepository(
                        auth = auth,
                        database = database,
                        cryptoManager = cryptoManager
                    )
                ) as T
            }
            modelClass.isAssignableFrom(NotesViewModel::class.java) -> {
                return NotesViewModel(
                    dataRepository = DataRepository(
                        auth = auth,
                        database = database,
                        cryptoManager = cryptoManager
                    )
                ) as T
            }
            modelClass.isAssignableFrom(NavigationViewModel::class.java) -> {
                return NavigationViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}