package com.security.passwordmanager.domain.model.entity

import com.google.firebase.database.Exclude
import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.BankCard
import com.security.passwordmanager.domain.model.UID

data class Bank(
    val name: String = "",
    val address: String = "",
    val cards: Map<UID, BankCard> = mapOf(),
    val accounts: Map<UID, Account> = mapOf(),
) : DatabaseEntity {

    @Exclude
    override val type = EntityType.Bank

    @get:Exclude
    override val stringToCompare get() = name

    @Exclude
    override val keyName = ::name.name

    @get:Exclude
    override val keyValue get() = name


    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                address.contains(query, ignoreCase = true) ||
                cards.values.any { query in it } ||
                accounts.values.any { query in it }
    }


    override fun isEmpty() =
        name.isEmpty() || cards.isEmpty() || cards.values.any { it.isEmpty() }


    override fun encrypt(encryption: (String) -> String) = copy(
        name = encryption(name),
        address = encryption(address),
        cards = cards.mapValues { it.value.encrypt(encryption) }
    )

    override fun decrypt(decryption: (String) -> String) = copy(
        name = decryption(name),
        address = decryption(address),
        cards = cards.mapValues { it.value.decrypt(decryption) }
    )
}