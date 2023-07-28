package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.security.passwordmanager.R
import com.security.passwordmanager.checkNetworkConnection
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.ConnectException

class LoginViewModel(
    private val repository: LoginRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private companion object {
        const val TAG = "LoginViewModel"
        const val loadingTimeMillis = 250
    }


    enum class ViewModelState {
        PreLoading,
        Loading,
        Ready
    }


    internal enum class EntryState(var message: String) {
        Identification("null"),
        Authentication(""),
        Registration("")
    }


    var viewModelState by mutableStateOf(ViewModelState.PreLoading)
        private set


    internal var entryState by mutableStateOf(EntryState.Identification)


    var email by mutableStateOf(preferences.email)

    var password by mutableStateOf("")
    var repeatedPassword by mutableStateOf(password)

    var username by mutableStateOf("")


    var isPasswordVisible by mutableStateOf(false)
    var isRepeatedPasswordVisible by mutableStateOf(false)

    var emailErrorMessage by mutableStateOf("")

    var passwordErrorMessage by mutableStateOf("")


    private var loadingTime by mutableStateOf(0f)

    internal val loadingProgress get() = loadingTime / loadingTimeMillis

//        when (startLoadingTime) {
//        null -> 0f
//        else -> (System.currentTimeMillis() - startLoadingTime).toFloat() / loadingTimeMillis
//    }


    init {
        viewModelScope.launch {
            loadingTime = 0f
            repeat(loadingTimeMillis) {
                loadingTime++
                delay(1)
            }
            viewModelState = ViewModelState.Ready
            loadingTime = 0f
            entryState =
                if (hasEmailInPreferences()) EntryState.Authentication else EntryState.Identification
        }
    }

    fun hasEmailInPreferences(): Boolean = preferences.email.isNotBlank()


    internal fun onEmailNext(block: (isEmailValid: Boolean) -> Unit = {}) {
        if (isEmailValid()) {
            viewModelState = ViewModelState.Loading
            repository.checkEmailExists(email) { emailExists ->
                viewModelState = ViewModelState.Ready
                entryState = if (emailExists) EntryState.Authentication else EntryState.Registration
            }
            emailErrorMessage = ""
            block(true)
        } else block(false)
    }


    internal fun signWithEmail(
        context: Context,
        afterBlock: (result: Result<FirebaseUser>, state: EntryState) -> Unit = { _, _ -> }
    ) {
        viewModelState = ViewModelState.Loading

        when {
            !context.checkNetworkConnection() -> {
                entryState.message = ""
                afterBlock(
                    Result.Error(ConnectException(context.getString(R.string.check_internet_connection))),
                    entryState
                )
                return
            }

            password.isBlank() -> {
                entryState.message = context.getString(R.string.empty_password)
                afterBlock(Result.Error(Exception()), entryState)
                return
            }
        }


        when (entryState) {
            EntryState.Authentication -> {
                repository.login(email, password) { result ->
                    when (result) {
                        is Result.Loading -> viewModelState = ViewModelState.Loading
                        is Result.Error -> {
                            entryState.message = ""
                            Log.e(TAG, result.exception.localizedMessage, result.exception)
                            afterBlock(result, entryState)
                            viewModelState = ViewModelState.Ready
                        }

                        is Result.Success -> {
                            saveLogin(email)
                            entryState.message = ""
                            afterBlock(result, entryState)
                            viewModelState = ViewModelState.Ready
                        }
                    }
                }
            }

            EntryState.Registration -> {
                if (!isPasswordValid()) {
                    entryState.message = context.getString(R.string.invalid_password)
                    afterBlock(Result.Error(Exception()), entryState)
                    return
                }

                repository.register(email, password, username) { result ->
                    when (result) {
                        is Result.Loading -> viewModelState = ViewModelState.Loading
                        is Result.Error -> {
                            entryState.message = ""
                            afterBlock(result, entryState)
                        }

                        is Result.Success -> {
                            saveLogin(email)
                            entryState.message = ""

                            afterBlock(result, entryState)
                        }
                    }
                }
            }

            EntryState.Identification -> return
        }
    }


    internal fun restorePassword(afterRestore: (success: Boolean) -> Unit) {
        viewModelState = ViewModelState.Loading

        if (!isEmailValid()) {
            viewModelState = ViewModelState.Ready
            afterRestore(false)
            return
        }

        repository.restorePassword(email) { result ->
            if (result !is Result.Loading) {
                viewModelState = ViewModelState.Ready
                afterRestore(result is Result.Success)
            }
        }
    }


    fun changeLogin() {
        restoreLogin()
        entryState = EntryState.Identification
        password = ""
        passwordErrorMessage = ""
        isPasswordVisible = false
    }


    private fun saveLogin(login: String) {
        preferences.email = login
    }


    private fun restoreLogin() {
        preferences.email = ""
    }


    /** A placeholder email validation check **/
    internal fun isEmailValid() =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()


    /** A placeholder password validation check **/
    internal fun isPasswordValid() =
        password.isNotBlank() && password.length > 6
}