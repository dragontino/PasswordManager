package com.security.passwordmanager.view.compose.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.R

private val screens = listOf(
    DrawerScreens.Home,
    DrawerScreens.Website,
    DrawerScreens.BankCard,
    DrawerScreens.Settings
)


@Composable
fun ColumnScope.DrawerContent(onDestinationClicked: (route: String) -> Unit) {
    NavHeader()

    screens.forEach { screen ->
        if (screen == DrawerScreens.Settings)
            Divider(
                color = colorResource(android.R.color.darker_gray),
                modifier = Modifier.padding(top = 3.dp, bottom = 2.dp)
            )
        DrawerItem(screen, onDestinationClicked)
    }
}


@Composable
private fun NavHeader() {
    Column(
        modifier = Modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFB6B8),
                        colorResource(R.color.raspberry),
                        Color(0xFFF1C4DF)
                    )
                )
            )
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = stringResource(R.string.nav_header_desc),
            tint = colorResource(android.R.color.holo_orange_dark),
            modifier = Modifier
                .padding(start = 4.dp)
                .padding(vertical = 12.dp)
                .scale(2.2f)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.app_label),
            color = Color.White,
            style = MaterialTheme.typography.body1
        )
        Spacer(Modifier.height(3.dp))
        Text(
            stringResource(R.string.app_version),
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}


@Composable
private fun ColumnScope.DrawerItem(screen: DrawerScreens, onDestinationClicked: (route: String) -> Unit) {
    Row(modifier = Modifier
        .clickable {
            onDestinationClicked(screen.route)
        }
        .align(Alignment.Start)
        .padding(vertical = 16.dp)
        .padding(start = 8.dp, end = 8.dp)
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = stringResource(screen.titleRes),
            tint = colorResource(R.color.raspberry)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = stringResource(screen.titleRes),
            color = colorResource(R.color.text_color),
            fontSize = 16.sp
        )
    }
}


@Preview
@Composable
private fun DrawerPreview() {
    Column {
        DrawerContent {}
    }
}