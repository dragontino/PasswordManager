package com.security.passwordmanager.presentation.view.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.IconTextItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ImageTextItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.bottomSheetBorderThickness

@ExperimentalMaterial3Api
@Composable
internal fun ModalNavigationDrawerContent(items: @Composable ColumnScope.() -> Unit) {
    val configuration = LocalConfiguration.current

    ModalDrawerSheet(
        drawerShape = MaterialTheme.shapes.large.copy(
            topStart = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp)
        ),
        drawerContainerColor = MaterialTheme.colorScheme.background.animate(),
        drawerContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        windowInsets = WindowInsets.tappableElement.only(WindowInsetsSides.Bottom),
        modifier = Modifier
            .padding(end = 80.dp)
            .fillMaxHeight()
            .width(
                width = minOf(
                    configuration.screenWidthDp,
                    configuration.screenHeightDp
                ).dp
            )
            .verticalScroll(rememberScrollState())
    ) {
        NavHeader(
            topPadding = WindowInsets.statusBars
                .only(WindowInsetsSides.Top)
                .asPaddingValues()
                .calculateTopPadding()
        )
        Spacer(modifier = Modifier.size(16.dp))
        items()
    }
}


@Composable
private fun NavHeader(topPadding: Dp = 0.dp) {
    Column(
        modifier = Modifier
            .background(
                Brush.linearGradient(
                    0f to MaterialTheme.colorScheme.primaryContainer.animate(),
                    0.5f to MaterialTheme.colorScheme.primary.animate(),
                    1f to MaterialTheme.colorScheme.primaryContainer.animate(),
                )
            )
            .padding(vertical = 16.dp, horizontal = 17.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(topPadding))

        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = stringResource(R.string.nav_header_desc),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.app_label),
            color = MaterialTheme.colorScheme.onPrimary.animate(),
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.height(3.dp))
        Text(
            stringResource(R.string.app_version),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary.animate().copy(alpha = 0.8f)
        )
    }
}



@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    title: String = "",
    subtitle: String = "",
    beautifulDesign: Boolean = false,
    bodyContent: @Composable (ColumnScope.() -> Unit)
) = BottomSheetContent(
    header = Header(title, subtitle, beautifulDesign),
    bodyContent = bodyContent,
    modifier = modifier,
)



@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    title: AnnotatedString = AnnotatedString(""),
    subtitle: AnnotatedString = AnnotatedString(""),
    beautifulDesign: Boolean = false,
    bodyContent: @Composable (ColumnScope.() -> Unit)
) = BottomSheetContent(
    header = AnnotatedHeader(title, subtitle, beautifulDesign),
    bodyContent = bodyContent,
    modifier = modifier
)



@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    header: HeadingInterface = Header(),
    bodyContent: @Composable (ColumnScope.() -> Unit),
) {
    Column(
        modifier = modifier
            .padding(
                WindowInsets.tappableElement
                    .only(WindowInsetsSides.Bottom)
                    .asPaddingValues()
            )
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colorScheme.background.animate(),
                shape = MaterialTheme.shapes.large
            )
            .border(
                width = bottomSheetBorderThickness,
                brush = Brush.verticalGradient(
                    0.1f to MaterialTheme.colorScheme.onBackground.animate(),
                    0.5f to MaterialTheme.colorScheme.background.animate()
                ),
                shape = MaterialTheme.shapes.large
            )
            .fillMaxWidth()
    ) {
        BottomSheetHeader(header)
        Spacer(modifier = Modifier.size(8.dp))
        bodyContent()
        Spacer(modifier = Modifier.size(8.dp))
    }
}


