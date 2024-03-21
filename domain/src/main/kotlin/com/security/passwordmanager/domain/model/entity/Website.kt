package com.security.passwordmanager.domain.model.entity

import com.google.firebase.database.Exclude
import com.security.passwordmanager.domain.model.Account

data class Website(
    val address: String = "",
    val name: String = "",
    val logoUrl: String? = null,
    val accounts: Map<String, Account> = mapOf(),
) : DatabaseEntity {
    @get:Exclude
    override val type = EntityType.Website

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

    @Exclude
    override fun isEmpty() =
        name.isEmpty() || address.isEmpty() ||
                accounts.isEmpty() || accounts.any { it.value.isEmpty() }

    override fun encrypt(encryption: (String) -> String) = copy(
        address = encryption(address),
        name = encryption(name),
        logoUrl = logoUrl?.let { encryption(it) },
        accounts = accounts.mapValues { it.value.encrypt(encryption) }
    )


    override fun decrypt(decryption: (String) -> String) = copy(
        address = decryption(address),
        name = decryption(name),
        logoUrl = logoUrl?.let { decryption(it) },
        accounts = accounts.mapValues { it.value.decrypt(decryption) }
    )
}