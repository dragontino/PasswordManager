package com.security.passwordmanager.view.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.security.passwordmanager.domain.model.ColorScheme
import com.security.passwordmanager.domain.model.Settings
import com.security.passwordmanager.domain.model.asTime
import java.time.LocalTime

private val darkColorScheme = darkColorScheme(
    primary = RaspberryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF853657),
    onPrimaryContainer = Color(red = 44, green = 18, blue = 90),
    secondary = Color(0xFF852626),
    background = BackgroundDark,
    onBackground = Color.White,
    surface = Color(red = 56, green = 43, blue = 50),
    onSurface = Color.White,
    surfaceVariant = Color(red = 59, green = 45, blue = 52),
    onSurfaceVariant = Color.White,
    surfaceTint = Color(red = 44, green = 37, blue = 42),
    error = Color.Red.copy(alpha = .8f)
)

private val lightColorScheme = lightColorScheme(
    primary = RaspberryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFB6B8),
    onPrimaryContainer = Color(red = 43, green = 43, blue = 133),
    secondary = Color(0xFFF05454),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(red = 249, green = 229, blue = 237),
    onSurface = Color.Black,
    surfaceVariant = Color(red = 232, green = 215, blue = 215),
    onSurfaceVariant = Color.Black,
    surfaceTint = Color(red = 252, green = 248, blue = 248),
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
    val startTime = settings.sunriseTime
    val endTime = settings.sunsetTime

    val isDarkTheme = when (settings.colorScheme) {
        ColorScheme.Light -> false
        ColorScheme.Dark -> true
        ColorScheme.System -> isSystemInDarkTheme()
        ColorScheme.Auto -> {
            val nowTime = LocalTime.now().asTime()
            when {
                startTime <= endTime -> nowTime !in startTime..endTime
                else -> nowTime in endTime..startTime
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
            when {
                isDarkTheme -> dynamicDarkColorScheme(context)
                else -> dynamicLightColorScheme(context)
            }.run {
                copy(surfaceTint = surface)
            }
        }

        isDarkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}