@Composable
private fun ColumnScope.BottomSheetHeader(header: HeadingInterface) {
    if (header.title.isBlank()) return

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .align(Alignment.Start)
            .fillMaxWidth()
    ) {
        val titleTextStyle =
            if (header.beautifulDesign) MaterialTheme.typography.titleLarge
            else MaterialTheme.typography.labelMedium


        when (header) {
            is AnnotatedHeader -> TitleWithSubtitle(header, titleTextStyle)
            is Header -> TitleWithSubtitle(header, titleTextStyle)
        }


        Divider(
            color = DarkerGray,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}


@Composable
private fun TitleWithSubtitle(header: Header, titleTextStyle: TextStyle) {
    Text(
        text = header.title,
        style = titleTextStyle,
        color = MaterialTheme.colorScheme.onBackground.animate(),
        modifier = Modifier
            .padding(start = 16.dp)
            .fillMaxWidth()
    )

    if (header.subtitle.isNotBlank()) {
        Text(
            text = header.subtitle,
            color = DarkerGray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 4.dp)
                .fillMaxWidth()
        )
    }
}


@Composable
private fun TitleWithSubtitle(header: AnnotatedHeader, titleTextStyle: TextStyle) {
    Text(
        text = header.title,
        style = titleTextStyle,
        color = MaterialTheme.colorScheme.onBackground.animate(),
        modifier = Modifier
            .padding(start = 16.dp)
            .fillMaxWidth()
    )

    if (header.subtitle.isNotBlank()) {
        Text(
            text = header.subtitle,
            color = DarkerGray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 4.dp)
                .fillMaxWidth()
        )
    }
}






object ModalSheetItems {
    @Composable
    fun ColumnScope.ImageTextItem(
        text: String,
        @DrawableRes image: Int?,
        modifier: Modifier = Modifier,
        imageModifier: Modifier = Modifier,
        imageTintColor: Color? = null,
        selected: Boolean = false,
        imageAlignment: Alignment.Horizontal = Alignment.Start,
        padding: Dp = 16.dp,
        onClick: (text: String) -> Unit
    ) = BottomSheetItem(text, imageAlignment, onClick, modifier, selected, padding) { horizontalPadding ->
        if (image != null) {
            Image(
                painter = painterResource(image),
                contentDescription = text,
                colorFilter = if (imageTintColor != null) ColorFilter.tint(imageTintColor) else null,
                contentScale = ContentScale.FillBounds,
                modifier = imageModifier
                    .padding(horizontal = horizontalPadding)
                    .scale(1.17f)
                    .clip(CircleShape.copy(CornerSize(40)))
            )
        }
    }

    @Composable
    fun ColumnScope.IconTextItem(
        text: String,
        icon: ImageVector?,
        iconTintColor: Color,
        modifier: Modifier = Modifier,
        iconModifier: Modifier = Modifier,
        selected: Boolean = false,
        iconAlignment: Alignment.Horizontal = Alignment.Start,
        padding: Dp = 16.dp,
        onClick: (text: String) -> Unit
    ) = BottomSheetItem(text, iconAlignment, onClick, modifier, selected, padding) { horizontalPadding ->
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconTintColor,
                modifier = iconModifier
                    .padding(horizontal = horizontalPadding)
                    .clip(CircleShape.copy(CornerSize(40)))
            )
        }
    }

    @Composable
    fun ColumnScope.ScreenTypeItem(
        screen: AppScreens,
        selected: Boolean = false,
        onClick: (text: String) -> Unit
    ) = IconTextItem(
        text = stringResource(screen.titleRes),
        icon = screen.icon,
        iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
        iconModifier = Modifier.scale(1.17f),
        selected = selected,
        onClick = onClick
    )

    @Composable
    fun ColumnScope.EditItem(
        text: String,
        selected: Boolean = false,
        onClick: (text: String) -> Unit
    ) = IconTextItem(
        text = text,
        icon = Icons.Outlined.Edit,
        iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
        iconModifier = Modifier.scale(1.17f),
        selected = selected,
        onClick = onClick
    )

    @Composable
    fun ColumnScope.CopyItem(
        text: String,
        selected: Boolean = false,
        onClick: (text: String) -> Unit
    ) = IconTextItem(
        text = text,
        icon = Icons.Outlined.ContentCopy,
        iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
        iconModifier = Modifier.scale(1.17f),
        selected = selected,
        onClick = onClick
    )

    @Composable
    fun ColumnScope.DeleteItem(
        text: String,
        selected: Boolean = false,
        onClick: (text: String) -> Unit
    ) = IconTextItem(
        text = text,
        icon = Icons.Outlined.Delete,
        iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
        iconModifier = Modifier.scale(1.17f),
        selected = selected,
        onClick = onClick
    )


    @Composable
    private fun ColumnScope.BottomSheetItem(
        text: String,
        imageAlignment: Alignment.Horizontal,
        onClick: (text: String) -> Unit,
        modifier: Modifier = Modifier,
        selected: Boolean = false,
        padding: Dp = 8.dp,
        image: @Composable (horizontalPadding: Dp) -> Unit
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable { onClick(text) }
                .background(
                    color = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                            .copy(alpha = 0.3f)
                            .animate()
                    } else {
                        Color.Transparent
                    }
                )
                .then(modifier)
                .align(Alignment.Start)
                .padding(vertical = padding)
                .fillMaxWidth(),
        ) {

            if (imageAlignment != Alignment.End) image(padding)
            else Spacer(modifier = Modifier.width(padding))

            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground.animate(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(2f)
            )

            if (imageAlignment == Alignment.End) image(padding)
        }
    }
}




