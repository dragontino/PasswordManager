package com.security.passwordmanager.domain.model.entity

import android.content.res.Resources
import com.google.firebase.database.Exclude
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.BankCard
import com.security.passwordmanager.domain.model.EncryptionHelper

data class Bank(
    val name: String = "",
    val address: String = "",
    val cards: Map<String, BankCard> = mapOf(),
    val accounts: Map<String, Account> = mapOf(),
) : DatabaseEntity {
    @Exclude
    override val type = EntityType.Bank
    @Exclude
    override val primaryKey: String = ::name.name
    @get:Exclude
    override val valueToCompare get() = name

    override fun compareByPrimaryKey(primaryValue: String): Int {
        return this.name.compareTo(primaryValue)
    }


    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                address.contains(query, ignoreCase = true) ||
                cards.values.any { query in it } ||
                accounts.values.any { query in it }
    }


    override fun encrypt(encryption: EncryptionHelper): Bank? {
        return copy(
            name = encryption.encrypt(name, ::name.name) ?: return null,
            address = encryption.encrypt(address, ::address.name) ?: return null,
            cards = cards.mapValues {
                it.value.encrypt(encryption.copy(::cards.name, it.key)) ?: return null
            },
            accounts = accounts.mapValues {
                it.value.encrypt(encryption.copy(::accounts.name, it.key)) ?: return null
            }
        )
    }

    override fun decrypt(decryption: EncryptionHelper): Bank? {
        return copy(
            name = decryption.decrypt(name, ::name.name) ?: return null,
            address = decryption.decrypt(address, ::address.name) ?: return null,
            cards = cards.mapValues {
                it.value.decrypt(decryption.copy(::cards.name, it.key)) ?: return null
            },
            accounts = accounts.mapValues {
                it.value.decrypt(decryption.copy(::accounts.name, it.key)) ?: return null
            }
        )
    }

    override fun convertToString(resources: Resources) = buildString {
        append(resources.getString(R.string.bank_name), ": ", name, "\n")

        append(cards.values.joinToString("\n\n") { it.convertToString(resources) })

        append(accounts.values.joinToString("\n\n") { it.convertToString(resources) })
    }
}