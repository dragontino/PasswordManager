package com.security.passwordmanager.domain.usecase

import android.util.Log
import com.security.passwordmanager.domain.model.InvalidEmailException
import com.security.passwordmanager.domain.model.SignInCredentials
import com.security.passwordmanager.domain.model.SignUpCredentials
import com.security.passwordmanager.domain.model.isValidEmail
import com.security.passwordmanager.domain.repository.LoginRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LoginUseCase(
    private val repository: LoginRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "LoginUseCase"
    }

    val userEmail: String get() = repository.userEmail

    suspend fun signUp(
        credentials: SignUpCredentials,
        resultAction: (Result<String>) -> Unit
    ) = withContext(dispatcher) {
        repository.signUp(credentials) { result ->
            result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
            resultAction(result)
        }
    }

    suspend fun signIn(
        credentials: SignInCredentials,
        resultAction: (Result<String>) -> Unit
    ) {
        withContext(dispatcher) {
            repository.signIn(credentials) { result ->
                result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
                resultAction(result)
            }
        }
    }

    suspend fun signOut() = withContext(dispatcher) {
        repository.signOut()
    }

    fun restoreEmail() = repository.restoreEmail()


    suspend fun checkUserExistsByEmail(
        email: String,
        isExists: (Result<Boolean>) -> Unit
    ) {
        withContext(dispatcher) {
            if (!email.isValidEmail()) {
                isExists(Result.failure(InvalidEmailException))
            } else {
                repository.checkUserExistsByEmail(email) { result ->
                    result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
                    isExists(result)
                }
            }
        }
    }

    suspend fun restorePassword(
        email: String,
        resultAction: (Result<Unit>) -> Unit
    ) {
        withContext(dispatcher) {
            if (!email.isValidEmail()) {
                resultAction(Result.failure(InvalidEmailException))
            } else {
                repository.restorePassword(email) { result ->
                    result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
                    resultAction(result)
                }
            }
        }
    }

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        resultAction: (Result<Unit>) -> Unit
    ) = withContext(dispatcher) {
        repository.changePassword(oldPassword, newPassword) { result ->
            result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
            resultAction(result)
        }
    }
}