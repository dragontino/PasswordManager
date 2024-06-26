package com.security.passwordmanager.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.security.passwordmanager.domain.model.entity.Bank

class ComposableBank(
    name: String = "",
    address: String = "",
    cards: List<ComposableBankCard> = listOf(ComposableBankCard()),
    accounts: List<ComposableAccount> = listOf(ComposableAccount()),
) : ComposableData {

    var name: String by mutableStateOf(name)
    var address: String by mutableStateOf(address)

    val cards = ComposableChildren(cards).ifEmpty {
        ComposableChildren(listOf(ComposableBankCard()))
    }

    val accounts = ComposableChildren(children = accounts).apply {
        if (isEmpty()) add(ComposableAccount())
    }

    override fun mapToUserData() = Bank(
        name,
        address,
        cards.associate { it.uid.toString() to it.mapToUserData() },
        accounts.associate { it.uid.toString() to it.mapToUserData() }
    )

    override val haveErrors: Boolean
        get() =
            cards.any { it.haveErrors } ||
                    accounts.any { it.haveErrors }

    override fun updateErrors(context: Context) {
        TODO("Not yet implemented")
    }

    override val updatedProperties = mutableStateMapOf<String, Pair<String, String>>()

}