package com.security.passwordmanager.viewmodel

import android.app.Application
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.navigation.NavigationView
import com.security.passwordmanager.AppPreferences
import com.security.passwordmanager.ColorStateList
import com.security.passwordmanager.R
import com.security.passwordmanager.data.MainDatabase
import com.security.passwordmanager.settings.Settings
import com.security.passwordmanager.settings.SettingsRepository
import com.security.passwordmanager.settings.ThemeDef
import com.security.passwordmanager.view.compose.Times
import com.security.passwordmanager.view.compose.toCalendar
import com.security.passwordmanager.view.compose.toTime
import com.security.passwordmanager.view.customviews.BeautifulTextView
import java.util.*

class SettingsViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        @Volatile
        private var INSTANCE: SettingsViewModel? = null

        fun getInstance(owner: ViewModelStoreOwner): SettingsViewModel {
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
    private val preferences = AppPreferences(mApplication)

    var startTime: Calendar
        get() = preferences.startTime.toCalendar()
        set(value) {
            preferences.startTime = value.toTime()
            updateColors()
        }
//    getDateFromPreferences(
//        keyHours = APP_PREFERENCES_START_HOURS,
//        keyMinutes = APP_PREFERENCES_START_MINUTES,
//        defHours = 7
//    )

    var endTime: Calendar
        get() = preferences.endTime.toCalendar()
        set(value) {
            preferences.endTime = value.toTime()
            updateColors()
        }
//        getDateFromPreferences(
//        keyHours = APP_PREFERENCES_END_HOURS,
//        keyMinutes = APP_PREFERENCES_END_MINUTES,
//        defHours = 23
//    )


    var theme: ThemeDef
        get() = ThemeDef.values().find {
            it.themeName == baseSettings.theme
        } ?: ThemeDef.LIGHT_THEME
        set(value) {
            settingsRepository.updateTheme(preferences.email, value)
            updateColors()

            mApplication.setTheme(currentAppTheme)
            AppCompatDelegate.setDefaultNightMode(
                when (value) {
                    ThemeDef.LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
                    ThemeDef.DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
                    ThemeDef.SYSTEM_THEME -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    ThemeDef.AUTO_THEME -> AppCompatDelegate.MODE_NIGHT_AUTO_TIME
                }
            )
        }


    val currentAppTheme get(): @StyleRes Int =
        if (isLightTheme)
            R.style.Theme_PasswordManager
        else
            R.style.Theme_PasswordManager_Dark


    val currentNightMode: Int get() =
        if (isLightTheme)
            AppCompatDelegate.MODE_NIGHT_NO
        else
            AppCompatDelegate.MODE_NIGHT_YES


    val baseSettings: Settings
    get() =
        settingsRepository.getSettings(preferences.email)


    @ColorInt var backgroundColor = Color.WHITE
    @ColorInt var fontColor = Color.BLACK
    @ColorInt var headerColor = mApplication.getColor(R.color.raspberry)
    @ColorInt var layoutBackgroundColor = mApplication.getColor(R.color.light_gray)
    @ColorInt var darkerGrayColor = mApplication.getColor(android.R.color.darker_gray)

    @DrawableRes var backgroundRes = R.drawable.text_view_style
    var beautifulBackgroundStyle = BeautifulTextView.BackgroundStyle.LIGHT
    @DrawableRes var buttonRes = R.drawable.button_style
    @StyleRes var switchStyle = R.style.SwitchTheme

    var isPasswordRemembered
        get() = preferences.isPasswordRemembered
        set(value) { preferences.isPasswordRemembered = value }

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
        window.statusBarColor = headerColor //mApplication.getColor(android.R.color.transparent)
        actionBar.setBackgroundDrawable(ColorDrawable(headerColor))

        if (navigationView != null) {
            navigationView.setBackgroundColor(backgroundColor)
            navigationView.itemTextColor = ColorStateList(fontColor)

            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled)
            )

            val colors = intArrayOf(mApplication.getColor(R.color.raspberry), fontColor)
            navigationView.itemIconTintList = ColorStateList(states, colors)
        }
    }

    val isLightTheme: Boolean = when(theme) {
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

    private fun updateColors() = if (isLightTheme) {
        backgroundColor = Color.WHITE
        fontColor = Color.BLACK
        headerColor = mApplication.getColor(R.color.raspberry)
        layoutBackgroundColor = mApplication.getColor(R.color.light_gray)
        backgroundRes = R.drawable.text_view_style
        beautifulBackgroundStyle = BeautifulTextView.BackgroundStyle.LIGHT
        buttonRes = R.drawable.button_style
        switchStyle = R.style.SwitchTheme
    }
    else {
        backgroundColor = mApplication.getColor(R.color.background_dark)
        fontColor = Color.WHITE
        headerColor = mApplication.getColor(R.color.header_dark)
        layoutBackgroundColor = mApplication.getColor(R.color.gray)
        backgroundRes = R.drawable.text_view_dark_style
        beautifulBackgroundStyle = BeautifulTextView.BackgroundStyle.DARK
        buttonRes = R.drawable.button_style_dark
        switchStyle = R.style.SwitchDark
    }


    fun updateBeautifulFont(usingFont: Boolean) =
        settingsRepository.updateUsingBeautifulFont(preferences.email, usingFont)

    fun updateUsingDataHints(usingHints: Boolean) =
        settingsRepository.updateDataHints(preferences.email, usingHints)

    fun updateUsingBottomView(usingBottomView: Boolean) =
        settingsRepository.updateUsingBottomView(preferences.email, usingBottomView)


    val indexTheme get() = ThemeDef.values().indexOf(theme)

//    fun setStartTime(startTime: Calendar) {
//        this.startTime = startTime
//
//        setDateToPreferences(
//            keyMinutes = APP_PREFERENCES_START_MINUTES,
//            keyHours = APP_PREFERENCES_START_HOURS,
//            date = startTime
//        )
//        updateColors()
//    }

//    fun setEndTime(endTime: Calendar) {
//        this.endTime = endTime
//        setDateToPreferences(
//            keyHours = APP_PREFERENCES_END_HOURS,
//            keyMinutes = APP_PREFERENCES_END_MINUTES,
//            date = endTime
//        )
//        updateColors()
//    }

    fun getTimes() = Times(startTime, endTime)

//    private fun getDateFromPreferences(
//        keyHours: EnumPreferences, defHours: Int, keyMinutes: EnumPreferences
//    ): Calendar {
//
//        val hours = preferences.getInt(keyHours.title, defHours)
//        val minutes = preferences.getInt(keyMinutes.title, 0)
//        val calendar = GregorianCalendar()
//
//        calendar[Calendar.HOUR_OF_DAY] = hours
//        calendar[Calendar.MINUTE] = minutes
//        return calendar
//    }

//    private fun setDateToPreferences(
//        keyHours: EnumPreferences,
//        keyMinutes: EnumPreferences,
//        date: Calendar,
//    ) =
//        preferences.edit().apply {
//            putInt(keyHours.title, date[Calendar.HOUR_OF_DAY])
//            putInt(keyMinutes.title, date[Calendar.MINUTE])
//        }.apply()
}