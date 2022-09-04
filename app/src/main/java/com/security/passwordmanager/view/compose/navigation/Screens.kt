package com.security.passwordmanager.view.compose.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.security.passwordmanager.R

@Composable
fun TopBar(
    title: String,
    navigationIcon: ImageVector,
    actions: List<TopBarAction> = listOf(),
    onNavigate: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onNavigate) {
                Icon(navigationIcon, contentDescription = title)
            }
        },
        actions = {
            actions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(
                        action.icon,
                        contentDescription = action.contentDescription,
                        tint = Color.White
                    )
                }
            }
        },
        backgroundColor = colorResource(R.color.header_color),
        contentColor = Color.White
    )
}


@Composable
fun TopBar(
    title: String,
    navigationIcon: ImageVector,
    action: TopBarAction,
    onNavigate: () -> Unit
)
= TopBar(
    title,
    navigationIcon,
    listOf(action),
    onNavigate
)


data class TopBarAction(
    val icon: ImageVector,
    val contentDescription: String = "",
    val onClick: () -> Unit
)


@Composable
private fun FragmentScreen(title: String, openDrawer: () -> Unit, onSearch: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = title,
            navigationIcon = Icons.Filled.Menu,
            actions = listOf(
                TopBarAction(icon = Icons.Filled.Search, onClick = onSearch)
            ),
            onNavigate = openDrawer
        )
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
        }
    }
}


@Composable
fun HomeScreen(openDrawer: () -> Unit, onSearch: () -> Unit) {
    FragmentScreen(
        title = stringResource(DrawerScreens.Home.titleRes),
        openDrawer = openDrawer,
        onSearch = onSearch
    )
}


@Composable
fun WebsitesScreen(openDrawer: () -> Unit, onSearch: () -> Unit) {
    FragmentScreen(
        title = stringResource(DrawerScreens.Website.titleRes),
        openDrawer = openDrawer,
        onSearch = onSearch
    )
}


@Composable
fun BankCardsScreen(openDrawer: () -> Unit, onSearch: () -> Unit) {
    FragmentScreen(title = stringResource(DrawerScreens.BankCard.titleRes),
        openDrawer = openDrawer,
        onSearch = onSearch
    )
}


@Composable
fun SettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = stringResource(DrawerScreens.Settings.titleRes),
            navigationIcon = Icons.Filled.ArrowBack,
            onNavigate = navController::popBackStack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(DrawerScreens.Settings.titleRes))
        }
    }
}


@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        openDrawer = {},
        onSearch = {}
    )
}


@Preview
@Composable
private fun WebsitesScreenPreview() {
    WebsitesScreen(openDrawer = {}, {})
}


@Preview
@Composable
private fun BankCardsScreenPreview() {
    BankCardsScreen({}, {})
}


@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}