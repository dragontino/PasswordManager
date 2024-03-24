package com.security.passwordmanager.view.screens.datascreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.BankCard
import com.security.passwordmanager.domain.model.entity.Bank
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.Website
import com.security.passwordmanager.model.Header
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.util.colorize
import com.security.passwordmanager.util.convertToColor
import com.security.passwordmanager.view.composables.DataTextField
import com.security.passwordmanager.view.composables.DataTextFieldDefaults
import com.security.passwordmanager.view.composables.TrailingActions.CopyIconButton
import com.security.passwordmanager.view.composables.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.view.composables.dialogs.ConfirmDeletionDialog
import com.security.passwordmanager.view.composables.dialogs.DialogType
import com.security.passwordmanager.view.composables.managment.ScreenEvents
import com.security.passwordmanager.view.composables.scaffold.toolbar.AppBarState
import com.security.passwordmanager.view.composables.sheets.ModalBottomSheet
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.CopyItem
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.DeleteItem
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.EditItem
import com.security.passwordmanager.view.navigation.EditScreen
import com.security.passwordmanager.view.theme.DarkerGray
import com.security.passwordmanager.view.theme.NotesScreenAnimations
import com.security.passwordmanager.view.theme.Orange
import com.security.passwordmanager.view.theme.PacificFont
import com.security.passwordmanager.view.theme.PasswordManagerTheme
import com.security.passwordmanager.viewmodel.EntityViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
internal fun <D : DatabaseEntity> EntityScreen(
    contentState: LazyListState,
    topBarState: AppBarState,
    viewModel: EntityViewModel<D>,
    navigateTo: (route: String) -> Unit,
    popBackStack: () -> Unit,
    showSnackbar: (message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.openedItemId.value }.collect { itemId ->
            if (itemId != null) {
                delay(50)
                contentState.animateScrollToItem(
                    index = viewModel.entities.value.keys
                        .indexOfFirst { it == itemId }
                        .coerceAtLeast(0),
                    scrollOffset = topBarState.offsetTopLimit.roundToInt()
                )
            }
        }
    }


    var dialogType: DialogType? by rememberSaveable { mutableStateOf(null) }
    LaunchedEffect(key1 = true) {
        viewModel.eventsFlow.collect { event ->
            when (event) {
                is ScreenEvents.OpenDialog -> dialogType = event.type
                ScreenEvents.CloseDialog -> dialogType = null
                is ScreenEvents.Navigate -> (event.args as? String)
                    ?.let(navigateTo)
                    ?: popBackStack()
                is ScreenEvents.ShowSnackbar -> showSnackbar(event.message)
            }
        }
    }

    dialogType?.Dialog()


    LazyColumn(
        state = contentState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        items(
            items = viewModel.entities.value.toList(),
            key = { it.first },
            contentType = { "dataItem" }
        ) { (id, data) ->
            Data(
                id = id,
                data = data,
                viewModel = viewModel,
                showAll = viewModel.openedItemId.value == id
            ) {
                viewModel.openedItemId.value = when (viewModel.openedItemId.value) {
                    id -> null
                    else -> id
                }
            }
        }

        item(contentType = "Spacer") {
            Spacer(
                modifier = Modifier
                    .windowInsetsPadding(
                        WindowInsets.tappableElement.only(WindowInsetsSides.Bottom)
                    )
                    .height(50.dp)
                    .fillMaxWidth(),
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <D : DatabaseEntity> Data(
    id: String,
    data: DatabaseEntity,
    viewModel: EntityViewModel<D>,
    showAll: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val header: Header
    val logoUrl: String?

    when (data) {
        is Website -> {
            header = Header(
                title = data.name,
                subtitle = data.address
            )
            logoUrl = data.logoUrl
        }

        is Bank -> {
            header = Header(
                title = data.name,
                subtitle = data.cards.values.firstOrNull()?.number ?: ""
            )
            logoUrl = null
        }
    }

    val bottomSheetContent = rememberSaveable { mutableStateOf<@Composable (() -> Unit)?>(null) }

    bottomSheetContent.value?.invoke()

    val borderStroke = when {
        showAll -> BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.tertiary,
                ).map { it.animate() }
            )
        )

        else -> BorderStroke(0.dp, Color.Transparent)
    }


    OutlinedCard(
        shape = MaterialTheme.shapes.small,
        border = borderStroke,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.secondary
                .copy(alpha = .07f)
                .animate()
        ),
        onClick = onClick,
        modifier = modifier
    ) {
        DataHeader(
            header = header,
            logoUrl = logoUrl,
            showAllContent = showAll,
            showDetails = {
                bottomSheetContent.value = {
                    ModalBottomSheet(
                        header = header,
                        onClose = { bottomSheetContent.value = null }
                    ) {
                        EditItem(text = stringResource(R.string.edit)) {
                            viewModel.navigateTo(
                                when (data) {
                                    is Website -> EditScreen.Website.createUrl(id)
                                    is Bank -> EditScreen.BankEdit.createUrl(id)
                                }
                            )
                            bottomSheetContent.value = null
                        }
                        CopyItem(text = stringResource(R.string.copy_info)) {
                            viewModel.copyData(
                                data = data,
                                context = context,
                                clipboardManager = clipboardManager
                            )
                            bottomSheetContent.value = null
                        }

                        DeleteItem(text = stringResource(R.string.delete_data)) {
                            val dataName = when (data) {
                                is Website -> data.name
                                is Bank -> data.name
                            }

                            bottomSheetContent.value = null
                            viewModel.openDialog(
                                type = ConfirmDeletionDialog(
                                    text = context.getString(
                                        R.string.deletion_data_confirmation,
                                        dataName
                                    ),
                                    onDismiss = viewModel::closeDialog,
                                    onConfirm = {
                                        viewModel.closeDialog()
                                        viewModel.deleteEntity(
                                            id = id,
                                            entityType = data.type,
                                            success = {
                                                viewModel.showSnackbar(
                                                    context.getString(
                                                        R.string.deletion_data_success,
                                                        dataName
                                                    )
                                                )
                                            }
                                        )
                                    }
                                )
                            )
                        }
                    }
                }
            },
            loadIcons = viewModel.settings.value.loadIcons,
        )
        val iterableItems = when (data) {
            is Bank -> data.cards.toSortedMap().values
            is Website -> data.accounts.toSortedMap().values
        }

        iterableItems.forEachIndexed { position, item ->
            // TODO: 07.09.2022 подумать над тем, как будут размещатся элементы bank Card

            AnimatedVisibility(
                visible = showAll,
                enter = NotesScreenAnimations.ListItemAnimation.enter,
                exit = NotesScreenAnimations.ListItemAnimation.exit
            ) {
                when (item) {
                    is Account -> Account(
                        account = item,
                        copyText = {
                            viewModel.copyText(
                                text = it,
                                context = context,
                                clipboardManager = clipboardManager
                            )
                        },
                        openUrl = {
                            val address = (data as Website).address
                            viewModel
                                .createIntentForUrl(address)
                                ?.let { context.startActivity(it) }
                                ?: viewModel.showSnackbar(
                                    context.getString(
                                        R.string.invalid_address,
                                        address
                                    )
                                )
                        }
                    ) { title ->
                        bottomSheetContent.value = {
                            ModalBottomSheet(
                                onClose = { bottomSheetContent.value = null },
                                title = title,
                                beautifulDesign = viewModel.settings.value.beautifulFont
                            ) {
                                EditItem(text = stringResource(R.string.edit)) {
                                    viewModel.navigateTo(
                                        EditScreen.Website.createUrl(
                                            id = id,
                                            startPosition = position
                                        )
                                    )
                                    bottomSheetContent.value = null
                                }
                            }
                        }
                    }

                    is BankCard -> BankCard(bankCard = item)
                }
            }
        }
    }
}


