package com.security.passwordmanager.model

import java.io.Serializable

data class DataUI(
    val title: Data,
    val accountList: MutableList<Data> = arrayListOf(title)
) : Serializable {
    companion object {
        val Website get() = DataUI(Website())
        val BankCard get() = DataUI(BankCard())
    }
}