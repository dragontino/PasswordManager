package com.security.passwordmanager.presentation.view.composables

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.IconTextItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ImageTextItem
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme


@Composable
fun ColumnScope.FeedbackSheetContent(
    beautifulDesign: Boolean,
    isDarkTheme: Boolean,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    val openAddress = { address: String ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        context.startActivity(intent)
        onClose()
    }

    BottomSheetContent(
        title = stringResource(R.string.feedback),
        beautifulDesign = beautifulDesign
    ) {
        ImageTextItem(
            text = stringResource(R.string.telegram),
            image = when {
                isDarkTheme -> R.drawable.telegram_logo_light
                else -> R.drawable.telegram_logo
            },
            imageSpace = 12.dp,
            imageModifier = Modifier.width(48.dp)
        ) {
            openAddress(context.getString(R.string.telegram_ref))
        }

        ImageTextItem(
            text = stringResource(R.string.github),
            image = when {
                isDarkTheme -> R.drawable.github_logo_white
                else -> R.drawable.github_logo
            },
            imageSpace = 12.dp,
            imageModifier = Modifier.width(48.dp)
        ) {
            openAddress(context.getString(R.string.github_ref))
        }

        ImageTextItem(
            text = stringResource(R.string.vk),
            image = when {
                isDarkTheme -> R.drawable.vk_logo_light
                else -> R.drawable.vk_logo
            },
            imageModifier = Modifier.width(48.dp),
            imageSpace = 12.dp
        ) {
            openAddress(context.getString(R.string.vk_ref))
        }

        IconTextItem(
            text = stringResource(R.string.email),
            icon = Icons.Default.AlternateEmail,
            iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
            iconSpace = 12.dp,
            iconModifier = Modifier.width(48.dp)
        ) {
            openAddress(context.getString(R.string.email_ref))
        }
    }
}


@Preview
@Composable
private fun FeedbackSheetPreview() {
    val isDarkTheme = true
    PasswordManagerTheme(isDarkTheme = isDarkTheme) {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            FeedbackSheetContent(beautifulDesign = true, isDarkTheme = isDarkTheme) {}
        }
    }
}