package com.security.passwordmanager.domain.model

import android.content.res.Resources
import com.security.passwordmanager.domain.R

data class Account(
    val name: String = "",
    val login: String = "",
    val password: String = "",
    val comment: String = "",
) : UserData {

    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                comment.contains(query, ignoreCase = true)
    }


    override fun encrypt(encryption: EncryptionHelper): Account? {
        return copy(
            name = encryption.encrypt(name, ::name.name) ?: return null,
            login = encryption.encrypt(login, ::login.name) ?: return null,
            password = encryption.encrypt(password, ::password.name) ?: return null,
            comment = encryption.encrypt(comment, ::comment.name) ?: return null
        )
    }


    override fun decrypt(decryption: EncryptionHelper): Account? {
        return copy(
            name = decryption.decrypt(name, ::name.name) ?: return null,
            login = decryption.decrypt(login, ::login.name) ?: return null,
            password = decryption.decrypt(password, ::password.name) ?: return null,
            comment = decryption.decrypt(comment, ::comment.name) ?: return null,
        )
    }


    override fun convertToString(resources: Resources) = buildString {
        if (name.isNotBlank()) {
            append(resources.getString(R.string.account), ": ", name, "\n")
        }

        append(
            resources.getString(R.string.login), ": ", login, "\n",
            resources.getString(R.string.password), ": ", password
        )
        if (comment.isNotBlank())
            append("\n", resources.getString(R.string.comment), ": ", comment)
    }
}
