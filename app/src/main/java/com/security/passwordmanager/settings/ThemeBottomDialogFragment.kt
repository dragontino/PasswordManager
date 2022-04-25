package com.security.passwordmanager.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.security.passwordmanager.*
import java.text.SimpleDateFormat
import java.util.*

interface Theme {
    fun timeListener(calendarPair: Pair<Calendar, Calendar>) : View.OnClickListener
    fun updateTheme()
}


@SuppressLint("InflateParams")
class ThemeBottomDialogFragment(private val theme: Theme, activity: AppCompatActivity)
    : ActionBottomDialogFragment(activity) {

    private val timeLayout: LinearLayout by lazy {
        layoutInflater.inflate(R.layout.auto_theme_times, null, false)
                as LinearLayout
    }

    init {
        val listener = View.OnClickListener {
            settings.theme = ThemeDef.values()[it.id]
            if (settings.theme == ThemeDef.AUTO_THEME) {
                timeLayout.show()
            } else
                dismiss()

            theme.updateTheme()
            updateColors()
        }

        activity.resources.getStringArray(R.array.themes).forEach {
            addView(
                image = R.drawable.radio_button_checked,
                imageBound = ImageBounds.RIGHT,
                text = it,
                listener = listener
            )
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeLayout.update()
        addView(timeLayout)
        updateColors()
    }

    private fun LinearLayout.update() = if (settings.theme != ThemeDef.AUTO_THEME) {
        hide()
    }
    else {
        show()
        val header = findViewById<TextView>(R.id.times_header)
        header.setTextColor(settings.fontColor)
        setBackgroundColor(settings.backgroundColor)
        updateTimes(this)
    }

    override fun updateColors() {
        super.updateColors()

        for(index in ThemeDef.values().indices) {
            if (index == settings.getIndexTheme())
                getView(index).setDrawables()
            else
                getView(index).removeDrawables()
        }

        timeLayout.update()
    }



    private fun updateTimes(root: LinearLayout) {
        val calendarPair = settings.getTimes()

        val startTime = getStringFromCalendar(calendarPair[0])
        val endTime = getStringFromCalendar(calendarPair[1])

        val start = root.findViewById<TextView>(R.id.theme_layout_start_time)
        val end = root.findViewById<TextView>(R.id.theme_layout_end_time)

        start.setOnClickListener(theme.timeListener(calendarPair))
        end.setOnClickListener(theme.timeListener(calendarPair))

        start.text = startTime
        end.text = endTime

        settings.fontColor.let {
            start.setTextColor(it)
            end.setTextColor(it)
        }

        settings.backgroundRes.let {
            start.setBackgroundResource(it)
            end.setBackgroundResource(it)
        }
    }

    private fun getStringFromCalendar(calendar: Calendar): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(calendar.time)
    }


    override fun onDestroyView() {
        removeView(timeLayout)
        super.onDestroyView()
    }
}