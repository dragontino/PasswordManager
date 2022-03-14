package com.security.passwordmanager.settings

import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.navigation.NavigationView
import com.security.passwordmanager.Pair
import com.security.passwordmanager.R
import com.security.passwordmanager.data.MainDatabase
import com.security.passwordmanager.settings.EnumPreferences.*
import java.util.*

class SettingsViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        @Volatile
        private var INSTANCE: SettingsViewModel? = null

        fun getInstance(owner: ViewModelStoreOwner) : SettingsViewModel {
            val temp = INSTANCE
            if (temp != null)
                return temp

            synchronized(this) {
                val instance = ViewModelProvider(owner)[SettingsViewModel::class.java]

                INSTANCE = instance
                return instance
            }
        }
    }

    private val settingsRepository: SettingsRepository
    private val preferences = mApplication.getSharedPreferences(APP_PREFERENCES.name, Context.MODE_PRIVATE)
    private var startTime = getDateFromPreferences(
        keyHours = APP_PREFERENCES_START_HOURS,
        keyMinutes = APP_PREFERENCES_START_MINUTES,
        defHours = 7
    )

    private var endTime = getDateFromPreferences(
        keyHours = APP_PREFERENCES_END_HOURS,
        keyMinutes = APP_PREFERENCES_END_MINUTES,
        defHours = 23
    )

    var theme: ThemeDef
        get() = ThemeDef.values().find {
            it.themeName == settingsRepository.getSettings().theme
        } ?: ThemeDef.LIGHT_THEME
        set(value) {
            settingsRepository.updateTheme(value.themeName)
            updateColors()
        }

    val baseSettings: Settings get() =
        settingsRepository.getSettings()


    @ColorInt var backgroundColor = Color.WHITE
    @ColorInt var fontColor = Color.BLACK
    @ColorInt var headerColor = mApplication.getColor(R.color.raspberry)
    @ColorInt var layoutBackgroundColor = mApplication.getColor(R.color.light_gray)
    @ColorInt var darkerGrayColor = mApplication.getColor(android.R.color.darker_gray)

    @DrawableRes var backgroundRes = R.drawable.text_view_style
    @DrawableRes var buttonRes = R.drawable.button_style
    @StyleRes var switchStyle = R.style.SwitchTheme

    var isPasswordRemembered
        get() = preferences.getBoolean(APP_PREFERENCES_IS_PASSWORD_REMEMBERED.title, false)
        set(value) {
            val editor = preferences.edit()
            editor.putBoolean(APP_PREFERENCES_IS_PASSWORD_REMEMBERED.title, value)
            editor.apply()
        }

    init {
        val settingsDao = MainDatabase.getDatabase(mApplication).settingsDao()
        settingsRepository = SettingsRepository(settingsDao)
        updateColors()
    }

    fun updateThemeInScreen(
        window: Window?,
        actionBar: ActionBar?,
        navigationView: NavigationView? = null,
    ) {
        if (window == null || actionBar == null) return

        window.decorView.setBackgroundColor(backgroundColor)
        window.statusBarColor = headerColor
        actionBar.setBackgroundDrawable(ColorDrawable(headerColor))

        if (navigationView != null) {
            navigationView.setBackgroundColor(backgroundColor)
            navigationView.itemTextColor = ColorStateList.valueOf(fontColor)

            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled)
            )

            val colors = intArrayOf(mApplication.getColor(R.color.raspberry), fontColor)
            navigationView.itemIconTintList = ColorStateList(states, colors)
        }
    }

    fun isLightTheme() = when(theme) {
        ThemeDef.LIGHT_THEME -> true
        ThemeDef.DARK_THEME -> false
        ThemeDef.SYSTEM_THEME -> {
            val currentNightMode =
                mApplication.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            currentNightMode == Configuration.UI_MODE_NIGHT_NO
        }
        ThemeDef.AUTO_THEME -> {
            val date = Date(System.currentTimeMillis())

            date.after(startTime.time) && date.before(endTime.time) || date == startTime.time
        }
    }

    private fun updateColors() = if (isLightTheme()) {
        backgroundColor = Color.WHITE
        fontColor = Color.BLACK
        headerColor = mApplication.getColor(R.color.raspberry)
        layoutBackgroundColor = mApplication.getColor(R.color.light_gray)
        backgroundRes = R.drawable.text_view_style
        buttonRes = R.drawable.button_style
        switchStyle = R.style.SwitchTheme
    }
    else {
        backgroundColor = mApplication.getColor(R.color.background_dark)
        fontColor = Color.WHITE
        headerColor = mApplication.getColor(R.color.header_dark)
        layoutBackgroundColor = mApplication.getColor(R.color.gray)
        backgroundRes = R.drawable.text_view_dark_style
        buttonRes = R.drawable.button_style_dark
        switchStyle = R.style.SwitchDark
    }


    fun updateBeautifulFont(usingFont: Boolean) =
        settingsRepository.updateUsingBeautifulFont(usingFont)

    fun updateUsingDataHints(usingHints: Boolean) =
        settingsRepository.updateDataHints(usingHints)

    fun updateUsingBottomView(usingBottomView: Boolean) =
        settingsRepository.updateUsingBottomView(usingBottomView)


    fun getIndexTheme() = ThemeDef.values().indexOf(theme)

    fun setStartTime(startTime: Calendar) {
        this.startTime = startTime
        setDateToPreferences(
            keyMinutes = APP_PREFERENCES_START_MINUTES,
            keyHours = APP_PREFERENCES_START_HOURS,
            date = startTime
        )
        updateColors()
    }

    fun setEndTime(endTime: Calendar) {
        this.endTime = endTime
        setDateToPreferences(
            keyHours = APP_PREFERENCES_END_HOURS,
            keyMinutes = APP_PREFERENCES_END_MINUTES,
            date = endTime
        )
        updateColors()
    }

    fun getTimes() = Pair(startTime, endTime)


    private fun getDateFromPreferences(
        keyHours: EnumPreferences, defHours: Int, keyMinutes: EnumPreferences): Calendar {

        val hours = preferences.getInt(keyHours.title, defHours)
        val minutes = preferences.getInt(keyMinutes.title, 0)
        val calendar = GregorianCalendar()

        calendar[Calendar.HOUR_OF_DAY] = hours
        calendar[Calendar.MINUTE] = minutes
        return calendar
    }

    private fun setDateToPreferences(
        keyHours: EnumPreferences, keyMinutes: EnumPreferences, date : Calendar) {

        val editor = preferences.edit()
        editor.putInt(keyHours.title, date[Calendar.HOUR_OF_DAY])
        editor.putInt(keyMinutes.title, date[Calendar.MINUTE])
        editor.apply()
    }
}