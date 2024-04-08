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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeout
import java.time.Duration

class App : Application() {
    private companion object {
        const val MAX_LOADING_SECONDS = 10L
    }

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .dataModule(DataModule(context = this))
            .domainModule(DomainModule(context = this))
            .build()
    }

    private val _isLoadingFlow = MutableStateFlow(true)
    val isLoadingFlow = _isLoadingFlow.asStateFlow()

    private val _messageFlow = MutableStateFlow("")
    val messageFlow = _messageFlow.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this@App)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
            true
        )
        Firebase.database.setPersistenceEnabled(true)

        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            withTimeout(Duration.ofSeconds(MAX_LOADING_SECONDS)) {
                try {
                    initializeKmsClient().exceptionOrNull()?.let { throw it }
                    _isLoadingFlow.emit(false)
                } catch (exception: Exception) {
                    _isLoadingFlow.emit(false)
                    val message: String? = when (exception) {
                        is TimeoutCancellationException -> getString(
                            R.string.failed_to_connect_to_server,
                            exception.localizedMessage ?: ""
                        )
                        else -> exception.localizedMessage
                    }
                    message?.let { _messageFlow.emit(it) }
                }
            }
        }
    }

    val version by lazy {
        val name = BuildConfig.VERSION_NAME
        val date = BuildConfig.VERSION_DATE.parseToDate("dd/MM/yyyy")
        val formattedDateString = date.parseToString("dd MMMM yyyy")
        return@lazy getString(R.string.app_version_pattern, name, formattedDateString)
    }

    val name by lazy { getString(R.string.app_name) }


    private suspend fun initializeKmsClient(): Result<Unit> {
        val kmsRepository = component.kmsRepository()
        return kmsRepository.initializeKmsClient()
    }
}