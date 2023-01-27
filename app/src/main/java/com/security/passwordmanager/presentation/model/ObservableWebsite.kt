package com.security.passwordmanager.presentation.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.security.passwordmanager.R
import com.security.passwordmanager.data.model.Website

class ObservableWebsite(
    id: Int = 0,
    email: String = "",
    address: String = "",
    nameWebsite: String = "",
    nameAccount: String = "",
    login: String = "",
    password: String = "",
    comment: String = ""
) : ObservableData(id, email) {

    var address by mutableStateOf(address)
    var nameWebsite by mutableStateOf(nameWebsite)
    var nameAccount by mutableStateOf(nameAccount)
    var login by mutableStateOf(login)
    var password by mutableStateOf(password)
    var comment by mutableStateOf(comment)

    var passwordIsVisible by mutableStateOf(false)

    var errorAddressMessage by mutableStateOf("")

    var errorNameWebsiteMessage by mutableStateOf("")

    var errorLoginMessage by mutableStateOf("")

    var errorPasswordMessage by mutableStateOf("")


    override fun toData(id: Int, email: String) =
        Website(id, email, address, nameWebsite, nameAccount, login, password, comment)

    override val errors: Array<String>
        get() = arrayOf(
            errorAddressMessage,
            errorNameWebsiteMessage,
            errorLoginMessage,
            errorPasswordMessage
        )

    override fun decrypt(decrypt: (String) -> String) {
        login = decrypt(login)
        password = decrypt(password)
        comment = decrypt(comment)
    }


    override val haveErrors: Boolean
        get() = super.haveErrors ||
                address.isBlank() ||
                nameWebsite.isBlank() ||
                login.isBlank() ||
                password.isBlank()

    fun updateErrors(context: Context) {
        errorAddressMessage = if (address.isBlank()) context.getString(R.string.empty_url) else ""

        errorNameWebsiteMessage = if (nameWebsite.isBlank())
            context.getString(R.string.empty_website_name)
        else ""

        errorLoginMessage = if (login.isBlank()) context.getString(R.string.empty_login) else ""

        errorPasswordMessage = if (password.isBlank())
            context.getString(R.string.empty_password)
        else ""
    }
}