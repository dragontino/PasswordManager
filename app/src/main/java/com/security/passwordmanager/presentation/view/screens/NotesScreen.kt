package com.security.passwordmanager.presentation.view.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DataArray
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.ObservableBankCard
import com.security.passwordmanager.presentation.model.ObservableWebsite
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.BottomSheetFragment
import com.security.passwordmanager.presentation.view.composablelements.*
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.composablelements.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.navigation.AppScreens
import com.security.passwordmanager.presentation.view.navigation.Header
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.CopyItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.DeleteItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.EditItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.navigation.createRouteToBankCardScreen
import com.security.passwordmanager.presentation.view.navigation.createRouteToWebsiteScreen
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.screenBorderThickness
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.NotesViewModel
import com.security.passwordmanager.presentation.viewmodel.NotesViewModel.TopBarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.*

@ExperimentalMaterialApi
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
    settings: Settings,
    fragmentManager: FragmentManager,
    openDrawer: () -> Unit,
    navigateTo: (route: String) -> Unit,
    isDarkStatusBarIcons: (Boolean) -> Unit
) {
    viewModel.dataType = dataType

    val scope = rememberCoroutineScope()

    val toolbarState = rememberCollapsingToolbarState()
    val scaffoldState = rememberCollapsingToolbarScaffoldState(toolbarState)
    val snackbarHostState = remember(::SnackbarHostState)

//    isDarkStatusBarIcons(toolbarState.progress() < 0.5f && !isDarkTheme)

//    if (scaffoldState.toolbarState.progress() == 0f)
//        viewModel.topBarState = DataViewModel.TopBarState.Hidden
//    else viewModel.topBarState = DataViewModel.TopBarState.Navigate


    val bottomFragment = BottomSheetFragment(
        title = stringResource(R.string.new_note),
        beautifulDesign = true
    ) { fragment ->
        ScreenTypeItem(AppScreens.Website) {
            navigateTo(createRouteToWebsiteScreen())
            fragment.dismiss()
        }

//        ScreenTypeItem(Screen.BankCard) {
//            navigateTo(createRouteToBankCardScreen(dataUI = DataUI.DefaultBankCard))
//            fragment.dismiss()
//        }
    }


    val showSnackbar = { message: String ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }



    ScrollableTopBarScaffold(
        state = scaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
        topBar = {
            Toolbar(
                viewModel = viewModel,
                title = title,
                onNavigate = openDrawer,
                modifier = Modifier
                    .progress {
                        println("progress = $it, isDarkTheme = $isDarkTheme")
                        isDarkStatusBarIcons(scaffoldState.toolbarState.scrollProgress < 0.5 && !isDarkTheme)
                    }
            )
        },
        floatingActionButton = {
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
                                navigateTo(createRouteToWebsiteScreen())
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
        contentShape = viewModel.screenShape(),
        contentBorder = BorderStroke(
            width = when (viewModel.topBarState) {
                TopBarState.Hidden -> 0.dp
                else -> screenBorderThickness
            },
            brush = Brush.verticalGradient(
                0.1f to MaterialTheme.colorScheme.primary.animate(),
                0.6f to MaterialTheme.colorScheme.background.animate()
            ),
        ),
        refreshing = viewModel.viewModelState == DataViewModel.DataViewModelState.Loading,
        onRefresh = viewModel::refreshData,
        isPullRefreshEnabled = !settings.disablePullToRefresh,
        pullRefreshIndicator = {
            PullRefreshIndicator(
                refreshing = viewModel.viewModelState == DataViewModel.DataViewModelState.Loading,
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background
            )
        },
        contentModifier = Modifier.fillMaxSize()
    ) {
        Crossfade(
            targetState = viewModel.viewModelState,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
        ) {
            when (it) {
                DataViewModel.DataViewModelState.Loading -> {
                    LoadingInBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )
                }
                DataViewModel.DataViewModelState.EmptyList -> with(viewModel) {
                    EmptyList(
                        text = when {
                            topBarState == TopBarState.Search && query.isBlank() ->
                                stringResource(R.string.empty_query)
                            topBarState == TopBarState.Search ->
                                stringResource(R.string.search_no_results)
                            else ->
                                stringResource(R.string.empty_data_list)
                        },
                        icon = {
                            when (topBarState) {
                                TopBarState.Search -> {
                                    if (query.isNotBlank()) {
                                        Text(
                                            "\\(o_o)/",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontSize = 70.sp,
                                            color = MaterialTheme.colorScheme.onBackground.animate(),
                                        )
                                    }
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Rounded.DataArray,
                                        contentDescription = "empty list",
                                        tint = MaterialTheme.colorScheme.onBackground.animate(),
                                        modifier = Modifier.scale(2.5f)
                                    )
                                }
                            }
                        }
                    )
                }
                DataViewModel.DataViewModelState.Ready -> {
                    PasswordList(
                        dataList = viewModel.dataList,
                        viewModel = viewModel,
                        fragmentManager = fragmentManager,
                        useBeautifulFont = settings.isUsingBeautifulFont,
                        isScrolling = { _, scrollUp ->
                            LaunchedEffect(key1 = scrollUp) {
                                delay(100)
                                viewModel.showFab = scrollUp
                            }
                        },
                        navigateTo = navigateTo,
                        showSnackbar = { msg -> showSnackbar(msg) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}




@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
private fun CollapsingToolbarScope.Toolbar(
    viewModel: NotesViewModel,
    title: String,
    modifier: Modifier = Modifier,
    onNavigate: () -> Unit
) {

    AnimatedContent(
        targetState = viewModel.topBarState,
        transitionSpec = {
            val anim = tween<IntOffset>(durationMillis = 600)
            val offsetSign = when (initialState) {
                TopBarState.Navigate -> 1
                else -> -1
            }
            slideInHorizontally(anim) { offsetSign * it / 2 } with
                    slideOutHorizontally(anim) { -offsetSign * it / 2 }
        }
    ) { state ->
        if (state == TopBarState.Search) {
            SearchBar(
                query = viewModel.query,
                onQueryChange = viewModel::query::set,
                onCloseSearchBar = viewModel::closeSearchbar,
                modifier = modifier
            )
        } else {
            ScrollableTopBar(
                title = title,
                navigationButton = {
                    ToolbarButton(
                        icon = Icons.Rounded.Menu,
                        contentDescription = title,
                        onClick = onNavigate
                    )
                },
                actions = {
                    ToolbarButton(
                        icon = Icons.Rounded.Search,
                        modifier = Modifier.animateEnterExit(
                            enter = slideInHorizontally(
                                tween(durationMillis = 400)
                            ),
                            exit = slideOutHorizontally(
                                tween(durationMillis = 400)
                            )
                        ),
                        onClick = viewModel::openSearchbar
                    )
                },
                modifier = modifier
            )
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
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.padding(top = 30.dp, bottom = 20.dp)) {
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
    viewModel: NotesViewModel,
    useBeautifulFont: Boolean,
    navigateTo: (route: String) -> Unit,
    isScrolling: @Composable (isScrolling: Boolean, scrollUp: Boolean) -> Unit,
    showSnackbar: (message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    if (viewModel.openedItem != null) {
        LaunchedEffect(key1 = viewModel.openedItem) {
            delay(50)
            listState.scrollToItem(
                index = dataList.indexOf(viewModel.openedItem).takeIf { it >= 0 } ?: 0
            )
        }
    }

    isScrolling(listState.isScrollInProgress, listState.isScrollingUp())


    LazyColumn(state = listState, modifier = modifier) {
        items(dataList) { dataUI ->
            DataListItem(
                dataUI = dataUI,
                viewModel = viewModel,
                showAll = { viewModel.openedItem == dataUI },
                showSnackbar = showSnackbar,
                showBottomFragment = { it.show(fragmentManager) },
                navigateTo = navigateTo,
                useBeautifulFont = useBeautifulFont,
                modifier = Modifier
                    .animateContentSize(spring(stiffness = Spring.StiffnessMedium))
                    .animateItemPlacement(spring(stiffness = Spring.StiffnessMedium)),
            ) {
                viewModel.openedItem = if (viewModel.openedItem == dataUI) null else dataUI
            }
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
    viewModel: NotesViewModel,
    showAll: () -> Boolean,
    navigateTo: (route: String) -> Unit,
    showSnackbar: (message: String) -> Unit,
    showBottomFragment: (fragment: BottomSheetFragment) -> Unit,
    useBeautifulFont: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val title: String
    val subtitle = when (dataUI.title) {
        is ObservableWebsite -> {
            title = dataUI.title.nameWebsite
            dataUI.title.address
        }
        is ObservableBankCard -> {
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
                    is ObservableWebsite ->
                        createRouteToWebsiteScreen(address = dataUI.title.address)
                    is ObservableBankCard ->
                        createRouteToBankCardScreen(bankName = dataUI.title.bankName)
                }
            )
            fragment.dismiss()
        }
        CopyItem(text = stringResource(R.string.copy_info)) {
            viewModel.copyAccountList(
                context = context,
                accountList = dataUI.accountList.map { it.toData() },
                resultMessage = showSnackbar
            )
            fragment.dismiss()
        }

        DeleteItem(text = stringResource(R.string.delete)) {
            viewModel.deleteRecords(dataUI.title.toData())
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
            openMoreContent = { showBottomFragment(bottomFragment) }
        )

        dataUI.accountList.forEachIndexed { position, observableData ->
            AnimatedVisibility(
                visible = showAll(),
                enter = viewModel.enterDataItemAnimation,
                exit = viewModel.exitDataItemAnimation,
            ) {
                // TODO: 07.09.2022 подумать над тем, как будут размещатся элементы bank Card
                when (observableData) {
                    is ObservableWebsite -> Website(
                        website = observableData,
                        copyText = {
                            viewModel.copyText(
                                context = context,
                                text = it,
                                resultMessage = showSnackbar
                            )
                        },
                        openUrl = { address ->
                            val urlString = when {
                                "www." in address -> "https://$address"
                                "https://www." in address || "http://www." in address -> address
                                else -> "https://www.$address"
                            }

                            if (urlString.isValidUrl()) {
                                val intent = Intent(Intent.ACTION_VIEW, urlString.toUri())
                                context.startActivity(intent)
                            } else {
                                showSnackbar(context.getString(R.string.invalid_address, address))
                            }
                        },
                        onClick = { title ->
                            BottomSheetFragment(
                                title = title,
                                beautifulDesign = useBeautifulFont
                            ) { fragment ->
                                EditItem(text = stringResource(R.string.edit)) {
                                    navigateTo(
                                        createRouteToWebsiteScreen(
                                            address = observableData.address,
                                            startPosition = position
                                        )
                                    )
                                    fragment.dismiss()
                                }
                            }.also(showBottomFragment)
                        }
                    )
                    is ObservableBankCard -> BankCard(
                        bankCard = observableData
                    )
                }
            }
        }

        Divider()
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
    website: ObservableWebsite,
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
private fun BankCard(bankCard: ObservableBankCard) {
    bankCard.cardNumber
}





@ExperimentalMaterial3Api
@Preview
@Composable
private fun WebsitePreview() {
    PasswordManagerTheme(isDarkTheme = true) {
        Website(
            website = ObservableWebsite(
                nameWebsite = "Wrecked",
                address = "Imagine Dragons",
                nameAccount = "Test",
                login = "petrovsd2002@yandex.ru",
                password = "qwerty123",
                comment = "Chill",
            ),
            {}, {}, {},
        )
    }
}