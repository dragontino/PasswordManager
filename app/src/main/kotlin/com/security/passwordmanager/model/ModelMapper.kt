package com.security.passwordmanager.model

import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.BankCard
import com.security.passwordmanager.domain.model.UID
import com.security.passwordmanager.domain.model.UserData
import com.security.passwordmanager.domain.model.entity.Bank
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.Website
import com.security.passwordmanager.domain.model.toUID
import com.security.passwordmanager.model.AccountMapper.mapToComposable
import com.security.passwordmanager.model.BankCardMapper.mapToComposable


sealed interface UserDataMapper<D: UserData> {
    fun D.mapToComposable(uid: UID): ComposableData
}



sealed interface EntityMapper<E: DatabaseEntity> : UserDataMapper<E> {
    override fun E.mapToComposable(uid: UID): ComposableData = mapToComposable()
    fun E.mapToComposable(): ComposableData
}



data object AccountMapper : UserDataMapper<Account> {
    override fun Account.mapToComposable(uid: UID) =
        ComposableAccount(uid, name, login, password, comment)
}



data object WebsiteMapper : EntityMapper<Website> {
    override fun Website.mapToComposable() = ComposableWebsite(
        address = address,
        name = name,
        logoUrl = logoUrl,
        accounts = accounts
            .mapKeys { it.key.toUID() }
            .toSortedMap()
            .map { it.value.mapToComposable(it.key) }
    )
}



data object BankCardMapper : UserDataMapper<BankCard> {
    override fun BankCard.mapToComposable(uid: UID) = ComposableBankCard(
        uid, name, number, holder, validityPeriod, cvv, paymentSystem, pin, comment
    )

}



data object BankMapper : EntityMapper<Bank> {
    override fun Bank.mapToComposable() = ComposableBank(
        name,
        address,
        cards = cards
            .mapKeys { it.key.toUID() }
            .toSortedMap()
            .map { it.value.mapToComposable(it.key) },
        accounts = accounts
            .mapKeys { it.key.toUID() }
            .toSortedMap()
            .map { it.value.mapToComposable(it.key) }
    )
}