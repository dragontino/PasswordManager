package com.security.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.util.PatternsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.databinding.ActivityMainBinding
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.ui.main.PasswordListActivity

class EmailPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private fun TextView.isEmpty() =
        TextUtils.isEmpty(text)

    companion object {
//        private const val TAG = "EmailPassword"

        fun getIntent(context: Context) : Intent {
            val intent = Intent(context, EmailPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }

    private lateinit var settings: SettingsViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        settings = SettingsViewModel.getInstance(this)

        binding.rememberPassword.isChecked = settings.isPasswordRemembered

        if (binding.rememberPassword.isChecked)
            startActivity(PasswordListActivity.getIntent(this))

        binding.rememberPassword.setOnCheckedChangeListener {_: CompoundButton?, isChecked: Boolean ->
            if (isChecked &&
                (binding.password.isEmpty() || binding.login.isEmpty()))
                    binding.rememberPassword.isChecked = false

            settings.isPasswordRemembered = isChecked
        }

        binding.signIn.setOnClickListener(this)
        binding.signUp.setOnClickListener(this)

        binding.login.addTextChangedListener(true)
        binding.password.addTextChangedListener(false)

        binding.passwordVisibility.setOnClickListener {
            updatePasswordView()
        }
    }


    override fun onResume() {
        super.onResume()
        binding.loading.hide()
        updateUI()
    }


    private fun EditText.addTextChangedListener(email : Boolean) {
        doOnTextChanged { text, _, _, _ ->
            if (email)
                validateForm(text.toString(), null)
            else
                validateForm(null, text.toString())
        }
    }


    private fun updateUI() {
        settings.updateThemeInScreen(window, supportActionBar)

        settings.fontColor.let {
            binding.mainLabel.setTextColor(it)
            binding.mainSubtitle.setTextColor(it)
            binding.login.setTextColor(it)
            binding.password.setTextColor(it)
            binding.rememberPassword.setTextColor(it)
            binding.login.backgroundTintList = ColorStateList.valueOf(it)
            binding.password.backgroundTintList = ColorStateList.valueOf(it)

            binding.passwordVisibility.imageTintList = ColorStateList.valueOf(it)
        }

        binding.signIn.setBackgroundResource(settings.buttonRes)
        binding.passwordVisibility.setBackgroundColor(settings.backgroundColor)
        binding.rememberPassword.buttonTintList = ColorStateList.valueOf(settings.headerColor)
    }

    private fun updatePasswordView() = binding.run {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            password.inputType = InputType.TYPE_CLASS_TEXT
            passwordVisibility.setImageResource(R.drawable.visibility_off)
            passwordVisibility.contentDescription = getString(R.string.hide_password)
        }
        else {
            password.inputType = 129
            passwordVisibility.setImageResource(R.drawable.visibility_on)
            passwordVisibility.contentDescription = getString(R.string.show_password)
        }
    }




    private fun registerUser() {
        val email = binding.login.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (!validateForm(email, password)) {
            binding.loading.hide()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    mAuth = FirebaseAuth.getInstance()

                    mAuth.currentUser?.uid?.let {
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(it)
                            .setValue(email)
                            .addOnCompleteListener { task1: Task<Void?> ->
                                if (task1.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        R.string.register_successful,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    startActivity(PasswordListActivity.getIntent(this))

                                } else Toast.makeText(
                                    this,
                                    R.string.register_failed,
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.loading.hide()
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.register_failed,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.loading.hide()
                }
            }
    }


    private fun loginUser() {
        val email = binding.login.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (!validateForm(email, password)) {
            binding.loading.hide()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful)
                    startActivity(PasswordListActivity.getIntent(this))
                else Toast.makeText(
                    this,
                    R.string.login_failed,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun validateForm(email: String?, password: String?): Boolean {

        if (email == null && password == null) return false

        if (email != null) {
            when {
                email.isEmpty() -> {
                    binding.login.error = getString(R.string.required)
                    binding.login.requestFocus()
                    return false
                }
                !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.login.error = getString(R.string.valid_email)
                    binding.login.requestFocus()
                    return false
                }
                else -> binding.login.error = null
            }
        }

        if (password != null) when {
            password.isEmpty() -> {
                binding.password.error = getString(R.string.required)
                binding.password.requestFocus()
                return false
            }
            password.length < 6 -> {
                binding.password.error = getString(R.string.min_password_length)
                binding.password.requestFocus()
                return false
            }
            else -> binding.password.error = null
        }
        return true
    }


    override fun onClick(v: View?) {
        binding.loading.show()

        when(v?.id) {
            R.id.sign_up -> registerUser()
            R.id.sign_in -> loginUser()
        }
    }
}