package com.security.passwordmanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.FirebaseAuth
import com.security.passwordmanager.TimePickerActivity.Companion.TimePickerActivityContract
import com.security.passwordmanager.databinding.ActivitySettingsBinding
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.settings.Theme
import com.security.passwordmanager.settings.ThemeBottomDialogFragment
import java.util.*

class SettingsActivity : AppCompatActivity(), Theme {
    private lateinit var settings: SettingsViewModel

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var themeBottomFragment : ThemeBottomDialogFragment

    private val currentThemeText: String
        get() {
            val themes = resources.getStringArray(R.array.themes)
            val position = settings.getIndexTheme()

            return getString(R.string.switchThemeText,
                themes[position].lowercase(Locale.getDefault())
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = SettingsViewModel.getInstance(this)
        themeBottomFragment = ActionBottom.themeInstance(this, this)

        val actionBottomFragment = ActionBottom.newInstance(this)
        actionBottomFragment.setHeading(getString(R.string.feedback), beautifulDesign = true)

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

        val images = intArrayOf(
            R.drawable.vk_icon,
            R.drawable.telegram_icon,
            R.drawable.instagram_icon,
            R.drawable.email
        )

        val strings = intArrayOf(
            R.string.vk,
            R.string.telegram,
            R.string.instagram,
            R.string.email
        )

        actionBottomFragment.addViews(images, strings, bottomClickListener)

        binding.switchTheme.setOnClickListener {
            themeBottomFragment.show(supportFragmentManager)
        }

        binding.beautifulFont.run {
            textViewName.setText(R.string.beautiful_font)
            textViewName.textSize = 18F

            textViewSubtitle.setText(R.string.beautiful_font_explain)
            textViewSubtitle.textSize = 15F

            root.setOnClickListener { binding.switchBeautifulFont.switch() }
        }


        binding.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            settings.isPasswordRemembered = false
            startActivity(EmailPasswordActivity.getIntent(this))
        }

        binding.haveQuestions.setOnClickListener {
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

    private fun SwitchCompat.switch() {
        isChecked = !isChecked
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as Vibrator
        } else return

        if (vibrator.hasVibrator())
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    300,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
    }


    //слушает нажатия на timeLayout в ThemeBottomFragment
    override fun timeListener(calendarPair: Pair<Calendar, Calendar>) = View.OnClickListener { v ->
        when (v.id) {
            R.id.theme_layout_start_time -> startLauncher.launch(calendarPair.first)
            else -> endLauncher.launch(calendarPair.second)
        }
    }

    override fun updateTheme() {
        settings.updateThemeInScreen(window, supportActionBar)
        binding.switchTheme.text = currentThemeText

        settings.fontColor.let {
            binding.apply {
                switchTheme.setTextColor(it)
                haveQuestions.setTextColor(it)
                beautifulFont.textViewName.setTextColor(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private val startLauncher = registerForActivityResult(
        TimePickerActivityContract(R.string.start_time)
    ) { result: Calendar? ->
        if (result != null) {
            settings.setStartTime(result)
            themeBottomFragment.updateColors()
            updateTheme()
        }
    }

    private var endLauncher = registerForActivityResult(
        TimePickerActivityContract(R.string.end_time)
    ) { result: Calendar? ->
        if (result != null) {
            settings.setEndTime(result)
            themeBottomFragment.updateColors()
            updateTheme()
        }
    }

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}