package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Entity
import androidx.room.Ignore
import com.security.passwordmanager.R

@Entity(tableName = "BankTable")
class BankCard(
        id : Int,
        var bankName: String,
        var cardNumber: String,
        var cardHolder: String,
        var validity: String,
        var cvv: Int,
        var pin : Int) : Data(id) {

    @Ignore
    constructor() : this(0, "", "", "", "", 0, 0)


    private fun getCvv(): String {
        return cvv.toString()
    }

    private fun setCvv(cvv: String) {
        this.cvv = cvv.toInt()
    }

    private fun getPin(): String {
        return pin.toString()
    }

    private fun setPin(pin: String) {
        this.pin = pin.toInt()
    }

    @Ignore
    override val key = bankName

    @Ignore
    override val type = DataType.BANK_CARD

    override fun encrypt(encrypt: (String) -> String): Data {
        cardNumber = encrypt(cardNumber)
        cardHolder = encrypt(cardHolder)
        validity = encrypt(validity)
        setCvv(encrypt(getCvv()))
        setPin(encrypt(getPin()))
        return this
    }

    override fun decrypt(decrypt: (String) -> String): Data {
        cardNumber = decrypt(cardNumber)
        cardHolder = decrypt(cardHolder)
        validity = decrypt(validity)
        setCvv(decrypt(getCvv()))
        setPin(decrypt(getPin()))
        return this
    }

    override fun toString(context: Context, needHeading: Boolean): String {
        val sb = StringBuilder()

        if (needHeading)
            sb.append(context.getString(R.string.bank_name)).append(": ")
                .append(bankName).append("\n")

        sb.append(context.getString(R.string.card_number)).append(": ")
                .append(cardNumber).append("\n")
                .append(context.getString(R.string.card_holder)).append(": ")
                .append(cardHolder).append("\n")
                .append(context.getString(R.string.validity_period)).append(": ")
                .append(validity).append("\n")
                .append(context.getString(R.string.card_cvv)).append(": ")
                .append(cvv).append("\n")
                .append(context.getString(R.string.pin_code)).append(": ")
                .append(pin).append("\n")

        return sb.toString()
    }

    //сравнение 2 объектов BankCard по bankName
    //или объекта BankCard с Website
    override fun compareTo(other: Data) : Int {

        val anotherString = when (other) {
            is BankCard -> other.bankName
            is Website -> other.nameWebsite
            else -> bankName
        }

        return bankName.compareTo(anotherString)
    }
}