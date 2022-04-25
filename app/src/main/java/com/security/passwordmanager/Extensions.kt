package com.security.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

fun TextView?.isEmpty() =
    this == null || text.isEmpty()

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

inline fun onBackPressedCallback(enabled: Boolean, crossinline handle: () -> Unit) =
    object : OnBackPressedCallback(enabled) {
        override fun handleOnBackPressed() = handle()
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
    Toast.makeText(context, text, duration).show()

fun showToast(context: Context?, @StringRes text: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, text, duration).show()