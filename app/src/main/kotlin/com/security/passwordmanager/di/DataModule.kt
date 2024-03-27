package com.security.passwordmanager.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.repository.EntityRepositoryImpl
import com.security.passwordmanager.data.repository.LoginRepositoryImpl
import com.security.passwordmanager.data.repository.PasswordGenerationRepositoryImpl
import com.security.passwordmanager.data.repository.SettingsRepositoryImpl
import com.security.passwordmanager.data.retrofit.RetrofitService
import com.security.passwordmanager.domain.repository.EntityRepository
import com.security.passwordmanager.domain.repository.LoginRepository
import com.security.passwordmanager.domain.repository.PasswordGenerationRepository
import com.security.passwordmanager.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class DataModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideLoginRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        cryptoManager: CryptoManager,
        preferences: AppPreferences
    ): LoginRepository {
        return LoginRepositoryImpl(
            auth = auth,
            database = database,
            cryptoManager = cryptoManager,
            preferences = preferences
        )
    }


    @Singleton
    @Provides
    fun provideSettingsRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        cryptoManager: CryptoManager
    ): SettingsRepository {
        return SettingsRepositoryImpl(
            auth = auth,
            database = database,
            cryptoManager = cryptoManager
        )
    }


    @Singleton
    @Provides
    fun provideEntityRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        cryptoManager: CryptoManager,
        retrofitService: RetrofitService
    ): EntityRepository {
        return EntityRepositoryImpl(
            auth = auth,
            database = database,
            cryptoManager = cryptoManager,
            retrofitService = retrofitService
        )
    }


    @Singleton
    @Provides
    fun providePasswordGenerationRepository(): PasswordGenerationRepository {
        return PasswordGenerationRepositoryImpl()
    }


    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }



    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database
    }

    @Singleton
    @Provides
    fun provideAppPreferences(): AppPreferences {
        return AppPreferences(context)
    }

    @Singleton
    @Provides
    fun provideRetrofitService(retrofit: Retrofit): RetrofitService {
        return retrofit.create(RetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://ya.ru").build()
    }


    @Singleton
    @Provides
    fun provideCryptoManager(): CryptoManager {
        return CryptoManager()
    }
}
