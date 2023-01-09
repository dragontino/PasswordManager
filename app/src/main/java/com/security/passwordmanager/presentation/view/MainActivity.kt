package com.security.passwordmanager.presentation.view

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.util.PatternsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.*
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.databinding.ActivityMainBinding
import com.security.passwordmanager.getString
import com.security.passwordmanager.presentation.viewmodel.OldSettingsViewModel

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
class MainActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
//        private const val TAG = "EmailPassword"
        private const val LOGIN_KEY = "Firebase Auth Login"
        private const val PASSWORD_KEY = "Firebase Auth Password"

    }

    private lateinit var settings: OldSettingsViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)


        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        settings = OldSettingsViewModel.getInstance(this)

        binding.login.txt = savedInstanceState.getString(LOGIN_KEY)
        binding.password.txt = savedInstanceState.getString(PASSWORD_KEY)

        binding.rememberPassword.isChecked = settings.isPasswordRemembered

        if (binding.rememberPassword.isChecked)
            startActivity(NavigationActivity.getIntent(this))
        // TODO: 25.03.2022 удалить

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


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LOGIN_KEY, binding.login.txt)
        outState.putString(PASSWORD_KEY, binding.password.txt)
    }


    private fun EditText.addTextChangedListener(email: Boolean) {
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
            binding.login.backgroundTintList = ColorStateList(it)
            binding.password.backgroundTintList = ColorStateList(it)

            binding.passwordVisibility.imageTintList = ColorStateList(it)
        }

        binding.signIn.setBackgroundResource(settings.buttonRes)
        binding.passwordVisibility.setBackgroundColor(settings.backgroundColor)
        binding.rememberPassword.buttonTintList = ColorStateList(settings.headerColor)
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

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    auth = FirebaseAuth.getInstance()

                    auth.currentUser?.uid?.let {
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

                                    goNext(email)
                                } else
                                    Toast.makeText(
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

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful)
                    goNext(email)
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
                    binding.login.error = getString(R.string.invalid_email)
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
                binding.password.error = getString(R.string.invalid_password)
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


    private fun goNext(email: String) {
        AppPreferences(application).email = email
        startActivity(NavigationActivity.getIntent(this))
    }
}