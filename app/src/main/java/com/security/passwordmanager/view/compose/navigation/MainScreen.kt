package com.security.passwordmanager.view.compose.navigation

import android.app.Application
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.DataArray
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.model.*
import com.security.passwordmanager.view.BankCardActivity
import com.security.passwordmanager.view.WebsiteActivity
import com.security.passwordmanager.view.compose.BottomSheetContent
import com.security.passwordmanager.view.compose.BottomSheetItems
import com.security.passwordmanager.viewmodel.DataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
internal fun MainScreen(
    screenType: ScreenType,
    dataViewModel: DataViewModel = DataViewModel(Application()),
    openDrawer: () -> Unit = {},
    openUrl: (address: String) -> Unit = {},
    bottomSheetState: ModalBottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, BottomAnimationSpec)
) {
    val scope = rememberCoroutineScope()
    val dataList: MutableState<List<DataUI>> = remember { mutableStateOf(emptyList()) }

    val getDataList = {
        scope.launch {
            dataList.value = dataViewModel.getDataUIList(screenType.toDataType())
        }
    }

    val search = remember { mutableStateOf(false) }
    if (!search.value)
        getDataList()

    Column(modifier = Modifier.fillMaxSize()) {
        if (search.value) {
            SearchBar(
                onQueryChange = {
                    scope.launch {
                        dataList.value = dataViewModel.searchData(it, screenType.toDataType())
                    }
                },
                onCloseSearchBar = {
                    search.value = false
                    getDataList()
                }
            )
        }
        else {
            TopBar(
                title = stringResource(screenType.pluralTitleRes),
                navigationIcon = Icons.Filled.Menu,
                actions = listOf(
                    TopBarAction(icon = Icons.Filled.Search, onClick = { search.value = true })
                ),
                onNavigate = openDrawer
            )
        }

        if (dataList.value.isEmpty()) {
            if (search.value)
                EmptyList(text = stringResource(R.string.search_no_results))
            else
                EmptyList(
                    text = stringResource(R.string.empty_data_list),
                    icon = Icons.Rounded.DataArray
                )
        }

        else PasswordList(
            dataList = dataList.value,
//            sheetState = bottomSheetState,
            dataViewModel = dataViewModel,
            openUrl = openUrl
        )
    }
}




