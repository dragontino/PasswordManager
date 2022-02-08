package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Entity
import androidx.room.Ignore
import com.security.passwordmanager.Cryptographer
import com.security.passwordmanager.R


@Entity(tableName = "WebsiteTable")
class Website(
        id: Int,
        var address: String,
        var nameWebsite: String,
        var nameAccount: String,
        var login: String,
        var password: String,
        var comment: String) : Data(id) {

    @Ignore
    constructor() : this(0, "", "", "", "", "", "")

    override fun getKey() = address

    override fun encrypt(cryptographer: Cryptographer): Data {
//        login = cryptographer.encrypt(login)
//        password = cryptographer.encrypt(password)
        return this
    }

    override fun decrypt(cryptographer: Cryptographer): Data {
//        login = cryptographer.decrypt(login)
//        password = cryptographer.decrypt(password)
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