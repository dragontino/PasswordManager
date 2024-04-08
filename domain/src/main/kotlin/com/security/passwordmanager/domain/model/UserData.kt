package com.security.passwordmanager.domain.model

import android.content.res.Resources

interface UserData {
    operator fun contains(query: String): Boolean
    fun encrypt(encryption: EncryptionHelper): UserData?
    fun decrypt(decryption: EncryptionHelper): UserData?
    fun convertToString(resources: Resources): String
}