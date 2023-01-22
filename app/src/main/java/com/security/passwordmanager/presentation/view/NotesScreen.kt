package com.security.passwordmanager.presentation.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DataArray
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.model.BankCard
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.composablelements.DataTextField
import com.security.passwordmanager.presentation.view.composablelements.SearchBar
import com.security.passwordmanager.presentation.view.composablelements.TopBar
import com.security.passwordmanager.presentation.view.composablelements.TopBarAction
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.navigation.Header
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.CopyItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.DeleteItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.EditItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.navigation.Screen
import com.security.passwordmanager.presentation.view.navigation.createRouteToBankCardScreen
import com.security.passwordmanager.presentation.view.navigation.createRouteToWebsiteScreen
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.screenBorderThickness
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.NotesViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import me.onebone.toolbar.*

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalToolbarApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.NotesScreen(
    title: String,
    isDarkTheme: Boolean,
    dataType: DataType,
    viewModel: NotesViewModel,
    dataViewModel: DataViewModel,
    settingsViewModel: SettingsViewModel,
    fragmentManager: FragmentManager,
    openDrawer: () -> Unit,
    navigateTo: (route: String) -> Unit,
    isDarkStatusBarIcons: (Boolean) -> Unit
) {
    val settings by settingsViewModel.settings.observeAsState(initial = Settings())

    viewModel.isLoading = true
    viewModel.dataList = when {
        viewModel.isSearching -> dataViewModel.searchData(viewModel.query, dataType)
        else -> dataViewModel.getDataUIList(dataType)
    }.observeAsState(listOf(DataUI.DefaultWebsite)).value
    viewModel.isLoading = false

    val toolbarState = rememberCollapsingToolbarState(0)
    val scaffoldState = rememberCollapsingToolbarScaffoldState(toolbarState)

    isDarkStatusBarIcons(toolbarState.progress() < 0.5f && !isDarkTheme)

    viewModel.showTopBar = scaffoldState.toolbarState.progress() != 0f


    val bottomFragment = BottomSheetFragment(
        title = stringResource(R.string.new_note),
        beautifulDesign = true
    ) { fragment ->
        ScreenTypeItem(Screen.Website) {
            navigateTo(createRouteToWebsiteScreen(address = ""))
            fragment.dismiss()
        }

//        ScreenTypeItem(Screen.BankCard) {
//            navigateTo(createRouteToBankCardScreen(dataUI = DataUI.DefaultBankCard))
//            fragment.dismiss()
//        }
    }


    Loading(
        isLoading = viewModel.isLoading ||
                viewModel.dataList.isEmpty() ||
                viewModel.dataList.any { it.title.isEmpty() },
    )
    if (viewModel.isLoading || viewModel.dataList.any { it.title.isEmpty() }) return



    ToolbarWithFabScaffold(
        state = scaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
        toolbar = {
            Toolbar(
                viewModel = viewModel,
                title = title,
                onNavigate = openDrawer,
                modifier = Modifier
                    .progress {
                        println("progress = $it, isDarkTheme = $isDarkTheme")
                        isDarkStatusBarIcons(scaffoldState.toolbarState.scrollProgress < 0.5 && !isDarkTheme)
                    }
                    .pin()
            )
        },
        fab = {
            AnimatedVisibility(
                visible = viewModel.showFab,
                enter = viewModel.enterFabAnimation,
                exit = viewModel.exitFabAnimation,
                modifier = Modifier.animateEnterExit(
                    enter = viewModel.enterScreenFabAnimation,
                    exit = viewModel.exitScreenFabAnimation
                )
            ) {
                FloatingActionButton(
                    onClick = {
                        when (dataType) {
                            DataType.All -> bottomFragment.show(fragmentManager)
                            DataType.Website ->
                                navigateTo(createRouteToWebsiteScreen(address = ""))
                            DataType.BankCard ->
                                TODO("Доделать")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary.animate(),
                    contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    elevation = FloatingActionButtonDefaults.loweredElevation(),
                    modifier = Modifier
                        .padding(
                            WindowInsets.navigationBars
                                .only(WindowInsetsSides.Vertical)
                                .asPaddingValues()
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "add new note"
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary.animate())
                .fillMaxSize()
        ) {
            Column(
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
                        width = if (viewModel.showTopBar) screenBorderThickness else 0.dp,
                        brush = Brush.verticalGradient(
                            0.1f to MaterialTheme.colorScheme.primary.animate(),
                            0.6f to MaterialTheme.colorScheme.background.animate()
                        ),
                        shape = viewModel.screenShape()
                    )
                    .fillMaxSize()
            ) {
                when {
                    viewModel.dataList.isEmpty() -> EmptyList(
                        text = stringResource(
                            if (viewModel.isSearching) R.string.search_no_results else R.string.empty_data_list
                        ),
                        icon = {
                            if (viewModel.isSearching) {
                                Text(
                                    "\\(o_o)/",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 70.sp,
                                    color = MaterialTheme.colorScheme.onBackground.animate(),
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.DataArray,
                                    contentDescription = "empty list",
                                    tint = MaterialTheme.colorScheme.onBackground.animate(),
                                    modifier = Modifier.scale(2.5f)
                                )
                            }
                        }
                    )
                    else -> PasswordList(
                        dataList = viewModel.dataList,
                        dataViewModel = dataViewModel,
                        notesViewModel = viewModel,
                        settings = settings,
                        fragmentManager = fragmentManager,
                        isScrolling = { _, scrollUp ->
                            LaunchedEffect(key1 = scrollUp) {
                                delay(100)
                                viewModel.showFab = scrollUp
                            }
                        },
                        navigateTo = navigateTo,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}



@ExperimentalMaterial3Api
@Composable
private fun Toolbar(
    viewModel: NotesViewModel,
    title: String,
    modifier: Modifier = Modifier,
    onNavigate: () -> Unit
) {
    AnimatedVisibility(
        visible = viewModel.isSearching,
        enter = fadeIn(tween(durationMillis = 600)),
        exit = fadeOut(tween(durationMillis = 600))
    ) {
        SearchBar(
            onQueryChange = viewModel::query::set,
            onCloseSearchBar = { viewModel.isSearching = false },
            modifier = modifier
        )
    }

    AnimatedVisibility(
        visible = !viewModel.isSearching,
        enter = fadeIn(tween(durationMillis = 600)),
        exit = fadeOut(tween(durationMillis = 600)),
    ) {
        TopBar(
            title = title,
            navigationIcon = Icons.Rounded.Menu,
            onNavigate = onNavigate,
            modifier = modifier
        ) {
            TopBarAction(icon = Icons.Filled.Search) {
                viewModel.isSearching = true
            }
        }
    }
}




@Composable
private fun EmptyList(
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit) = {}
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background.animate())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 30.dp, bottom = 20.dp)
        ) {
            icon()
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            textAlign = TextAlign.Center
        )
    }
}



@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun PasswordList(
    dataList: List<DataUI>,
    fragmentManager: FragmentManager,
    dataViewModel: DataViewModel,
    notesViewModel: NotesViewModel,
    settings: Settings,
    navigateTo: (route: String) -> Unit,
    isScrolling: @Composable (isScrolling: Boolean, scrollUp: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var openedItem: DataUI? by rememberSaveable { mutableStateOf(null) }

    if (openedItem != null) {
        LaunchedEffect(key1 = openedItem) {
            delay(50)
            listState.scrollToItem(
                index = dataList.indexOf(openedItem).takeIf { it >= 0 } ?: 0
            )
        }
    }

    isScrolling(listState.isScrollInProgress, listState.isScrollingUp())


    LazyColumn(state = listState, modifier = modifier) {
        items(dataList) { dataUI ->
            DataListItem(
                dataUI = dataUI,
                showAll = { openedItem == dataUI },
                fragmentManager = fragmentManager,
                copyText = { dataViewModel.copyText(context, it) },
                copyAccountList = { dataViewModel.copyAccountList(context, it) },
                useBottomView = settings.isUsingBottomView,
                deleteRecords = { dataViewModel.deleteRecords(it) },
                onClick = {
                    openedItem = if (openedItem == dataUI) null else dataUI
                },
                navigateTo = navigateTo,
                enterAnimation = notesViewModel.enterDataItemAnimation,
                exitAnimation = notesViewModel.exitDataItemAnimation,
                modifier = Modifier
                    .animateContentSize(spring(stiffness = Spring.StiffnessMediumLow))
                    .animateItemPlacement(spring(stiffness = Spring.StiffnessMediumLow))
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth(),
            )
        }
    }
}




@ExperimentalMaterial3Api
@Composable
private fun DataListItem(
    dataUI: DataUI,
    fragmentManager: FragmentManager,
    showAll: () -> Boolean,
    copyText: (String) -> Unit,
    copyAccountList: (List<Data>) -> Unit,
    useBottomView: Boolean,
    deleteRecords: (Data) -> Unit,
    navigateTo: (route: String) -> Unit,
    enterAnimation: EnterTransition,
    exitAnimation: ExitTransition,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
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

    val bottomFragment = BottomSheetFragment(
        title = title,
        subtitle = subtitle,
    ) { fragment ->
        EditItem(text = stringResource(R.string.edit)) {
            navigateTo(
                when (dataUI.title) {
                    is Website -> createRouteToWebsiteScreen(dataUI.title.address)
                    is BankCard -> createRouteToBankCardScreen(dataUI)
                }
            )
            fragment.dismiss()
//            openScreen(dataUI.title.type)
//            context.getActivity()?.startActivity(
//                when (dataUI.title) {
//                    is Website -> WebsiteActivity.getIntent(context, dataUI)
//                    is BankCard -> BankCardActivity.getIntent(context, dataUI)
//                }
//            )
        }
        CopyItem(text = stringResource(R.string.copy_info)) {
            copyAccountList(dataUI.accountList)
            fragment.dismiss()
        }

        DeleteItem(text = stringResource(R.string.delete_password)) {
            deleteRecords(dataUI.title)
            fragment.dismiss()
        }
    }



    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background.animate())
    ) {
        DataItemHeader(
            title = title,
            subtitle = subtitle,
            onClick = onClick,
            openMoreContent = { bottomFragment.show(fragmentManager) }
        )

        dataUI.accountList.forEachIndexed { position, data ->
            AnimatedVisibility(
                visible = showAll(),
                enter = enterAnimation,
                exit = exitAnimation,
            ) {
                // TODO: 07.09.2022 подумать над тем, как будут размещатся элементы bank Card
                when (data) {
                    is Website -> Website(
                        website = data,
                        copyText = copyText,
                        openUrl = { address ->
                            val urlString = when {
                                "www." in address -> "https://$address"
                                "https://www." in address || "http://www." in address -> address
                                else -> "https://www.$address"
                            }

                            if (urlString.isValidUrl()) {
                                val intent = Intent(Intent.ACTION_VIEW, urlString.toUri())
                                context.startActivity(intent)
                            } else
                                showToast(context, "Адрес $address — некорректный!")
                        },
                        onClick = { title ->
                            if (useBottomView) {
                                BottomSheetFragment(title = title, beautifulDesign = true) { fragment ->
                                    EditItem(text = stringResource(R.string.edit)) {
                                        navigateTo(
                                            createRouteToWebsiteScreen(
                                                address = (dataUI.title as Website).address,
                                                startPosition = position
                                            )
                                        )
                                        fragment.dismiss()
                                    }
                                }.show(fragmentManager)
                            }
                        }
                    )
                    is BankCard -> BankCard(
                        bankCard = data
                    )
                }
            }
        }

        Divider(color = DarkerGray)
    }
}


@Composable
private fun DataItemHeader(
    title: String = "",
    subtitle: String = "",
    beautifulDesign: Boolean = false,
    onClick: () -> Unit,
    openMoreContent: () -> Unit
) = DataItemHeader(
    header = Header(title, subtitle, beautifulDesign),
    onClick = onClick,
    openMoreContent = openMoreContent
)


@Composable
private fun DataItemHeader(
    header: Header = Header(),
    onClick: () -> Unit,
    openMoreContent: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.background.animate())
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {
        // TODO: 06.09.2022 сделать анимацию
        Icon(
            Icons.Outlined.ArrowForwardIos,
            contentDescription = stringResource(R.string.more_info),
            tint = MaterialTheme.colorScheme.primary.animate(),
            modifier = Modifier
                .width(40.dp)
                .padding(start = 20.dp, end = 4.dp)
        )

        Header(header)

        IconButton(
            onClick = { openMoreContent() },
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape),
        ) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "show bottom sheet",
                tint = MaterialTheme.colorScheme.onBackground.animate(),
                modifier = Modifier.scale(1.5f)
            )
        }
    }
}


