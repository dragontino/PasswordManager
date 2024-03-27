package com.security.passwordmanager.app

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.database.database
import com.google.firebase.initialize
import com.security.passwordmanager.BuildConfig
import com.security.passwordmanager.di.AppComponent
import com.security.passwordmanager.di.DaggerAppComponent
import com.security.passwordmanager.di.DataModule
import com.security.passwordmanager.di.DomainModule
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.util.parseToDate
import com.security.passwordmanager.util.parseToString

class App : Application() {
    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .dataModule(DataModule(context = this))
            .domainModule(DomainModule(context = this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        Firebase.database.setPersistenceEnabled(true)
    }

    val version by lazy {
        val name = BuildConfig.VERSION_NAME
        val date = BuildConfig.VERSION_DATE.parseToDate("dd/MM/yyyy")
        val formattedDateString = date.parseToString("dd MMMM yyyy")
        return@lazy getString(R.string.app_version_pattern, name, formattedDateString)
    }

    val name by lazy { getString(R.string.app_name) }
}