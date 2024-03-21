package com.security.passwordmanager.domain.model

import com.google.firebase.database.Exclude

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

    @Exclude
    override fun isEmpty() = arrayOf(number, holder, validityPeriod, cvv, pin)
        .any { it.isEmpty() }


    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                number.contains(query, ignoreCase = true) ||
                paymentSystem.contains(query, ignoreCase = true) ||
                comment.contains(query, ignoreCase = true)
    }


    override fun encrypt(encryption: (String) -> String) = copy(
        name = encryption(name),
        number = encryption(number),
        holder = encryption(holder),
        validityPeriod = encryption(validityPeriod),
        cvv = encryption(cvv),
        paymentSystem = encryption(paymentSystem),
        pin = encryption(pin),
        comment = encryption(comment)
    )

    override fun decrypt(decryption: (String) -> String) = copy(
        name = decryption(name),
        number = decryption(number),
        holder = decryption(holder),
        validityPeriod = decryption(validityPeriod),
        cvv = decryption(cvv),
        paymentSystem = decryption(paymentSystem),
        pin = decryption(pin),
        comment = decryption(comment)
    )
}
