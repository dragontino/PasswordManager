package com.security.passwordmanager.data.model.dao.usersdatachild

import android.content.Context
import com.google.firebase.database.Exclude
import com.security.passwordmanager.R
import com.security.passwordmanager.data.UID
import com.security.passwordmanager.data.model.dao.FirebaseDao
import com.security.passwordmanager.presentation.model.data.ComposableAccount

data class Account(
    var name: String = "",
    var login: String = "",
    var password: String = "",
    var comment: String = "",
) : FirebaseDao {

    override fun convertToString(context: Context) = buildString {
        if (name.isNotBlank()) {
            append(context.getString(R.string.account), ": ", name, "\n")
        }
        append(
            context.getString(R.string.login), ": ", login, "\n",
            context.getString(R.string.password), ": ", password
        )
        if (comment.isNotBlank())
            append("\n", context.getString(R.string.comment), ": ", comment)
    }


    override fun convertToComposable(uid: UID) =
        ComposableAccount(uid, name, login, password, comment)

    @Exclude
    override fun isEmpty() = login.isEmpty() || password.isEmpty()


    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                comment.contains(query, ignoreCase = true)
    }


    override fun encrypt(encryption: (String) -> String) {
        name = encryption(name)
        login = encryption(login)
        password = encryption(password)
        comment = encryption(comment)

    }


    override fun decrypt(decryption: (String) -> String) {
        name = decryption(name)
        login = decryption(login)
        password = decryption(password)
        comment = decryption(comment)
    }
}
