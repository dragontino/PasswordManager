package com.security.passwordmanager.view.compose.navigation

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.rememberSystemUiController


data class TopBarAction(
    val icon: ImageVector,
    val contentDescription: String = "",
    val modifier: Modifier = Modifier,
    val onClick: () -> Unit
)


@Composable
internal fun TopBar(
    title: String,
    navigationIcon: ImageVector,
    actions: List<TopBarAction> = listOf(),
    onNavigate: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(MaterialTheme.colors.primary)

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
                        tint = Color.White,
                        modifier = action.modifier
                    )
                }
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    )
}


@Preview
@Composable
fun TopBarPreview() {
    TopBar(title = "Wrecked", navigationIcon = Icons.Default.Menu) { }
}