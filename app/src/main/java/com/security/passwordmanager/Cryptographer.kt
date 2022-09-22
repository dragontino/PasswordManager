package com.security.passwordmanager

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security
import javax.crypto.Cipher

class Cryptographer(context: Context) {

    fun encrypt(defaultString: String) = defaultString //crypt(defaultString, true)

    fun decrypt(defaultString: String) = defaultString //crypt(defaultString, false)


    private fun generateKeyPair(): KeyPair? = try {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(1024)
        keyPairGenerator.genKeyPair()
    } catch (e: Exception) {
        Log.e("Crypto", "RSA key pair error")
        null
    }


    private fun crypt(defaultString: String, encrypt: Boolean): String {
        Security.addProvider(BouncyCastleProvider())

        val keyPair = generateKeyPair() ?: return defaultString

        try {
//            val secureRandom = SecureRandom.getInstance("SHA1PRNG")
//            val keyGenerator = KeyGenerator
//                .getInstance("AES")
//                .apply { init(128, secureRandom) }
//            val keyBytes = key.toByteArray()
//            val secretKey = SecretKeySpec(keyBytes, "AES")
//            val secretKey = SecretKeySpec(keyGenerator.generateKey().encoded, "AES")
//            val input = if (encrypt)
//                defaultString.toByteArray()
//            else
//                Base64.decode(defaultString.trim { it <= ' ' }.toByteArray())

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("RSA")
                if (encrypt)
                    cipher.init(Cipher.ENCRYPT_MODE, keyPair.private)
                else
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.public)

                val resultBytes = cipher.doFinal(defaultString.toByteArray())

                return if (encrypt)
                    String(Base64.encode(resultBytes))
                else
                    String(resultBytes)

//                val cryptText = ByteArray(cipher.getOutputSize(input.size))
//                var cryptLength = cipher.update(
//                    input, 0, input.size,
//                    cryptText, 0
//                )
//
//                cryptLength += cipher.doFinal(cryptText, cryptLength)
//
//                return if (encrypt)
//                    String(Base64.encode(cryptText))
//                else String(cryptText).trim { it <= ' ' }
            }
        } catch (e: Exception) {
            Log.e("Crypto", "RSA ${if (encrypt) "Encryption" else "Decryption"} error")
        }

        return null.toString()
    }


    @SuppressLint("HardwareIds")
    private val key = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}