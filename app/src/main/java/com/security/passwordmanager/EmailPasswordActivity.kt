package com.security.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.util.PatternsCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.ui.main.PasswordListActivity

class EmailPasswordActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
//        private const val TAG = "EmailPassword"

        fun getIntent(context: Context) : Intent {
            val intent = Intent(context, EmailPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var settings: SettingsViewModel
    private lateinit var mEmailField: EditText
    private lateinit var mPasswordField: EditText
    private lateinit var signIn: Button
    private lateinit var isPasswordRemember: CheckBox

    private lateinit var label: TextView
    private lateinit var subtitle: TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        settings = SettingsViewModel.getInstance(this)

        label = findViewById(R.id.text_view_main_label)
        subtitle = findViewById(R.id.text_view_main_subtitle)

        mEmailField = findViewById(R.id.username)
        mPasswordField = findViewById(R.id.password)
        signIn = findViewById(R.id.signIn)
        val signUp = findViewById<Button>(R.id.signUp)

        isPasswordRemember = findViewById(R.id.remember_password)
        isPasswordRemember.isChecked = settings.isPasswordRemembered

        if (isPasswordRemember.isChecked)
            startActivity(PasswordListActivity.getIntent(this))

        isPasswordRemember.setOnCheckedChangeListener {_: CompoundButton?, isChecked: Boolean ->
            if (isChecked &&
                (TextUtils.isEmpty(mPasswordField.text) || TextUtils.isEmpty(mEmailField.text)))
                    isPasswordRemember.isChecked = false

            settings.isPasswordRemembered = isChecked
        }

        progressBar = findViewById(R.id.loading)

        signIn.setOnClickListener(this)
        signUp.setOnClickListener(this)

        mEmailField.addTextChangedListener(true)
        mPasswordField.addTextChangedListener(false)
    }


    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.GONE
        updateUI()
    }


    private fun EditText.addTextChangedListener(email : Boolean) {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (email)
                    validateForm(s.toString(), null)
                else
                    validateForm(null, s.toString())
            }

            override fun afterTextChanged(s: Editable) = Unit
        })
    }


    private fun updateUI() {
        settings.updateThemeInScreen(window, supportActionBar)

        settings.fontColor.let {
            label.setTextColor(it)
            subtitle.setTextColor(it)
            mEmailField.setTextColor(it)
            mPasswordField.setTextColor(it)
            isPasswordRemember.setTextColor(it)
            mEmailField.backgroundTintList = ColorStateList.valueOf(it)
            mPasswordField.backgroundTintList = ColorStateList.valueOf(it)
        }

        signIn.setBackgroundResource(settings.buttonRes)
        isPasswordRemember.buttonTintList = ColorStateList.valueOf(settings.headerColor)
    }


    private fun registerUser() {
        val email = mEmailField.text.toString().trim()
        val password = mPasswordField.text.toString().trim()

        if (!validateForm(email, password)) {
            progressBar.visibility = View.GONE
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
                                progressBar.visibility = View.GONE
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.register_failed,
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar.visibility = View.GONE
                }
            }
    }


    private fun loginUser() {
        val email = mEmailField.text.toString().trim()
        val password = mPasswordField.text.toString().trim()

        if (!validateForm(email, password)) {
            progressBar.visibility = View.GONE
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
                    mEmailField.error = getString(R.string.required)
                    mEmailField.requestFocus()
                    return false
                }
                !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() -> {
                    mEmailField.error = getString(R.string.valid_email)
                    mEmailField.requestFocus()
                    return false
                }
                else -> mEmailField.error = null
            }
        }

        if (password != null) when {
            password.isEmpty() -> {
                mPasswordField.error = getString(R.string.required)
                mPasswordField.requestFocus()
                return false
            }
            password.length < 6 -> {
                mPasswordField.error = getString(R.string.min_password_length)
                mPasswordField.requestFocus()
                return false
            }
            else -> mPasswordField.error = null
        }
        return true
    }


    override fun onClick(v: View?) {
        progressBar.visibility = View.VISIBLE

        when(v?.id) {
            R.id.signUp -> registerUser()
            R.id.signIn -> loginUser()
        }
    }
}