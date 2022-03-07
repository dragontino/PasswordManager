package com.security.passwordmanager

import android.text.Editable
import android.text.TextWatcher
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

interface MyTextWatcher: TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {}
}