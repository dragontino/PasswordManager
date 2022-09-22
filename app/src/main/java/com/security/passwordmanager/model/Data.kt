package com.security.passwordmanager.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
sealed class Data(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var email: String = ""
) : Serializable, Comparable<Data> {

    override fun equals(other : Any?) = when (other) {
        is Data -> key == other.key
        else -> super.equals(other)
    }

    abstract val key: String
    abstract val type: DataType
    abstract fun encrypt(encrypt: (String) -> String) : Data
    abstract fun decrypt(decrypt: (String) -> String) : Data
    abstract fun toString(context: Context, needFirstLine: Boolean = true) : String
    abstract override fun compareTo(other : Data) : Int

    override fun hashCode() = id

    abstract fun isEmpty(): Boolean
}