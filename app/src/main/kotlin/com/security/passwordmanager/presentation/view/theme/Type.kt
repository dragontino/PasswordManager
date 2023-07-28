package com.security.passwordmanager.presentation.view.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontSize = 19.sp,
        fontFamily = FontFamily(RobotoFont)
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily(RobotoFont)
    ),
    bodySmall = TextStyle(
        fontSize = 13.sp,
        fontFamily = FontFamily(RobotoFont)
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily(VerdanaFont),
        fontSize = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily(VerdanaFont),
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily(VerdanaFont),
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(BeautifulFont),
        fontSize = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(BeautifulFont),
        fontSize = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily(BeautifulFont),
        fontSize = 20.sp
    ),

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)