package com.security.passwordmanager.data.model.dao.usersdatachild

import android.content.Context
import com.google.firebase.database.Exclude
import com.security.passwordmanager.R
import com.security.passwordmanager.data.UID
import com.security.passwordmanager.data.model.dao.FirebaseDao
import com.security.passwordmanager.presentation.model.data.ComposableBankCard

data class BankCard(
    var name: String = "",
    var number: String = "",
    var holder: String = "",
    var validityPeriod: String = "",
    var cvv: String = "",
    var paymentSystem: String = "",
    var pin: String = "",
    var comment: String = "",
) : FirebaseDao {

    override fun convertToString(context: Context) = buildString {
        if (name.isNotBlank()) {
            append(context.getString(R.string.bank_card), ": $name\n")
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

    override fun convertToComposable(uid: UID) = ComposableBankCard(
        uid,
        name,
        number,
        holder,
        validityPeriod,
        cvv,
        paymentSystem,
        pin,
        comment
    )

    @Exclude
    override fun isEmpty() = arrayOf(number, holder, validityPeriod, cvv, pin)
        .any { it.isEmpty() }


    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                number.contains(query, ignoreCase = true) ||
                paymentSystem.contains(query, ignoreCase = true) ||
                comment.contains(query, ignoreCase = true)
    }


    override fun encrypt(encryption: (String) -> String) {
        name = encryption(name)
        number = encryption(number)
        holder = encryption(holder)
        validityPeriod = encryption(validityPeriod)
        cvv = encryption(cvv)
        paymentSystem = encryption(paymentSystem)
        pin = encryption(pin)
        comment = encryption(comment)
    }

    override fun decrypt(decryption: (String) -> String) {
        name = decryption(name)
        number = decryption(number)
        holder = decryption(holder)
        validityPeriod = decryption(validityPeriod)
        cvv = decryption(cvv)
        paymentSystem = decryption(paymentSystem)
        pin = decryption(pin)
        comment = decryption(comment)
    }
}
