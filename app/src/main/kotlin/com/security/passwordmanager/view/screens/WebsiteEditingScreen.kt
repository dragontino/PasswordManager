package com.security.passwordmanager.view.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.RichTooltipColors
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.model.ComposableAccount
import com.security.passwordmanager.model.contains
import com.security.passwordmanager.util.LoadingInBox
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.util.keyboardAsState
import com.security.passwordmanager.util.reversed
import com.security.passwordmanager.util.scrollingUpState
import com.security.passwordmanager.util.smoothScrollToItem
import com.security.passwordmanager.view.composables.EditableDataTextField
import com.security.passwordmanager.view.composables.TrailingActions.CopyIconButton
import com.security.passwordmanager.view.composables.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.view.composables.dialogs.ConfirmDeletionDialog
import com.security.passwordmanager.view.composables.dialogs.DialogType
import com.security.passwordmanager.view.composables.managment.ScreenEvents
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffold
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffoldDefaults
import com.security.passwordmanager.view.composables.scaffold.DpShape
import com.security.passwordmanager.view.composables.scaffold.ToolbarButton
import com.security.passwordmanager.view.composables.scaffold.ToolbarButtonDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingAppBarDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingToolbar
import com.security.passwordmanager.view.composables.scaffold.toolbar.rememberAppBarState
import com.security.passwordmanager.view.composables.sheets.ModalBottomSheet
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.CopyItem
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.DeleteItem
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.EditItem
import com.security.passwordmanager.view.theme.AnimationConstants
import com.security.passwordmanager.view.theme.DarkerGray
import com.security.passwordmanager.view.theme.LightGray
import com.security.passwordmanager.view.theme.PasswordManagerTheme
import com.security.passwordmanager.view.theme.ScreenContentAnimation
import com.security.passwordmanager.view.theme.ScreenToolbarAnimation
import com.security.passwordmanager.view.theme.WebsiteScreenFabAnimation
import com.security.passwordmanager.view.theme.screenBorderThickness
import com.security.passwordmanager.viewmodel.ViewModelState
import com.security.passwordmanager.viewmodel.WebsiteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AnimatedVisibilityScope.WebsiteEditingScreen(
    viewModel: WebsiteViewModel,
    popBackStack: () -> Unit,
    isDarkTheme: Boolean,
    startPosition: Int = 0,
    isDarkStatusBarIcons: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val keyboardState = keyboardAsState()
    val focusManager = LocalFocusManager.current

    val contentState = rememberLazyListState()
    val topBarState = rememberAppBarState()
    val snackbarHostState = remember { SnackbarHostState() }


    val showSnackbar = remember {
        fun(message: String) {
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { keyboardState.value }.collect {
            if (!it) focusManager.clearFocus()
        }
    }


    var dialogType: DialogType? by rememberSaveable { mutableStateOf(null) }
    LaunchedEffect(key1 = true) {
        viewModel.eventsFlow.collect { event ->
            when (event) {
                is ScreenEvents.OpenDialog -> dialogType = event.type
                ScreenEvents.CloseDialog -> dialogType = null
                is ScreenEvents.Navigate -> popBackStack()
                is ScreenEvents.ShowSnackbar -> showSnackbar(event.message)
            }
        }
    }


    dialogType?.Dialog()


    LaunchedEffect(Unit) {
        delay(900)
        contentState.smoothScrollToItem(targetPosition = startPosition + 2)
    }

    BackHandler {
        viewModel.onBackPress(context)
    }

    // TODO: 29.02.2024 переделать
    SideEffect {
        isDarkStatusBarIcons(!topBarState.isVisible && !isDarkTheme)
    }


    CollapsingToolbarScaffold(
        contentState = contentState,
        contentModifier = Modifier
            .animateEnterExit(
                enter = ScreenContentAnimation.enter,
                exit = ScreenContentAnimation.exit
            )
            .fillMaxSize(),
        topBar = {
            CollapsingToolbar(
                title = stringResource(R.string.website_label),
                navigationButton = {
                    ToolbarButton(
                        icon = when {
                            viewModel.isInEdit -> Icons.Rounded.Close
                            else -> Icons.AutoMirrored.Rounded.ArrowBackIos
                        },
                        contentDescription = "close screen"
                    ) {
                        viewModel.onBackPress(context)
                    }
                },
                actions = {
                    ToolbarButton(
                        icon = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.delete_data),
                        colors = ToolbarButtonDefaults.colors(
                            borderColor = MaterialTheme.colorScheme.primary,
                            transparentBorderColor = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = .4f
                            )
                        ),
                        iconModifier = Modifier.scale(1.1f)
                    ) {
                        when {
                            !viewModel.isNew -> with(viewModel) {
                                openDialog(
                                    type = ConfirmDeletionDialog(
                                        text = context.getString(
                                            R.string.deletion_website_confirmation,
                                            website.name
                                        ),
                                        onDismiss = ::closeDialog,
                                        onConfirm = {
                                            closeDialog()
                                            deleteWebsite { viewModel.navigateTo(null) }
                                        }
                                    )
                                )
                            }

                            else -> viewModel.navigateTo(null)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))

                    ToolbarButton(
                        icon = Icons.Rounded.Save,
                        contentDescription = stringResource(R.string.save),
                        iconModifier = Modifier.scale(1.1f),
                        colors = ToolbarButtonDefaults.colors(
                            borderColor = MaterialTheme.colorScheme.primary,
                            transparentBorderColor = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = .4f
                            )
                        ),
                        onClick = {
                            viewModel.saveInfo(context) { viewModel.navigateTo(null) }
                        }
                    )
                },
                modifier = Modifier.animateEnterExit(
                    enter = ScreenToolbarAnimation.enter,
                    exit = ScreenToolbarAnimation.exit
                )
            )
        },
        topBarScrollBehavior = CollapsingAppBarDefaults.underContentScrollBehavior(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    actionOnNewLine = true,
                    shape = MaterialTheme.shapes.medium,
                    actionColor = MaterialTheme.colorScheme.primary.animate(),
                    containerColor = MaterialTheme.colorScheme.onBackground.animate(),
                    contentColor = MaterialTheme.colorScheme.background.animate()
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !keyboardState.value && viewModel.state == ViewModelState.Ready,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationConstants.animationTimeMillis / 2
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = AnimationConstants.animationTimeMillis / 2
                    )
                ),
                modifier = Modifier.animateEnterExit(
                    enter = WebsiteScreenFabAnimation.enter,
                    exit = WebsiteScreenFabAnimation.exit,
                )
            ) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = stringResource(R.string.add_account),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "add"
                        )
                    },
                    onClick = {
                        viewModel.website.accounts.add(ComposableAccount())
                        scope.launch {
                            delay(300)
                            contentState.smoothScrollToItem(
                                viewModel.website.accounts.lastIndex + 1
                            )
                        }
                    },
                    expanded = contentState.scrollingUpState().value,
                    containerColor = MaterialTheme.colorScheme.secondary.animate(),
                    contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    /*modifier = Modifier.windowInsetsPadding(
                            WindowInsets.tappableElement.only(WindowInsetsSides.Bottom)
                        )*/
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        contentBorder = BorderStroke(
            width = screenBorderThickness,
            brush = Brush.verticalGradient(
                0.1f to MaterialTheme.colorScheme.secondary.animate(),
                0.6f to MaterialTheme.colorScheme.background.animate()
            )
        ),
        contentShape = DpShape(top = 18.dp),
        onRefresh = { viewModel.onBackPress(context) },
        pullToRefreshIndicator = { pullToRefreshState ->
            Icon(
                imageVector = Icons.Outlined.ArrowCircleLeft,
                contentDescription = "return",
                tint = MaterialTheme.colorScheme.onBackground.reversed,
                modifier = Modifier.graphicsLayer(
                    alpha = pullToRefreshState.progress,
                    scaleX = pullToRefreshState.progress,
                    scaleY = pullToRefreshState.progress
                ).fillMaxSize()
            )
        },
        pullToRefreshEnabled = viewModel.settings.pullToRefresh,
        colors = CollapsingToolbarScaffoldDefaults.colors(
            toolbarContainerColor = MaterialTheme.colorScheme.secondary,
            toolbarNavigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            toolbarActionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {

        Crossfade(
            targetState = viewModel.state,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            modifier = Modifier.fillMaxSize(),
            label = "content"
        ) { state ->
            when (state) {
                ViewModelState.Loading -> LoadingInBox(
                    loadingModifier = Modifier.scale(2.4f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )

                else -> WebsiteContent(contentState, viewModel)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WebsiteContent(
    listState: LazyListState,
    viewModel: WebsiteViewModel,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            FirstTwoItems(
                firstItem = {
                    EditableDataTextField(
                        text = viewModel.website.address,
                        onTextChange = {
                            viewModel.website.apply {
                                updateValue(::address, it)
                                updateAddressError(context)

                                viewModel.needUpdateWebsiteName = ::address in updatedProperties
                            }
                        },
                        label = context.getString(R.string.url_address),
                        keyboardType = KeyboardType.Uri,
                        error = viewModel.showErrors &&
                                viewModel.website.errorAddressMessage.isNotBlank(),
                        errorMessage = viewModel.website.errorAddressMessage,
                        onImeActionClick = {
                            with(viewModel) {
                                if (website.name.isBlank() && needUpdateWebsiteName && settings.autofill) {
                                    getDomainNameByUrl(
                                        context = context,
                                        failure = {
                                            needUpdateWebsiteName = false
                                            showSnackbar(it)
                                        },
                                        success = {
                                            needUpdateWebsiteName = false
                                            website.name = it
                                        }
                                    )
                                }
                            }
                        },
                        trailingActions = {
                            if (viewModel.website.address.isNotBlank()) {
                                CopyIconButton {
                                    viewModel.copyText(
                                        text = viewModel.website.name,
                                        context = context,
                                        clipboardManager = clipboardManager
                                    )
                                }
                            }
                        },
                    )
                },
                secondItem = {
                    Column {
                        EditableDataTextField(
                            text = viewModel.website.name,
                            onTextChange = { nameWebsite ->
                                viewModel.website.apply {
                                    updateValue(::name, nameWebsite)
                                    updateNameError(context)
                                }
                            },
                            readOnly = viewModel.settings.autofill &&
                                    viewModel.needUpdateWebsiteName &&
                                    viewModel.website.name.isBlank(),
                            label = stringResource(R.string.name_website),
                            error = viewModel.showErrors && viewModel
                                .website
                                .errorNameMessage
                                .isNotBlank(),
                            errorMessage = context.getString(R.string.empty_website_name),
                            trailingActions = {
                                if (viewModel.website.name.isNotBlank()) {
                                    CopyIconButton {
                                        viewModel.copyText(
                                            text = viewModel.website.name,
                                            context = context,
                                            clipboardManager = clipboardManager
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.padding(bottom = 0.dp)
                        )

                        Spacer(Modifier.size(4.dp))

                        CheckboxWithText(
                            text = stringResource(R.string.autofill_name_website),
                            isChecked = viewModel.settings.autofill,
                            onCheckedChange = viewModel::updateAutofill,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            )
        }

        itemsIndexed(
            items = viewModel.website.accounts.sorted(),
            key = { _, account -> account.uid },
            contentType = { _, _ -> "Account" }
        ) { index, account ->

            Account(
                account = account,
                position = index,
                viewModel = viewModel,
                isLast = index == viewModel.website.accounts.lastIndex,
                modifier = Modifier
                    .animateContentSize(
                        tween(
                            durationMillis = 200,
                            easing = LinearOutSlowInEasing
                        )
                    )
                    .animateItemPlacement(
                        tween(
                            durationMillis = 200,
                            easing = FastOutSlowInEasing
                        )
                    )
            )
        }

        item(contentType = "Spacer") {
            Spacer(
                modifier = Modifier.height(50.dp),
            )
        }
    }
}


@Composable
private fun FirstTwoItems(
    firstItem: @Composable (() -> Unit),
    secondItem: @Composable (() -> Unit)
) {
    val orientation = LocalConfiguration.current.orientation

    Column(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.weight(1f)
                ) {
                    firstItem()
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.weight(1f)
                ) {
                    secondItem()
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth()
            ) {
                firstItem()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth()
            ) {
                secondItem()
            }
        }

        HorizontalDivider(Modifier.padding(top = 20.dp, bottom = 16.dp))
    }
}


@Composable
private fun CheckboxWithText(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier
                .shadow(elevation = 1.5.dp, shape = MaterialTheme.shapes.small)
                .clip(MaterialTheme.shapes.small)
                .toggleable(
                    value = isChecked,
                    role = Role.Checkbox,
                    onValueChange = onCheckedChange
                )
                .background(MaterialTheme.colorScheme.background.animate())
                .padding(16.dp)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary.animate(),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    uncheckedColor = MaterialTheme.colorScheme.primary.animate()
                )
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.animate()
            )
        }
        
        AutofillTooltip()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutofillTooltip(modifier: Modifier = Modifier) {
    val state = rememberTooltipState(isPersistent = true)
    val coroutineScope = rememberCoroutineScope()

    val showTooltip = {
        coroutineScope.launch { state.show() }
    }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                text = {
                    Text(
                        text = stringResource(R.string.autofill_desc),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                action = {
                    TextButton(onClick = state::dismiss) {
                        Text(
                            text = stringResource(R.string.understandably),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                shape = MaterialTheme.shapes.large,
                colors = RichTooltipColors(
                    containerColor = MaterialTheme.colorScheme.surface.animate(),
                    contentColor = MaterialTheme.colorScheme.onSurface.animate(),
                    titleContentColor = MaterialTheme.colorScheme.onSurface.animate(),
                    actionContentColor = MaterialTheme.colorScheme.primary.animate()
                )
            )
        },
        state = state,
        modifier = modifier
    ) {
        IconButton(
            onClick = { showTooltip() },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary.animate()
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                contentDescription = "description"
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Account(
    account: ComposableAccount,
    position: Int,
    viewModel: WebsiteViewModel,
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val defaultNameAccount = stringResource(R.string.account_start, position + 1)
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = account.isNameRenaming) {
        account.isNameRenaming = false
    }

    LaunchedEffect(viewModel.currentAccountPosition) {
        if (viewModel.currentAccountPosition != position) {
            account.isNameRenaming = false
        }
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            title = account.name,
            beautifulDesign = viewModel.settings.beautifulFont,
            onClose = { isSheetOpen = false }
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            EditItem(
                text = when {
                    account.isNameRenaming -> context.getString(R.string.cancel_renaming_data)
                    else -> context.getString(R.string.rename_data)
                }
            ) {
                account.isNameRenaming = !account.isNameRenaming
                if (account.isNameRenaming) {
                    viewModel.currentAccountPosition = position
                }
                isSheetOpen = false
            }

            CopyItem(text = stringResource(R.string.copy_info)) {
                viewModel.copyWebsite(context = context, clipboardManager = clipboardManager)
                isSheetOpen = false
            }

            DeleteItem(text = stringResource(R.string.delete_account)) {
                isSheetOpen = false

                when {
                    viewModel.website.accounts.size > 1 -> {
                        viewModel.website.accounts.remove(account)
                    }
                    !viewModel.isNew -> with(viewModel) {
                        openDialog(
                            type = ConfirmDeletionDialog(
                                text = context.getString(R.string.deletion_last_account_confirmation),
                                onDismiss = ::closeDialog,
                                onConfirm = {
                                    closeDialog()
                                    deleteWebsite {
                                        viewModel.navigateTo(null)
                                    }
                                }
                            )
                        )
                    }
                    else -> viewModel.navigateTo(null)
                }
            }
        }
    }

    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.animate(),
            contentColor = MaterialTheme.colorScheme.onBackground.animate()
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        ),
        modifier = modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        AccountHeader(
            text = account.name.ifBlank {
                if (account.isNameRenaming) "" else defaultNameAccount
            },
            updateHeading = {
                account.updateValue(account::name, it)
                viewModel.website.accounts.update(account.uid)
            },
            enabled = account.isNameRenaming,
            onMoreClick = { isSheetOpen = true },
            keyboardAction = {
                focusManager.moveFocus(FocusDirection.Down)
                account.isNameRenaming = false
                if (account.name == defaultNameAccount) account.name = ""
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditableDataTextField(
            text = account.login,
            onTextChange = {
                viewModel.website.accounts.update(account.uid) {
                    updateValue(::login, it)
                    errorLoginMessage = when {
                        login.isEmpty() -> context.getString(R.string.empty_login)
                        else -> ""
                    }
                }
            },
            label = stringResource(R.string.login),
            error = viewModel.showErrors && account.errorLoginMessage.isNotBlank(),
            errorMessage = account.errorLoginMessage,
            trailingActions = {
                if (account.login.isNotBlank()) {
                    CopyIconButton {
                        viewModel.copyText(
                            text = account.login,
                            context = context,
                            clipboardManager = clipboardManager
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableDataTextField(
            text = account.password,
            onTextChange = {
                if (' ' !in it) {
                    viewModel.website.accounts.update(account.uid) {
                        updateValue(::password, it)
                        errorPasswordMessage = when {
                            password.isEmpty() -> context.getString(R.string.empty_password)
                            else -> ""
                        }
                    }
                }
            },
            label = stringResource(R.string.password),
            keyboardType = KeyboardType.Password,
            visualTransformation = if (account.passwordIsVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            error = viewModel.showErrors && account.errorPasswordMessage.isNotBlank(),
            errorMessage = account.errorPasswordMessage,
            trailingActions = {
                VisibilityIconButton(visible = account.passwordIsVisible) {
                    account.passwordIsVisible = it
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (account.password.isNotBlank()) {
                    CopyIconButton {
                        viewModel.copyText(
                            text = account.password,
                            context = context,
                            clipboardManager = clipboardManager
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableDataTextField(
            text = account.comment,
            onTextChange = {
                viewModel.website.accounts.update(account.uid) {
                    updateValue(::comment, it)
                }
            },
            label = stringResource(R.string.comment),
            imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
            singleLine = false,
            trailingActions = {
                if (account.comment.isNotBlank()) {
                    CopyIconButton {
                        viewModel.
                        copyText(
                            text = account.comment,
                            context = context,
                            clipboardManager = clipboardManager
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
private fun AccountHeader(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    keyboardAction: KeyboardActionScope.() -> Unit = {},
    updateHeading: (String) -> Unit = {},
    onMoreClick: () -> Unit,
) {
    val focusRequester = remember(::FocusRequester)

    LaunchedEffect(enabled) {
        when {
            enabled -> focusRequester.requestFocus()
            else -> focusRequester.freeFocus()
        }
    }

    TextField(
        value = text,
        onValueChange = updateHeading,
        placeholder = {
            Text(
                text = stringResource(R.string.account_name_placeholder),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onMoreClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground.animate(),
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.rename_data),
                    modifier = Modifier.scale(1.3f)
                )
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Center
        ),
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = keyboardAction),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onBackground.animate(),
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.animate(),
            disabledTextColor = DarkerGray,
            focusedPlaceholderColor = DarkerGray,
            unfocusedPlaceholderColor = DarkerGray,
            disabledPlaceholderColor = DarkerGray,
            focusedIndicatorColor = MaterialTheme.colorScheme.onBackground.animate(),
            unfocusedIndicatorColor = LightGray,
            disabledIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .then(modifier),
    )
}


@Preview
@Composable
private fun CheckboxWithTextPreview() {
    var isChecked by remember { mutableStateOf(true) }

    PasswordManagerTheme(isDarkTheme = false) {
        CheckboxWithText(
            text = "Imagine Dragons",
            isChecked = isChecked,
            onCheckedChange = { isChecked = it }
        )
    }
}


@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun FirstTwoItemsPreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        FirstTwoItems(
            firstItem = {
                Text(
                    text = stringResource(R.string.url_address),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                )
            },
            secondItem = {
                Column {
                    Text(
                        text = stringResource(R.string.name_website),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.animate(),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxWidth()
                    )

                    Spacer(Modifier.size(4.dp))

                    CheckboxWithText(
                        text = stringResource(R.string.autofill_name_website),
                        isChecked = true,
                        onCheckedChange = {},
                    )
                }
            }
        )
    }
}