package com.security.passwordmanager.view.compose

import android.content.res.AssetManager
import android.graphics.Typeface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.security.passwordmanager.R

@Composable
fun AppTheme(assetManager: AssetManager, content: @Composable () -> Unit) {
    val robotoFont = FontFamily(Typeface.createFromAsset(assetManager, "fonts/roboto.ttf"))
    val beautifulFont = FontFamily(
        Typeface.createFromAsset(assetManager, "fonts/beautiful_font.otf")
    )

    MaterialTheme(
        typography = Typography(
            defaultFontFamily = robotoFont,
            subtitle1 = TextStyle(fontFamily = beautifulFont)
        ),
        colors = if (isSystemInDarkTheme())
            darkColors(
                primary = colorResource(R.color.header_dark),
                secondary = Color.White,
                background = colorResource(R.color.background_dark),
                onBackground = Color.White,
                onSurface = Color.White,
                surface = colorResource(R.color.gray)
            )
        else
            lightColors(
                primary = colorResource(R.color.raspberry),
                secondary = colorResource(R.color.gray),
                background = Color.White,
                onBackground = Color.Black,
                onSurface = Color.Black,
                surface = colorResource(R.color.light_gray)
            ),
        content = content
    )
}