package com.security.passwordmanager.data.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import com.security.passwordmanager.R
import com.security.passwordmanager.getString
import com.security.passwordmanager.presentation.model.ObservableBankCard
import com.security.passwordmanager.presentation.model.enums.DataType

@Entity(tableName = "BankTable")
class BankCard(
    id: Int = 0,
    email: String = "",
    var bankName: String = "",
    var bankCardName: String = "",
    var cardNumber: String = "",
    var cardHolder: String = "",
    var validity: String = "",
    var cvv: String = "",
    var pin: String = "",
    var comment: String = "",
) : Data(id, email) {

    override val key get() = bankName
    override val type get() = DataType.BankCard
    override val stringToCompare get() = bankName

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        email = parcel.getString(),
        bankName = parcel.getString(),
        bankCardName = parcel.getString(),
        cardNumber = parcel.getString(),
        cardHolder = parcel.getString(),
        validity = parcel.getString(),
        cvv = parcel.getString(),
        pin = parcel.getString(),
        comment = parcel.getString()
    )

    override fun encrypt(encrypt: String.() -> String): Data {
        cardNumber = encrypt(cardNumber)
        cardHolder = encrypt(cardHolder)
        validity = encrypt(validity)
        cvv = encrypt(cvv)
        pin = encrypt(pin)
        return this
    }

    override fun decrypt(decrypt: (String) -> String): Data {
        cardNumber = decrypt(cardNumber)
        cardHolder = decrypt(cardHolder)
        validity = decrypt(validity)
        cvv = decrypt(cvv)
        pin = decrypt(pin)
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

    override fun observe() =
        ObservableBankCard(id, email)

    override fun isEmpty() =
        bankName.isEmpty() || cardNumber.isEmpty() || cardHolder.isEmpty()
                || validity.isEmpty() || cvv.isEmpty() || pin.isEmpty()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bankName)
        parcel.writeString(bankCardName)
        parcel.writeString(cardNumber)
        parcel.writeString(cardHolder)
        parcel.writeString(validity)
        parcel.writeString(cvv)
        parcel.writeString(pin)
        parcel.writeString(comment)
    }


    companion object CREATOR : Parcelable.Creator<BankCard> {
        override fun createFromParcel(parcel: Parcel): BankCard {
            return BankCard(parcel)
        }

        override fun newArray(size: Int): Array<BankCard?> {
            return arrayOfNulls(size)
        }
    }
}