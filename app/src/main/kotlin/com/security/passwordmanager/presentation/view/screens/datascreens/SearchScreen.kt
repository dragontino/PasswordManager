package com.security.passwordmanager.presentation.view.screens.datascreens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.security.passwordmanager.LoadingInBox
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffold
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffoldDefaults
import com.security.passwordmanager.presentation.view.composables.managment.ToolbarType
import com.security.passwordmanager.presentation.view.composables.managment.rememberScrollableScaffoldState
import com.security.passwordmanager.presentation.view.navigation.ModalSheetDefaults
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.SearchScreenAnimation
import com.security.passwordmanager.presentation.viewmodel.DataViewModel.DataViewModelState
import com.security.passwordmanager.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun AnimatedVisibilityScope.SearchScreen(
    viewModel: SearchViewModel,
    dataType: DataType,
    settings: Settings,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit
) {
    viewModel.dataType = dataType

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScrollableScaffoldState(
        lazyListState = rememberLazyListState(),
        toolbarType = ToolbarType.PinnedToolbar,
        maxToolbarAlpha = 0f
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = ModalSheetDefaults.AnimationSpec,
        skipHalfExpanded = true
    )


    val showBottomSheet = {
        coroutineScope.launch {
            delay(50)
            bottomSheetState.show()
        }
    }

    val hideBottomSheet = {
        coroutineScope.launch { bottomSheetState.hide() }
    }

    fun showSnackbar(message: String, actionLabel: String? = null) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel
            )
        }
    }



    BackHandler(onBack = popBackStack)

    ModalBottomSheetLayout(
        sheetContent = viewModel.bottomSheetContent,
        sheetState = bottomSheetState,
        sheetShape = ModalSheetDefaults.Shape,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface.animate()
    ) {
        ScrollableToolbarScaffold(
            state = scaffoldState,
            modifier = Modifier.animateEnterExit(
                enter = SearchScreenAnimation.enter,
                exit = SearchScreenAnimation.exit
            ),
            contentModifier = Modifier
                .imePadding()
                .fillMaxSize(),
            topBar = {
                SearchField(
                    query = viewModel.query,
                    onQueryChange = viewModel::query::set,
                    onClose = popBackStack,
                    modifier = Modifier
                        .windowInsetsPadding(
                            insets = WindowInsets.statusBars.only(WindowInsetsSides.Top)
                        )
                        .padding(horizontal = 38.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .placeholder(
                            visible = viewModel.viewModelState == DataViewModelState.PreLoading,
                            color = MaterialTheme.colorScheme.primaryContainer.animate(),
                            shape = MaterialTheme.shapes.large,
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = MaterialTheme.colorScheme.onPrimaryContainer.animate()
                            )
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
            isPullRefreshEnabled = true,
            onRefresh = {
                viewModel.refreshData {
                    if (it != null) showSnackbar(it)
                }
            },
            colors = ScrollableToolbarScaffoldDefaults.colors(
                toolbarContainerColor = MaterialTheme.colorScheme.background,
                transparentToolbarContainerColor = Color.Transparent,
            )
        ) {

            Crossfade(
                targetState = viewModel.viewModelState,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "search_loading"
            ) { state ->
                when (state) {
                    DataViewModelState.PreLoading -> PreLoadingScreen(
                        modifier = Modifier.fillMaxSize()
                    )

                    DataViewModelState.Loading -> LoadingInBox(
                        loadingModifier = Modifier.scale(1.4f),
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )

                    DataViewModelState.EmptyList -> EmptyList(
                        text = when {
                            viewModel.query.isBlank() -> stringResource(R.string.empty_query)
                            else -> stringResource(R.string.search_no_results)
                        },
                        icon = {
                            if (viewModel.query.isNotBlank()) {
                                Text(
                                    "\\(o_o)/",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 70.sp,
                                    color = MaterialTheme.colorScheme.onBackground.animate(),
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )

                    DataViewModelState.Ready -> DataScreen(
                        scaffoldState = scaffoldState,
                        viewModel = viewModel,
                        showBottomSheet = { showBottomSheet() },
                        hideBottomSheet = { hideBottomSheet() },
                        useBeautifulFont = settings.beautifulFont,
                        loadIcons = settings.loadIcons,
                        navigateTo = navigateTo,
                        showSnackbar = { showSnackbar(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


@Composable
private fun SearchField(
    query: String,
    onQueryChange: (query: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(80)
        focusRequester.requestFocus()
    }

    val textColor = MaterialTheme.colorScheme.onBackground.animate()
    val containerColor = MaterialTheme.colorScheme.primaryContainer.animate()
    val placeholderColor = textColor.copy(alpha = .6f).animate()

    TextField(
        value = query,
        onValueChange = onQueryChange,
        maxLines = 6,
        placeholder = {
            Text(
                text = stringResource(R.string.search_label),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "search",
                modifier = Modifier.padding(start = 6.dp)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    onClose()
                },
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "close searchbar")
            }
        },
        shape = MaterialTheme.shapes.large,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            cursorColor = MaterialTheme.colorScheme.onPrimary.animate(),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLeadingIconColor = textColor,
            unfocusedLeadingIconColor = textColor,
            focusedTrailingIconColor = textColor,
            unfocusedTrailingIconColor = textColor,
            focusedPlaceholderColor = placeholderColor,
            unfocusedPlaceholderColor = placeholderColor,
            selectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.secondary.animate(),
                backgroundColor = MaterialTheme.colorScheme.onSecondary.animate()
            )
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .then(modifier)
    )
}


@Composable
private fun PreLoadingScreen(modifier: Modifier = Modifier) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val itemsCount = 8
    val itemsSpace = 16.dp

    Column(
        modifier = modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(itemsSpace)
    ) {
        repeat(itemsCount) {
            Box(
                modifier = Modifier
                    .height(screenHeight / itemsCount - itemsSpace)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        color = Color.LightGray,
                        shape = MaterialTheme.shapes.small,
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = Color.White
                        )
                    )
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .then(modifier)
    ) {
        icon()

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
private fun SearchFieldPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        SearchField(
            query = """
                Imagine Dragons
                Ragged Insomnia
                """.trimIndent(),
            onQueryChange = {},
            onClose = {}
        )
    }
}