package com.security.passwordmanager

import android.app.Application
import com.security.passwordmanager.viewmodel.SettingsViewModelFactory

class PasswordManagerApplication: Application() {

    lateinit var settingsViewModelFactory: SettingsViewModelFactory
        private set

    override fun onCreate() {
        super.onCreate()
        settingsViewModelFactory = SettingsViewModelFactory(this)
    }
}