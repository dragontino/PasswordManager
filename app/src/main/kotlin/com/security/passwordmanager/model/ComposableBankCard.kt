package com.security.passwordmanager.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.security.passwordmanager.domain.model.BankCard
import com.security.passwordmanager.domain.model.UID

class ComposableBankCard(
    override val uid: UID = UID(),
    name: String = "",
    number: String = "",
    holder: String = "",
    validityPeriod: String = "",
    cvv: String = "",
    paymentSystem: String = "",
    pin: String = "",
    comment: String = "",
) : ComposableChild {

    var name by mutableStateOf(name)
    var number by mutableStateOf(number)
    var holder by mutableStateOf(holder)
    var validityPeriod by mutableStateOf(validityPeriod)
    var cvv by mutableStateOf(cvv)
    var paymentSystem by mutableStateOf(paymentSystem)
    var pin by mutableStateOf(pin)
    var comment by mutableStateOf(comment)


    override var isNameRenaming by mutableStateOf(false)


    override fun mapToUserData() = BankCard(
        name,
        number,
        holder,
        validityPeriod,
        cvv,
        paymentSystem,
        pin,
        comment
    )

    override val haveErrors: Boolean
        get() = false

    override fun updateErrors(context: Context) {

    }

    override val updatedProperties = mutableStateMapOf<String, Pair<String, String>>()
}