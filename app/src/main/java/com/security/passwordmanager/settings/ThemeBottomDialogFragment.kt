package com.security.passwordmanager.settings

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.Pair
import com.security.passwordmanager.R
import java.text.SimpleDateFormat
import java.util.*

interface Theme {
    fun timeListener(calendarPair: Pair<Calendar, Calendar>) : View.OnClickListener
    fun updateTheme()
}


class ThemeBottomDialogFragment(private val theme: Theme, private val activity: AppCompatActivity)
    : BottomSheetDialogFragment() {

    private lateinit var rootView : View
    private lateinit var support : SettingsViewModel
    private lateinit var adapter: ThemeAdapter
    private lateinit var themes: Array<String>
    private lateinit var recyclerView : RecyclerView

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = inflater.inflate(R.layout.switch_theme_layout, container, false)
        support = SettingsViewModel.getInstance(activity)
        recyclerView = rootView.findViewById(R.id.theme_bottom_sheet_container)

        rootView.backgroundTintList = ColorStateList.valueOf(support.backgroundColor)
        recyclerView.layoutManager = LinearLayoutManager(context)

        themes = resources.getStringArray(R.array.themes)

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ThemeAdapter()
        recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyAdapter() =
        adapter.notifyDataSetChanged()


    private inner class ThemeAdapter : RecyclerView.Adapter<ThemeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_theme, parent, false)
            return ThemeHolder(view)
        }

        override fun onBindViewHolder(holder: ThemeHolder, position: Int) {
            val text = themes[position]
            val isChecked = position == support.getIndexTheme()
            holder.bindTheme(text, isChecked)

            holder.setOnClickListener {
                dismiss()
                support.theme = ThemeDef.values()[position]
                theme.updateTheme()
            }
        }

        override fun getItemCount() = themes.size
    }



    private inner class ThemeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewTheme = itemView.findViewById<TextView>(R.id.list_item_theme_text_view)
        private val times = itemView.findViewById<LinearLayout>(R.id.list_item_theme_layout_time)
        private var calendarPair: Pair<Calendar, Calendar> =
            support.getTimes()


        fun bindTheme(text : String, isChecked : Boolean) {
            textViewTheme.text = text

            textViewTheme.setTextColor(support.fontColor)
            support.backgroundColor.let {
                textViewTheme.setBackgroundColor(it)
                itemView.setBackgroundColor(it)
            }

            val color = if (isChecked)
                SettingsViewModel.RASPBERRY
            else
                support.backgroundColor

            TextViewCompat.setCompoundDrawableTintList(
                textViewTheme,
                ColorStateList.valueOf(color)
            )

            if (isChecked && support.theme == ThemeDef.AUTO_THEME) {
                times.visibility = View.VISIBLE
                updateTimes()
            } else
                times.visibility = View.GONE
        }

        fun setOnClickListener(listener : View.OnClickListener) =
            textViewTheme.setOnClickListener(listener)


        private fun updateTimes() {
            calendarPair = support.getTimes()

            val startTime = getStringFromCalendar(calendarPair.first)
            val endTime = getStringFromCalendar(calendarPair.second)

            val start = times.findViewById<TextView>(R.id.theme_layout_start_time)
            val end = times.findViewById<TextView>(R.id.theme_layout_end_time)

            start.setOnClickListener(theme.timeListener(calendarPair))
            end.setOnClickListener(theme.timeListener(calendarPair))

            start.text = startTime
            end.text = endTime

            support.fontColor.let {
                start.setTextColor(it)
                end.setTextColor(it)
            }

            support.backgroundRes.let {
                start.setBackgroundResource(it)
                end.setBackgroundResource(it)
            }

            times.setBackgroundColor(support.backgroundColor)
        }

        private fun getStringFromCalendar(calendar: Calendar) : String {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(calendar.time)
        }
    }
}