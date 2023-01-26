package com.security.passwordmanager.presentation.view.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.presentation.model.ObservableWebsite
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.composablelements.DeleteDialog
import com.security.passwordmanager.presentation.view.composablelements.EditableDataTextField
import com.security.passwordmanager.presentation.view.composablelements.ExitDialog
import com.security.passwordmanager.presentation.view.composablelements.TopBar
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.CopyItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.DeleteItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.EditItem
import com.security.passwordmanager.presentation.view.navigation.ToolbarAction
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.screenBorderThickness
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import com.security.passwordmanager.presentation.viewmodel.WebsiteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.*
import me.onebone.toolbar.FabPosition

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@ExperimentalToolbarApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.WebsiteScreen(
    address: String,
    viewModel: WebsiteViewModel,
    dataViewModel: DataViewModel,
    settingsViewModel: SettingsViewModel,
    popBackStack: () -> Unit,
    startPosition: Int = 0,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    
    val bottomState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = BottomAnimationSpec
    )
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberCollapsingToolbarScaffoldState()


    val isInEditing by rememberSaveable { mutableStateOf(false) }


    if (!isKeyboardOpen) {
        focusManager.clearFocus()
    }


    fun saveInfo() = with(viewModel) {
        showErrors = true

        if (!checkWebsites()) {
            updateErrorMessages(context)
            scope.launch {
                snackbarHostState.showSnackbar(message = "Введите корректные данные!")
            }
            return
        }

        itemsToDelete.forEach {
            dataViewModel.deleteData(it)
        }

        accountList.forEachIndexed { index, website ->
            if (website.toData() !in itemsToDelete) {
                when {
                    index < accountList.size - newWebsites ->
                        dataViewModel.updateData(website.toData())
                    else ->
                        dataViewModel.addData(website.toData())
                }
            }
        }
        popBackStack()
    }


