package com.security.passwordmanager

import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes

fun TextView.isEmpty() =
    text.isEmpty()

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

operator fun <T: View> View.get(@IdRes name: Int): T =
    findViewById(name)

inline fun onBackPressedCallback(enabled: Boolean, crossinline handle: () -> Unit) =
    object : OnBackPressedCallback(enabled) {
        override fun handleOnBackPressed() = handle()
    }