package com.security.passwordmanager

import android.view.View
import android.widget.TextView

fun TextView.isEmpty() =
    text.isEmpty()

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}