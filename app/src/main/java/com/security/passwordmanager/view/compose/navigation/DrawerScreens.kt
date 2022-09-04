package com.security.passwordmanager.view.compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.security.passwordmanager.R

sealed class DrawerScreens(
    val titleRes: Int,
    val icon: ImageVector,
    val route: String
) {
    object Home : DrawerScreens(
        titleRes = R.string.all_entries,
        icon = Icons.Outlined.ListAlt,
        route = "home"
    )

    object Website : DrawerScreens(
        titleRes = R.string.websites,
        icon = Icons.Outlined.AccountCircle,
        route = "website"
    )

    object BankCard : DrawerScreens(
        titleRes = R.string.bank_cards,
        icon = Icons.Outlined.CreditCard,
        route = "bankcard"
    )

    object Settings : DrawerScreens(
        titleRes = R.string.settings,
        icon = Icons.Outlined.Settings,
        route = "settings"
    )
}
