package com.security.passwordmanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.security.passwordmanager.TimePickerActivity.Companion.TimePickerActivityContract
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.settings.Theme
import com.security.passwordmanager.settings.ThemeBottomDialogFragment
import java.util.*

class SettingsActivity : AppCompatActivity(), Theme {

    private lateinit var switchTheme: TextView
    private lateinit var logout: Button
    private lateinit var settings: SettingsViewModel
    private lateinit var bottomDialogFragment : ThemeBottomDialogFragment

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

        settings = ViewModelProvider(this)[SettingsViewModel::class.java]
        switchTheme = findViewById(R.id.switchTheme)
        logout = findViewById(R.id.button_logout)
        bottomDialogFragment = ActionBottom.themeInstance(this, this)

        val needLogout = intent.getBooleanExtra(EXTRA, true)
        if (!needLogout) logout.visibility = View.GONE

        switchTheme.setOnClickListener {
            bottomDialogFragment.show(supportFragmentManager)
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            settings.isPasswordRemembered = false
            startActivity(EmailPasswordActivity.getIntent(this))
        }

        updateTheme()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0)
                setTitle(R.string.title_activity_settings)
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
        //TODO переделать
        settings.updateThemeInScreen(window, supportActionBar)
        switchTheme.text = currentThemeText

        settings.fontColor.let {
            switchTheme.setTextColor(it)
            logout.setTextColor(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }



//    private inner class ThemeBottomSheet(context: Context) {
//
//        private val bottomSheetDialog = BottomSheetDialog(context)
//        private val bottomSheetView = LayoutInflater.from(context).inflate(
//            R.layout.switch_theme_layout,
//            findViewById(R.id.theme_bottom_sheet_container)
//        )
//
//        private val mRecyclerView =
//            bottomSheetView.findViewById<RecyclerView>(R.id.theme_bottom_sheet_container)
//
//        init {
//            mRecyclerView.layoutManager = LinearLayoutManager(context)
//        }
//
//        var mAdapter: ThemeAdapter? = null
//        private val themes = resources.getStringArray(R.array.themes)
//
//        @SuppressLint("NotifyDataSetChanged")
//        fun start() {
//            bottomSheetView.setBackgroundColor(support?.backgroundColor.unwrap())
//            bottomSheetDialog.setContentView(bottomSheetView)
//            bottomSheetDialog.show()
//
//            if (mAdapter == null) {
//                mAdapter = ThemeAdapter()
//                mRecyclerView.adapter = mAdapter
//            } else
//                mAdapter?.notifyDataSetChanged()
//        }
//
//        inner class ThemeAdapter : RecyclerView.Adapter<ThemeHolder>() {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeHolder {
//                val view = layoutInflater.inflate(R.layout.list_item_theme, parent, false)
//                return ThemeHolder(view)
//            }
//
//            override fun onBindViewHolder(holder: ThemeHolder, position: Int) {
//                val text = themes[position]
//                val isChecked = position == support?.getIndexTheme()
//                holder.bindTheme(text, isChecked)
//                holder.setOnClickListener {
//                    support?.theme = ThemeDef.getTheme(position)
//                    updateTheme()
//                    bottomSheetDialog.dismiss()
//                }
//            }
//
//            override fun getItemCount() = themes.size
//        }
//
//
//
//        private inner class ThemeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//            private val textViewTheme = itemView.findViewById<TextView>(R.id.list_item_theme_text_view)
//            private val times = itemView.findViewById<LinearLayout>(R.id.list_item_theme_layout_time)
//            private val listener: View.OnClickListener
//            private var calendarPair: Pair<Calendar, Calendar>
//
//            init {
//                calendarPair = support?.getTimes() ?: Pair(Calendar.getInstance(), Calendar.getInstance())
//
//                listener = View.OnClickListener { v: View ->
//                    if (v.id == R.id.them_layout_start_time)
//                        startLauncher.launch(calendarPair.first)
//                    else
//                        endLauncher.launch(calendarPair.second)
//                }
//            }
//
//            fun bindTheme(text: String, isChecked: Boolean) {
//                textViewTheme.text = text
//
//                textViewTheme.setTextColor(support?.fontColor.unwrap())
//                textViewTheme.setBackgroundColor(support?.backgroundColor.unwrap())
//                itemView.setBackgroundColor(support?.backgroundColor.unwrap())
//
//                val color = if (isChecked)
//                    applicationContext.getColor(R.color.raspberry)
//                else
//                    support?.backgroundColor
//
//                TextViewCompat.setCompoundDrawableTintList(
//                    textViewTheme,
//                    ColorStateList.valueOf(color.unwrap())
//                )
//
//                if (isChecked && support?.theme == ThemeDef.AUTO_THEME) {
//                    times.visibility = View.VISIBLE
//                    updateTimes()
//                } else
//                    times.visibility = View.GONE
//            }
//
//            fun setOnClickListener(listener: View.OnClickListener) =
//                textViewTheme.setOnClickListener(listener)
//
//
//            private fun updateTimes() {
//                calendarPair = support?.getTimes() ?: Pair(Calendar.getInstance(), Calendar.getInstance())
//
//                val startTime = getStringFromCalendar(calendarPair.first)
//                val endTime = getStringFromCalendar(calendarPair.second)
//
//                val start = times.findViewById<TextView>(R.id.them_layout_start_time)
//                val end = times.findViewById<TextView>(R.id.them_layout_end_time)
//
//                start.setOnClickListener(listener)
//                end.setOnClickListener(listener)
//
//                start.text = startTime
//                end.text = endTime
//
//                support?.fontColor?.let { start.setTextColor(it) }
//                support?.fontColor?.let { end.setTextColor(it) }
//
//                support?.backgroundRes?.let { start.setBackgroundResource(it) }
//                support?.backgroundRes?.let { end.setBackgroundResource(it) }
//                support?.backgroundColor?.let { times.setBackgroundColor(it) }
//            }
//
//            private fun getStringFromCalendar(calendar: Calendar): String {
//                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
//                return format.format(calendar.time)
//            }
//        }
//    }

    private var startLauncher = registerForActivityResult(
        TimePickerActivityContract(R.string.start_time)
    ) { result: Calendar? ->
        if (result != null) {
            settings.setStartTime(result)
            bottomDialogFragment.notifyAdapter()
            updateTheme()
        }
    }

    private var endLauncher = registerForActivityResult(
        TimePickerActivityContract(R.string.end_time)
    ) { result: Calendar? ->
        if (result != null) {
            settings.setEndTime(result)
            bottomDialogFragment.notifyAdapter()
            updateTheme()
        }
    }

    companion object {
        private const val EXTRA = "logout_visibility"

        fun getIntent(context: Context?, need_logout_button: Boolean): Intent {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(EXTRA, need_logout_button)
            return intent
        }
    }
}