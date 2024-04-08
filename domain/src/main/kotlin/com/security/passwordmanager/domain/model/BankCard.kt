package com.security.passwordmanager.domain.model

import android.content.res.Resources
import com.security.passwordmanager.domain.R

data class BankCard(
    val name: String = "",
    val number: String = "",
    val holder: String = "",
    val validityPeriod: String = "",
    val cvv: String = "",
    val paymentSystem: String = "",
    val pin: String = "",
    val comment: String = "",
) : UserData {

    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                number.contains(query, ignoreCase = true) ||
                paymentSystem.contains(query, ignoreCase = true) ||
                comment.contains(query, ignoreCase = true)
    }


    override fun encrypt(encryption: EncryptionHelper): BankCard? {
        return copy(
            name = encryption.encrypt(name, ::name.name) ?: return null,
            number = encryption.encrypt(number, ::number.name) ?: return null,
            holder = encryption.encrypt(holder, ::holder.name) ?: return null,
            validityPeriod = encryption.encrypt(validityPeriod, ::validityPeriod.name) ?: return null,
            cvv = encryption.encrypt(cvv, ::cvv.name) ?: return null,
            paymentSystem = encryption.encrypt(paymentSystem, ::paymentSystem.name) ?: return null,
            pin = encryption.encrypt(pin, ::pin.name) ?: return null,
            comment = encryption.encrypt(comment, ::comment.name) ?: return null,
        )
    }

    override fun decrypt(decryption: EncryptionHelper): BankCard? {
        return copy(
            name = decryption.decrypt(name, ::name.name) ?: return null,
            number = decryption.decrypt(number, ::number.name) ?: return null,
            holder = decryption.decrypt(holder, ::holder.name) ?: return null,
            validityPeriod = decryption.decrypt(validityPeriod, ::validityPeriod.name) ?: return null,
            cvv = decryption.decrypt(cvv, ::cvv.name) ?: return null,
            paymentSystem = decryption.decrypt(paymentSystem, ::paymentSystem.name) ?: return null,
            pin = decryption.decrypt(pin, ::pin.name) ?: return null,
            comment = decryption.decrypt(comment, ::comment.name) ?: return null,
        )
    }

    override fun convertToString(resources: Resources) = buildString {
        if (name.isNotBlank()) {
            append(resources.getString(R.string.card), ": $name\n")
        }

        append(
            resources.getString(R.string.card_number), ": $number\n",
            resources.getString(R.string.card_holder), ": $holder\n",
            resources.getString(R.string.validity_period), ": $validityPeriod\n",
            resources.getString(R.string.card_cvv), ": $cvv\n",
            resources.getString(R.string.pin_code), ": $pin\n"
        )

        if (paymentSystem.isNotBlank()) {
            append(resources.getString(R.string.payment_system), ": $paymentSystem\n")
        }
        if (comment.isNotBlank()) {
            append(resources.getString(R.string.comment), ":$comment\n")
        }
    }
}
