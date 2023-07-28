package com.security.passwordmanager.presentation.view.screens.datascreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.convertToColor
import com.security.passwordmanager.data.model.dao.usersdata.Bank
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.model.dao.usersdata.Website
import com.security.passwordmanager.data.model.dao.usersdatachild.Account
import com.security.passwordmanager.data.model.dao.usersdatachild.BankCard
import com.security.passwordmanager.presentation.view.composables.DataTextField
import com.security.passwordmanager.presentation.view.composables.DataTextFieldDefaults
import com.security.passwordmanager.presentation.view.composables.DeleteDialog
import com.security.passwordmanager.presentation.view.composables.ScrollableScaffoldState
import com.security.passwordmanager.presentation.view.composables.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.composables.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.navigation.AppScreens
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.Header
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.CopyItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.DeleteItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.EditItem
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.NotesScreenAnimations
import com.security.passwordmanager.presentation.view.theme.PacificFont
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun <D : UsersData> DataScreen(
    scaffoldState: ScrollableScaffoldState<LazyListState>,
    viewModel: DataViewModel<D>,
    showBottomSheet: () -> Unit,
    hideBottomSheet: () -> Unit,
    useBeautifulFont: Boolean,
    loadIcons: Boolean,
    navigateTo: (route: String) -> Unit,
    showSnackbar: (message: String) -> Unit,
    modifier: Modifier = Modifier
) {

    val topBarHeightPx = scaffoldState.toolbarState.heightPx


    LaunchedEffect(viewModel.openedItemId) {
        if (viewModel.openedItemId != null) {
            delay(50)
            scaffoldState.contentState.animateScrollToItem(
                index = viewModel.dataList.keys
                    .indexOfFirst { it == viewModel.openedItemId }
                    .coerceAtLeast(0),
                scrollOffset = -topBarHeightPx.roundToInt()
            )
        }
    }


    if (viewModel.showDialog) {
        viewModel.dialogContent()
    }


    LazyColumn(
        state = scaffoldState.contentState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        items(
            items = viewModel.dataList.entries.sortedBy { it.value },
            key = { it.key },
            contentType = { "dataItem" }
        ) { (id, data) ->
            Data(
                id = id,
                data = data,
                viewModel = viewModel,
                showAll = { viewModel.openedItemId == id },
                showSnackbar = showSnackbar,
                showBottomSheet = showBottomSheet,
                hideBottomSheet = hideBottomSheet,
                navigateTo = navigateTo,
                useBeautifulFont = useBeautifulFont,
                loadIcons = loadIcons,
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = with(NotesScreenAnimations.ListItemAnimation) {
                                if (viewModel.openedItemId == id) enterTimeMillis / 16 else exitTimeMillis / 16
                            },
                            easing = FastOutSlowInEasing
                        )
                    )
                    .animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = with(NotesScreenAnimations.ListItemAnimation) {
                                if (viewModel.openedItemId == id) enterTimeMillis / 16 else exitTimeMillis / 16
                            },
                            easing = FastOutSlowInEasing
                        )
                    )
            ) {
                viewModel.openedItemId = when (viewModel.openedItemId) {
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
private fun <D : UsersData> Data(
    id: String,
    data: UsersData,
    viewModel: DataViewModel<D>,
    showAll: () -> Boolean,
    navigateTo: (route: String) -> Unit,
    showSnackbar: (message: String) -> Unit,
    showBottomSheet: () -> Unit,
    hideBottomSheet: () -> Unit,
    useBeautifulFont: Boolean,
    loadIcons: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
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

    val dataBottomSheetContent: @Composable (ColumnScope.() -> Unit) = {
        BottomSheetContent(header) {
            EditItem(text = stringResource(R.string.edit)) {
                navigateTo(
                    when (data) {
                        is Website -> AppScreens.WebsiteEdit.createUrl(id)
                        is Bank -> AppScreens.BankEdit.createUrl(id)
                    }
                )
                hideBottomSheet()
            }
            CopyItem(text = stringResource(R.string.copy_info)) {
                viewModel.copyData(context, data, result = showSnackbar)
                hideBottomSheet()
            }

            DeleteItem(text = stringResource(R.string.delete_data)) {
                val dataName = when (data) {
                    is Website -> data.name
                    is Bank -> data.name
                }

                hideBottomSheet()
                viewModel.openDialog {
                    DeleteDialog(
                        text = stringResource(
                            R.string.deletion_data_confirmation,
                            dataName
                        ),
                        onDismiss = viewModel::closeDialog,
                        onConfirm = {
                            viewModel.closeDialog()
                            viewModel.deleteData(id, dataType = data.type) { success ->
                                when {
                                    success -> showSnackbar(
                                        context.getString(R.string.deletion_data_success, dataName)
                                    )
                                    else -> showSnackbar(context.getString(R.string.cannot_delete_data))
                                }
                            }
                        }
                    )
                }
            }
        }
    }


    val borderStroke = when {
        showAll() -> BorderStroke(
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
            showAllContent = showAll(),
            showDetails = {
                viewModel.bottomSheetContent = dataBottomSheetContent
                showBottomSheet()
            },
            loadIcons = loadIcons,
        )
        val iterableItems = when (data) {
            is Bank -> data.cards.toSortedMap().values
            is Website -> data.accounts.toSortedMap().values
        }

        iterableItems.forEachIndexed { position, item ->
            // TODO: 07.09.2022 подумать над тем, как будут размещатся элементы bank Card

            AnimatedVisibility(
                visible = showAll(),
                enter = NotesScreenAnimations.ListItemAnimation.enter,
                exit = NotesScreenAnimations.ListItemAnimation.exit
            ) {
                when (item) {
                    is Account -> Account(
                        account = item,
                        copyText = {
                            viewModel.copyText(
                                context = context,
                                text = it,
                                result = showSnackbar
                            )
                        },
                        openUrl = {
                            val address = (data as Website).address
                            viewModel
                                .createIntentForUrl(address)
                                ?.let { context.startActivity(it) }
                                ?: showSnackbar(
                                    context.getString(
                                        R.string.invalid_address,
                                        address
                                    )
                                )
                        }
                    ) { title ->
                        viewModel.bottomSheetContent = {
                            BottomSheetContent(
                                title = title,
                                beautifulDesign = useBeautifulFont
                            ) {
                                EditItem(text = stringResource(R.string.edit)) {
                                    navigateTo(
                                        AppScreens.WebsiteEdit.createUrl(
                                            id = id,
                                            startPosition = position
                                        )
                                    )
                                    hideBottomSheet()
                                }
                            }
                        }
                        showBottomSheet()
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
    val arrowRotation = remember { Animatable(90f) }

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
            Text(
                text = header.title,
                style = when {
                    header.beautifulDesign -> MaterialTheme.typography.titleLarge
                    else -> MaterialTheme.typography.bodyLarge
                },
            )
        },
        supportingContent = when {
            header.subtitle.isBlank() -> null
            else -> {
                {
                    Text(text = header.subtitle, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowForwardIos,
                    contentDescription = stringResource(R.string.more_info),
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
                            val boxColor = header.title.convertToColor()
                            val textColor = when {
                                boxColor.luminance() < 0.5f -> Color.White
                                else -> Color.Black
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .clip(CircleShape)
                                    .border(
                                        width = 0.2.dp,
                                        color = textColor,
                                        shape = CircleShape
                                    )
                                    .background(boxColor)
                            ) {
                                Text(
                                    text = header.title.first().uppercase(),
                                    fontFamily = FontFamily(PacificFont),
                                    fontSize = 17.sp,
                                    textAlign = TextAlign.Center,
                                    color = textColor,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
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
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = account.name,
                        color = DarkerGray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        color = DarkerGray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            DataTextField(
                text = account.login,
                heading = stringResource(R.string.login),
            ) {
                CopyIconButton {
                    copyText(account.login)
                }
            }

            DataTextField(
                text = account.password,
                heading = stringResource(R.string.password),
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