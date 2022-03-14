package com.security.passwordmanager.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.security.passwordmanager.databinding.ActivityDialogSaveBinding

class DialogSaveActivity: AppCompatActivity() {

    companion object {
        fun getIntent(context: Context) =
            Intent(context, DialogSaveActivity::class.java)
    }

    private lateinit var binding: ActivityDialogSaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDialogSaveBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}