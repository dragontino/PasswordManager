package com.security.passwordmanager.di

import android.content.Context
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.repository.EntityRepository
import com.security.passwordmanager.domain.repository.LoginRepository
import com.security.passwordmanager.domain.repository.PasswordGenerationRepository
import com.security.passwordmanager.domain.repository.SettingsRepository
import com.security.passwordmanager.domain.usecase.EntityUseCase
import com.security.passwordmanager.domain.usecase.GeneratePasswordUseCase
import com.security.passwordmanager.domain.usecase.GetAppVersionInfoUseCase
import com.security.passwordmanager.domain.usecase.GetWebsiteDomainNameUseCase
import com.security.passwordmanager.domain.usecase.GetWebsiteLogoUseCase
import com.security.passwordmanager.domain.usecase.LoginUseCase
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import com.security.passwordmanager.domain.usecase.UsernameUseCase
import com.security.passwordmanager.model.ExceptionMessageImpl
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class DomainModule(private val context: Context) {
    @Singleton
    @Provides
    fun provideExceptionMessage(): ExceptionMessage {
        return ExceptionMessageImpl(context)
    }

    @Provides
    fun provideSettingsUseCase(repository: SettingsRepository) = SettingsUseCase(
        repository = repository,
        dispatcher = Dispatchers.IO
    )


    @Provides
    fun provideLoginUseCase(repository: LoginRepository) = LoginUseCase(
        repository = repository,
        dispatcher = Dispatchers.IO
    )


    @Provides
    fun provideUsernameUseCase(repository: SettingsRepository) = UsernameUseCase(
        repository = repository,
        dispatcher = Dispatchers.IO
    )


    @Provides
    fun provideGetAppVersionInfoUseCase(repository: SettingsRepository) = GetAppVersionInfoUseCase(
        repository = repository,
        dispatcher = Dispatchers.IO
    )


    @Provides
    fun provideEntityUseCase(repository: EntityRepository) = EntityUseCase(
        repository = repository,
        dispatcher = Dispatchers.IO
    )


    @Provides
    fun provideGetWebsiteDomainUseCase(repository: EntityRepository) = GetWebsiteDomainNameUseCase(
        repository = repository,
        dispatcher = Dispatchers.IO
    )


    @Provides
    fun provideGetWebsiteLogoUseCase(repository: EntityRepository) = GetWebsiteLogoUseCase(
        repository = repository,
        dispatcher = Dispatchers.IO
    )


    @Provides
    fun provideGeneratePasswordUseCase(repository: PasswordGenerationRepository) = GeneratePasswordUseCase(
        repository = repository,
        dispatcher = Dispatchers.Default
    )
}