@Composable
private fun EmptyList(text: String, icon: ImageVector? = null) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon == null) {
            Text(
                "\\(o_o)/",
                fontSize = 70.sp,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(top = 30.dp, bottom = 20.dp)
            )
        } else {
            Icon(
                icon,
                contentDescription = text,
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .scale(2.5f)
                    .padding(top = 30.dp, bottom = 20.dp)
            )
        }

        Text(
            text,
            fontSize = 18.sp,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
private fun PasswordList(
    dataList: List<DataUI>,
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = BottomAnimationSpec
    ),
    dataViewModel: DataViewModel,
    openUrl: (address: String) -> Unit
) {
    val openedItem: MutableState<DataUI?> = rememberSaveable { mutableStateOf(null) }
    val titleWithSubtitle = remember { mutableStateOf(TitleWithSubtitle()) }
    val rememberedDataUI: MutableState<DataUI> = remember { mutableStateOf(DataUI.Website) }
    val drawBeautifulDesign = remember { mutableStateOf(false) }
    val activity = LocalContext.current.getActivity()

    val scope = rememberCoroutineScope { Dispatchers.IO }

    val openBottomSheet = {
        scope.launch { sheetState.show() }
    }

    val closeBottomSheet = {
        scope.launch { sheetState.hide() }
    }

    val onHeaderIconButtonClick: (TitleWithSubtitle, DataUI) -> Unit = { tws, data ->
        titleWithSubtitle.value = tws
        drawBeautifulDesign.value = false
        rememberedDataUI.value = data
        openBottomSheet()
    }

    val onItemClick: (Data, position: Int) -> Unit = { d, position ->
        // TODO: 08.09.2022 сделать
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            BottomSheetContent(
                titleWithSubtitle = titleWithSubtitle.value,
                drawBeautifulDesign = drawBeautifulDesign.value,
                bottomItems = arrayOf(
                    BottomSheetItems.edit(text = stringResource(R.string.edit)),
                    BottomSheetItems.copy(text = stringResource(R.string.copy_info)),
                    BottomSheetItems.delete(text = stringResource(R.string.delete_password))
                )
            ) {
                activity?.startActivity(
                    when(rememberedDataUI.value.title) {
                        is Website ->  WebsiteActivity.getIntent(activity, rememberedDataUI.value)
                        is BankCard -> BankCardActivity.getIntent(activity, rememberedDataUI.value)
                    }
                )
                closeBottomSheet()
            }
        },
        sheetShape = BottomSheetShape,
        sheetBackgroundColor = MaterialTheme.colors.background
    ) {

//        Column(
//            modifier = Modifier
//                .verticalScroll(rememberScrollState())
//                .fillMaxWidth()
//        ) {
//            repeat(dataList.size) {
//                val dataUI = dataList[it]
//                DataListItem(
//                    dataUI,
//                    showAll = openedItem.value == dataUI,
//                    copyText = dataViewModel::copyText,
//                    openUrl = openUrl,
//                    onHeaderClick = {
//                        openedItem.value =
//                            if (openedItem.value == dataUI) null
//                            else dataUI
//                        Log.d("OpenItem", (openedItem.value?.accountList?.size ?: 0).toString())
//                    },
//                    onHeaderIconButtonClick = onHeaderIconButtonClick,
//                    // TODO: 07.09.2022 сделать onItemClick
//                    onItemClick = { tws, d, position ->
//                        titleWithSubtitle.value = tws
//                        drawBeautifulDesign.value = true
//                        onItemClick(d, position)
//                        openBottomSheet()
//                    },
////                    modifier = Modifier.animateItemPlacement(
////                        animationSpec = spring(
////                            stiffness = Spring.StiffnessLow,
////                            visibilityThreshold = IntOffset.VisibilityThreshold
////                        )
////                    )
//                )
//            }
//
//            Spacer(
//                modifier = Modifier
//                    .height(80.dp)
//                    .fillMaxWidth(),
//            )
//        }

        LazyColumn {
//            repeat(250) {
//                item {
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        modifier = Modifier
//                            .animateContentSize()
//                            .animateItemPlacement(
//                                animationSpec = spring(
//                                    stiffness = Spring.StiffnessLow,
//                                    visibilityThreshold = IntOffset.VisibilityThreshold
//                                )
//                            )
//                            .fillMaxWidth()
//                    ) {
//                        Text(
//                            "Item №${it + 1}",
//                            textAlign = TextAlign.Center,
//                            fontSize = 22.sp,
//                            modifier = Modifier.padding(8.dp)
//                        )
//                    }
//
//                    Divider()
//                }
//            }
////            items(250) { index ->
////                Row(
////                    horizontalArrangement = Arrangement.Center,
////                    modifier = Modifier
////                        .animateContentSize()
////                        .animateItemPlacement(
////                            animationSpec = spring(
////                                stiffness = Spring.StiffnessLow,
////                                visibilityThreshold = IntOffset.VisibilityThreshold
////                            )
////                        )
////                        .fillMaxWidth()
////                ) {
////                    Text(
////                        "Item №${index + 1}",
////                        textAlign = TextAlign.Center,
////                        fontSize = 22.sp,
////                        modifier = Modifier.padding(8.dp)
////                    )
////                }
////
////                Divider()
////            }
//            repeat(dataList.size) {
//                item {
//                    val dataUI = dataList[it]
//                    Log.d("DataItem", dataUI.title.key)
//                    DataListItem(
//                        dataUI,
//                        showAll = openedItem.value == dataUI,
//                        copyText = dataViewModel::copyText,
//                        openUrl = openUrl,
//                        onHeaderClick = {
//                            openedItem.value =
//                                if (openedItem.value == dataUI) null
//                                else dataUI
//                            Log.d("OpenItem", (openedItem.value?.accountList?.size ?: 0).toString())
//                        },
//                        onHeaderIconButtonClick = onHeaderIconButtonClick,
//                        // TODO: 07.09.2022 сделать onItemClick
//                        onItemClick = { tws, d, position ->
//                            titleWithSubtitle.value = tws
//                            drawBeautifulDesign.value = true
//                            onItemClick(d, position)
//                            openBottomSheet()
//                        },
//                        modifier = Modifier.animateItemPlacement(
//                            animationSpec = spring(
//                                stiffness = Spring.StiffnessLow,
//                                visibilityThreshold = IntOffset.VisibilityThreshold
//                            )
//                        )
//                    )
//                }
//            }
            items(dataList) { dataUI ->
                Log.d("DataItem", dataUI.title.key)
                DataListItem(
                    dataUI,
                    showAll = openedItem.value == dataUI,
                    copyText = dataViewModel::copyText,
                    openUrl = openUrl,
                    onHeaderClick = {
                        openedItem.value =
                            if (openedItem.value == dataUI) null
                            else dataUI
                        Log.d("OpenedItem", (openedItem.value?.accountList?.size ?: 0).toString())
                    },
                    onHeaderIconButtonClick = onHeaderIconButtonClick,
                    // TODO: 07.09.2022 сделать onItemClick
                    onItemClick = { tws, d, position ->
                        titleWithSubtitle.value = tws
                        drawBeautifulDesign.value = true
                        onItemClick(d, position)
                        openBottomSheet()
                    },
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                    )
                )
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(),
                )
            }
        }
    }
}



