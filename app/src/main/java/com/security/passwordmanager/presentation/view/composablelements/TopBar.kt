package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme


@ExperimentalMaterial3Api
@Composable
internal fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    navigationIcon : ImageVector ? = null,
    onNavigate: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
        },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigate) {
                    Icon(navigationIcon, contentDescription = title)
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.animate(),
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
            titleContentColor = MaterialTheme.colorScheme.onPrimary.animate()
        ),
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState),
        modifier = modifier
    )
}


@Composable
fun RowScope.TopBarAction(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary.animate()
        ),
        shape = CircleShape,
        modifier = modifier.align(Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}




@ExperimentalMaterial3Api
@Preview
@Composable
private fun TopBarPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        TopBar(title = "Wrecked", navigationIcon = Icons.Default.Menu)
    }
}