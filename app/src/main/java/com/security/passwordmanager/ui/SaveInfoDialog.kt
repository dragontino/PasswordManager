package com.security.passwordmanager.ui

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.security.passwordmanager.R

class SaveInfoDialog(context: Context, @StringRes private val messageId: Int):
    AlertDialog(context, R.style.DialogTheme) {

    private val builder = Builder(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        builder.setView(R.layout.activity_dialog_save)
        builder.setMessage(messageId)
        builder.setCancelable(true)
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
    }

    override fun show() {
        builder.create().show()
    }
}