@ExperimentalMaterialApi
@Composable
private fun DataListItem(
    dataUI: DataUI,
    showAll: Boolean,
    copyText: (String) -> Unit,
    openUrl: (address: String) -> Unit,
    onHeaderClick: () -> Unit,
    onHeaderIconButtonClick: (TitleWithSubtitle, DataUI) -> Unit,
    onItemClick: (TitleWithSubtitle, Data, position: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val title: String
    val subtitle = when (dataUI.title) {
        is Website -> {
            title = dataUI.title.nameWebsite
            dataUI.title.address
        }
        is BankCard -> {
            title = dataUI.title.bankName
            dataUI.title.cardNumber
        }
    }
    Column(
        modifier = modifier.background(MaterialTheme.colors.background)
    ) {
        DataItemHeader(
            title = title,
            subtitle = subtitle,
            onClick = onHeaderClick,
            openBottomSheet = { onHeaderIconButtonClick(TitleWithSubtitle(title, subtitle), dataUI) }
        )

        if (showAll) {
            DataListItemExpanded(dataUI.accountList, copyText, openUrl, onItemClick)
        }

        Divider(color = colorResource(android.R.color.darker_gray))
    }
}


@ExperimentalMaterialApi
@Composable
private fun DataItemHeader(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    openBottomSheet: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(MaterialTheme.colors.background)
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {
        // TODO: 06.09.2022 сделать анимацию
        Icon(
            Icons.Outlined.ArrowForwardIos,
            contentDescription = stringResource(R.string.more_info),
            tint = MaterialTheme.colors.primary,
            modifier = Modifier
                .width(40.dp)
                .padding(start = 20.dp, end = 4.dp)
        )
        TitleWithSubtitle(title, subtitle)
        IconButton(
            onClick = { openBottomSheet() },
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape),
        ) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "show bottom sheet",
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier.scale(1.5f)
            )
        }
    }
}


