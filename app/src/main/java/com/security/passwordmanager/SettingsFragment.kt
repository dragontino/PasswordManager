package com.security.passwordmanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.security.passwordmanager.databinding.FragmentSettingsBinding
import com.security.passwordmanager.databinding.SwitchViewBinding
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.settings.Theme
import com.security.passwordmanager.settings.ThemeBottomDialogFragment
import com.security.passwordmanager.ui.entry.MainActivity
import java.util.*


class SettingsFragment: Fragment(), Theme {

    private lateinit var settings: SettingsViewModel

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var themeBottomFragment : ThemeBottomDialogFragment

    private val currentThemeText: String
        get() {
            val themes = resources.getStringArray(R.array.themes)
            val position = settings.getIndexTheme()

            return getString(R.string.switchThemeText,
                themes[position].lowercase(Locale.getDefault())
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        settings = SettingsViewModel.getInstance(activity as AppCompatActivity)
        themeBottomFragment = ThemeBottomDialogFragment(this, activity as AppCompatActivity)

        val actionBottomFragment = ActionBottom.newInstance(activity as AppCompatActivity)
        actionBottomFragment.setHeading(getString(R.string.feedback), beautifulDesign = true)

        val bottomClickListener = View.OnClickListener {
            val address = when (it.id) {
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
            themeBottomFragment.show(parentFragmentManager)
        }

        binding.switchTheme.textSize = 16F

        binding.beautifulFont.setSwitchOptions(
            settings.baseSettings.isUsingBeautifulFont,
            R.string.beautiful_font,
            R.string.beautiful_font_explain,
            settings::updateBeautifulFont
        )

        binding.datsHints.setSwitchOptions(
            settings.baseSettings.isShowingDataHints,
            R.string.data_hints,
            R.string.data_hints_description,
            settings::updateUsingDataHints
        )

        binding.showBottomView.setSwitchOptions(
            settings.baseSettings.isUsingBottomView,
            R.string.using_bottom_view,
            R.string.bottom_view_explain,
            settings::updateUsingBottomView
        )


        binding.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            settings.isPasswordRemembered = false
            startActivity(MainActivity.getIntent(activity as AppCompatActivity))
        }

        binding.haveQuestions.setOnClickListener {
            actionBottomFragment.show(parentFragmentManager)
        }

        updateTheme()
    }

    private fun SwitchCompat.switch() {
        isChecked = !isChecked
        // TODO: 10.03.2022 сделать вибратор
    }

    private fun SwitchViewBinding.setOnCheckedChangeListener(action: (Boolean) -> Unit) {
        textView.root.setOnClickListener {
            switchCompat.switch()
            action(switchCompat.isChecked)
        }
        switchCompat.setOnCheckedChangeListener { _, isChecked -> action(isChecked) }
    }


    private fun SwitchViewBinding.setSwitchOptions(
        isChecked: Boolean,
        @StringRes headingText: Int,
        @StringRes subtitleText: Int,
        listener: (Boolean) -> Unit
    ) {
        switchCompat.isChecked = isChecked

        textView.heading.setText(headingText)
        textView.heading.textSize = 16F

        textView.subtitle.setText(subtitleText)
        textView.subtitle.textSize = 13F

        setOnCheckedChangeListener { listener(it) }
    }



    //слушает нажатия на timeLayout в ThemeBottomFragment
    override fun timeListener(calendarPair: Pair<Calendar, Calendar>) = View.OnClickListener {
        when (it.id) {
            R.id.theme_layout_start_time -> startLauncher.launch(calendarPair.first)
            else -> endLauncher.launch(calendarPair.second)
        }
    }

    override fun updateTheme() {
        settings.updateThemeInScreen(activity?.window, (activity as AppCompatActivity).supportActionBar)
        binding.switchTheme.text = currentThemeText

        settings.fontColor.let {
            binding.apply {
                switchTheme.setTextColor(it)
                haveQuestions.setTextColor(it)
                beautifulFont.textView.heading.setTextColor(it)
                datsHints.textView.heading.setTextColor(it)
                showBottomView.textView.heading.setTextColor(it)
            }
        }
    }

    private val startLauncher = registerForActivityResult(
        TimePickerActivity.Companion.TimePickerActivityContract(R.string.start_time)) {
        if (it != null) {
            settings.setStartTime(it)
            themeBottomFragment.updateColors()
            updateTheme()
        }
    }

    private var endLauncher = registerForActivityResult(
        TimePickerActivity.Companion.TimePickerActivityContract(R.string.end_time)) {
        if (it != null) {
            settings.setEndTime(it)
            themeBottomFragment.updateColors()
            updateTheme()
        }
    }
}