package com.security.passwordmanager.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.domain.model.EmailException
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.PasswordException
import com.security.passwordmanager.domain.model.Settings
import com.security.passwordmanager.domain.usecase.LoginUseCase
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import com.security.passwordmanager.model.EntryComposable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    settingsUseCase: SettingsUseCase,
    private val exceptionMessage: ExceptionMessage
) : EventsViewModel() {

    enum class State {
        PreLoading,
        Loading,
        Ready
    }


    internal enum class EntryState {
        Identification,
        Authentication,
        Registration;
    }


    private val _viewModelState = mutableStateOf(State.PreLoading)
    val viewModelState = derivedStateOf { _viewModelState.value }

    private val _settings = mutableStateOf(Settings())
    val settings = derivedStateOf { _settings.value }


    internal val entryState = mutableStateOf(
        when {
            hasEmailInPreferences() -> EntryState.Authentication
            else -> EntryState.Identification
        }
    )


    val entry = EntryComposable(email = loginUseCase.userEmail)


    val loadingProgress = mutableFloatStateOf(0f)

    val isPreLoading: Boolean
        get() = viewModelState.value == State.PreLoading


    init {
        settingsUseCase.fetchSettings()
            .onEach { _settings.value = it }
            .launchIn(viewModelScope)
    }

    fun onPreLoadingFinish() {
        _viewModelState.value = State.Ready
    }

    fun hasEmailInPreferences(): Boolean = loginUseCase.userEmail.isNotBlank()


    fun onEmailNext() {
        viewModelScope.launch {
            _viewModelState.value = State.Loading
            loginUseCase.checkUserExistsByEmail(entry.email) { result ->
                entryState.value = when (result.getOrNull()) {
                    null -> {
                        result.exceptionOrNull()
                            ?.let(exceptionMessage::getMessage)
                            ?.let(::showSnackbar)
                        EntryState.Identification
                    }
                    true -> EntryState.Authentication
                    false -> EntryState.Registration
                }
                _viewModelState.value = State.Ready
            }
        }
    }


    fun signWithEmail(success: suspend () -> Unit) {
        viewModelScope.launch {
            when (entryState.value) {
                EntryState.Authentication -> signInWithEmail {
                    viewModelScope.launch { success() }
                }

                EntryState.Registration -> signUpWithEmail {
                    viewModelScope.launch { success() }
                }

                EntryState.Identification -> return@launch
            }
        }
    }



    private suspend fun signInWithEmail(success: () -> Unit) {
        _viewModelState.value = State.Loading
        loginUseCase.signIn(entry.asSignInCredentials()) { result ->
            _viewModelState.value = State.Ready
            result.getOrNull()?.let(entry::username::set)
            result.exceptionOrNull()?.let { exception ->
                val message = exceptionMessage.getMessage(exception) ?: return@let
                when (exception) {
                    is EmailException -> entry.emailErrorMessage = message
                    is PasswordException -> entry.passwordErrorMessage = message
                    else -> showSnackbar(message)
                }
            }
            if (result.isSuccess) success()
        }
    }


    private suspend fun signUpWithEmail(success: () -> Unit) {
        _viewModelState.value = State.Loading
        loginUseCase.signUp(entry.asSignUpCredentials()) { result ->
            _viewModelState.value = State.Ready
            result.getOrNull()?.let(entry::username::set)
            result.exceptionOrNull()?.let { exception ->
                val message = exceptionMessage.getMessage(exception) ?: return@let
                when (exception) {
                    is EmailException -> entry.emailErrorMessage = message
                    is PasswordException -> entry.passwordErrorMessage = message
                    else -> showSnackbar(message)
                }
            }
            if (result.isSuccess) success()
        }
    }



    fun restorePassword(success: () -> Unit) {
        viewModelScope.launch {
            _viewModelState.value = State.Loading
            loginUseCase.restorePassword(entry.email) {
                it.exceptionOrNull()?.let { exception ->
                    val message = exceptionMessage.getMessage(exception) ?: return@let
                    when (exception) {
                        is EmailException -> entry.emailErrorMessage = message
                        is PasswordException -> entry.passwordErrorMessage = message
                        else -> showSnackbar(message)
                    }
                }
                _viewModelState.value = State.Ready
                if (it.isSuccess) success()
            }
        }
    }


    fun changeLogin() {
        entry.clear()
        loginUseCase.restoreEmail()
        entryState.value = EntryState.Identification
    }
}