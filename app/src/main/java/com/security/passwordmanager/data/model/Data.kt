package com.security.passwordmanager.data.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.security.passwordmanager.presentation.model.ObservableData
import com.security.passwordmanager.presentation.model.enums.DataType

@Entity
sealed class Data(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var email: String = ""
) : Parcelable, Comparable<Data> {

    override fun equals(other : Any?) = when (other) {
        is Data -> key == other.key
        else -> super.equals(other)
    }

    abstract val key: String
    abstract val type: DataType
    protected abstract val stringToCompare: String
    abstract fun encrypt(encrypt: (String) -> String): Data
    abstract fun decrypt(decrypt: (String) -> String): Data
    abstract fun toString(context: Context, needFirstLine: Boolean = true) : String
    override fun compareTo(other : Data) : Int {
        return this.stringToCompare.compareTo(other.stringToCompare)
    }

    abstract fun observe(): ObservableData


    override fun hashCode() = 31 + type.hashCode() + key.hashCode() + id.hashCode()

    abstract fun isEmpty(): Boolean


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(email)
    }

    override fun describeContents() = 0
}