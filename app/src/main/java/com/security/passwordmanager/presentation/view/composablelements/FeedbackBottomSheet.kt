package com.security.passwordmanager.presentation.view.composablelements

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.BottomSheetState
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.IconTextItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ImageTextItem
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme

@Composable
internal fun feedbackBottomState(beautifulDesign: Boolean) =
    BottomSheetState(
        title = stringResource(R.string.feedback),
        beautifulDesign = beautifulDesign
    ) {
        FeedbackSheetContent(onClose = it::dismiss)
    }



@Composable
private fun ColumnScope.FeedbackSheetContent(onClose: () -> Unit) {
    val context = LocalContext.current
    
    val openAddress = { address: String ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        context.startActivity(intent)
        onClose()
    }
    
    ImageTextItem(
        text = stringResource(R.string.telegram),
        image = R.drawable.telegram_logo,
        imageTintColor = MaterialTheme.colorScheme.secondary.animate(),
        imageSpace = 12.dp
    ) {
        openAddress("https://t.me/cepetroff")
    }

    ImageTextItem(
        text = stringResource(R.string.vk),
        image = R.drawable.vk_logo,
        imageTintColor = MaterialTheme.colorScheme.secondary.animate(),
        imageModifier = Modifier.scale(1.2f),
        imageSpace = 12.dp
    ) {
        openAddress("https://vk.com/cepetroff")
    }

    IconTextItem(
        text = stringResource(R.string.email),
        icon = Icons.Default.AlternateEmail,
        iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
        iconSpace = 12.dp
    ) {
        openAddress("mailto:petrovsd2002@gmail.com")
    }
}




@Preview
@Composable
private fun FeedbackSheetPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        Column {
            FeedbackSheetContent {}
        }
    }
}