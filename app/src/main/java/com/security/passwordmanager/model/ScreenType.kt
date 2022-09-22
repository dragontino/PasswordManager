package com.security.passwordmanager.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.security.passwordmanager.R

enum class ScreenType(
    val id: Int,
    @StringRes val singleTitleRes: Int,
    @StringRes val pluralTitleRes: Int,
    val icon: ImageVector,
    val route: String
) {
    Home(
        id = 0,
        singleTitleRes = R.string.all_entries,
        pluralTitleRes = R.string.all_entries,
        icon = Icons.Outlined.ListAlt,
        route = "home"
    ),
    Website(
        id = 1,
        singleTitleRes = R.string.website_label,
        pluralTitleRes = R.string.websites,
        icon = Icons.Outlined.AccountCircle,
        route = "website"
    ),

    BankCard(
        id = 2,
        singleTitleRes = R.string.bank_label,
        pluralTitleRes = R.string.bank_cards,
        icon = Icons.Outlined.Payment,
        route = "bankcard"
    ),

    Settings(
        id = 3,
        singleTitleRes = R.string.settings_label,
        pluralTitleRes = R.string.settings,
        icon = Icons.Outlined.Settings,
        route = "settings"
    );

    fun toDataType() = when (this) {
        Home -> null
        Website -> DataType.Website
        BankCard -> DataType.BankCard
        Settings -> null
    }
}