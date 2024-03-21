package com.security.passwordmanager.domain.model

import com.google.firebase.database.Exclude
import com.security.passwordmanager.domain.util.Encrypt

data class Account(
    val name: String = "",
    val login: String = "",
    val password: String = "",
    val comment: String = "",
) : UserData {
    @Exclude
    override fun isEmpty() = login.isEmpty() || password.isEmpty()


    override fun contains(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
                comment.contains(query, ignoreCase = true)
    }


    override fun encrypt(encryption: Encrypt) = copy(
        name = encryption(name),
        login = encryption(login),
        password = encryption(password),
        comment = encryption(comment)
    )


    override fun decrypt(decryption: Encrypt) = copy(
        name = decryption(name),
        login = decryption(login),
        password = decryption(password),
        comment = decryption(comment)
    )
}
