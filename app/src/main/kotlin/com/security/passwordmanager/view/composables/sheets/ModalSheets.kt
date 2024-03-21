package com.security.passwordmanager.view.composables.sheets

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.model.Header
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.IconTextItem
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.view.navigation.AppScreen
import com.security.passwordmanager.view.navigation.HomeScreen
import com.security.passwordmanager.view.navigation.NavigationDrawerItem
import com.security.passwordmanager.view.navigation.SettingsScreen
import com.security.passwordmanager.view.theme.DarkerGray
import com.security.passwordmanager.view.theme.PasswordManagerTheme
import com.security.passwordmanager.view.theme.bottomSheetBorderThickness
import kotlinx.coroutines.launch

@Composable
internal fun ModalNavigationDrawerContent(
    appName: String,
    appVersion: String,
    items: @Composable ColumnScope.() -> Unit
) {
    val configuration = LocalConfiguration.current

    ModalDrawerSheet(
        drawerShape = ModalSheetDefaults.NavigationDrawerShape,
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
            appName = appName,
            appVersion = appVersion,
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.statusBars.only(WindowInsetsSides.Top)
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        items()
    }
}


@Composable
private fun NavHeader(
    appName: String,
    appVersion: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .background(
                Brush.linearGradient(
                    0f to MaterialTheme.colorScheme.primaryContainer.animate(),
                    0.5f to MaterialTheme.colorScheme.primary.animate(),
                    1f to MaterialTheme.colorScheme.secondary.animate(),
                )
            )
            .then(modifier)
            .padding(vertical = 16.dp, horizontal = 17.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = "Navigation header",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = appName,
            color = MaterialTheme.colorScheme.onPrimary.animate(),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = appVersion,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary.animate().copy(alpha = 0.8f)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheet(
    onClose: () -> Unit,
    state: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: CharSequence = "",
    subtitle: CharSequence = "",
    beautifulDesign: Boolean = false,
    contentWindowInsets: WindowInsets = ModalSheetDefaults.contentWindowInsets,
    bodyContent: @Composable (ColumnScope.() -> Unit)
) {
    ModalBottomSheet(
        onClose = onClose,
        state = state,
        header = Header(title, subtitle, beautifulDesign),
        contentWindowInsets = contentWindowInsets,
        bodyContent = bodyContent
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheet(
    onClose: () -> Unit,
    state: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    header: Header = Header(),
    contentWindowInsets: WindowInsets = ModalSheetDefaults.contentWindowInsets,
    bodyContent: @Composable (ColumnScope.() -> Unit)
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = {
            scope.launch {
                state.hide()
                onClose()
            }
        },
        shape = ModalSheetDefaults.BottomSheetShape,
        containerColor = MaterialTheme.colorScheme.surface.animate(),
        contentColor = MaterialTheme.colorScheme.onSurface.animate(),
        dragHandle = when {
            header.isEmpty() -> {
                { BottomSheetDefaults.DragHandle() }
            }
            else -> null
        },
        windowInsets = WindowInsets(0),
        tonalElevation = 40.dp
    ) {
        BottomSheetContent(
            header = header,
            contentWindowInsets = contentWindowInsets,
            bodyContent = bodyContent
        )
    }
}


@Composable
private fun ColumnScope.BottomSheetContent(
    title: CharSequence = "",
    subtitle: CharSequence = "",
    beautifulDesign: Boolean = false,
    contentWindowInsets: WindowInsets =
        WindowInsets.tappableElement.only(WindowInsetsSides.Bottom),
    bodyContent: @Composable (ColumnScope.() -> Unit)
) = BottomSheetContent(
    header = Header(title, subtitle, beautifulDesign),
    contentWindowInsets = contentWindowInsets,
    bodyContent = bodyContent
)


@Composable
private fun ColumnScope.BottomSheetContent(
    header: Header = Header(),
    contentWindowInsets: WindowInsets = ModalSheetDefaults.contentWindowInsets,
    bodyContent: @Composable (ColumnScope.() -> Unit),
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val initialTopPadding = contentWindowInsets
        .asPaddingValues()
        .calculateTopPadding()

    var topInsetsPadding by remember { mutableStateOf(initialTopPadding) }

    val scrollModifier = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> Modifier.verticalScroll(rememberScrollState())
        else -> Modifier
    }

    Column(
        modifier = Modifier
            .then(scrollModifier)
            .align(Alignment.CenterHorizontally)
            .padding(top = topInsetsPadding)
            .windowInsetsPadding(contentWindowInsets.only(WindowInsetsSides.Bottom))
            .onGloballyPositioned {
                with(density) {
                    topInsetsPadding = when {
                        it.size.height.toDp() >= configuration.screenHeightDp.dp / 2 -> initialTopPadding
                        else -> 16.dp
                    }
                }
            }
    ) {
        if (!header.isEmpty())  {
            BottomSheetHeader(header)
        }
        bodyContent()
    }
}


@Composable
private fun ColumnScope.BottomSheetHeader(header: Header) {
    Title(
        header = header,
        modifier = Modifier
            .align(Alignment.Start)
            .padding(start = 16.dp)
            .fillMaxWidth()
    )

    if (header.subtitle.isNotBlank()) {
        Subtitle(
            header = header,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp, bottom = 4.dp)
                .fillMaxWidth()
        )
    }

    HorizontalDivider(
        color = DarkerGray,
        modifier = Modifier.padding(top = 16.dp)
    )
}


@Composable
private fun Title(header: Header, modifier: Modifier = Modifier) {
    val style = when {
        header.beautifulDesign -> MaterialTheme.typography.headlineLarge
        else -> MaterialTheme.typography.titleMedium
    }
    val color = MaterialTheme.colorScheme.onBackground.animate()

    when (header.title) {
        is String -> Text(
            text = header.title,
            style = style,
            color = color,
            modifier = modifier
        )

        is AnnotatedString -> Text(
            text = header.title,
            style = style,
            color = color,
            modifier = modifier
        )
    }
}


@Composable
private fun Subtitle(header: Header, modifier: Modifier = Modifier) {
    val color = DarkerGray
    val style = MaterialTheme.typography.titleSmall

    when (header.subtitle) {
        is String -> Text(
            text = header.subtitle,
            color = color,
            style = style,
            modifier = modifier
        )

        is AnnotatedString -> Text(
            text = header.subtitle,
            color = color,
            style = style,
            modifier = modifier
        )
    }
}


object ModalSheetItems {
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
    fun <S> ColumnScope.ScreenTypeItem(
        screen: S,
        modifier: Modifier = Modifier,
        selected: Boolean = false,
        iconTintColor: Color = MaterialTheme.colorScheme.secondary,
        onClick: (screen: S) -> Unit
    ) where S : AppScreen, S : NavigationDrawerItem {
        if (screen.titleRes == null) return
        IconTextItem(
            text = screen.title(),
            icon = screen.icon,
            iconTintColor = iconTintColor.animate(),
            modifier = modifier,
            iconModifier = Modifier.scale(1.17f),
            selected = selected,
            onClick = { onClick(screen) }
        )
    }


    @Composable
    fun ColumnScope.ImageTextItem(
        text: String,
        @DrawableRes image: Int,
        modifier: Modifier = Modifier,
        imageModifier: Modifier = Modifier,
        imageTintColor: Color? = null,
        selected: Boolean = false,
        imageAlignment: Alignment.Horizontal = Alignment.Start,
        onClick: (text: String) -> Unit
    ) {
        val imageContent = @Composable {
            Image(
                painter = painterResource(image),
                contentDescription = text,
                colorFilter = imageTintColor?.let { ColorFilter.tint(it) },
                contentScale = ContentScale.Fit,
                modifier = imageModifier.clip(RoundedCornerShape(20))
            )
        }

        BottomSheetItem(
            text = text,
            onClick = onClick,
            modifier = modifier,
            selected = selected,
            leadingIcon = {
                if (imageAlignment == Alignment.Start) {
                    imageContent()
                }
            },
            trailingIcon = {
                if (imageAlignment == Alignment.End) {
                    imageContent()
                }
            },
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
        onClick: (text: String) -> Unit
    ) {
        val iconContent = @Composable {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconTintColor,
                modifier = iconModifier.clip(RoundedCornerShape(40))
            )
        }

        BottomSheetItem(
            text = text,
            onClick = onClick,
            modifier = modifier,
            selected = selected,
            leadingIcon = {
                if (iconAlignment == Alignment.Start) {
                    iconContent()
                }
            },
            trailingIcon = {
                if (iconAlignment == Alignment.End) {
                    iconContent()
                }
            }
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
        modifier = modifier,
        selected = selected,
        onClick = onClick,
    )


    @Composable
    private fun ColumnScope.BottomSheetItem(
        text: String,
        onClick: (text: String) -> Unit,
        modifier: Modifier = Modifier,
        selected: Boolean = false,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null
    ) {
        NavigationDrawerItem(
            label = {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(2f)
                )
            },
            icon = leadingIcon,
            badge = trailingIcon,
            selected = selected,
            onClick = { onClick(text) },
            shape = MaterialTheme.shapes.small,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Transparent,
                unselectedContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .background(
                    brush = when {
                        selected -> Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondary,
                            )
                        )

                        else -> SolidColor(Color.Transparent)
                    },
                    shape = MaterialTheme.shapes.small,
                    alpha = .25f
                )
                .then(modifier)
                .align(Alignment.Start)
                .fillMaxWidth()
        )
    }
}


object ModalSheetDefaults {
    private val shapeCornerRadius = 18.dp

    val AnimationSpec = tween<Float>(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )

    val BottomSheetShape = RoundedCornerShape(
        topStart = shapeCornerRadius,
        topEnd = shapeCornerRadius
    )
    val NavigationDrawerShape = RoundedCornerShape(
        topEnd = shapeCornerRadius,
        bottomEnd = shapeCornerRadius
    )

    @OptIn(ExperimentalLayoutApi::class)
    val contentWindowInsets: WindowInsets
        @Composable
        get() = WindowInsets.systemBarsIgnoringVisibility


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


@Preview
@Composable
private fun ModalNavigationContentPreview() {
    PasswordManagerTheme(
        isDarkTheme = false
    ) {
        ModalNavigationDrawerContent(
            appName = "Password Manager",
            appVersion = "1.1.1"
        ) {
            ScreenTypeItem(HomeScreen.AllNotes, selected = true) {}
            ScreenTypeItem(SettingsScreen) {}
        }
    }
}


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
                    text = "Add account",
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