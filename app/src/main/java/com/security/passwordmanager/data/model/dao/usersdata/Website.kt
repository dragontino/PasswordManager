package com.security.passwordmanager.data.model.dao.usersdata

import android.content.Context
import com.google.firebase.database.Exclude
import com.security.passwordmanager.R
import com.security.passwordmanager.data.model.dao.usersdatachild.Account
import com.security.passwordmanager.data.toUID
import com.security.passwordmanager.presentation.model.data.ComposableWebsite
import com.security.passwordmanager.presentation.model.enums.DataType

data class Website(
    var address: String = "",
    var name: String = "",
    var logoUrl: String? = null,
    val accounts: Map<String, Account> = mapOf(),
) : UsersData {
    @get:Exclude
    override val type = DataType.Website

    @get:Exclude
    override val stringToCompare get() = name

    @get:Exclude
    override val keyName = ::address.name

    @get:Exclude
    override val keyValue get() = address


    @Exclude
    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                address.contains(query, ignoreCase = true) ||
                accounts.values.any { query in it }
    }


    override fun convertToString(context: Context) = buildString {
        append(
            context.getString(R.string.website_label), ": ", name, "\n",
            context.getString(R.string.url_address), ": ", address, "\n"
        )

        append(accounts.values.joinToString("\n\n") { it.convertToString(context) })
    }

    override fun convertToComposable() = ComposableWebsite(
        address = address,
        name = name,
        logoUrl = logoUrl,
        accounts = accounts
            .toSortedMap { a1, a2 -> a1.toUID().compareTo(a2.toUID()) }
            .map { it.value.convertToComposable(it.key.toUID()) }
    )

    @Exclude
    override fun isEmpty() =
        name.isEmpty() || address.isEmpty() ||
                accounts.isEmpty() || accounts.any { it.value.isEmpty() }

    override fun encrypt(encryption: (String) -> String) {
        address = encryption(address)
        name = encryption(name)
        logoUrl = logoUrl?.let { encryption(it) }
        accounts.values.forEach {
            it.encrypt(encryption)
        }
    }


    override fun decrypt(decryption: (String) -> String) {
        address = decryption(address)
        name = decryption(name)
        logoUrl = logoUrl?.let { decryption(it) }
        accounts.values.forEach {
            it.decrypt(decryption)
        }
    }
}