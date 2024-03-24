package com.security.passwordmanager.model

import android.content.Context
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.ChangePasswordException
import com.security.passwordmanager.domain.model.ChangeUsernameException
import com.security.passwordmanager.domain.model.ConnectionFailedException
import com.security.passwordmanager.domain.model.EmptyPasswordException
import com.security.passwordmanager.domain.model.EntityDeletionException
import com.security.passwordmanager.domain.model.EntityInsertionException
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.IncorrectEmailException
import com.security.passwordmanager.domain.model.IncorrectPasswordException
import com.security.passwordmanager.domain.model.InformationNotFoundException
import com.security.passwordmanager.domain.model.InternetConnectionLostException
import com.security.passwordmanager.domain.model.InvalidEmailException
import com.security.passwordmanager.domain.model.LoadEntitiesException
import com.security.passwordmanager.domain.model.LoadIconException
import com.security.passwordmanager.domain.model.LoadWebsiteNameException
import com.security.passwordmanager.domain.model.PasswordGenerationException
import com.security.passwordmanager.domain.model.PasswordLengthException
import com.security.passwordmanager.domain.model.PasswordManagerException
import com.security.passwordmanager.domain.model.PasswordResetException
import com.security.passwordmanager.domain.model.ServerRequestException
import com.security.passwordmanager.domain.model.SettingsNotFoundException
import com.security.passwordmanager.domain.model.SignInException
import com.security.passwordmanager.domain.model.SignUpException
import com.security.passwordmanager.domain.model.TitleNotFoundException
import com.security.passwordmanager.domain.model.TooManyRequestsException
import com.security.passwordmanager.domain.model.UpdateSettingsException
import com.security.passwordmanager.domain.model.UserDisabledException
import com.security.passwordmanager.domain.model.UserEmailCheckException
import com.security.passwordmanager.domain.model.UserNotLoggedException
import com.security.passwordmanager.domain.model.UsernameNotFoundException
import com.security.passwordmanager.model.EntityTypeMapper.tableTitle

class ExceptionMessageImpl(private val context: Context) : ExceptionMessage {
    override fun getMessage(exception: PasswordManagerException): String = when (exception) {
        is UserNotLoggedException -> context.getString(R.string.user_not_authenticated_exception)
        is IncorrectEmailException -> context.getString(R.string.incorrect_email)
        is InvalidEmailException -> context.getString(R.string.invalid_email_exception)
        is IncorrectPasswordException -> context.getString(R.string.invalid_password)
        is UserDisabledException -> context.getString(R.string.user_disabled_exception)
        is TooManyRequestsException -> context.getString(R.string.too_many_requests_exception)
        is ConnectionFailedException -> context.getString(R.string.failed_to_connect_to_server)
        is TitleNotFoundException -> context.getString(R.string.title_not_found_exception, exception.url)
        is SettingsNotFoundException -> context.getString(R.string.settings_not_found)
        is UpdateSettingsException -> context.getString(R.string.update_settings_exception, exception.propertyName)
        is InternetConnectionLostException -> context.getString(R.string.check_internet_connection)
        is InformationNotFoundException -> context.getString(R.string.cannot_find_info)
        is ChangeUsernameException -> context.getString(R.string.change_username_exception)
        is UsernameNotFoundException -> context.getString(R.string.username_not_found_exception)
        is ChangePasswordException -> context.getString(R.string.change_password_exception)
        is PasswordResetException -> context.getString(R.string.password_reset_exception)
        is SignInException -> context.getString(R.string.sign_in_exception, exception.name)
        is SignUpException -> context.getString(R.string.sign_up_exception, exception.name)
        is UserEmailCheckException -> context.getString(R.string.user_email_check_exception, exception.email)
        is EntityInsertionException -> context.getString(R.string.entity_insertion_exception, exception.type)
        is LoadEntitiesException -> context.getString(
            R.string.load_entities_exception,
            exception.entityNames.joinToString(" ") { "«${it.tableTitle(context)}»" }
        )
        is EntityDeletionException -> context.getString(
            R.string.entity_deletion_exception,
            exception.type.tableTitle(context)
        )
        is ServerRequestException -> context.getString(R.string.server_request_exception, exception.code, exception.message)
        is LoadIconException -> context.getString(R.string.load_icon_exception, exception.url, exception.code)
        is LoadWebsiteNameException -> context.getString(R.string.load_website_name_exception, exception.url, exception.code)
        is PasswordGenerationException -> context.getString(R.string.password_generation_exception)
        is PasswordLengthException -> context.getString(R.string.password_length_exception)
        EmptyPasswordException -> context.getString(R.string.empty_password)
    }
}