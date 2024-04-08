package com.security.passwordmanager.domain.model

import android.content.res.Resources
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.entity.EntityType

sealed class PasswordManagerException : Exception() {
    abstract fun getMessage(resources: Resources): String

    protected fun readResolve(): Any = Exception(message)
}

sealed interface EmailException
sealed interface PasswordException

data object UserNotAuthenticatedException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.user_not_authenticated_exception)
}

data object IncorrectEmailException : PasswordManagerException(), EmailException {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.incorrect_email)
}
data object InvalidEmailException : PasswordManagerException(), EmailException {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.invalid_email_exception)
}
data class IncorrectPasswordException(
    private val errorCode: String,
    override val message: String? = null
) : PasswordManagerException(), PasswordException {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.invalid_password, errorCode, message)
}
data object UserDisabledException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.user_disabled_exception)
}
data object TooManyRequestsException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.too_many_requests_exception)
}
data class ConnectionFailedException(override val message: String) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.failed_to_connect_to_server, message)
}
data class TitleNotFoundException(val url: String) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.title_not_found_exception, url)
}

data class LoadIconException(
    val url: String,
    val code: Int
) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.load_icon_exception, url, code)
}

data class LoadWebsiteNameException(
    val url: String,
    val code: Int,
    override val message: String? = null
) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.load_website_name_exception, url, code, message)
}

data object SettingsNotFoundException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.settings_not_found)
}
data class UpdateSettingsException(val propertyName: String) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.update_settings_exception, propertyName)
}
data object InternetConnectionLostException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.check_internet_connection)

}
data object InformationNotFoundException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.cannot_find_info)

}
data object ChangeUsernameException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.change_username_exception)

}
data object UsernameNotFoundException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.username_not_found_exception)

}

data class SignUpException(private val parentName: String?) : PasswordManagerException() {
    val name: String
        get() = parentName ?: this::class.simpleName ?: this::class.java.name

    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.sign_up_exception, name)
}

data class SignInException(private val parentName: String?) : PasswordManagerException() {
    val name: String
        get() = parentName ?: this::class.simpleName ?: this::class.java.name

    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.sign_in_exception, name)
}

data class UserEmailCheckException(val email: String) : PasswordManagerException(), EmailException {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.user_email_check_exception, email)
}

data object EmptyPasswordException : PasswordManagerException(), PasswordException {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.empty_password)

}
data object PasswordResetException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.password_reset_exception)

}
data object ChangePasswordException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.change_password_exception)

}
data class EntityInsertionException(val type: EntityType) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.entity_insertion_exception, type)
}

data class LoadEntitiesException(val entityNames: List<EntityType>) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String {
        return resources.getString(
            R.string.load_entities_exception,
            entityNames.joinToString(" ") { "«${resources.getString(it.tableTitleRes)}»" }
        )
    }
}

data class LoadEntityException(
    val type: EntityType,
    val parentException: Exception
) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String {
        val parentExceptionMessage = when (parentException) {
            is PasswordManagerException -> parentException.getMessage(resources)
            else -> parentException.message ?: "Exception"
        }
        return resources
            .getString(R.string.load_one_entity_exception, type, parentExceptionMessage)
    }
}

data class EntityDeletionException(val type: EntityType) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String {
        return resources.getString(
            R.string.entity_deletion_exception,
            resources.getString(type.tableTitleRes)
        )
    }

}
data class ServerRequestException(
    val code: Int,
    override val message: String
) : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.server_request_exception, code, message)
}

data object PasswordLengthException : PasswordManagerException(), PasswordException {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.password_length_exception)
}

data object PasswordGenerationException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.password_generation_exception)
}

data object EncryptionException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.encryption_exception)
}

data object DecryptionException : PasswordManagerException() {
    override fun getMessage(resources: Resources): String =
        resources.getString(R.string.decryption_exception)
}