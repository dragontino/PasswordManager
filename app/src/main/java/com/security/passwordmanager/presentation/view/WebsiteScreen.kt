package com.security.passwordmanager.presentation.view

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
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
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.presentation.model.ObservableWebsite
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.CopyItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.DeleteItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.EditItem
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import com.security.passwordmanager.presentation.viewmodel.WebsiteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalLayoutApi
@SuppressLint("SourceLockedOrientationActivity")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
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

    // TODO: 04.12.2022 удалить
    context.getActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    
    val bottomState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = BottomAnimationSpec
    )
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val settings by settingsViewModel.settings.observeAsState(initial = Settings())


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


//    systemUiController.setSystemBarsColor(
//        color = MaterialTheme.colorScheme.primary.animate(),
//        darkIcons = false
//    )

//    viewModel.result = loadWebsiteList(address = address, dataViewModel = dataViewModel).value
//    if (viewModel.result is Result.Success) {
//        viewModel.accountList.swapList(
//            (viewModel.result as Result.Success<List<Website>>)
//                .data
//                .map { it.observe() }
//        )
//    }


    LaunchedEffect(key1 = address) {
        viewModel.isLoading = true
        delay(200)
        val newList = dataViewModel
            .getAccountList(key = address, dataType = DataType.Website)
            .ifEmpty { listOf(Website()) }

        viewModel.accountList.swapList(
            newList
                .map { it.observe() }
                .filterIsInstance<ObservableWebsite>()
        )
        delay(200)

        viewModel.isLoading = false
    }


//    LaunchedEffect(key1 = startPosition) {
//        delay(50)
//        listState.smoothScrollToItem(
//            targetPosition = startPosition + (startPosition > 0).toInt()
//        )
//    }


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
        Scaffold(
            topBar = {
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
                    }
                ) {
                    TopBarAction(
                        icon = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.delete_password),
                        modifier = Modifier.scale(1.2f)
                    ) {
                        if (address.isNotEmpty()) {
                            viewModel.openDialog {
                                DeleteDialog(
                                    text = "Вы уверены, что хотите удалить данные?",
                                    onConfirm = {
                                        viewModel.closeDialog()
                                        val itemToDelete = viewModel.accountList.firstOrNull()
                                            ?: return@DeleteDialog

                                        scope.launch(Dispatchers.IO) {
                                            dataViewModel.deleteRecords(itemToDelete.toData())
                                        }
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
                    TopBarAction(
                        icon = Icons.Rounded.Save,
                        contentDescription = stringResource(R.string.button_save),
                        modifier = Modifier.scale(1.2f),
                        onClick = ::saveInfo
                    )

                }
            },
            floatingActionButton = {
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
//                        scope.launch {
//                            delay(100)
//                            listState.smoothScrollToItem(accountList.lastIndex + 1)
//                        }

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
            floatingActionButtonPosition = FabPosition.Center,
            contentWindowInsets = WindowInsets(0.dp),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(
                        snackbarData = it,
                        shape = RoundedCornerShape(11.dp)
                    )
                }
            }
        ) { contentPadding ->

            Loading(
                isLoading = viewModel.isLoading,
                modifier = Modifier.animateEnterExit(
                    enter = viewModel.enterScreenAnimation,
                    exit = viewModel.exitScreenAnimation
                )
            )
//            if (viewModel.isLoading) return@Scaffold


            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 16.dp),
                modifier = Modifier
                    .animateEnterExit(
                        enter = viewModel.enterScreenAnimation,
                        exit = viewModel.exitScreenAnimation
                    )
                    .padding(contentPadding)
            ) {
                item {
                    FirstTwoItems(
                        firstItem = { modifier ->
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
                                modifier = modifier
                            )
                        },
                        secondItem = { modifier ->
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
                                        settings.isShowingDataHints &&
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
//                                textIsChanged = { isInEditing = it },
                                modifier = modifier
                            )
                        }
                    )
                }


                itemsIndexed(viewModel.accountList) { index, website ->
                    Website(
                        website = website,
                        position = index,
                        viewModel = viewModel,
                        copyText = { dataViewModel.copyText(context, it) },
                        openBottomSheet = {
                            scope.launch { viewModel.openBottomSheet(bottomState, index) }
                        },
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



@ExperimentalMaterial3Api
@Composable
private fun FirstTwoItems(
    firstItem: @Composable (Modifier) -> Unit,
    secondItem: @Composable (Modifier) -> Unit
) {
    val orientation = LocalConfiguration.current.orientation

    Column {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                firstItem(Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                secondItem(Modifier.weight(1f))
            }
        } else {
            firstItem(Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            secondItem(Modifier.fillMaxWidth())
        }

        Divider(
            color = DarkerGray,
            modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
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
            enabled = viewModel.run { position == currentWebsitePosition && isNameRenaming },
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






@ExperimentalMaterial3Api
@Preview
@Composable
private fun WebsitePreview() {
    PasswordManagerTheme {
        Website(
            ObservableWebsite(
                login = "Imagine Dragons",
                password = "Wrecked",
                nameAccount = "Dan",
                nameWebsite = "Dragons",
                address = "imagine dragons"
            ),
            viewModel = WebsiteViewModel(),
            position = 1,
            copyText = {},
            openBottomSheet = {},
        )
    }
}