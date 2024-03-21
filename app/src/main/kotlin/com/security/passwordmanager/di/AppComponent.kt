package com.security.passwordmanager.di


import com.security.passwordmanager.viewmodel.AllNotesViewModel
import com.security.passwordmanager.viewmodel.LoginViewModel
import com.security.passwordmanager.viewmodel.MainViewModel
import com.security.passwordmanager.viewmodel.PasswordGenerationViewModel
import com.security.passwordmanager.viewmodel.SearchViewModel
import com.security.passwordmanager.viewmodel.SettingsViewModel
import com.security.passwordmanager.viewmodel.WebsiteViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DomainModule::class, DataModule::class])
interface AppComponent {

    fun mainViewModel(): MainViewModel

    fun settingsViewModel(): SettingsViewModel

    fun loginViewModel(): LoginViewModel

    fun allNotesViewModel(): AllNotesViewModel

    fun searchViewModel(): SearchViewModel.Factory

    fun websiteViewModel(): WebsiteViewModel.Factory

    fun passwordGenerationViewModel(): PasswordGenerationViewModel
}