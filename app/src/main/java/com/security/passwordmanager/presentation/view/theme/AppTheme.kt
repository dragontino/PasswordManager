package com.security.passwordmanager.presentation.view.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.presentation.model.enums.ColorDesign
import com.security.passwordmanager.presentation.model.toCalendar
import java.util.*

private val DarkColorScheme = darkColorScheme(
    primary = RaspberryDark,
    primaryContainer = Color(0xFF853657),
    secondary = Color(0xFF852626),
    onPrimary = Color.White,
    onPrimaryContainer = Color(red = 44, green = 18, blue = 90),
    background = BackgroundDark,
    onBackground = Color.White,
    surface = Gray,
    error = Color.Red.copy(alpha = .8f)
)

private val LightColorScheme = lightColorScheme(
    primary = RaspberryLight,
    primaryContainer = Color(0xFFFFB6B8),
    secondary = Color(0xFFF05454),
    background = Color.White,
    onBackground = Color.Black,
    onPrimary = Color.White,
    onPrimaryContainer = Color(red = 33, green = 0, blue = 93),
    surface = LightGray,
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
    settings: Settings,
    content: @Composable (isDarkTheme: Boolean) -> Unit,
) {
    val startDate = settings.sunriseTime.toCalendar().time
    val endDate = settings.sunsetTime.toCalendar().time

    val isDarkTheme = when (settings.colorDesign) {
        ColorDesign.Light -> false
        ColorDesign.Dark -> true
        ColorDesign.System -> isSystemInDarkTheme()
        ColorDesign.Auto -> {
            val date = Date(System.currentTimeMillis())
            when {
                startDate <= endDate -> date !in startDate..endDate
                else -> date in endDate..startDate
            }
        }
    }

    PasswordManagerTheme(
        isDarkTheme = isDarkTheme,
        dynamicColor = settings.dynamicColor,
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