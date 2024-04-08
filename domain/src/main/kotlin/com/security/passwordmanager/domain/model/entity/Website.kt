package com.security.passwordmanager.domain.model.entity

import android.content.res.Resources
import com.google.firebase.database.Exclude
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.EncryptionHelper

data class Website(
    val address: String = "",
    val name: String = "",
    val logoUrl: String? = null,
    val accounts: Map<String, Account> = mapOf(),
) : DatabaseEntity {
    @get:Exclude
    override val type = EntityType.Website

    @get:Exclude
    override val valueToCompare get() = name

    @get:Exclude
    override val primaryKey = ::address.name

    override fun compareByPrimaryKey(primaryValue: String): Int {
        return address.compareTo(primaryValue)
    }


    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                address.contains(query, ignoreCase = true) ||
                accounts.values.any { query in it }
    }

    override fun encrypt(encryption: EncryptionHelper): Website? {
        return copy(
            address = encryption.encrypt(address, ::address.name) ?: return null,
            name = encryption.encrypt(name, ::name.name) ?: return null,
            logoUrl = logoUrl?.let { encryption.encrypt(it, ::logoUrl.name) ?: return null },
            accounts = accounts.mapValues {
                it.value.encrypt(encryption.copy(::accounts.name, it.key))
                    ?: return null
            }
        )
    }


    override fun decrypt(decryption: EncryptionHelper): Website? {
        return copy(
            address = decryption.decrypt(address, ::address.name) ?: return null,
            name = decryption.decrypt(name, ::name.name) ?: return null,
            logoUrl = logoUrl?.let { decryption.decrypt(it, ::logoUrl.name) ?: return null },
            accounts = accounts.mapValues {
                it.value.decrypt(decryption.copy(::accounts.name, it.key))
                    ?: return null
            }
        )
    }


    override fun convertToString(resources: Resources) = buildString {
        append(
            resources.getString(R.string.website_label), ": ", name, "\n",
            resources.getString(R.string.url_address), ": ", address, "\n"
        )

        append(accounts.values.joinToString("\n\n") { it.convertToString(resources) })
    }
}