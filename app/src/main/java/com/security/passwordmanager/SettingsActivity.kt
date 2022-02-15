package com.security.passwordmanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.security.passwordmanager.TimePickerActivity.Companion.TimePickerActivityContract
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.settings.Theme
import com.security.passwordmanager.settings.ThemeBottomDialogFragment
import java.util.*

class SettingsActivity : AppCompatActivity(), Theme {

    private lateinit var switchTheme: TextView
    private lateinit var questions: Button
    private lateinit var settings: SettingsViewModel
    private lateinit var themeBottomFragment : ThemeBottomDialogFragment

    private val currentThemeText: String
        get() {
            val themes = resources.getStringArray(R.array.themes)
            val position = settings.getIndexTheme()

            return getString(R.string.switchThemeText,
                themes[position].lowercase(Locale.getDefault())
            )
        }

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settings = SettingsViewModel.getInstance(this)
        switchTheme = findViewById(R.id.switchTheme)
        val logout = findViewById<Button>(R.id.button_logout)
        questions = findViewById(R.id.button_questions)
        themeBottomFragment = ActionBottom.themeInstance(this, this)

        val actionBottomFragment = ActionBottom.newInstance(this)
        actionBottomFragment.setHeading(getString(R.string.feedback), null, true)

        val bottomClickListener = View.OnClickListener {
            val address = when(it.id) {
                0 -> "https://vk.com/cepetroff"
                1 -> "https://t.me/cepetroff"
                2 -> "https://instagram.com/ce.petroff"
                3 -> "mailto:petrovsd2002@gmail.com"
                else -> return@OnClickListener
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
            startActivity(intent)
        }

        actionBottomFragment.addView(R.drawable.vk_icon, R.string.vk, bottomClickListener)
        actionBottomFragment.addView(R.drawable.telegram_icon, R.string.telegram, bottomClickListener)
        actionBottomFragment.addView(R.drawable.instagram_icon, R.string.instagram, bottomClickListener)
        actionBottomFragment.addView(R.drawable.email, R.string.email, bottomClickListener)


        switchTheme.setOnClickListener {
            themeBottomFragment.show(supportFragmentManager)
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            settings.isPasswordRemembered = false
            startActivity(EmailPasswordActivity.getIntent(this))
        }

        questions.setOnClickListener {
            actionBottomFragment.show(supportFragmentManager)
        }

        updateTheme()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0)
                setTitle(R.string.settings_label)
        }

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun timeListener(calendarPair: Pair<Calendar, Calendar>) = View.OnClickListener { v ->
        if (v.id == R.id.theme_layout_start_time)
            startLauncher.launch(calendarPair.first)
        else
            endLauncher.launch(calendarPair.second)

    }

    override fun updateTheme() {
        settings.updateThemeInScreen(window, supportActionBar)
        switchTheme.text = currentThemeText

        settings.fontColor.let {
            switchTheme.setTextColor(it)
            questions.setTextColor(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private var startLauncher = registerForActivityResult(
        TimePickerActivityContract(R.string.start_time)
    ) { result: Calendar? ->
        if (result != null) {
            settings.setStartTime(result)
            themeBottomFragment.notifyAdapter()
            updateTheme()
        }
    }

    private var endLauncher = registerForActivityResult(
        TimePickerActivityContract(R.string.end_time)
    ) { result: Calendar? ->
        if (result != null) {
            settings.setEndTime(result)
            themeBottomFragment.notifyAdapter()
            updateTheme()
        }
    }

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}