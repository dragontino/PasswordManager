package com.security.passwordmanager.data.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import com.security.passwordmanager.R
import com.security.passwordmanager.getString
import com.security.passwordmanager.presentation.model.ObservableWebsite
import com.security.passwordmanager.presentation.model.enums.DataType


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
    override val type get() = DataType.Website

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        email = parcel.getString(),
        address = parcel.getString(),
        nameWebsite = parcel.getString(),
        nameAccount = parcel.getString(),
        login = parcel.getString(),
        password = parcel.getString(),
        comment = parcel.getString()
    )

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
        }
        return nameWebsite.compareTo(anotherString)
    }

    override fun observe() =
        ObservableWebsite(
            id, email, address, nameWebsite, nameAccount, login, password, comment
        )

    override fun isEmpty() =
        nameWebsite.isEmpty() || address.isEmpty() || login.isEmpty() || password.isEmpty()


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(nameWebsite)
        parcel.writeString(nameAccount)
        parcel.writeString(login)
        parcel.writeString(password)
        parcel.writeString(comment)
    }



    companion object CREATOR : Parcelable.Creator<Website> {
        override fun createFromParcel(parcel: Parcel): Website {
            return Website(parcel)
        }

        override fun newArray(size: Int): Array<Website?> {
            return arrayOfNulls(size)
        }
    }
}