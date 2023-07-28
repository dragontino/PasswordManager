package com.security.passwordmanager.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {
    data class Success<T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading"
        }
    }

    fun <R : Any> map(block: (T) -> R): Result<R> = when (this) {
        is Success -> Success(block(data))
        is Error -> Error(exception)
        is Loading -> Loading
    }
}