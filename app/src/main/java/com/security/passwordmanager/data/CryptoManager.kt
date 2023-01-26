package com.security.passwordmanager.data

import android.util.Log
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Security
import javax.crypto.Cipher

class CryptoManager {

    // FIXME: 27.01.2023 сделать шифрование
    fun encrypt(defaultString: String) = defaultString //crypt(defaultString, true)

    // FIXME: 27.01.2023 сделать шифрование
    fun decrypt(defaultString: String) = defaultString //crypt(defaultString, false)


//    fun newEncrypt(defaultString: String, writeIv: (ByteArray) -> Unit): String {
//        val encryptedBytes = encryptCipher.doFinal(defaultString.encodeToByteArray())
//        writeIv(encryptCipher.iv)
//        return encryptedBytes.decodeToString()
//    }
//
//    fun newDecrypt(defaultString: String, iv: ByteArray): String {
//        println("iv = $iv")
//        println("inputBytes = ${defaultString.encodeToByteArray()}")
//        return getDecryptCipherForIv(iv)
//            .doFinal(defaultString.encodeToByteArray())
//            .decodeToString()
//    }



//    private companion object {
//        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
//        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
//        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
//        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
//    }


    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

//    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
//        init(Cipher.ENCRYPT_MODE, getKey())
//    }

//    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
//        return Cipher.getInstance(TRANSFORMATION).apply {
//            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
//        }
//    }



//    private fun getKey(): SecretKey {
//        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
//        return existingKey?.secretKey ?: createKey()
//    }


//    private fun createKey(): SecretKey {
//        return KeyGenerator.getInstance(ALGORITHM).apply {
//            init(
//                KeyGenParameterSpec
//                    .Builder(
//                        "secret",
//                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
//                    )
//                    .setBlockModes(BLOCK_MODE)
//                    .setEncryptionPaddings(PADDING)
//                    .setUserAuthenticationRequired(false)
//                    .setRandomizedEncryptionRequired(true)
//                    .build()
//            )
//        }.generateKey()
//    }


    private fun generateKeyPair(): KeyPair? = try {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        keyPairGenerator.genKeyPair()
    } catch (e: Exception) {
        Log.e("Crypto", "RSA key pair error")
        null
    }


    fun crypt(defaultString: String, encrypt: Boolean): String {
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
                if (encrypt) {
                    cipher.init(Cipher.ENCRYPT_MODE, keyPair.private)
                } else {
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.public)
                }

                val resultBytes = cipher.doFinal(defaultString.encodeToByteArray())

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


//    @SuppressLint("HardwareIds")
//    private val key = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}