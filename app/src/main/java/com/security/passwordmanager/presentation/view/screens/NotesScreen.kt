package com.security.passwordmanager.presentation.view.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import coil.compose.AsyncImage
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.model.dao.usersdata.Bank
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.model.dao.usersdata.Website
import com.security.passwordmanager.data.model.dao.usersdatachild.Account
import com.security.passwordmanager.data.model.dao.usersdatachild.BankCard
import com.security.passwordmanager.data.model.settings.Settings
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

    val context = LocalContext.current

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


    fun showSnackbar(message: String, actionLabel: String? = null) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel
            )
        }
    }



    BackHandler {
        with(viewModel) {
            when {
                topBarState == TopBarState.Search -> closeSearchbar()
                notifyUserAboutFinishApp -> {
                    scope.launch {
                        notifyUserAboutFinishApp = false
                        delay(5000)
                        notifyUserAboutFinishApp = true
                    }

                    showSnackbar(message = context.getString(R.string.close_app))
                }
                else -> context.getActivity()?.finish()
            }
        }

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
                            DataType.Bank -> {
                                // TODO: 23.03.2023 доделать
                            }
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
                    actionOnNewLine = false,
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
        onRefresh = {
            viewModel.refreshData {
                if (it != null) showSnackbar(it)
            }
        },
        isPullRefreshEnabled = settings.pullToRefresh,
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
                        useBeautifulFont = settings.beautifulFont,
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
    dataList: Map<String, UsersData>,
    fragmentManager: FragmentManager,
    viewModel: NotesViewModel,
    useBeautifulFont: Boolean,
    navigateTo: (route: String) -> Unit,
    isScrolling: @Composable (isScrolling: Boolean, scrollUp: Boolean) -> Unit,
    showSnackbar: (message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    if (viewModel.openedItemId != null) {
        LaunchedEffect(key1 = viewModel.openedItemId) {
            delay(50)
            listState.scrollToItem(
                index = dataList.keys
                    .indexOfFirst { it == viewModel.openedItemId }
                    .coerceAtLeast(0)
            )
        }
    }

    isScrolling(listState.isScrollInProgress, listState.isScrollingUp())


    LazyColumn(state = listState, modifier = modifier) {
        items(dataList.entries.toList()) { (id, data) ->

            Data(
                dataId = id,
                data = data,
                viewModel = viewModel,
                showAll = { viewModel.openedItemId == id },
                showSnackbar = showSnackbar,
                showBottomFragment = { it.show(fragmentManager) },
                navigateTo = navigateTo,
                useBeautifulFont = useBeautifulFont,
                modifier = Modifier
                    .animateContentSize(spring(stiffness = Spring.StiffnessMedium))
                    .animateItemPlacement(spring(stiffness = Spring.StiffnessMedium)),
            ) {
                viewModel.openedItemId = when (viewModel.openedItemId) {
                    id -> null
                    else -> id
                }
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
private fun Data(
    dataId: String,
    data: UsersData,
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
    val subtitle: String
    val logoUrl: String?

    when (data) {
        is Website -> {
            title = data.name
            subtitle = data.address
            logoUrl = data.logoUrl
        }
        is Bank -> {
            title = data.name
            subtitle = data.cards.values.firstOrNull()?.number ?: ""
            logoUrl = null
        }
    }

    val bottomFragment = BottomSheetFragment(
        title = title,
        subtitle = subtitle,
    ) { fragment ->
        EditItem(text = stringResource(R.string.edit)) {
            navigateTo(
                when (data) {
                    is Website -> createRouteToWebsiteScreen(dataId)
                    is Bank -> createRouteToBankCardScreen(dataId)
                }
            )
            fragment.dismiss()
        }
        CopyItem(text = stringResource(R.string.copy_info)) {
            viewModel.copyData(context, data, result = showSnackbar)
            fragment.dismiss()
        }

        DeleteItem(text = stringResource(R.string.delete_data)) {
            viewModel.deleteData(dataId, dataType = data.type)
            fragment.dismiss()
        }
    }



    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background.animate())
    ) {
        DataHeader(
            title = title,
            subtitle = subtitle,
            logoUrl = logoUrl,
            showAllContent = showAll(),
            onClick = onClick
        ) { showBottomFragment(bottomFragment) }
        val iterableItems = when (data) {
            is Bank -> data.cards.values
            is Website -> data.accounts.values
        }

        iterableItems.forEachIndexed { position, item ->
            // TODO: 07.09.2022 подумать над тем, как будут размещатся элементы bank Card

            AnimatedVisibility(
                visible = showAll(),
                enter = viewModel.enterDataChildAnimation,
                exit = viewModel.exitDataChildAnimation
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
                        BottomSheetFragment(
                            title = title,
                            beautifulDesign = useBeautifulFont
                        ) { fragment ->
                            EditItem(text = stringResource(R.string.edit)) {
                                navigateTo(
                                    createRouteToWebsiteScreen(
                                        id = dataId,
                                        startPosition = position
                                    )
                                )
                                fragment.dismiss()
                            }
                        }.also(showBottomFragment)
                    }
                    is BankCard -> BankCard(bankCard = item)
                }
            }
        }

        Divider()
    }
}


@Composable
private fun DataHeader(
    title: String = "",
    subtitle: String = "",
    beautifulDesign: Boolean = false,
    logoUrl: String? = null,
    showAllContent: Boolean = false,
    onClick: () -> Unit,
    showDetails: () -> Unit
) = DataHeader(
    header = Header(title, subtitle, beautifulDesign),
    logoUrl = logoUrl,
    showAllContent = showAllContent,
    onClick = onClick,
    showDetails = showDetails
)


@Composable
private fun DataHeader(
    header: Header = Header(),
    logoUrl: String? = null,
    showAllContent: Boolean,
    onClick: () -> Unit,
    showDetails: () -> Unit
) {
    val arrowRotation = remember { Animatable(90f) }

    
    
    LaunchedEffect(showAllContent) {
        arrowRotation.animateTo(
            targetValue = if (showAllContent) 90f else 0f,
            animationSpec = tween(
                durationMillis = animationTimeMillis / 3,
                easing = LinearEasing
            )
        )
    }

    
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
            imageVector = Icons.Rounded.ArrowForwardIos,
            contentDescription = stringResource(R.string.more_info),
            tint = MaterialTheme.colorScheme.primary.animate(),
            modifier = Modifier
                .rotate(arrowRotation.value)
                .height(20.dp)
                .padding(
                    start = 20.dp,
                    end = 4.dp
                )
        )

        AsyncImage(
            model = logoUrl,
            contentDescription = "logo",
            placeholder = painterResource(R.drawable.default_internet_logo),
            error = painterResource(R.drawable.default_internet_logo),
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.High,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(35.dp)
        )

        Header(header)

        IconButton(
            onClick = showDetails,
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape),
        ) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "show details",
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
private fun Account(
    account: Account,
    copyText: (String) -> Unit,
    openUrl: () -> Unit,
    onClick: (title: String) -> Unit
) {
    val title = account.name.ifBlank { account.login }
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
        if (account.name.isNotBlank()) {
            Text(
                text = account.name,
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
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None
            else PasswordVisualTransformation()
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


@Composable
private fun BankCard(bankCard: BankCard) {
    bankCard.number
}





@ExperimentalMaterial3Api
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
            title = "Imagine Dragons",
            subtitle = "Ragged Insomnia",
            logoUrl = "https://www.spotify.com/apple-touch-icon.png",
            onClick = { showAll = !showAll }
        ) {}
    }
}