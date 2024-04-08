package com.security.passwordmanager.data.repository

import com.security.passwordmanager.data.crypto.CryptoManager
import com.security.passwordmanager.domain.model.EncryptionHelper

sealed class EncryptionRepository(private val cryptoManager: CryptoManager) {
    fun <R : Any> R.encrypt(
        context: String,
        parseToString: (R) -> String = { it.toString() }
    ): String? = when {
        cryptoManager.hasAnyClient() -> cryptoManager.encrypt(
            defaultString = parseToString(this),
            aadContext = context
        )

        else -> null
    }


    private fun <R : Any> String.decrypt(
        context: String,
        parseResult: (String) -> R
    ): R? = when {
        cryptoManager.hasAnyClient() -> cryptoManager
            .decrypt(encryptedString = this, aadContext = context)
            ?.let(parseResult)

        else -> null
    }


    fun String.decrypt(context: String): String? {
        return decrypt(context) { it }
    }




    inner class EncryptionHelperImpl : EncryptionHelper {
        override val parentDir: String

        private constructor(parentDir: String) {
            this.parentDir = parentDir
        }

        constructor(vararg parentDirs: String) {
            this.parentDir = parentDirs.getDir()
        }


        override fun copy(vararg parentDirs: String): EncryptionHelper {
            val newParentDir = "${this.parentDir}/${parentDirs.getDir()}"
            return EncryptionHelperImpl(newParentDir)
        }

        override fun encrypt(value: String, valueName: String): String? {
            val context = createContext(valueName)
            return value.encrypt(context)
        }


        override fun decrypt(value: String, valueName: String): String? {
            val context = createContext(valueName)
            return value.decrypt(context)
        }


        private fun createContext(valueName: String): String = when {
            valueName.isBlank() -> parentDir
            else -> "$parentDir/$valueName"
        }

        private fun Array<out String>.getDir(): String = this
            .filter { it.isNotBlank() }
            .joinToString("/")
    }
}