package com.security.passwordmanager.view.compose.navigation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.R
import com.security.passwordmanager.model.ScreenType

private val screens = listOf(
    ScreenType.Home,
    ScreenType.Website,
    ScreenType.BankCard,
    ScreenType.Settings
)


@Composable
fun DrawerContent(onDestinationClicked: (route: String) -> Unit) {

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        NavHeader()

        screens.forEach { screen ->
            if (screen == ScreenType.Settings)
                Divider(
                    color = colorResource(android.R.color.darker_gray),
                    modifier = Modifier.padding(top = 3.dp, bottom = 2.dp)
                )
            DrawerItem(screen, onDestinationClicked)
        }
    }
}


@Composable
private fun NavHeader() {
    Column(
        modifier = Modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colors.primaryVariant,
                        MaterialTheme.colors.primary,
                        MaterialTheme.colors.primaryVariant
                    )
                )
            )
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = stringResource(R.string.nav_header_desc),
            modifier = Modifier.size(80.dp)
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
private fun ColumnScope.DrawerItem(screen: ScreenType, onDestinationClicked: (route: String) -> Unit) {
    Row(modifier = Modifier
        .clickable {
            onDestinationClicked(screen.route)
        }
        .align(Alignment.Start)
        .padding(vertical = 16.dp)
        .padding(start = 8.dp, end = 8.dp)
        .fillMaxWidth()
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = stringResource(screen.pluralTitleRes),
            tint = colorResource(R.color.raspberry)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(screen.pluralTitleRes),
            color = MaterialTheme.colors.onBackground,
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