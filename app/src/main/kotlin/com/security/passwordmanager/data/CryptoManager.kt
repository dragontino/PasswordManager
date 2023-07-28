package com.security.passwordmanager.data

import android.security.keystore.KeyProperties
import android.util.Log
import com.security.passwordmanager.buildString
import com.security.passwordmanager.data.CryptoManager.Companion.IvLength
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs
import kotlin.math.pow

class CryptoManager {
    private companion object {
        const val IvLength = 16
        const val TAG = "Crypto"

        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }


    /**
     * Function, that encrypts given [defaultString] using AES encryption
     * @param defaultString string to encrypt
     * @param passcode encryption key. [passcode] length must be 32 bytes.
     * To create passcode from any string you may use [createPasscodeFrom] function
     * @return encrypted string
     */
    fun encrypt(defaultString: String, passcode: String): String = try {
        val iv = generateIV()
        val key = SecretKeySpec(passcode.encodeToByteArray(), ALGORITHM)
        val ivParams = IvParameterSpec(iv)

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.ENCRYPT_MODE, key, ivParams)
            }

            val encryptedBytes = cipher.doFinal(defaultString.encodeToByteArray())
            return@synchronized (iv + encryptedBytes).encodeToBase64String()
        }
    } catch (e: Exception) {
        Log.e(TAG, "AES Encryption exception: $e")
        defaultString
    }


    /**
     * Function, that decrypts given [encryptedString] using AES encryption
     * @param encryptedString string to decrypt. It must be encrypted using [encrypt] function
     * @param passcode decryption key. [passcode] must be same as in encryption
     * @return decrypted string
     */
    fun decrypt(encryptedString: String, passcode: String): String = try {
        val (iv, encryptedBytes) = encryptedString
            .decodeToBase64ByteArray()
            .let {
                it.sliceArray(0 until IvLength) to
                        it.sliceArray(IvLength until it.size)
            }


        val key = SecretKeySpec(passcode.encodeToByteArray(), ALGORITHM)
        val ivParams = IvParameterSpec(iv)

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, key, ivParams)
            }

            return@synchronized cipher.doFinal(encryptedBytes).decodeToString()
        }
    } catch (e: Exception) {
        Log.e(TAG, "AES Decryption exception: $e")
        encryptedString
    }


    /**
     * Generates initialization vector for encryption / decryption with length [IvLength] bytes
     */
    private fun generateIV() = ByteArray(IvLength).apply {
        SecureRandom.getInstanceStrong().nextBytes(this)
    }


    /**
     * Creates passcode from given string. You can use passcode to generate a key for encryption and decryption functions.
     *
     * NOTE: if you want to use passcode for [encrypt] and [decrypt] functions, [resultLength] param must be 32
     * @param string the string that will be the basis for passcode
     * @param resultLength the length of result string. Default is 32
     * @return passcode
     */
    fun createPasscodeFrom(string: String, resultLength: Int = 32) = buildString(string) {
        val neededSymbolsCount = resultLength - string.length

        for (i in 0 until abs(neededSymbolsCount)) {
            val index = 2.0.pow(i.toDouble()).toInt().coerceIn(string.indices)
            if (neededSymbolsCount > 0) append(string[index]) else deleteAt(index)
        }
    }


    private fun ByteArray.encodeToBase64String(): String =
        Base64.getEncoder().encodeToString(this)


    private fun String.decodeToBase64ByteArray(): ByteArray =
        Base64.getDecoder().decode(this)
}