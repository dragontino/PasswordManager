package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.R
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.repository.LoginRepository
import com.security.passwordmanager.presentation.view.login.LoggedInUserView
import com.security.passwordmanager.presentation.view.login.LoginFormState
import com.security.passwordmanager.presentation.view.login.LoginResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"

        fun getInstance(owner: ViewModelStoreOwner, factory: ViewModelFactory) =
            ViewModelProvider(owner, factory)[LoginViewModel::class.java]
    }


    enum class ViewModelState {
        Loading,
        Ready
    }



    var viewModelState by mutableStateOf(ViewModelState.Loading)
        private set


    internal var currentEntryState by mutableStateOf(EntryState.Undefined)

    private val firebaseAuth = FirebaseAuth.getInstance()


    var email by mutableStateOf(preferences.email)

    var password by mutableStateOf("")
    var repeatedPassword by mutableStateOf(password)

    var enterLogin by mutableStateOf(preferences.email.isBlank())

    var isPasswordVisible by mutableStateOf(false)
    var isRepeatedPasswordVisible by mutableStateOf(false)

    var isLoading by mutableStateOf(false)

    var emailErrorMessage by mutableStateOf("")

    var passwordErrorMessage by mutableStateOf("")


    init {
        viewModelScope.launch {
            isLoading = true
            delay(400)
            viewModelState = ViewModelState.Ready
            updateEntryState()
            isLoading = false
        }
    }


    fun hasEmailInPreferences(): Boolean = preferences.email.isNotBlank()


    private val _loginForm = MutableLiveData<LoginFormState>()
    @Deprecated("Use email property instead", replaceWith = ReplaceWith("email"))
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult


    private fun updateEntryState(emailExists: Boolean = true) {
        currentEntryState = when {
            enterLogin -> EntryState.Undefined
            emailExists -> EntryState.SignIn
            else -> EntryState.Registration
        }
    }


    internal fun onEmailNext(block: (isEmailValid: Boolean) -> Unit = {}) {
        if (isEmailValid()) {
            isLoading = true
            checkEmailExists {
                updateEntryState(emailExists = it)
                isLoading = false
            }
            enterLogin = false
            emailErrorMessage = ""
            block(true)
        }
        else block(false)
    }


    internal fun loginOrRegisterUser(
        context: Context,
        afterBlock: (isSuccess: Boolean, state: EntryState) -> Unit = { _, _ -> }
    ) {
        if (!checkNetworkConnection(context)) {
            currentEntryState.message = context.getString(R.string.check_internet_connection)
            afterBlock(false, currentEntryState)
            return
        }

        when (currentEntryState) {
            EntryState.SignIn -> {
                loginUser { isSuccess ->
                    currentEntryState.message = when {
                        isSuccess -> {
                            saveLogin()
                            ""
                        }
                        password.isBlank() -> context.getString(R.string.empty_password)
                        else -> context.getString(R.string.login_failed)
                    }
                    afterBlock(isSuccess, currentEntryState)
                }
            }
            EntryState.Registration -> {
                registerUser { isSuccess ->
                    currentEntryState.message = when {
                        isSuccess -> {
                            saveLogin()
                            context.getString(R.string.register_successful)
                        }
                        password.isBlank() -> context.getString(R.string.empty_password)
                        password.length <= 6 -> context.getString(R.string.invalid_password)
                        else -> context.getString(R.string.register_failed)
                    }
                    afterBlock(isSuccess, currentEntryState)
                }
            }
            EntryState.Undefined -> return
        }
    }


    fun changeLogin() {
        restoreLogin()
        enterLogin = true
        password = ""
        passwordErrorMessage = ""
        isPasswordVisible = false
        currentEntryState = EntryState.Undefined
    }



    private fun saveLogin() {
        preferences.email = email
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



    private fun registerUser(afterRegistration: (isSuccess: Boolean) -> Unit) {
        isLoading = true

        if (!isEmailValid() || !isPasswordValid()) {
            afterRegistration(false)
            isLoading = false
        }
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    afterRegistration(false)
                    isLoading = false
                    return@addOnCompleteListener
                }

                firebaseAuth.currentUser?.uid?.let {
                    FirebaseDatabase
                        .getInstance()
                        .getReference("Users")
                        .child(it)
                        .setValue(email)
                        .addOnCompleteListener { task1 ->
                            afterRegistration(task1.isSuccessful)
                            isLoading = false
                        }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                afterRegistration(false)
                isLoading = false
            }
    }


    private fun loginUser(afterLogin: (isSuccess: Boolean) -> Unit) {
        isLoading = true

        if (!isEmailValid() || !isPasswordValid()) {
            isLoading = false
            afterLogin(false)
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                afterLogin(task.isSuccessful)
                isLoading = false
            }
    }


    private fun checkEmailExists(isExists: (Boolean) -> Unit) = firebaseAuth
        .fetchSignInMethodsForEmail(email)
        .addOnCompleteListener { task: Task<SignInMethodQueryResult?> ->
            Log.d(TAG, task.result?.signInMethods?.size.toString())
                isExists(!task.result?.signInMethods.isNullOrEmpty())
        }





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
        if (!isEmailValid()) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_email)
        } else if (!isPasswordValid()) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    /** A placeholder email validation check **/
    internal fun isEmailValid() =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /** A placeholder password validation check **/
    internal fun isPasswordValid() =
        password.length > 6



    internal enum class EntryState(var message: String) {
        Undefined("null"),
        SignIn(""),
        Registration("")
    }
}