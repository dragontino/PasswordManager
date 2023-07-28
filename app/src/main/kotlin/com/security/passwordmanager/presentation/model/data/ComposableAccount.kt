package com.security.passwordmanager.presentation.model.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.security.passwordmanager.R
import com.security.passwordmanager.data.UID
import com.security.passwordmanager.data.model.dao.usersdatachild.Account

class ComposableAccount(
    override var uid: UID = UID(),
    name: String = "",
    login: String = "",
    password: String = "",
    comment: String = "",
) : ComposableChild {

    var name: String by mutableStateOf(name)
    var login: String by mutableStateOf(login)
    var password: String by mutableStateOf(password)
    var comment: String by mutableStateOf(comment)

    var passwordIsVisible by mutableStateOf(false)

    var errorLoginMessage by mutableStateOf("")
    var errorPasswordMessage by mutableStateOf("")

    override var isNameRenaming by mutableStateOf(false)


    override val updatedProperties = mutableStateMapOf<String, Pair<String, String>>()


    override fun convertToDao() =
        Account(name, login, password, comment)

    override val haveErrors: Boolean
        get() =
            errorLoginMessage.isNotBlank() || errorPasswordMessage.isNotBlank()


    override fun updateErrors(context: Context) {
        errorLoginMessage = when {
            login.isBlank() -> context.getString(R.string.empty_login)
            else -> ""
        }

        errorPasswordMessage = when {
            password.isBlank() -> context.getString(R.string.empty_password)
            else -> ""
        }
    }
}
