package com.security.passwordmanager.presentation.view.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.security.passwordmanager.LoadingInBox
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.isScrollingUp
import com.security.passwordmanager.keyboardAsState
import com.security.passwordmanager.presentation.model.data.ComposableAccount
import com.security.passwordmanager.presentation.model.data.contains
import com.security.passwordmanager.presentation.view.composables.CollapsingToolbar
import com.security.passwordmanager.presentation.view.composables.DeleteDialog
import com.security.passwordmanager.presentation.view.composables.EditableDataTextField
import com.security.passwordmanager.presentation.view.composables.ExitDialog
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffold
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffoldDefaults
import com.security.passwordmanager.presentation.view.composables.ToolbarButton
import com.security.passwordmanager.presentation.view.composables.ToolbarButtonDefaults
import com.security.passwordmanager.presentation.view.composables.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.composables.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.composables.rememberScrollableScaffoldState
import com.security.passwordmanager.presentation.view.managment.ToolbarType
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.ModalSheetDefaults
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.CopyItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.DeleteItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.EditItem
import com.security.passwordmanager.presentation.view.theme.AnimationConstants
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.LightGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.ScreenContentAnimation
import com.security.passwordmanager.presentation.view.theme.ScreenToolbarAnimation
import com.security.passwordmanager.presentation.view.theme.WebsiteScreenFabAnimation
import com.security.passwordmanager.presentation.view.theme.screenBorderThickness
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import com.security.passwordmanager.presentation.viewmodel.WebsiteViewModel
import com.security.passwordmanager.presentation.viewmodel.WebsiteViewModel.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalAnimationApi::class,
)
@Composable
internal fun AnimatedVisibilityScope.WebsiteEditingScreen(
    id: String,
    viewModel: WebsiteViewModel,
    settingsViewModel: SettingsViewModel,
    popBackStack: () -> Unit,
    isDarkTheme: Boolean,
    startPosition: Int = 0,
    isDarkStatusBarIcons: (Boolean) -> Unit = {},
) {
    viewModel.id = id

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = ModalSheetDefaults.AnimationSpec,
        skipHalfExpanded = true
    )

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startPosition)
    val scaffoldState = rememberScrollableScaffoldState(
        contentState = listState,
        toolbarType = ToolbarType.ToolbarBelowContent
    )
    val snackbarHostState = remember { SnackbarHostState() }


    val showSnackbar: (String) -> Unit = { message: String ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    val openBottomSheet = { position: Int ->
        viewModel.currentAccountPosition = position
        scope.launch { bottomSheetState.show() }
    }
    val closeBottomSheet = {
        scope.launch { bottomSheetState.hide() }
    }


    if (!isKeyboardOpen) {
        focusManager.clearFocus()
    }


    fun saveInfo() = with(viewModel) {
        showErrors = true
        website.updateErrors(context)

        if (website.haveErrors) {
            scope.launch {
                showSnackbar(context.getString(R.string.invalid_data))
            }
            return
        }

        when {
            this.id.isBlank() -> addWebsite {
                popBackStack()
            }

            else -> updateWebsite {
                popBackStack()
            }
        }
    }

    fun checkUnsavedDataAndGoBack() = when {
        viewModel.isInEdit -> {
            viewModel.openDialog {
                ExitDialog(
                    onConfirm = ::saveInfo,
                    onDismiss = popBackStack,
                    onClose = viewModel::closeDialog
                )
            }
        }

        else -> popBackStack()
    }


    LaunchedEffect(key1 = startPosition) {
        delay(50)
        listState.scrollToItem(index = startPosition)
    }


    if (viewModel.showDialog) {
        viewModel.dialogContent()
    }

    BackHandler {
        when {
            viewModel.currentAccount.isNameRenaming -> {
                viewModel.currentAccount.isNameRenaming = false
            }
            else -> checkUnsavedDataAndGoBack()
        }
    }

    isDarkStatusBarIcons(!scaffoldState.toolbarState.isVisible && !isDarkTheme)




    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            BottomSheetContent(
                title = viewModel.currentAccount.name,
                beautifulDesign = settingsViewModel.settings.beautifulFont
            ) {
                Spacer(modifier = Modifier.size(8.dp))
                EditItem(text = viewModel.getBottomSheetEditName(context)) {
                    viewModel.currentAccount.isNameRenaming =
                        !viewModel.currentAccount.isNameRenaming
                    closeBottomSheet()
                }

                CopyItem(text = stringResource(R.string.copy_info)) {
                    viewModel.copyWebsite(
                        context = context,
                        clipboardManager = clipboardManager,
                        result = showSnackbar
                    )
                    closeBottomSheet()
                }

                DeleteItem(text = stringResource(R.string.delete_account)) {
                    closeBottomSheet()

                    when {
                        viewModel.website.accounts.size > 1 -> {
                            viewModel.website.accounts.remove(viewModel.currentAccount)
                        }
                        viewModel.id.isNotBlank() -> with(viewModel) {
                            openDialog {
                                DeleteDialog(
                                    text = stringResource(id = R.string.deletion_last_account_confirmation),
                                    onDismiss = ::closeDialog,
                                    onConfirm = {
                                        closeDialog()
                                        deleteWebsite { success ->
                                            when {
                                                success -> popBackStack()
                                                else -> showSnackbar(
                                                    context.getString(R.string.cannot_delete_data)
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        else -> popBackStack()
                    }
                }
            }
        },
        sheetShape = ModalSheetDefaults.Shape,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface.animate()
    ) {
        ScrollableToolbarScaffold(
            state = scaffoldState,
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
                        Crossfade(
                            targetState = viewModel.isInEdit,
                            animationSpec = tween(durationMillis = 100, easing = LinearEasing)
                        ) { edit ->
                            ToolbarButton(
                                icon = when {
                                    edit -> Icons.Rounded.Close
                                    else -> Icons.Rounded.ArrowBackIos
                                },
                                contentDescription = "close screen"
                            ) {
                                checkUnsavedDataAndGoBack()
                            }
                        }
                    },
                    actions = {
                        ToolbarButton(
                            icon = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.delete_data),
                            colors = ToolbarButtonDefaults.colors(
//                                transparentContentColor = MaterialTheme.colorScheme.onBackground,
                                borderColor = MaterialTheme.colorScheme.primary,
                                transparentBorderColor = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = .4f
                                )
                            ),
                            iconModifier = Modifier.scale(1.1f)
                        ) {
                            when {
                                viewModel.id.isNotEmpty() -> with(viewModel) {
                                    openDialog {
                                        DeleteDialog(
                                            text = stringResource(
                                                R.string.deletion_website_confirmation,
                                                viewModel.website.name
                                            ),
                                            onDismiss = ::closeDialog,
                                            onConfirm = {
                                                closeDialog()
                                                deleteWebsite { success ->
                                                    when {
                                                        success -> popBackStack()
                                                        else -> showSnackbar(
                                                            context.getString(R.string.cannot_delete_data)
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }

                                else -> popBackStack()
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))

                        ToolbarButton(
                            icon = Icons.Rounded.Save,
                            contentDescription = stringResource(R.string.save),
                            iconModifier = Modifier.scale(1.1f),
                            colors = ToolbarButtonDefaults.colors(
//                                transparentContentColor = MaterialTheme.colorScheme.onBackground,
                                borderColor = MaterialTheme.colorScheme.primary,
                                transparentBorderColor = MaterialTheme.colorScheme.onBackground
                                    .copy(alpha = .4f)
                            ),
                            onClick = ::saveInfo
                        )
                    },
                    modifier = Modifier.animateEnterExit(
                        enter = ScreenToolbarAnimation.enter,
                        exit = ScreenToolbarAnimation.exit
                    )
                )
            },
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
                    visible = !isKeyboardOpen && viewModel.state == State.Ready,
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
                                delay(100)
//                                listState.smoothScrollToItem(viewModel.website.accounts.lastIndex)
                                listState.animateScrollToItem(
                                    viewModel.website.accounts.toList().lastIndex
                                )
                            }

                        },
                        expanded = listState.isScrollingUp(),
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
            contentShape = MaterialTheme.shapes.large.copy(
                bottomStart = CornerSize(0),
                bottomEnd = CornerSize(0)
            ),
            onRefresh = ::checkUnsavedDataAndGoBack,
            pullRefreshIndicator = {
                Icon(
                    imageVector = Icons.Outlined.ArrowCircleLeft,
                    contentDescription = "return",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .scale(it)
                )
            },
            isPullRefreshEnabled = settingsViewModel.settings.pullToRefresh,
            colors = ScrollableToolbarScaffoldDefaults.colors(
                toolbarContainerColor = MaterialTheme.colorScheme.secondary,
                toolbarNavigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                toolbarActionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {

            Crossfade(
                targetState = viewModel.state,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                modifier = Modifier.fillMaxSize()
            ) { state ->
                when (state) {
                    State.Loading -> LoadingInBox(
                        loadingModifier = Modifier.scale(2.4f),
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )

                    else -> WebsiteContent(
                        viewModel = viewModel,
                        settings = settingsViewModel.settings,
                        listState = listState,
                        copyText = { text ->
                            viewModel.copyText(
                                context = context,
                                clipboardManager = clipboardManager,
                                text = text,
                                result = showSnackbar
                            )
                        },
                        updateSettingsProperty = { name, value ->
                            settingsViewModel.updateSettingsProperty(name, value) { errorMsg ->
                                if (errorMsg != null) {
                                    showSnackbar(errorMsg)
                                }
                            }
                        },
                        openBottomSheet = { openBottomSheet(it) },
                        showSnackbar = { showSnackbar(it) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WebsiteContent(
    listState: LazyListState,
    viewModel: WebsiteViewModel,
    settings: Settings,
    updateSettingsProperty: (name: String, value: Any) -> Unit,
    copyText: (String) -> Unit,
    openBottomSheet: (position: Int) -> Unit,
    showSnackbar: (message: String) -> Unit
) {
    val context = LocalContext.current


    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
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
                                if (needUpdateWebsiteName && settings.autofill) {
                                    getDomainNameByUrl(context) { result ->
                                        needUpdateWebsiteName = false
                                        when (result) {
                                            is Result.Error -> result.exception
                                                .localizedMessage
                                                ?.let { showSnackbar(it) }

                                            is Result.Success -> {
                                                if (settings.autofill) website.name = result.data
                                            }

                                            else -> {}
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.placeholder(
                            visible = viewModel.state == State.PreLoading,
                            color = MaterialTheme.colorScheme.primary.animate(),
                            shape = MaterialTheme.shapes.medium,
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = MaterialTheme.colorScheme.onPrimary.animate()
                            )
                        )
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
                            readOnly = settings.autofill && viewModel.needUpdateWebsiteName,
                            label = stringResource(R.string.name_website),
                            error = viewModel.showErrors && viewModel
                                .website
                                .errorNameMessage
                                .isNotBlank(),
                            errorMessage = context.getString(R.string.empty_website_name),
                            modifier = Modifier
                                .padding(bottom = 0.dp)
                                .placeholder(
                                    visible = viewModel.state == State.PreLoading,
                                    color = MaterialTheme.colorScheme.primary.animate(),
                                    shape = MaterialTheme.shapes.medium,
                                    highlight = PlaceholderHighlight.shimmer(
                                        highlightColor = MaterialTheme.colorScheme.onPrimary.animate()
                                    )
                                )
                        )

                        Spacer(Modifier.size(4.dp))

                        CheckboxWithText(
                            text = stringResource(R.string.autofill_name_website),
                            isChecked = settings.autofill,
                            onCheckedChange = {
                                updateSettingsProperty(Settings::autofill.name, it)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                            checkboxModifier = Modifier.placeholder(
                                visible = viewModel.state == State.PreLoading,
                                color = MaterialTheme.colorScheme.primary.animate(),
                                shape = RoundedCornerShape(3.dp),
                                highlight = PlaceholderHighlight.shimmer(
                                    highlightColor = MaterialTheme.colorScheme.onPrimary.animate()
                                )
                            )
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
                copyText = copyText,
                openBottomSheet = { openBottomSheet(index) },
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

    Column {
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

        Divider(Modifier.padding(top = 20.dp, bottom = 16.dp))
    }
}


@Composable
private fun CheckboxWithText(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkboxModifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .toggleable(
                value = isChecked,
                role = Role.Checkbox,
                onValueChange = onCheckedChange
            )
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background.animate())
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary.animate(),
                checkmarkColor = MaterialTheme.colorScheme.onPrimary.animate(),
                uncheckedColor = MaterialTheme.colorScheme.primary.animate()
            ),
            modifier = checkboxModifier
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Composable
private fun Account(
    account: ComposableAccount,
    position: Int,
    viewModel: WebsiteViewModel,
    copyText: (String) -> Unit,
    openBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val defaultNameAccount = stringResource(R.string.account_start, position + 1)

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
            enabled = with(viewModel) { account == currentAccount && account.isNameRenaming },
            openBottomSheet = openBottomSheet,
            keyboardAction = {
                focusManager.moveFocus(FocusDirection.Down)
                account.isNameRenaming = false
                if (account.name == defaultNameAccount) account.name = ""
            },
            modifier = Modifier.placeholder(
                visible = viewModel.state == State.PreLoading,
                color = Color.Transparent,
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = MaterialTheme.colorScheme.onBackground.animate()
                )
            )
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
                        copyText(account.login)
                    }
                }
            },
            modifier = Modifier.placeholder(
                visible = viewModel.state == State.PreLoading,
                color = MaterialTheme.colorScheme.primary.animate(),
                shape = MaterialTheme.shapes.medium,
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = MaterialTheme.colorScheme.onPrimary.animate()
                )
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableDataTextField(
            text = account.password,
            onTextChange = {
                viewModel.website.accounts.update(account.uid) {
                    updateValue(::password, it)
                    errorPasswordMessage = when {
                        password.isEmpty() -> context.getString(R.string.empty_password)
                        else -> ""
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
                        copyText(account.password)
                    }
                    Spacer(Modifier.width(8.dp))
                }
            },
            modifier = Modifier.placeholder(
                visible = viewModel.state == State.PreLoading,
                color = MaterialTheme.colorScheme.primary.animate(),
                shape = MaterialTheme.shapes.medium,
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = MaterialTheme.colorScheme.onPrimary.animate()
                )
            )
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
                        copyText(account.comment)
                    }
                }
            },
            modifier = Modifier.placeholder(
                visible = viewModel.state == State.PreLoading,
                color = MaterialTheme.colorScheme.primary.animate(),
                shape = MaterialTheme.shapes.medium,
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = MaterialTheme.colorScheme.onPrimary.animate()
                )
            )
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
    openBottomSheet: () -> Unit = {},
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
                onClick = openBottomSheet,
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
            focusedPlaceholderColor = LightGray,
            unfocusedPlaceholderColor = LightGray,
            disabledPlaceholderColor = DarkerGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
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
                    text = "Адрес",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                )
            },
            secondItem = {
                Column(
                    modifier = Modifier.placeholder(
                        visible = true,
                        color = Color.Gray,
                        shape = RoundedCornerShape(12.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = Color.White
                        )
                    )
                ) {
                    Text(
                        text = "Название сайта",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.animate(),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxWidth()
                    )

                    Spacer(Modifier.size(4.dp))

                    CheckboxWithText(
                        text = "Автозаполнение",
                        isChecked = true,
                        onCheckedChange = {},
                    )
                }
            }
        )
    }
}