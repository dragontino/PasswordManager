package com.security.passwordmanager.presentation.view.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
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




@ExperimentalMaterial3Api
@Composable
fun ColumnScope.BottomSheetContent(
    title: String = "",
    subtitle: String = "",
    beautifulDesign: Boolean = false,
    bodyContent: @Composable (ColumnScope.() -> Unit)
) = BottomSheetContent(
    header = Header(title, subtitle, beautifulDesign),
    bodyContent = bodyContent
)




@ExperimentalMaterial3Api
@Composable
fun ColumnScope.BottomSheetContent(
    title: AnnotatedString = AnnotatedString(""),
    subtitle: AnnotatedString = AnnotatedString(""),
    beautifulDesign: Boolean = false,
    bodyContent: @Composable (ColumnScope.() -> Unit)
) = BottomSheetContent(
    header = AnnotatedHeader(title, subtitle, beautifulDesign),
    bodyContent = bodyContent
)




@ExperimentalMaterial3Api
@Composable
fun ColumnScope.BottomSheetContent(
    header: HeadingInterface = Header(),
    bodyContent: @Composable (ColumnScope.() -> Unit),
) {
    if (header.isEmpty()) {
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            BottomSheetDefaults.DragHandle()
        }
    } else {
        BottomSheetHeader(header)
        Spacer(modifier = Modifier.size(8.dp))
    }

    bodyContent()
    Spacer(modifier = Modifier.size(8.dp))
}




@Composable
private fun ColumnScope.BottomSheetHeader(header: HeadingInterface) {
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
        @DrawableRes image: Int,
        modifier: Modifier = Modifier,
        imageModifier: Modifier = Modifier,
        imageTintColor: Color? = null,
        selected: Boolean = false,
        imageAlignment: Alignment.Horizontal = Alignment.Start,
        imageSpace: Dp = 8.dp,
        onClick: (text: String) -> Unit
    ) = BottomSheetItem(text, imageAlignment, onClick, modifier, selected, imageSpace) { horizontalPadding ->
        Image(
            painter = painterResource(image),
            contentDescription = text,
            colorFilter = if (imageTintColor != null) ColorFilter.tint(imageTintColor) else null,
            contentScale = ContentScale.Fit,
            modifier = imageModifier
                .padding(horizontal = horizontalPadding)
                .clip(RoundedCornerShape(20))
        )
    }


    @Composable
    fun ColumnScope.IconTextItem(
        text: String,
        icon: ImageVector,
        iconTintColor: Color,
        modifier: Modifier = Modifier,
        iconModifier: Modifier = Modifier,
        selected: Boolean = false,
        iconAlignment: Alignment.Horizontal = Alignment.Start,
        iconSpace: Dp = 8.dp,
        onClick: (text: String) -> Unit
    ) = BottomSheetItem(text, iconAlignment, onClick, modifier, selected, iconSpace) { horizontalPadding ->
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTintColor,
            modifier = iconModifier
                .padding(horizontal = horizontalPadding)
                .clip(CircleShape.copy(CornerSize(40)))
        )
    }


    @Composable
    fun ColumnScope.TextItem(
        text: String,
        modifier: Modifier = Modifier,
        selected: Boolean = false,
        onClick: (text: String) -> Unit
    ) = BottomSheetItem(
        text = text,
        imageAlignment = Alignment.Start,
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        image = { Spacer(modifier = Modifier.padding(horizontal = it + it)) }
    )


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
        spaceAfterImage: Dp = 8.dp,
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
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
        ) {

            if (imageAlignment != Alignment.End) image(spaceAfterImage)
            else Spacer(modifier = Modifier.width(spaceAfterImage))

            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground.animate(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(2f)
            )

            if (imageAlignment == Alignment.End) image(spaceAfterImage)
        }
    }
}




object ModalSheetDefaults {
    val AnimationSpec = tween<Float>(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )

    val Shape = RoundedCornerShape(18.dp)


    @Composable
    fun borderStroke(
        firstColor: Color = MaterialTheme.colorScheme.onBackground,
        secondColor: Color = MaterialTheme.colorScheme.surface,
        width: Dp = bottomSheetBorderThickness
    ) = BorderStroke(
        width = width,
        brush = Brush.verticalGradient(
            0.1f to firstColor.animate(),
            0.5f to secondColor.animate()
        )
    )
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



@ExperimentalMaterial3Api
@Preview
@Composable
private fun BottomSheetContentPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        Column {
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
}