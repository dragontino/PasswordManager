package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Entity
import com.security.passwordmanager.R


@Entity(tableName = "WebsiteTable")
class Website(
        id: Int = 0,
        email: String = "",
        var address: String = "",
        var nameWebsite: String = "",
        var nameAccount: String = "",
        var login: String = "",
        var password: String = "",
        var comment: String = "") : Data(id, email) {


    override val key get() = address
    override val type get() = DataType.WEBSITE

    override fun encrypt(encrypt: (String) -> String): Data {
        login = encrypt(login)
        password = encrypt(password)
        return this
    }

    override fun decrypt(decrypt: (String) -> String): Data {
        login = decrypt(login)
        password = decrypt(password)
        return this
    }

    override fun toString(context: Context, needFirstLine: Boolean) =
        buildString {

            if (needFirstLine)
                append(
                    context.getString(R.string.website_label), ": ", nameWebsite, "\n",
                    context.getString(R.string.url_address), ": ", address, "\n"
                )

            append(
                context.getString(R.string.account), ": ", nameAccount, "\n",
                context.getString(R.string.login), ": ", login, "\n",
                context.getString(R.string.password), ": ", password, "\n"
            )

            if (comment.isNotEmpty())
                append(context.getString(R.string.comment), ": ", comment, "\n")
        }

    override fun compareTo(other: Data) : Int {

        val anotherString = when (other) {
            is Website -> other.nameWebsite
            is BankCard -> other.bankName
            else -> ""
        }

        return nameWebsite.compareTo(anotherString)
    }

    override fun isEmpty() =
        login.isEmpty() || password.isEmpty()
}