package com.security.passwordmanager.util

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import androidx.appcompat.app.AppCompatActivity

internal fun Bundle?.getInt(key: String, defaultValue: Int) =
    this?.getInt(key) ?: defaultValue


internal fun Bundle?.getString(key: String, defaultValue: String = "") =
    this?.getString(key)?.takeIf { it != "null" } ?: defaultValue


@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun <D : Enum<*>> Bundle?.getEnum(key: String, defaultValue: D): D =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            this?.getSerializable(key, defaultValue::class.java) ?: defaultValue
        }

        else -> this?.getSerializable(key) as D? ?: defaultValue
    }


fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}


fun Parcel.getString(defaultValue: String = "") = readString() ?: defaultValue