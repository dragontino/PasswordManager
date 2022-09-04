package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Entity
import com.security.passwordmanager.R

@Entity(tableName = "BankTable")
class BankCard(
        id : Int = 0,
        email: String = "",
        var bankName: String = "",
        var bankCardName: String = "",
        var cardNumber: String = "",
        var cardHolder: String = "",
        var validity: String = "",
        var cvv: Int = 0,
        var pin: Int = 0,
        var comment: String = "") : Data(id, email) {

    // TODO: 18.07.2022 сделать cvv и pin строками

    override val key get() = bankName
    override val type get() = DataType.BANK_CARD

    var cvvString get() = cvv.toString()
        set(value) {
            cvv = value.toInt()
        }

    var pinString get() = pin.toString()
        set(value) {
            pin = value.toInt()
        }

    override fun encrypt(encrypt: String.() -> String): Data {
        cardNumber = encrypt(cardNumber)
        cardHolder = encrypt(cardHolder)
        validity = encrypt(validity)
        cvvString = encrypt(cvvString)
        pinString = encrypt(pinString)
        return this
    }

    override fun decrypt(decrypt: (String) -> String): Data {
        cardNumber = decrypt(cardNumber)
        cardHolder = decrypt(cardHolder)
        validity = decrypt(validity)
        cvvString = decrypt(cvvString)
        pinString = decrypt(pinString)
        return this
    }

    override fun toString(context: Context, needFirstLine: Boolean) =
        buildString {
            if (needFirstLine)
                append(context.getString(R.string.bank_name), ": ", bankName, "\n")

            append(
                context.getString(R.string.card_number), ": ", cardNumber, "\n",
                context.getString(R.string.card_holder), ": ", cardHolder, "\n",
                context.getString(R.string.validity_period), ": ", validity, "\n",
                context.getString(R.string.card_cvv), ": ", cvv, "\n",
                context.getString(R.string.pin_code), ": ", pin, "\n"
            )
        }

    //сравнение 2 объектов BankCard по bankName
    //или объекта BankCard с Website
    override fun compareTo(other: Data) : Int {

        val anotherString = when (other) {
            is BankCard -> other.bankName
            is Website -> other.nameWebsite
            else -> ""
        }

        return bankName.compareTo(anotherString)
    }

    override fun isEmpty() =
        bankName.isEmpty() || cardNumber.isEmpty() || cardHolder.isEmpty()
                || validity.isEmpty() || cvvString.isEmpty() || pinString.isEmpty()
}