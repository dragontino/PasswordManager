package com.security.passwordmanager

import android.app.Application
import com.security.passwordmanager.presentation.viewmodel.ViewModelFactory

class PasswordManagerApplication: Application() {
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }
}