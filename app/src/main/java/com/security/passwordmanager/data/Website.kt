package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Entity
import androidx.room.Ignore
import com.security.passwordmanager.R


@Entity(tableName = "WebsiteTable")
class Website(
        id: Int = 0,
        var address: String = "",
        var nameWebsite: String = "",
        var nameAccount: String = "",
        var login: String = "",
        var password: String = "",
        var comment: String = "") : Data(id) {

    @Ignore
    override val key = address

    @Ignore
    override val type = DataType.WEBSITE

    override fun encrypt(encrypt: (String) -> (String)): Data {
//        login = encrypt(login)
//        password = encrypt(password)
        return this
    }

    override fun decrypt(decrypt: (String) -> String): Data {
//        login = decrypt(login)
//        password = decrypt(password)
        return this
    }

    override fun toString(context: Context, needHeading: Boolean): String {
        val sb = StringBuilder()

        if (needHeading) {
            sb.append(context.getString(R.string.password_label)).append(": ")
                    .append(nameWebsite).append("\n")
                    .append(context.getString(R.string.url_address)).append(": ")
                    .append(address).append("\n")
        }

        sb.append(context.getString(R.string.account)).append(": ")
                .append(nameAccount).append("\n")
                .append(context.getString(R.string.login)).append(": ")
                .append(login).append("\n")
                .append(context.getString(R.string.password)).append(": ")
                .append(password).append("\n")



        if (comment.isNotEmpty())
            sb.append(context.getString(R.string.comment)).append(": ")
                .append(comment).append("\n")

        return sb.toString()
    }

    override fun compareTo(other: Data) : Int {

        val anotherString = when (other) {
            is Website -> other.nameWebsite
            is BankCard -> other.bankName
            else -> nameWebsite
        }

        return nameWebsite.compareTo(anotherString)
    }
}