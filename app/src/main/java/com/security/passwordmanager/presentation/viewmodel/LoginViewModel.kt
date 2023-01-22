package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.security.passwordmanager.R
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.Result.Loading
import com.security.passwordmanager.data.Result.Success
import com.security.passwordmanager.data.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException

class LoginViewModel(
    private val repository: LoginRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private companion object {
        const val TAG = "LoginViewModel"
        const val loadingTimeMillis = 400L
    }


    enum class ViewModelState {
        PreLoading,
        Loading,
        Ready
    }


    internal enum class EntryState(var message: String) {
        Undefined("null"),
        SignIn(""),
        Registration("")
    }



    var viewModelState by mutableStateOf(ViewModelState.PreLoading)
        private set


    internal var entryState by mutableStateOf(EntryState.Undefined)


    var email by mutableStateOf(preferences.email)

    var password by mutableStateOf("")
    var repeatedPassword by mutableStateOf(password)

    var isPasswordVisible by mutableStateOf(false)
    var isRepeatedPasswordVisible by mutableStateOf(false)

    var emailErrorMessage by mutableStateOf("")

    var passwordErrorMessage by mutableStateOf("")


    private var startLoadingTime = -1L


    init {
        viewModelScope.launch {

            startLoadingTime = System.currentTimeMillis()
            delay(loadingTimeMillis)
            startLoadingTime = -1
            viewModelState = ViewModelState.Ready
            entryState = if (hasEmailInPreferences()) EntryState.SignIn else EntryState.Undefined
        }
    }

    internal val loadingProgress: Float get() = when {
        startLoadingTime < 0 -> 0f
        else -> (System.currentTimeMillis() - startLoadingTime).toFloat() / loadingTimeMillis
    }


    fun hasEmailInPreferences(): Boolean = preferences.email.isNotBlank()


    internal fun onEmailNext(block: (isEmailValid: Boolean) -> Unit = {}) {
        if (isEmailValid()) {
            viewModelState = ViewModelState.Loading
            repository.checkEmailExists(email) { emailExists ->
                viewModelState = ViewModelState.Ready
                entryState = if (emailExists) EntryState.SignIn else EntryState.Registration
            }
            emailErrorMessage = ""
            block(true)
        }
        else block(false)
    }


    internal fun signWithEmail(
        context: Context,
        afterBlock: (result: Result<FirebaseUser>, state: EntryState) -> Unit = { _, _ -> }
    ) {
        if (!checkNetworkConnection(context)) {
            entryState.message = context.getString(R.string.check_internet_connection)
            afterBlock(Result.Error(ConnectionShutdownException()), entryState)
            return
        }

        when (entryState) {
            EntryState.SignIn -> {
                repository.login(email, password) { result ->
                    if (result is Loading) {
                        viewModelState = ViewModelState.Loading
                    } else {
                        viewModelState = ViewModelState.Ready
                        entryState.message = when {
                            result is Success -> {
                                saveCredentials(email, username = result.data.displayName ?: "")
                                ""
                            }
                            password.isBlank() -> context.getString(R.string.empty_password)
                            else -> context.getString(
                                R.string.login_failed,
                                (result as Result.Error).exception
                            )
                        }

                        afterBlock(result, entryState)
                    }
                }
            }

            EntryState.Registration -> {
                if (!isPasswordValid()) {
                    entryState.message = context.getString(R.string.invalid_password)
                    afterBlock(Result.Error(ConnectionShutdownException()), entryState)
                    return
                }

                repository.register(email, password) { result ->
                    if (result is Loading) viewModelState = ViewModelState.Loading
                    else {
                        viewModelState = ViewModelState.Ready
                        entryState.message = when (result) {
                            is Success -> {
                                saveCredentials(email, username = result.data.displayName ?: "")
                                context.getString(R.string.register_successful)
                            }
                            else -> context.getString(
                                R.string.register_failed,
                                (result as Result.Error).exception
                            )
                        }

                        afterBlock(result, entryState)
                    }
                }
            }
            EntryState.Undefined -> return
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
            if (result !is Loading) {
                viewModelState = ViewModelState.Ready
                afterRestore(result is Success)
            }
        }
    }


    fun changeLogin() {
        restoreLogin()
        entryState = EntryState.Undefined
        password = ""
        passwordErrorMessage = ""
        isPasswordVisible = false
    }



    private fun saveCredentials(email: String, username: String) {
        preferences.email = email
        preferences.username = username
    }

    private fun restoreLogin() {
        preferences.email = ""
    }


    private fun checkNetworkConnection(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val capabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities != null
    }


    /** A placeholder email validation check **/
    internal fun isEmailValid() =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()


    /** A placeholder password validation check **/
    internal fun isPasswordValid() =
        password.isNotBlank() && password.length > 6
}