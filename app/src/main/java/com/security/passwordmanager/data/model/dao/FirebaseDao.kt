package com.security.passwordmanager.data.model.dao

import android.content.Context
import com.security.passwordmanager.data.UID
import com.security.passwordmanager.presentation.model.data.ComposableData

interface FirebaseDao {
    fun convertToString(context: Context) : String
    fun convertToComposable(uid: UID): ComposableData
    fun isEmpty(): Boolean

    fun encrypt(encryption: (String) -> String)

    fun decrypt(decryption: (String) -> String)

    operator fun contains(query: String): Boolean
}