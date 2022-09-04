package com.security.passwordmanager.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.databinding.SwitchViewBinding
import com.security.passwordmanager.view.compose.*
import com.security.passwordmanager.viewmodel.SettingsViewModel
import java.util.*


class SettingsFragment: Fragment(), Theme {

    private lateinit var settings: SettingsViewModel

//    private lateinit var binding: FragmentSettingsBinding
    private lateinit var themeBottomFragment : ThemeBottomSheetFragment

    private val currentThemeText: String
        get() {
            val themes = resources.getStringArray(R.array.themes)
            val position = settings.indexTheme

            return getString(
                R.string.switchThemeText,
                themes[position].lowercase(Locale.getDefault())
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(assetManager = inflater.context.assets) {
                    SettingsFragment {
                        themeBottomFragment.show(parentFragmentManager)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        settings = SettingsViewModel.getInstance(activity as AppCompatActivity)
        themeBottomFragment = ThemeBottomSheetFragment(
            context = requireContext(),
            theme = this,
            settingsViewModel = settings
        )

        val bottomSheet = BottomDialogFragment(settings)
        bottomSheet.setHeading(getString(R.string.feedback), beautifulDesign = true)

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

        bottomSheet.addViews(images.size) { index ->
            val image = images[index]
            val text = getString(strings[index])
            BottomView(image, text, bottomClickListener)
        }

//        binding.switchTheme.setOnClickListener {
//            themeBottomFragment.show(parentFragmentManager)
//        }
//
//        binding.switchTheme.textSize = 16F
//
//        binding.beautifulFont.setSwitchOptions(
//            isChecked = settings.baseSettings.isUsingBeautifulFont,
//            headingText = R.string.beautiful_font,
//            subtitleText = R.string.beautiful_font_explain,
//            listener = settings::updateBeautifulFont
//        )
//
//        binding.datsHints.setSwitchOptions(
//            settings.baseSettings.isShowingDataHints,
//            R.string.data_hints,
//            R.string.data_hints_description,
//            settings::updateUsingDataHints
//        )
//
//        binding.showBottomView.setSwitchOptions(
//            settings.baseSettings.isUsingBottomView,
//            R.string.using_bottom_view,
//            R.string.using_bottom_view_explain,
//            settings::updateUsingBottomView
//        )
//
//
//        binding.buttonLogout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            settings.isPasswordRemembered = false
//            startActivity(MainActivity.getIntent(activity as AppCompatActivity))
//        }
//
//        binding.haveQuestions.setOnClickListener {
//            bottomSheet.show(parentFragmentManager)
//        }

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

        setOnCheckedChangeListener(listener)
    }
    

    override fun updateTheme() {
        settings.updateThemeInScreen(activity?.window, (activity as AppCompatActivity).supportActionBar)
//        binding.switchTheme.text = currentThemeText
//
//        settings.fontColor.let {
//            binding.apply {
//                switchTheme.setTextColor(it)
//                haveQuestions.setTextColor(it)
//                beautifulFont.textView.heading.setTextColor(it)
//                datsHints.textView.heading.setTextColor(it)
//                showBottomView.textView.heading.setTextColor(it)
//            }
//        }
    }

    private val startLauncher = registerForActivityResult(
        TimePickerActivity.Companion.TimePickerActivityContract(R.string.start_time)) {
        if (it != null) {
            settings.startTime = it
//            themeBottomFragment.updateColors()
            updateTheme()
        }
    }

    private var endLauncher = registerForActivityResult(
        TimePickerActivity.Companion.TimePickerActivityContract(R.string.end_time)) {
        if (it != null) {
            settings.endTime = it
//            themeBottomFragment.updateColors()
            updateTheme()
        }
    }
    
    
    
    
    @Composable
    private fun SettingsFragment(switchThemeAction: () -> Unit) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = currentThemeText,
                fontSize = 16.sp,
                color = colorResource(R.color.text_color),
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = switchThemeAction)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )
            Divider(color = colorResource(android.R.color.darker_gray))
            SwitchView(
                isChecked = settings.baseSettings.isUsingBeautifulFont,
                titleTextRes = R.string.beautiful_font,
                subtitleTextRes = R.string.beautiful_font_explain,
                listener = settings::updateBeautifulFont
            )
            Divider(color = colorResource(android.R.color.darker_gray))
            SwitchView(
                isChecked = settings.baseSettings.isShowingDataHints,
                titleTextRes = R.string.data_hints,
                subtitleTextRes = R.string.data_hints_description,
                listener = settings::updateUsingDataHints
            )
            Divider(color = colorResource(android.R.color.darker_gray))
            SwitchView(
                isChecked = settings.baseSettings.isUsingBottomView,
                titleTextRes = R.string.using_bottom_view,
                subtitleTextRes = R.string.using_bottom_view_explain,
                listener = settings::updateUsingBottomView
            )
        }
    }


    @Composable
    private fun SwitchView(
        isChecked: Boolean,
        @StringRes titleTextRes: Int,
        @StringRes subtitleTextRes: Int,
        listener: (Boolean) -> Unit
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clickable { listener(!isChecked) }
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(2f),
            ) {

                Text(
                    text = stringResource(titleTextRes),
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onSurface
                )

                Text(
                    text = stringResource(subtitleTextRes),
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onSurface
                )
            }

            Switch(
                checked = isChecked,
                onCheckedChange = listener,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.secondary,
                    uncheckedThumbColor = colorResource(R.color.light_gray),
                    uncheckedTrackColor = MaterialTheme.colors.surface
                ),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .scale(1.2f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}


@Preview
@Composable
fun PreviewSwitch() {
    val isChecked = remember { mutableStateOf(false) }
    val listener = { checked: Boolean -> isChecked.value = checked }
    val titleText = "Wrecked"
    val subtitleText = "Imagine Dragons kls cldskmcwkcmdsoejdskcnewi3id398ejkdcmdkckckzczxc"

    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .clickable { listener(!isChecked.value) }
            .background(MaterialTheme.colors.background)
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(2f),
        ) {

            Text(
                text = titleText,
                fontSize = 16.sp,
                color = MaterialTheme.colors.onSurface
            )

            Text(
                text = subtitleText,
                fontSize = 13.sp,
                color = MaterialTheme.colors.onSurface
            )
        }

        Switch(
            checked = isChecked.value,
            onCheckedChange = listener,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.secondary,
                uncheckedThumbColor = colorResource(R.color.light_gray),
                uncheckedTrackColor = MaterialTheme.colors.surface
            ),
            modifier = Modifier
                .padding(end = 16.dp)
                .scale(1.2f)
                .align(Alignment.CenterVertically)
        )
    }
}