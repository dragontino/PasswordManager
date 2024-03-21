package com.security.passwordmanager.domain.model

import com.security.passwordmanager.domain.util.Encrypt

interface UserData {
    fun isEmpty(): Boolean
    operator fun contains(query: String): Boolean
    fun encrypt(encryption: Encrypt): UserData
    fun decrypt(decryption: Encrypt): UserData
}