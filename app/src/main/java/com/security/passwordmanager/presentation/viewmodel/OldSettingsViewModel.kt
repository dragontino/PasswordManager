package com.security.passwordmanager.presentation.viewmodel

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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.navigation.NavigationView
import com.security.passwordmanager.ColorStateList
import com.security.passwordmanager.R
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.presentation.model.enums.Themes
import com.security.passwordmanager.presentation.model.toCalendar
import com.security.passwordmanager.presentation.model.toTime
import java.util.*

class OldSettingsViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        @Volatile
        private var INSTANCE: OldSettingsViewModel? = null

        fun getInstance(owner: ViewModelStoreOwner): OldSettingsViewModel {
            val temp = INSTANCE
            if (temp != null)
                return temp

            synchronized(this) {
                val instance = ViewModelProvider(owner)[OldSettingsViewModel::class.java]

                INSTANCE = instance
                return instance
            }
        }
    }

//    private val settingsRepository: SettingsRepository
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


    var theme: Themes
        get() = Themes.Light
        set(value) {
//            settingsRepository.updateTheme(preferences.email, value)
            updateColors()

            mApplication.setTheme(currentAppTheme)
        }


    private val currentAppTheme get(): @StyleRes Int =
        if (isLightTheme)
            R.style.Theme_PasswordManager
        else
            R.style.Theme_PasswordManager_Dark


    //    val baseSettings: Settings
//    get() =
//        settingsRepository.getSettings(preferences.email)


    @ColorInt var backgroundColor = Color.WHITE
    @ColorInt var fontColor = Color.BLACK
    @ColorInt var headerColor = mApplication.getColor(R.color.raspberry)
    @ColorInt var layoutBackgroundColor = mApplication.getColor(R.color.light_gray)

    @DrawableRes var backgroundRes = R.drawable.text_view_style
    @DrawableRes var buttonRes = R.drawable.button_style
    @StyleRes var switchStyle = R.style.SwitchTheme

    var isPasswordRemembered
        get() = preferences.isPasswordRemembered
        set(value) { preferences.isPasswordRemembered = value }

    init {
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
        Themes.Light -> true
        Themes.Dark -> false
        Themes.System -> {
            val currentNightMode =
                mApplication.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            currentNightMode == Configuration.UI_MODE_NIGHT_NO
        }
        Themes.Auto -> {
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


//    fun updateBeautifulFont(usingFont: Boolean) =
//        settingsRepository.updateUsingBeautifulFont(preferences.email, usingFont)
//
//    fun updateUsingDataHints(usingHints: Boolean) =
//        settingsRepository.updateDataHints(preferences.email, usingHints)
//
//    fun updateUsingBottomView(usingBottomView: Boolean) =
//        settingsRepository.updateUsingBottomView(preferences.email, usingBottomView)


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