@Composable
private fun DataHeader(
    header: Header,
    modifier: Modifier = Modifier,
    logoUrl: String? = null,
    showAllContent: Boolean = false,
    showDetails: () -> Unit = {},
    loadIcons: Boolean = true
) {
    val arrowRotation = remember { Animatable(0f) }

    LaunchedEffect(showAllContent) {
        arrowRotation.animateTo(
            targetValue = if (showAllContent) 90f else 0f,
            animationSpec = tween(
                durationMillis = with(NotesScreenAnimations.ListItemAnimation) {
                    if (showAllContent) enterTimeMillis else exitTimeMillis
                },
                easing = LinearEasing
            )
        )
    }


    ListItem(
        headlineContent = {
            val style = when {
                header.beautifulDesign -> MaterialTheme.typography.headlineLarge
                else -> MaterialTheme.typography.bodyLarge
            }
            when (header.title) {
                is String -> Text(text = header.title, style = style)
                is AnnotatedString -> Text(text = header.title, style = style)
            }
        },
        supportingContent = when {
            header.subtitle.isBlank() -> null
            else -> {
                {
                    when (header.subtitle) {
                        is String -> Text(
                            text = header.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        is AnnotatedString -> Text(
                            text = header.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = "More info",
                    tint = LocalContentColor.current,
                    modifier = Modifier
                        .rotate(arrowRotation.value)
                        .height(20.dp)
                )


                var imageSize by remember { mutableStateOf(40.dp) }

                if (loadIcons) {
                    SubcomposeAsyncImage(
                        model = logoUrl,
                        contentDescription = "logo",
                        loading = {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = "logo",
                                tint = DarkerGray
                            )
                        },
                        error = {
                            ErrorLogo(
                                title = header.title.toString(),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        },
                        onLoading = { imageSize = 40.dp },
                        onError = { imageSize = 50.dp },
                        onSuccess = { imageSize = 40.dp },
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.Medium,
                        modifier = Modifier.size(imageSize)
                    )
                }
            }
        },
        trailingContent = {
            IconButton(
                onClick = showDetails,
                modifier = Modifier
                    .clip(CircleShape),
            ) {
                Icon(
                    Icons.Outlined.MoreVert,
                    contentDescription = "show details",
                    modifier = Modifier.scale(1.5f)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            headlineColor = MaterialTheme.colorScheme.onBackground.animate(),
            disabledHeadlineColor = MaterialTheme.colorScheme.onBackground
                .copy(alpha = .4f).animate(),
            supportingColor = DarkerGray,
            trailingIconColor = MaterialTheme.colorScheme.onBackground.animate(),
            disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground
                .copy(alpha = .4f).animate(),
            leadingIconColor = MaterialTheme.colorScheme.primary.animate(),
        ),
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    )
}




@Composable
private fun ErrorLogo(title: String, modifier: Modifier = Modifier) {
    val boxColor = title.convertToColor()
    val textColor = when {
        boxColor.luminance() < 0.5f -> Color.White
        else -> Color.Black
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .border(
                width = 0.2.dp,
                color = textColor,
                shape = CircleShape
            )
            .background(boxColor)
            .then(modifier)
    ) {
        Text(
            text = title.first().uppercase(),
            fontFamily = FontFamily(PacificFont),
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier.padding(6.dp)
        )
    }
}




@Composable
private fun Account(
    account: Account,
    copyText: (String) -> Unit,
    openUrl: () -> Unit,
    onClick: (title: String) -> Unit
) {
    val title = account.name.ifBlank { account.login }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface.animate(),
        contentColor = MaterialTheme.colorScheme.onBackground.animate(),
        shadowElevation = 8.dp,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick(title) }
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (account.name.isNotBlank()) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = account.name,
                        color = DarkerGray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = DarkerGray)
                }
            }

            DataTextField(
                text = account.login,
                heading = stringResource(R.string.login),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                )
            ) {
                CopyIconButton {
                    copyText(account.login)
                }
            }

            DataTextField(
                text = account.password.colorize { char ->
                    when {
                        char.isDigit() -> Color.Blue
                        char in "!@#\$%^&*—_+=;:.,/?\'\"\\|`~()[]{}<>" -> Orange
                        else -> Color.Unspecified
                    }
                },
                heading = stringResource(R.string.password),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
                keyboardType = KeyboardType.Password,
                visualTransformation = DataTextFieldDefaults
                    .passwordVisualTransformation(isPasswordVisible)
            ) {
                VisibilityIconButton(
                    visible = isPasswordVisible,
                ) { isPasswordVisible = it }
                Spacer(modifier = Modifier.width(8.dp))
                CopyIconButton(modifier = Modifier.padding(end = 8.dp)) {
                    copyText(account.password)
                }
            }

            if (account.comment.isNotBlank()) {
                DataTextField(
                    text = account.comment,
                    heading = stringResource(R.string.comment)
                ) {
                    CopyIconButton {
                        copyText(account.comment)
                    }
                }
            }

            Button(
                onClick = openUrl,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.animate(),
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.button_open_url),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Composable
private fun BankCard(bankCard: BankCard) {
    ElevatedCard {
        bankCard.paymentSystem
    }
}


@Preview
@Composable
private fun AccountPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        Column {
            Account(
                account = Account(
                    name = "Imagine Dragons",
                    login = "petrovsd2002@yandex.ru",
                    password = "qwerty123",
                    comment = "Chill",
                ),
                {}, {},
            ) {}
        }
    }
}


@Preview
@Composable
private fun BankCardPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        BankCard(
            bankCard = BankCard(
                number = "1234 5678 9876 5432",
                pin = "0000",
            ),
        )
    }
}


@Preview
@Composable
private fun DataHeaderPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        var showAll by remember { mutableStateOf(false) }

        DataHeader(
            header = Header(
                title = "Imagine Dragons",
                subtitle = "Ragged Insomnia",
            ),
            logoUrl = "https://www.spotify.com/apple-touch-icon.png",
            modifier = Modifier.clickable { showAll = !showAll }
        )
    }
}