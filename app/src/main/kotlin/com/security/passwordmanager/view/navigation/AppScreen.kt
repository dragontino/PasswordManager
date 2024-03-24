package com.security.passwordmanager.view.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.entity.EntityType

interface NavigationDrawerItem {
    val icon: ImageVector
}

sealed interface AppScreen {
    interface Args {
        fun toStringArray(): Array<String>
    }

    val baseRoute: String

    @get:StringRes
    val titleRes: Int? get() = null

    val args: Args? get() = null

    val destinationRoute: String
        get() = buildString {
            append(baseRoute)
            args?.toStringArray()
                ?.joinToString("/") { "{$it}" }
                ?.let { append("/", it) }
        }


    @Composable
    fun title() = titleRes
        ?.let { stringResource(it) }
        ?: ""


}

data object LoginScreen : AppScreen {
    override val baseRoute = "login"
    fun createUrl(): String = baseRoute
}


sealed class HomeScreen : AppScreen {
    companion object {
        const val rootRoute = "home"
    }

    abstract val childRoute: String

    final override val baseRoute: String
        get() = "$rootRoute-$childRoute"

    data object AllNotes : HomeScreen(), NavigationDrawerItem {
        override val childRoute = "all"
        override val titleRes = R.string.all_entries
        override val icon = Icons.AutoMirrored.Outlined.ListAlt

        fun createUrl(): String = baseRoute
    }


    data object WebsiteNotes : HomeScreen(), NavigationDrawerItem {
        override val childRoute = "websites"
        override val icon = Icons.Outlined.AccountCircle
        override val titleRes = R.string.websites

        fun createUrl(): String = baseRoute
    }


    data object BankNotes : HomeScreen(), NavigationDrawerItem {
        override val childRoute: String = "banks"
        override val icon = Icons.Outlined.CreditCard
        override val titleRes = R.string.bank_cards

        fun createUrl(): String = baseRoute
    }


    data object Search : HomeScreen() {
        override val childRoute = "search"
        override val titleRes = R.string.search_label

        object Args : AppScreen.Args {
            const val EntityType = "entityType"
            override fun toStringArray() = arrayOf(EntityType)
        }

        override val args = Args

        fun createUrl(entityType: EntityType): String =
            "$baseRoute/$entityType"
    }
}


sealed class EditScreen : AppScreen {
    companion object {
        const val rootRoute = "edit"
    }

    abstract val childRoute: String

    final override val baseRoute: String
        get() = "$rootRoute-$childRoute"


    data object Website : EditScreen() {
        override val childRoute = "website"
        override val titleRes = R.string.website_label

        object Args : AppScreen.Args {
            const val EntityId = "entityId"
            const val StartPosition = "startPosition"

            override fun toStringArray() = arrayOf(EntityId, StartPosition)
        }

        override val args = Args

        fun createUrl(id: String? = null, startPosition: Int = 0): String =
            "$baseRoute/$id/$startPosition"
    }

    data object BankEdit : EditScreen() {
        override val childRoute = "bank"
        override val titleRes = R.string.bank_label

        object Args : AppScreen.Args {
            const val EntityId = "entityId"
            const val StartPosition = "startPosition"

            override fun toStringArray() = arrayOf(EntityId, StartPosition)
        }

        override val args = Args

        fun createUrl(id: String? = null, startPosition: Int = 0): String =
            "$baseRoute/$id/$startPosition"
    }
}


data object SettingsScreen : AppScreen, NavigationDrawerItem {
    override val baseRoute: String = "settings"
    override val icon = Icons.Outlined.Settings
    override val titleRes = R.string.settings

    fun createUrl(): String = baseRoute
}

data object PasswordGenerationScreen : AppScreen, NavigationDrawerItem {
    override val baseRoute = "generation"
    override val icon = Icons.Outlined.AutoFixHigh
    override val titleRes = R.string.password_generator

    fun createUrl(): String = baseRoute
}