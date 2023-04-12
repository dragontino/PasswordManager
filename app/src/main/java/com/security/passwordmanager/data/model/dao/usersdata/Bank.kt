package com.security.passwordmanager.data.model.dao.usersdata

import android.content.Context
import com.google.firebase.database.Exclude
import com.security.passwordmanager.R
import com.security.passwordmanager.data.UID
import com.security.passwordmanager.data.model.dao.usersdatachild.Account
import com.security.passwordmanager.data.model.dao.usersdatachild.BankCard
import com.security.passwordmanager.presentation.model.data.ComposableBank
import com.security.passwordmanager.presentation.model.enums.DataType

data class Bank(
    var name: String = "",
    var address: String = "",
    val cards: Map<UID, BankCard> = mapOf(),
    val accounts: Map<UID, Account> = mapOf(),
) : UsersData {

    @Exclude
    override val type = DataType.Bank

    @get:Exclude
    override val stringToCompare get() = name

    @Exclude
    override val keyName = ::name.name

    @get:Exclude
    override val keyValue get() = name

    override fun convertToString(context: Context) = buildString {
        append(context.getString(R.string.bank_name), ": ", name, "\n")

        append(cards.values.joinToString("\n\n") { it.convertToString(context) })

        append(accounts.values.joinToString("\n\n") { it.convertToString(context) })
    }


    override fun convertToComposable() = ComposableBank(
        name,
        address,
        cards.map { it.value.convertToComposable(it.key) },
        accounts.map { it.value.convertToComposable(it.key) }
    )


    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                address.contains(query, ignoreCase = true) ||
                cards.values.any { query in it } ||
                accounts.values.any { query in it }
    }



    override fun isEmpty() =
        name.isEmpty() || cards.isEmpty() || cards.values.any { it.isEmpty() }



    override fun encrypt(encryption: (String) -> String) {
        name = encryption(name)
        address = encryption(address)
        cards.values.forEach { it.encrypt(encryption) }
    }

    override fun decrypt(decryption: (String) -> String) {
        name = decryption(name)
        address = decryption(address)
        cards.values.forEach { it.decrypt(decryption) }
    }
}