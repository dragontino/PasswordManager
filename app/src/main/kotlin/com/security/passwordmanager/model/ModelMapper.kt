package com.security.passwordmanager.model

import android.content.Context
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.BankCard
import com.security.passwordmanager.domain.model.ColorScheme
import com.security.passwordmanager.domain.model.ColorScheme.Auto
import com.security.passwordmanager.domain.model.ColorScheme.Dark
import com.security.passwordmanager.domain.model.ColorScheme.Light
import com.security.passwordmanager.domain.model.ColorScheme.System
import com.security.passwordmanager.domain.model.UID
import com.security.passwordmanager.domain.model.UserData
import com.security.passwordmanager.domain.model.entity.Bank
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.model.entity.Website
import com.security.passwordmanager.domain.model.toUID
import com.security.passwordmanager.model.AccountMapper.convertToString
import com.security.passwordmanager.model.AccountMapper.mapToComposable
import com.security.passwordmanager.model.BankCardMapper.convertToString
import com.security.passwordmanager.model.BankCardMapper.mapToComposable
import com.security.passwordmanager.model.BankMapper.convertToString
import com.security.passwordmanager.model.WebsiteMapper.convertToString


sealed interface UserDataMapper<D: UserData> {
    fun D.convertToString(context: Context): String
    fun D.mapToComposable(uid: UID): ComposableData
}



sealed interface EntityMapper<E: DatabaseEntity> : UserDataMapper<E> {
    override fun E.mapToComposable(uid: UID): ComposableData = mapToComposable()
    fun E.mapToComposable(): ComposableData
}



data object AccountMapper : UserDataMapper<Account> {
    override fun Account.convertToString(context: Context) = buildString {
        if (name.isNotBlank()) {
            append(context.getString(R.string.account), ": ", name, "\n")
        }

        append(
            context.getString(R.string.login), ": ", login, "\n",
            context.getString(R.string.password), ": ", password
        )
        if (comment.isNotBlank())
            append("\n", context.getString(R.string.comment), ": ", comment)
    }

    override fun Account.mapToComposable(uid: UID) =
        ComposableAccount(uid, name, login, password, comment)
}



data object WebsiteMapper : EntityMapper<Website> {
    override fun Website.convertToString(context: Context) = buildString {
        append(
            context.getString(R.string.website_label), ": ", name, "\n",
            context.getString(R.string.url_address), ": ", address, "\n"
        )

        append(accounts.values.joinToString("\n\n") { it.convertToString(context) })
    }

    override fun Website.mapToComposable() = ComposableWebsite(
        address = address,
        name = name,
        logoUrl = logoUrl,
        accounts = accounts
            .toSortedMap { a1, a2 -> a1.toUID().compareTo(a2.toUID()) }
            .map { it.value.mapToComposable(it.key.toUID()) }
    )
}



data object BankCardMapper : UserDataMapper<BankCard> {
    override fun BankCard.convertToString(context: Context) = buildString {
        if (name.isNotBlank()) {
            append(context.getString(R.string.card), ": $name\n")
        }

        append(
            context.getString(R.string.card_number), ": $number\n",
            context.getString(R.string.card_holder), ": $holder\n",
            context.getString(R.string.validity_period), ": $validityPeriod\n",
            context.getString(R.string.card_cvv), ": $cvv\n",
            context.getString(R.string.pin_code), ": $pin\n"
        )

        if (paymentSystem.isNotBlank()) {
            append(context.getString(R.string.payment_system), ": $paymentSystem\n")
        }
        if (comment.isNotBlank()) {
            append(context.getString(R.string.comment), ":$comment\n")
        }
    }

    override fun BankCard.mapToComposable(uid: UID) = ComposableBankCard(
        uid, name, number, holder, validityPeriod, cvv, paymentSystem, pin, comment
    )

}



data object BankMapper : EntityMapper<Bank> {
    override fun Bank.convertToString(context: Context) = buildString {
        append(context.getString(R.string.bank_name), ": ", name, "\n")

        append(cards.values.joinToString("\n\n") { it.convertToString(context) })

        append(accounts.values.joinToString("\n\n") { it.convertToString(context) })
    }

    override fun Bank.mapToComposable() = ComposableBank(
        name,
        address,
        cards = cards.map { it.value.mapToComposable(it.key) },
        accounts = accounts.map { it.value.mapToComposable(it.key) }
    )
}



data object ColorSchemeMapper {
    fun ColorScheme.title(context: Context): String = when (this) {
        Light -> context.getString(R.string.light_theme)
        Dark -> context.getString(R.string.dark_theme)
        System -> context.getString(R.string.system_theme)
        Auto -> context.getString(R.string.auto_theme)
    }
}


data object EntityTypeMapper {
    fun EntityType.tableTitle(context: Context): String = when (this) {
        EntityType.All -> context.getString(R.string.all_entries)
        EntityType.Website -> context.getString(R.string.website_table_name)
        EntityType.Bank -> context.getString(R.string.bank_table_name)
    }
}



fun DatabaseEntity.convertToString(context: Context): String = when(this) {
    is Bank -> this.convertToString(context)
    is Website -> this.convertToString(context)
}