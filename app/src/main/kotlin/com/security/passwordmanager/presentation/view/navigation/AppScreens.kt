package com.security.passwordmanager.presentation.view.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.security.passwordmanager.R
import com.security.passwordmanager.presentation.model.enums.DataType

sealed class AppScreens(
    protected val root: String,
    @StringRes val titleRes: Int?
) {
    protected abstract val args: Array<String>

    @Composable
    fun title() = when (titleRes) {
        null -> ""
        else -> stringResource(titleRes)
    }

    val destinationRoute: String
        get() = buildString {
            append(root)
            if (args.isNotEmpty()) {
                append(
                    "/",
                    args.joinToString(separator = "/") { "{$it}" }
                )
            }
        }

    protected fun <T : Enum<*>> Array<T>.toStringArray(): Array<String> =
        map { it.name }.toTypedArray()


    data object Login : AppScreens(root = "login", titleRes = null) {
        override val args: Array<String> = emptyArray()

        fun createUrl(): String = root
    }


    sealed class NavigationDrawerScreens(
        root: String,
        val icon: ImageVector,
        @StringRes titleRes: Int
    ) : AppScreens(
        root = root,
        titleRes = titleRes
    )


    data object AllNotes : NavigationDrawerScreens(
        root = "all",
        icon = Icons.Outlined.ListAlt,
        titleRes = R.string.all_notes
    ) {
        override val args: Array<String> = emptyArray()

        fun createUrl(): String = root
    }


    data object WebsiteNotes : NavigationDrawerScreens(
        root = "websites",
        icon = Icons.Outlined.AccountCircle,
        titleRes = R.string.websites
    ) {
        override val args: Array<String> = emptyArray()

        fun createUrl(): String = root
    }


    data object BankNotes : NavigationDrawerScreens(
        root = "banks",
        icon = Icons.Outlined.CreditCard,
        titleRes = R.string.bank_cards
    ) {
        override val args: Array<String> = emptyArray()

        fun createUrl(): String = root

    }


    data object Search : AppScreens(
        root = "search",
        titleRes = R.string.search_label
    ) {
        enum class Args {
            DataType
        }

        override val args: Array<String>
            get() = Args.values().toStringArray()

        fun createUrl(dataType: DataType): String =
            "$root/$dataType"
    }


    data object WebsiteEdit : AppScreens(
        root = "website-edit",
        titleRes = R.string.website_label
    ) {
        enum class Args {
            DataId,
            StartPosition
        }

        override val args: Array<String> = Args.values().toStringArray()

        fun createUrl(id: String? = null, startPosition: Int = 0): String =
            "$root/$id/$startPosition"
    }


    data object BankEdit : AppScreens(
        root = "bank-edit",
        titleRes = R.string.bank_label
    ) {
        enum class Args {
            DataId,
            StartPosition
        }

        override val args = Args.values().toStringArray()

        fun createUrl(id: String? = null, startPosition: Int = 0): String =
            "$root/$id/$startPosition"
    }


    data object Settings : NavigationDrawerScreens(
        root = "settings",
        icon = Icons.Outlined.Settings,
        titleRes = R.string.settings
    ) {
        override val args: Array<String> = emptyArray()

        fun createUrl(): String = root
    }


    data object PasswordGeneration : NavigationDrawerScreens(
        root = "generation",
        icon = Icons.Outlined.AutoFixHigh,
        titleRes = R.string.password_generation
    ) {
        override val args: Array<String> = emptyArray()

        fun createUrl(): String = root
    }
}