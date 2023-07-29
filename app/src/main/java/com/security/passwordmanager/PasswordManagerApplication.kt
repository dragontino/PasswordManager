package com.security.passwordmanager

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.security.passwordmanager.presentation.viewmodel.ViewModelFactory

class PasswordManagerApplication: Application() {
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.database.setPersistenceEnabled(true)
    }
}