@Composable
private fun RowScope.TitleWithSubtitle(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .padding(start = 5.dp)
            .weight(3f)
            .fillMaxWidth()
    ) {

        Text(
            text = title,
            fontSize = 20.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = subtitle,
            color = colorResource(android.R.color.darker_gray),
            fontSize = 17.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}






@Composable
private fun ColumnScope.DataListItemExpanded(
    accountList: List<Data>,
    copyText: (String) -> Unit,
    openUrl: (address: String) -> Unit,
    onItemClick: (TitleWithSubtitle, Data, position: Int) -> Unit
) {
    // TODO: 07.09.2022 подумать над тем, как будут размещатся элементы bank Card

    accountList.forEachIndexed { position, data ->
        when (data) {
            is Website -> Website(
                website = data,
                copyText,
                openUrl,
                onWebsiteClick = { onItemClick(it, data, position) }
            )
            is BankCard -> BankCard(
                bankCard = data
            )
        }
    }
}


@Composable
private fun Website(
    website: Website,
    copyText: (String) -> Unit,
    openUrl: (address: String) -> Unit,
    onWebsiteClick: (TitleWithSubtitle) -> Unit
) {
    val title = website.nameAccount.ifBlank { website.login }

    Card(
        shape = DataCardShape,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onBackground,
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onWebsiteClick(TitleWithSubtitle(title = title)) }
            .padding(3.dp)
            .fillMaxWidth()
    ) {
        Column {
            if (website.nameAccount.isNotBlank()) {
                Text(
                    website.nameAccount,
                    color = colorResource(android.R.color.darker_gray),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                )
                Divider(
                    color = colorResource(android.R.color.darker_gray),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            DataField(
                heading = stringResource(R.string.login),
                dataItem = website.login,
                onCopyClick = copyText
            )

            DataField(
                heading = stringResource(R.string.password),
                dataItem = website.password,
                drawButtonVisibility = true,
                onCopyClick = copyText
            )

            if (website.comment.isNotBlank())
                DataField(
                    heading = stringResource(R.string.comment),
                    dataItem = website.comment,
                    onCopyClick = copyText
                )

            Button(
                onClick = { openUrl(website.address) },
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White,
                    disabledBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.6f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.button_open_url),
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Composable
private fun ColumnScope.DataField(
    heading: String,
    dataItem: String,
    drawButtonVisibility: Boolean = false,
    drawButtonCopy: Boolean = true,
    onCopyClick: (String) -> Unit
) {
    val passwordIsVisible = rememberSaveable { mutableStateOf(false) }

    TextField(
        value = dataItem,
        onValueChange = {},
        readOnly = true,
        textStyle = TextStyle(fontSize = 17.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (!drawButtonVisibility || passwordIsVisible.value)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onBackground,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        label = {
            Text(
                text = heading,
                fontSize = 12.5.sp,
                color = MaterialTheme.colors.onBackground
            )
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                if (drawButtonVisibility)
                    WebsiteIconButton(
                        if (passwordIsVisible.value) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        contentDescription = stringResource(R.string.show_password),
                        modifier = Modifier.padding(end = 1.dp),
                    ) {
                        passwordIsVisible.value = !passwordIsVisible.value
//                        Log.d("PasswordVisibleTest", passwordIsVisible.value.toString())
                    }

                if (drawButtonCopy) {
                    WebsiteIconButton(
                        Icons.Outlined.ContentCopy,
                        contentDescription = stringResource(R.string.copy_info),
                        onClick = { onCopyClick(dataItem) },
                    )
                }
            }
        },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(),
    )
}


@Composable
private fun WebsiteIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.background(Color.Transparent)
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.size(25.dp)
        )
    }
}


@Composable
private fun BankCard(bankCard: BankCard) {

}


@Preview
@Composable
private fun WebsitePreview() {
    Website(website = Website(
        nameWebsite = "Wrecked",
        address = "Imagine Dragons",
        nameAccount = "Test",
        login = "petrovsd2002@yandex.ru",
        password = "qwertyuiop123",
        comment = "Kaif"
    ), {}, {}, {})
}


@ExperimentalMaterialApi
@Preview
@Composable
fun DataListItemPreview() {
    val website = Website(
        nameWebsite = "Wrecked",
        address = "Imagine Dragons",
        login = "qwerevmlkkiekekxxkxiekdkd",
        password = "12sskskxkxksmmsmsmssmms"
    )
    DataListItem(
        dataUI = DataUI(
            title = website,
            accountList = mutableListOf(website, Website(), Website())
        ),
        showAll = true,
        copyText = {},
        openUrl = {},
        onHeaderClick = {},
        onHeaderIconButtonClick = { _, _ -> },
        onItemClick = { _ , _ , _ -> }
    )
}