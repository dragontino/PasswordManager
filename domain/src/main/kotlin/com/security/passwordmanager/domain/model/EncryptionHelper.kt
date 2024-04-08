package com.security.passwordmanager.domain.model

interface EncryptionHelper {
    val parentDir: String

    fun encrypt(value: String, valueName: String): String?
    fun decrypt(value: String, valueName: String): String?
    fun copy(vararg parentDirs: String): EncryptionHelper
}