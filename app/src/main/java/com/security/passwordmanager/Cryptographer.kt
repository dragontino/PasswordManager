package com.security.passwordmanager

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Secure
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class Cryptographer(context: Context) {

    fun encrypt(defaultString: String) = crypt(defaultString, true)

    fun decrypt(defaultString: String) = crypt(defaultString, false)


    @SuppressLint("GetInstance")
    private fun crypt(defaultString: String, encrypt: Boolean): String {
        Security.addProvider(BouncyCastleProvider())

        try {
            val keyBytes = key.toByteArray()
            val secretKey = SecretKeySpec(keyBytes, "AES")
            val input = if (encrypt)
                defaultString.toByteArray()
            else
                Base64.decode(defaultString.trim { it <= ' ' }.toByteArray())

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(
                    if (encrypt) Cipher.ENCRYPT_MODE
                    else Cipher.DECRYPT_MODE,
                    secretKey
                )

                val cryptText = ByteArray(cipher.getOutputSize(input.size))
                var cryptLength = cipher.update(
                    input, 0, input.size,
                    cryptText, 0
                )

                cryptLength += cipher.doFinal(cryptText, cryptLength)

                return if (encrypt)
                    String(Base64.encode(cryptText))
                else String(cryptText).trim { it <= ' ' }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null.toString()
    }


    @SuppressLint("HardwareIds")
    private val key = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
}