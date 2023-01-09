package com.security.passwordmanager.presentation.model

import com.security.passwordmanager.data.model.BankCard

class ObservableBankCard(id: Int, email: String) : ObservableData(id, email) {
    override fun toData(id: Int, email: String) = BankCard(id, email)

    override val errors: Array<String>
        get() = arrayOf()
}