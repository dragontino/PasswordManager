package com.security.passwordmanager.presentation.view.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.getActivity
import com.security.passwordmanager.presentation.model.Times
import com.security.passwordmanager.presentation.model.enums.Themes
import com.security.passwordmanager.presentation.model.toCalendar
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import java.util.*

private val DarkColorScheme = darkColorScheme(
    primary = RaspberryDark,
    primaryContainer = Color(0xFF853657),
    onPrimary = Color.White,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = Gray,
    tertiaryContainer = Color(0xFF857467),
    error = Color.Red.copy(alpha = .8f)
)

private val LightColorScheme = lightColorScheme(
    primary = RaspberryLight,
    primaryContainer = Color(0xFFFFB6B8),
    background = Color.White,
    onBackground = Color.Black,
    onPrimary = Color.White,
    surface = LightGray,
    // TODO: 07.11.2022 изменить цвет
    tertiaryContainer = Color(0xFFF0DED1),
    error = Color.Red

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun PasswordManagerTheme(
    content: @Composable (isDarkTheme: Boolean) -> Unit,
) {
    val application = LocalContext.current.getActivity()?.application as PasswordManagerApplication?
    val settingsViewModel = viewModel<SettingsViewModel>(factory = application?.viewModelFactory)

    val settings by settingsViewModel.settings.observeAsState(initial = Settings())
    val times by settingsViewModel.times.collectAsState(initial = Times.Undefined)

    // TODO: 29.10.2022 сделать dynamicColor в settings
    val dynamicColor = false
    val isDarkTheme = when (settings.theme) {
        Themes.Light -> false
        Themes.Dark -> true
        Themes.System -> isSystemInDarkTheme()
        Themes.Auto -> {
            val date = Date(System.currentTimeMillis())
            date.before(times.startTime.toCalendar().time) ||
                    date.after(times.endTime.toCalendar().time) ||
                    date == times.endTime.toCalendar().time
        }
    }

    PasswordManagerTheme(
        isDarkTheme = isDarkTheme,
        dynamicColor = dynamicColor,
        content = { content(isDarkTheme) }
    )
}



@Composable
fun PasswordManagerTheme(
    isDarkTheme: Boolean,
    dynamicColor: Boolean = false,
    content: @Composable (() -> Unit),
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}