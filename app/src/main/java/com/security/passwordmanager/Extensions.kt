package com.security.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import java.lang.Integer.min
import android.content.res.ColorStateList as AndroidColorList

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


inline fun buildString(initString: String, builderAction: StringBuilder.() -> Unit) =
    StringBuilder(initString).apply(builderAction).toString()


inline fun SearchView.doOnQueryTextChange(crossinline doInChange: (query: String) -> Unit) =
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = false

        override fun onQueryTextChange(newText: String): Boolean {
            doInChange(newText)
            return true
        }
    })


fun <T> MutableList<T>.updateAll(list: List<T>) {
    val minSize = min(this.size, list.size)

    for (index in 0 until minSize)
        this[index] = list[index]

    if (this.size < list.size)
        this.addAll(list.subList(this.size, list.size))
}

inline fun EditText.setOnEnterListener(crossinline function: () -> Unit) =
    setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            function()
            true
        }
        else false
    }


fun EditText.deleteLast() {
    txt = txt.substring(0, txt.lastIndex)
}


// TODO: 10.07.2022 проверить работоспособность 
fun TextView.setFont(path: String, style: Int? = null) {
    typeface = if (style == null)
        Typeface.createFromAsset(context.assets, path)
    else
        Typeface.create(Typeface.createFromAsset(context.assets, path), style)
}


fun Context.findDrawableById(@DrawableRes resId: Int) =
    AppCompatResources.getDrawable(this, resId)

fun ColorStateList(@ColorInt color: Int) =
    AndroidColorList.valueOf(color)


fun StringBuilder.deleteLast(count: Int = 1) =
    deleteRange(length - count, length)