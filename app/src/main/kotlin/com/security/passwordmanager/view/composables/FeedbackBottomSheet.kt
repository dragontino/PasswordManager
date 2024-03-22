package com.security.passwordmanager.view.composables

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.composables.sheets.ModalBottomSheet
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.IconTextItem
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.ImageTextItem
import com.security.passwordmanager.view.theme.PasswordManagerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    beautifulDesign: Boolean,
    isDarkTheme: Boolean,
    state: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onClose: () -> Unit
) {
    val context = LocalContext.current

    val openAddress = { address: String ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        context.startActivity(intent)
        onClose()
    }

    ModalBottomSheet(
        state = state,
        title = stringResource(R.string.feedback),
        beautifulDesign = beautifulDesign,
        onClose = onClose
    ) {
        ImageTextItem(
            text = stringResource(R.string.telegram),
            image = when {
                isDarkTheme -> R.drawable.telegram_logo_light
                else -> R.drawable.telegram_logo
            },
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
        ) {
            openAddress(context.getString(R.string.vk_ref))
        }

        IconTextItem(
            text = stringResource(R.string.email),
            icon = Icons.Default.AlternateEmail,
            iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
            iconModifier = Modifier.width(48.dp)
        ) {
            openAddress(context.getString(R.string.email_ref))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun FeedbackSheetPreview() {
    val isDarkTheme = true
    PasswordManagerTheme(isDarkTheme = isDarkTheme) {

        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            FeedbackBottomSheet(beautifulDesign = true, isDarkTheme = isDarkTheme) {}
        }
    }
}