package com.security.passwordmanager.presentation.view.navigation

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Login
import androidx.compose.ui.graphics.vector.ImageVector
import com.security.passwordmanager.R
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.enums.DataType

sealed class AppScreens(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    val route: String
) {
    protected abstract val args: Array<String>

    val fullRoute: String get() = buildString {
        append(route)
        if (args.isNotEmpty()) {
            append("/")
            append(args.joinToString(separator = "/") { "{$it}" })
        }
    }

    protected fun <T : Enum<*>> Array<T>.toStringArray(): Array<String> =
        map { it.name }.toTypedArray()




    object Login : AppScreens(
        icon = Icons.Rounded.Login,
        titleRes = R.string.login,
        route = "login"
    ) {
        override val args: Array<String> = emptyArray()
    }


    object Notes : AppScreens(
        icon = Icons.Outlined.ListAlt,
        titleRes = R.string.all_notes,
        route = "home"
    ) {
        enum class Args {
            NotesScreenType,
            Title
        }

        override val args: Array<String>
        get() = Args.values().toStringArray()
    }


    object Website : AppScreens(
        icon = Icons.Outlined.AccountCircle,
        titleRes = R.string.website_label,
        route = "website"
    ) {
        enum class Args {
            DataKey,
            StartPosition
        }

        override val args: Array<String> = Args.values().toStringArray()
    }


    object BankCard : AppScreens(
        icon = Icons.Outlined.CreditCard,
        titleRes = R.string.bank_label,
        route = "bankcard"
    ) {
        enum class Args {
            DataKey,
            StartPosition
        }

        override val args = Args.values().toStringArray()
    }


    object Settings : AppScreens(
        icon = Icons.Outlined.Settings,
        titleRes = R.string.settings_label,
        route = "settings"
    ) {
        override val args: Array<String> = emptyArray()
    }
}


fun createRouteToLoginScreen() = AppScreens.Login.route

fun createRouteToNotesScreen(dataType: DataType, title: String) =
    "${AppScreens.Notes.route}/$dataType/$title"

fun createRouteToWebsiteScreen(address: String, startPosition: Int = 0): String {
    val route = "${AppScreens.Website.route}/${address.ifBlank { null }}/$startPosition"
    Log.d("NotesScreen", "route = $route")
    return route
}

fun createRouteToSettingsScreen() = AppScreens.Settings.route

fun createRouteToBankCardScreen(dataUI: DataUI, startPosition: Int = 0): String {
    return "${AppScreens.BankCard.route}/$dataUI/$startPosition"
}