@Composable
fun RowScope.ToolbarAction(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    onClick: () -> Unit
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




@Composable
internal fun FeedbackSheet(
    context: Context,
    beautifulDesign: Boolean,
    closeBottomSheet: () -> Unit,
) {
    val openAddress = { address: String ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        context.startActivity(intent)
        closeBottomSheet()
    }

    BottomSheetContent(
        header = Header(
            title = stringResource(R.string.feedback),
            beautifulDesign = beautifulDesign
        )
    ) {
        ImageTextItem(
            text = stringResource(R.string.telegram),
            image = R.drawable.telegram_icon,
            imageTintColor = MaterialTheme.colorScheme.secondary.animate(),
            imageModifier = Modifier.scale(1.7f)
        ) {
            openAddress("https://t.me/cepetroff")
        }

        ImageTextItem(
            text = stringResource(R.string.vk),
            image = R.drawable.vk_icon,
            imageTintColor = MaterialTheme.colorScheme.secondary.animate(),
            imageModifier = Modifier.scale(1.4f)
        ) {
            openAddress("https://vk.com/cepetroff")
        }

        IconTextItem(
            text = stringResource(R.string.email),
            icon = Icons.Default.AlternateEmail,
            iconTintColor = MaterialTheme.colorScheme.secondary.animate(),
            iconModifier = Modifier.scale(0.9f)
        ) {
            openAddress("mailto:petrovsd2002@gmail.com")
        }
    }
}




@ExperimentalMaterial3Api
@Preview
@Composable
private fun ModalNavigationContentPreview() {
    PasswordManagerTheme(
        isDarkTheme = false
    ) {
        ModalNavigationDrawerContent {
            ScreenTypeItem(AppScreens.Notes, selected = true) {}
            ScreenTypeItem(AppScreens.Settings) {}
        }
    }
}



@Preview
@Composable
private fun BottomSheetContentPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        BottomSheetContent(
            title = "Wrecked",
            subtitle = "Imagine Dragons",
            beautifulDesign = true
        ) {
            IconTextItem(
                text = "Добавить аккаунт",
                icon = Icons.Outlined.AccountCircle,
                iconTintColor = MaterialTheme.colorScheme.primary.animate(),
            ) {}

            IconTextItem(
                text = "Bank Account",
                icon = Icons.Outlined.CreditCard,
                iconTintColor = MaterialTheme.colorScheme.primary.animate(),
                iconAlignment = Alignment.End,
                selected = true
            ) {}
        }
    }
}