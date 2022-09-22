package com.security.passwordmanager.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.security.passwordmanager.R
import com.security.passwordmanager.hide
import com.security.passwordmanager.model.Themes
import com.security.passwordmanager.show
import com.security.passwordmanager.view.compose.Time
import com.security.passwordmanager.view.compose.toCalendar
import com.security.passwordmanager.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*

//interface Theme {
//    fun timeListener(times: Times) : View.OnClickListener
//    fun updateTheme()
//}


@SuppressLint("InflateParams")
class ThemeBottomDialogFragment(settingsViewModel: SettingsViewModel)
    : BottomDialogFragment(settingsViewModel) {

    private val timeLayout: LinearLayout by lazy {
        layoutInflater.inflate(R.layout.auto_theme_times, null, false)
                as LinearLayout
    }

    init {
        fun listener(view: View) {
            settingsViewModel.theme = Themes.values()[view.id]
            if (settingsViewModel.theme == Themes.AUTO_THEME) {
                timeLayout.show()
            } else
                dismiss()

//            theme.updateTheme()
            updateColors()
        }

        resources.getStringArray(R.array.themes).forEach {
            val bottomView = BottomView(
                image = R.drawable.radio_button_checked,
                text = it,
                listener = ::listener
            )
            bottomView.imageBound = ImageBounds.RIGHT
            addViewToBuffer(bottomView)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeLayout.update()
        addView(timeLayout)
        updateColors()
    }

    private fun LinearLayout.update() = if (settingsViewModel.theme != Themes.AUTO_THEME) {
        hide()
    }
    else {
        show()
        val header = findViewById<TextView>(R.id.times_header)
        header.setTextColor(settingsViewModel.fontColor)
        setBackgroundColor(settingsViewModel.backgroundColor)
        updateTimes(this)
    }

    override fun updateColors() {
        super.updateColors()

        for(index in Themes.values().indices) {
            if (index == settingsViewModel.indexTheme)
                getView(index).setDrawables()
            else
                getView(index).removeDrawables()
        }

        timeLayout.update()
    }



    private fun updateTimes(root: LinearLayout) {
        val times = settingsViewModel.getTimes()

        val startTime = getStringFromCalendar(times.startTime)
        val endTime = getStringFromCalendar(times.endTime)

        val start = root.findViewById<TextView>(R.id.theme_layout_start_time)
        val end = root.findViewById<TextView>(R.id.theme_layout_end_time)

//        start.setOnClickListener(theme.timeListener(times))
//        end.setOnClickListener(theme.timeListener(times))

        start.text = startTime
        end.text = endTime

        settingsViewModel.fontColor.let {
            start.setTextColor(it)
            end.setTextColor(it)
        }

        settingsViewModel.backgroundRes.let {
            start.setBackgroundResource(it)
            end.setBackgroundResource(it)
        }
    }

    private fun getStringFromCalendar(time: Time): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(time.toCalendar().time)
    }


    override fun onDestroyView() {
        removeView(timeLayout)
        super.onDestroyView()
    }
}