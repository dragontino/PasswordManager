package com.security.passwordmanager.domain.model

import com.security.passwordmanager.domain.model.entity.EntityType

sealed class PasswordManagerException(override val message: String? = null) : Exception(message)

sealed interface EmailException
sealed interface PasswordException

data object UserNotLoggedException : PasswordManagerException()
data object IncorrectEmailException : PasswordManagerException(), EmailException
data object InvalidEmailException : PasswordManagerException(), EmailException
data class IncorrectPasswordException(
    val errorCode: String,
    override val message: String? = null
) : PasswordManagerException(), PasswordException
data object UserDisabledException : PasswordManagerException()
data object TooManyRequestsException : PasswordManagerException()
data class ConnectionFailedException(override val message: String? = null) : PasswordManagerException(message)
data class TitleNotFoundException(val url: String) : PasswordManagerException()

data class LoadIconException(
    val url: String,
    val code: Int,
    override val message: String? = null
) : PasswordManagerException(message)

data class LoadWebsiteNameException(
    val url: String,
    val code: Int,
    override val message: String? = null
) : PasswordManagerException()

data object SettingsNotFoundException : PasswordManagerException()
data class UpdateSettingsException(val propertyName: String) : PasswordManagerException()
data object InternetConnectionLostException : PasswordManagerException()
data object InformationNotFoundException : PasswordManagerException()
data object ChangeUsernameException : PasswordManagerException()
data object UsernameNotFoundException : PasswordManagerException()

data class SignUpException(private val parentName: String?) : PasswordManagerException() {
    val name: String
        get() = parentName ?: this::class.simpleName ?: this::class.java.name
}

data class SignInException(private val parentName: String?) : PasswordManagerException() {
    val name: String
        get() = parentName ?: this::class.simpleName ?: this::class.java.name
}

data class UserEmailCheckException(val email: String) : PasswordManagerException(), EmailException

data object EmptyPasswordException : PasswordManagerException(), PasswordException
data object PasswordResetException : PasswordManagerException()
data object ChangePasswordException : PasswordManagerException()
data class EntityInsertionException(val type: EntityType) : PasswordManagerException()
data class LoadEntitiesException(val entityNames: List<EntityType>) : PasswordManagerException()
data class EntityDeletionException(val type: EntityType) : PasswordManagerException()
data class ServerRequestException(
    val code: Int,
    override val message: String
) : PasswordManagerException()

data object PasswordLengthException : PasswordManagerException(), PasswordException
data object PasswordGenerationException : PasswordManagerException()