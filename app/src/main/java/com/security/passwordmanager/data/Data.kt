package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.security.passwordmanager.Cryptographer
import java.io.Serializable

@Entity
abstract class Data(@PrimaryKey(autoGenerate = true) var id: Int) :
    Serializable, Comparable<Data> {

    constructor() : this(0)

    override fun equals(other : Any?) = when (other) {
        is Data -> getKey() == other.getKey()
        else -> super.equals(other)
    }

    abstract fun getKey() : String
    abstract fun encrypt(cryptographer: Cryptographer) : Data
    abstract fun decrypt(cryptographer: Cryptographer) : Data
    abstract fun toString(context : Context, needHeading : Boolean) : String
    abstract override fun compareTo(other : Data) : Int

    override fun hashCode() = id
}