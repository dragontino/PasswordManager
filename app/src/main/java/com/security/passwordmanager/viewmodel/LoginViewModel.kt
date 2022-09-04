package com.security.passwordmanager.viewmodel

import android.util.Patterns
import androidx.lifecycle.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.LoginRepository
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.view.login.LoggedInUserView
import com.security.passwordmanager.view.login.LoginFormState
import com.security.passwordmanager.view.login.LoginResult

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    companion object {
        fun getInstance(owner: ViewModelStoreOwner, factory: LoginViewModelFactory) =
            ViewModelProvider(owner, factory)[LoginViewModel::class.java]
    }

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}