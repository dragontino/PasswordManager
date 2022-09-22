package com.security.passwordmanager.view.compose

import android.content.res.AssetManager
import android.graphics.Typeface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.security.passwordmanager.R
import com.security.passwordmanager.model.Themes
import com.security.passwordmanager.viewmodel.MySettingsViewModel
import java.util.*

@Composable
fun AppTheme(
    assetManager: AssetManager,
    settingsViewModel: MySettingsViewModel,
    content: @Composable () -> Unit
) {
    val robotoFont = FontFamily(Typeface.createFromAsset(assetManager, "fonts/roboto.ttf"))
    val beautifulFont = FontFamily(
        Typeface.createFromAsset(assetManager, "fonts/beautiful_font.otf")
    )

    val theme = settingsViewModel.theme.collectAsState()
    val startTime = settingsViewModel.times.collectAsState().value.startTime.toCalendar()
    val endTime = settingsViewModel.times.collectAsState().value.endTime.toCalendar()

    val isDarkTheme = when (theme.value) {
        Themes.LIGHT_THEME -> false
        Themes.DARK_THEME -> true
        Themes.SYSTEM_THEME -> isSystemInDarkTheme()
        Themes.AUTO_THEME -> {
            val date = Date(System.currentTimeMillis())
            date.before(startTime.time)
                    || date.after(endTime.time)
                    || date == endTime.time
        }
    }

    MaterialTheme(
        typography = Typography(
            defaultFontFamily = robotoFont,
            subtitle1 = TextStyle(fontFamily = beautifulFont),
            caption = TextStyle(fontFamily = robotoFont)
        ),
        colors = if (isDarkTheme)
            darkColors(
                primary = colorResource(R.color.header_dark),
                primaryVariant = Color(0xFF853657),
                secondary = Color.White,
                background = colorResource(R.color.background_dark),
                onBackground = Color.White,
                onSurface = Color.Black,
                surface = colorResource(R.color.gray)
            )
        else
            lightColors(
                primary = colorResource(R.color.raspberry),
                primaryVariant = Color(0xFFFFB6B8),
                secondary = colorResource(R.color.gray),
                background = Color.White,
                onBackground = Color.Black,
                surface = colorResource(R.color.light_gray)
            ),
        content = content
    )
}