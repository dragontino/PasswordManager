package com.security.passwordmanager.presentation.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.security.passwordmanager.data.model.BankCard

class ObservableBankCard(
    id: Int = 0,
    email: String = "",
    bankName: String = "",
    bankCardName: String = "",
    cardNumber: String = "",
    cardHolder: String = "",
    validity: String = "",
    cvv: String = "",
    pin: String = "",
    comment: String = ""
) : ObservableData(id, email) {

    var bankName by mutableStateOf(bankName)
    var bankCardName by mutableStateOf(bankCardName)
    var cardNumber by mutableStateOf(cardNumber)
    var cardHolder by mutableStateOf(cardHolder)
    var validity by mutableStateOf(validity)
    var cvv by mutableStateOf(cvv)
    var pin by mutableStateOf(pin)
    var comment by mutableStateOf(comment)


    override fun toData(id: Int, email: String) = BankCard(id, email)

    override val errors: Array<String>
        get() = arrayOf()

    override fun decrypt(decrypt: (String) -> String) {
        TODO("Not yet implemented")
    }
}