@Composable
private fun RowScope.Header(header: Header) {
    Column(
        modifier = Modifier
            .padding(start = 5.dp)
            .weight(3f)
            .fillMaxWidth()
    ) {
        Text(
            text = header.title,
            style = if (header.beautifulDesign)
                MaterialTheme.typography.titleMedium
            else
                MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = header.subtitle,
            color = DarkerGray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth()
        )
    }
}




@ExperimentalMaterial3Api
@Composable
private fun Website(
    website: Website,
    copyText: (String) -> Unit,
    openUrl: (address: String) -> Unit,
    onClick: (title: String) -> Unit
) {
    val title = website.nameAccount.ifBlank { website.login }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.animate(),
            contentColor = MaterialTheme.colorScheme.onBackground.animate()
        ),
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick(title) }
            )
            .padding(16.dp)
    ) {
        if (website.nameAccount.isNotBlank()) {
            Text(
                text = website.nameAccount,
                color = DarkerGray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            )
            Divider(
                color = DarkerGray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        DataTextField(
            text = website.login,
            heading = stringResource(R.string.login),
        ) {
            CopyIconButton {
                copyText(website.login)
            }
        }

        DataTextField(
            text = website.password,
            heading = stringResource(R.string.password),
            keyboardType = KeyboardType.Password,
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None
            else PasswordVisualTransformation()
        ) {
            VisibilityIconButton(
                visible = isPasswordVisible,
                changeVisibility = { isPasswordVisible = it },
            )
            Spacer(modifier = Modifier.width(8.dp))
            CopyIconButton(modifier = Modifier.padding(end = 8.dp)) {
                copyText(website.password)
            }
        }

        if (website.comment.isNotBlank()) {
            DataTextField(
                text = website.comment,
                heading = stringResource(R.string.comment)
            ) {
                CopyIconButton {
                    copyText(website.comment)
                }
            }
        }

        Button(
            onClick = { openUrl(website.address) },
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


@Composable
private fun BankCard(bankCard: BankCard) {

}





@ExperimentalMaterial3Api
@Preview
@Composable
private fun WebsitePreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        Website(
            website = Website(
                nameWebsite = "Wrecked",
                address = "Imagine Dragons",
                nameAccount = "Test",
                login = "petrovsd2002@yandex.ru",
                password = "qwertyuiop123",
                comment = "Kaif",
            ),
            {}, {}, {},
        )
    }
}


@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun DataListItemPreview() {
    val website = Website(
        nameWebsite = "Wrecked",
        address = "Imagine Dragons",
        login = "qwerevmlkkiekekxxkxiekdkd",
        password = "12sskskxkxksmmsmsmssmms"
    )
    PasswordManagerTheme(isDarkTheme = false) {
        DataListItem(
            dataUI = DataUI(
                title = website,
                accountList = mutableListOf(website, Website(), Website())
            ),
            fragmentManager = AppCompatActivity().supportFragmentManager,
            showAll = { true },
            copyText = {},
            copyAccountList = {},
            navigateTo = {},
            deleteRecords = {},
            onClick = {},
            useBottomView = true,
            enterAnimation = fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing,
                ),
            ) + expandVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing,
                ),
            ),
            exitAnimation = fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing
                )
            ) + shrinkVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing,
                ),
            )
        )
    }
}