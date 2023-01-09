package com.security.passwordmanager

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.view.Gravity
import android.view.View
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.content.res.ColorStateList as AndroidColorList

fun TextView?.isEmpty() =
    this == null || text.isEmpty()

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

internal fun Bundle?.getInt(key: String, defaultValue: Int) =
    this?.getInt(key) ?: defaultValue

internal fun Bundle?.getString(key: String, defaultValue: String = "") =
    this?.getString(key) ?: defaultValue

fun SharedPreferences.getString(key: String) =
    this.getString(key, "")

var EditText.txt: String
    get() = text.toString()
    set(value) = setText(value)


inline fun <reified T: AppCompatActivity>createIntent(context: Context?, block: Intent.() -> Unit) =
    Intent(context, T::class.java).apply(block)


fun showToast(context: Context?, text: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, text, duration).modify().show()

fun showToast(context: Context?, @StringRes text: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, text, duration).modify().show()

private fun Toast.modify() = apply {
    setGravity(Gravity.CENTER, 0, 0)
}


inline fun buildString(initString: String = "", builderAction: StringBuilder.() -> Unit) =
    StringBuilder(initString).apply(builderAction).toString()



fun ColorStateList(@ColorInt color: Int) =
    AndroidColorList.valueOf(color)


fun StringBuilder.deleteFromLast(count: Int = 1) =
    deleteRange(length - count, length)

fun <E> List<E>.slice(fromIndex: Int = 0, toIndex: Int = size) =
    subList(fromIndex, toIndex)

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}


fun Parcel.getString(defaultValue: String = "") =
    readString() ?: defaultValue


//fun Intent.getDataUIExtra(name : String, defaultValue : DataUI) =
//    if (Build.VERSION.SDK_INT >= 33) {
//        getSerializableExtra(name, DataUI::class.java)
//    } else {
//        getSerializableExtra(name) as DataUI?
//    } ?: defaultValue


fun String.isValidUrl() = URLUtil.isValidUrl(this)

@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun <D : Enum<*>> Bundle?.getEnum(key: String, defaultValue: D): D =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            this?.getSerializable(key, defaultValue::class.java) ?: defaultValue
        }
        else -> {
            this?.getSerializable(key) as D? ?: defaultValue
        }
    }


fun <T> MutableList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}