//    viewModel.result = loadWebsiteList(address = address, dataViewModel = dataViewModel).value
//    if (viewModel.result is Result.Success) {
//        viewModel.accountList.swapList(
//            (viewModel.result as Result.Success<List<Website>>)
//                .data
//                .map { it.observe() }
//        )
//    }



    LaunchedEffect(key1 = Unit) {
        viewModel.viewModelState = WebsiteViewModel.ViewModelState.Loading
        delay(200)
        val newList = dataViewModel
            .getAccountList(key = address, dataType = DataType.Website)
            .ifEmpty {
                viewModel.newWebsites = 1
                listOf(Website())
            }

        viewModel.accountList.swapList(
            newList
                .map { it.observe() }
                .filterIsInstance<ObservableWebsite>()
        )
        viewModel.viewModelState = WebsiteViewModel.ViewModelState.Ready
    }


    LaunchedEffect(key1 = startPosition) {
        delay(50)
        listState.scrollToItem(
            index = startPosition
        )
    }


    if (viewModel.showDialog) {
        viewModel.dialogContent()
    }


    BackHandler {
        if (isInEditing) {
            viewModel.openDialog {
                ExitDialog(
                    onConfirm = ::saveInfo,
                    onDismiss = popBackStack,
                    onClose = viewModel::closeDialog
                )
            }
        }
        else popBackStack()
    }


    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            BottomSheetContent(title = viewModel.currentWebsite.nameAccount, beautifulDesign = true) {
                EditItem(text = viewModel.getEditItemName(context)) {
                    viewModel.isNameRenaming = !viewModel.isNameRenaming
                    scope.launch { bottomState.hide() }
                }

                CopyItem(text = stringResource(R.string.copy_info)) {
                    dataViewModel.copyData(context, viewModel.currentWebsite.toData())
                    scope.launch { bottomState.hide() }
                }

                DeleteItem(text = stringResource(R.string.delete_account)) {
                    with (viewModel) {
                        if (currentWebsitePosition < accountList.size - newWebsites) {
                            itemsToDelete += currentWebsite.toData() as Website
                        } else {
                            newWebsites--
                        }
                        accountList.remove(currentWebsite)
                        if (accountList.isEmpty()) saveInfo()
                        scope.launch { bottomState.hide() }
                    }
                }
            }
        },
        sheetShape = MaterialTheme.shapes.large,
        sheetBackgroundColor = MaterialTheme.colorScheme.background.animate()
    ) {
        ToolbarWithFabScaffold(
            state = scaffoldState,
            scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
            toolbar = {
                TopBar(
                    title = stringResource(R.string.website_label),
                    navigationIcon = if (isInEditing) Icons.Rounded.Close else Icons.Rounded.ArrowBackIos,
                    onNavigate = {
                        if (isInEditing) {
                            viewModel.openDialog {
                                ExitDialog(
                                    onConfirm = ::saveInfo,
                                    onDismiss = popBackStack,
                                    onClose = viewModel::closeDialog
                                )
                            }
                        } else {
                            popBackStack()
                        }
                    },
//                    modifier = Modifier.animateEnterExit(
//                        enter = EnterTopBarAnimation,
//                        exit = ExitTopBarAnimation
//                    )
                ) {
                    ToolbarAction(
                        icon = Icons.Rounded.Delete,
                        modifier = Modifier.scale(1.2f),
                        contentDescription = stringResource(R.string.delete)
                    ) {
                        if (address.isNotEmpty()) {
                            viewModel.openDialog {
                                DeleteDialog(
                                    text = "Вы уверены, что хотите удалить данные?",
                                    onConfirm = {
                                        viewModel.closeDialog()
                                        val itemToDelete = viewModel.accountList.firstOrNull()
                                            ?: return@DeleteDialog

                                        dataViewModel.deleteRecords(itemToDelete.toData())
                                        popBackStack()
                                    },
                                    onDismiss = {
                                        viewModel.closeDialog()
                                    }
                                )
                            }
                        } else {
                            popBackStack()
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    ToolbarAction(
                        icon = Icons.Rounded.Save,
                        modifier = Modifier.scale(1.2f),
                        contentDescription = stringResource(R.string.save),
                        onClick = ::saveInfo
                    )

                }
            },
            fab = {
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
                        viewModel.accountList.add(
                            ObservableWebsite(
                                address = viewModel.accountList[0].address,
                                nameWebsite = viewModel.accountList[0].nameWebsite
                            )
                        )
                        viewModel.newWebsites++
                        scope.launch {
                            delay(100)
//                            listState.smoothScrollToItem(accountList.lastIndex + 1)
                            listState.scrollToItem(viewModel.accountList.lastIndex)
                        }

                    },
                    expanded = listState.isScrollingUp(),
                    containerColor = MaterialTheme.colorScheme.primary.animate(),
                    contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    modifier = Modifier
                        .animateEnterExit(
                            enter = viewModel.enterFabAnimation,
                            exit = viewModel.exitFabAnimation,
                        )
                        .padding(
                            WindowInsets.tappableElement
                                .only(WindowInsetsSides.Vertical)
                                .asPaddingValues()
                        )
                )
            },
            fabPosition = FabPosition.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary.animate())
                .fillMaxSize()
        ) {

            Crossfade(
                targetState = viewModel.viewModelState,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                modifier = Modifier
                    .animateEnterExit(
                        enter = EnterContentAnimation,
                        exit = ExitContentAnimation
                    )
                    .clip(viewModel.screenShape())
                    .background(
                        color = MaterialTheme.colorScheme.background.animate(),
                        shape = viewModel.screenShape()
                    )
                    .border(
                        width = screenBorderThickness,
                        shape = viewModel.screenShape(),
                        brush = Brush.verticalGradient(
                            0.1f to MaterialTheme.colorScheme.primary.animate(),
                            0.6f to MaterialTheme.colorScheme.background.animate()
                        )
                    )
                    .fillMaxSize()
            ) { state ->
                when (state) {
                    WebsiteViewModel.ViewModelState.Loading -> Loading(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                    WebsiteViewModel.ViewModelState.Ready -> {
                        WebsiteContentScreen(
                            viewModel = viewModel,
                            settingsViewModel = settingsViewModel,
                            listState = listState,
                            copyText = { dataViewModel.copyText(context, it) },
                            openBottomSheet = {
                                scope.launch { viewModel.openBottomSheet(bottomState, it) }
                            }
                        )

                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Snackbar(
                                snackbarData = it,
                                shape = RoundedCornerShape(11.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


//@Composable
//private fun loadWebsiteList(
//    address: String,
//    dataViewModel: DataViewModel,
//): State<Result<List<Website>>> {
//    return produceState(
//        initialValue = Result.loading(),
//        key1 = address,
//        key2 = dataViewModel
//    ) {
//        delay(100)
//        val websiteList = withContext(Dispatchers.IO) {
//            dataViewModel
//                .getAccountList(address, DataType.Website)
//                .filterIsInstance<Website>()
//                .ifEmpty { listOf(Website()) }
//        }
//        delay(100)
//        value = Result.Success(websiteList)
//    }
//}





@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun WebsiteContentScreen(
    listState: LazyListState,
    viewModel: WebsiteViewModel,
    settingsViewModel: SettingsViewModel,
    copyText: (String) -> Unit,
    openBottomSheet: (index: Int) -> Unit
) {
    val context = LocalContext.current


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
                        text = viewModel.accountList.firstOrNull()?.address ?: "",
                        onTextChange = { address ->
                            viewModel.accountList.forEach {
                                it.address = address
                                it.errorAddressMessage = if (address.isEmpty())
                                    context.getString(R.string.empty_url)
                                else ""
                            }
                        },
                        hint = stringResource(R.string.url_address),
                        keyboardType = KeyboardType.Uri,
                        isError = viewModel.showErrors &&
                                viewModel
                                    .accountList
                                    .firstOrNull()
                                    ?.errorAddressMessage
                                    ?.isNotBlank() == true,
                        errorMessage = viewModel
                            .accountList.firstOrNull()
                            ?.errorAddressMessage
                            ?: "",
//                                textIsChanged = { isInEditing = it },
                    )
                },
                secondItem = {
                    Column {
                        EditableDataTextField(
                            text = viewModel.accountList.firstOrNull()?.nameWebsite ?: "",
                            onTextChange = { nameWebsite ->
                                viewModel.accountList.forEach {
                                    it.nameWebsite = nameWebsite
                                    it.errorNameWebsiteMessage = if (nameWebsite.isEmpty())
                                        context.getString(R.string.empty_website_name)
                                    else ""
                                }
                            },
                            hint = stringResource(R.string.name_website),
                            isError = viewModel.showErrors &&
                                    viewModel
                                        .accountList.firstOrNull()
                                        ?.errorNameWebsiteMessage
                                        ?.isNotBlank() == true,
                            errorMessage = context.getString(R.string.empty_website_name),
                            whenFocused = {
                                if (
                                    settingsViewModel.settings.isUsingAutofill
                                    &&
                                    viewModel
                                        .accountList
                                        .firstOrNull()
                                        ?.nameWebsite
                                        ?.isEmpty() == true
                                    &&
                                    viewModel
                                        .accountList.firstOrNull()
                                        ?.address
                                        ?.isNotEmpty() == true
                                ) {
                                    viewModel.accountList.map {
                                        it.nameWebsite = viewModel.generateNameWebsite()
                                    }
                                }
                            },
                            modifier = Modifier.padding(bottom = 0.dp)
//                                textIsChanged = { isInEditing = it },
                        )

                        Spacer(Modifier.size(4.dp))

                        CheckboxWithText(
                            text = stringResource(R.string.autofill_name_website),
                            isChecked = settingsViewModel.settings.isUsingAutofill,
                            onCheckedChange = settingsViewModel::updateAutofill,
                            modifier = Modifier
                                .padding(top = 0.dp)
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            )
        }

        itemsIndexed(viewModel.accountList) { index, website ->
            Website(
                website = website,
                position = index,
                viewModel = viewModel,
                copyText = copyText,
                openBottomSheet = { openBottomSheet(index) },
                isLast = index == viewModel.accountList.lastIndex,
//                        isChanged = { isInEditing = it },
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

        item {
            Spacer(modifier = Modifier.height(80.dp))
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

        Divider(
            color = DarkerGray,
            modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
        )
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
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onCheckedChange(!isChecked) }
            .background(MaterialTheme.colorScheme.background.animate())
            .fillMaxWidth()
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
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
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@ExperimentalMaterial3Api
@Composable
private fun Website(
    website: ObservableWebsite,
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
            containerColor = MaterialTheme.colorScheme.surface.animate(),
            contentColor = MaterialTheme.colorScheme.onBackground.animate()
        ),
        modifier = modifier.padding(16.dp)
    ) {
        WebsiteHeader(
            heading = website.nameAccount.ifBlank {
                if (viewModel.isNameRenaming) "" else defaultNameAccount
            },
            updateHeading = website::nameAccount::set,
            enabled = with(viewModel) { position == currentWebsitePosition && isNameRenaming },
            openBottomSheet = openBottomSheet,
            keyboardAction = {
                focusManager.moveFocus(FocusDirection.Down)
                viewModel.isNameRenaming = false
                if (website.nameAccount == defaultNameAccount) website.nameAccount = ""
            }
        )

        EditableDataTextField(
            text = website.login,
            onTextChange = {
                website.login = it
                website.errorLoginMessage = if (website.login.isEmpty())
                    context.getString(R.string.empty_login)
                else ""
            },
            hint = stringResource(R.string.login),
            isError = viewModel.showErrors && website.errorLoginMessage.isNotBlank(),
            errorMessage = website.errorLoginMessage,
            trailingActions = {
                CopyIconButton {
                    copyText(website.login)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableDataTextField(
            text = website.password,
            onTextChange = {
                website.password = it
                website.errorPasswordMessage = if (website.password.isEmpty())
                    context.getString(R.string.empty_password)
                else ""
            },
            hint = stringResource(R.string.password),
            keyboardType = KeyboardType.Password,
            visualTransformation = if (website.passwordIsVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = viewModel.showErrors && website.errorPasswordMessage.isNotBlank(),
            errorMessage = website.errorPasswordMessage,
            trailingActions = {
                VisibilityIconButton(visible = website.passwordIsVisible) {
                    website.passwordIsVisible = it
                }
                Spacer(modifier = Modifier.width(8.dp))
                CopyIconButton {
                    copyText(website.password)
                }
                Spacer(Modifier.width(8.dp))
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        EditableDataTextField(
            text = website.comment,
            onTextChange = website::comment::set,
            hint = stringResource(R.string.comment),
            imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
            trailingActions = {
                CopyIconButton {
                    copyText(website.comment)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}



@ExperimentalMaterial3Api
@Composable
private fun WebsiteHeader(
    heading: String,
    enabled: Boolean,
    keyboardAction: KeyboardActionScope.() -> Unit,
    updateHeading: (String) -> Unit,
    openBottomSheet: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        TextField(
            value = heading,
            onValueChange = updateHeading,
            placeholder = {
                Text(
                    text = stringResource(R.string.account_name_placeholder),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
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
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(4f)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onBackground.animate(),
                placeholderColor = DarkerGray,
                containerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary.animate(),
                disabledTextColor = DarkerGray,
                disabledIndicatorColor = Color.Transparent
            ),
        )

        IconButton(
            onClick = openBottomSheet,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground.animate(),
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(end = 8.dp, start = 4.dp)
                .fillMaxHeight()
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.rename_data),
                modifier = Modifier.scale(1.3f)
            )
        }
    }
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



@ExperimentalMaterial3Api
@Preview
@Composable
private fun FirstThreeItemsPreview() {
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
                Column {
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