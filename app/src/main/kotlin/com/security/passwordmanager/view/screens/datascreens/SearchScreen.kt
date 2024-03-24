package com.security.passwordmanager.view.screens.datascreens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.util.LoadingInBox
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffold
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffoldDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingAppBarDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.rememberAppBarState
import com.security.passwordmanager.view.theme.AnyScreenAnimation
import com.security.passwordmanager.view.theme.PasswordManagerTheme
import com.security.passwordmanager.view.theme.SearchBarAnimation
import com.security.passwordmanager.viewmodel.ListState
import com.security.passwordmanager.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AnimatedVisibilityScope.SearchScreen(
    viewModel: SearchViewModel,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val contentState = rememberLazyListState()
    val topBarState = rememberAppBarState(maxAlpha = 0f)
    val snackbarHostState = remember(::SnackbarHostState)

    val showSnackbar = remember {
        fun(message: String) {
            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
        }
    }


    BackHandler(onBack = popBackStack)


    CollapsingToolbarScaffold(
        contentState = contentState,
        modifier = Modifier.animateEnterExit(
            enter = AnyScreenAnimation.enter,
            exit = AnyScreenAnimation.exit
        ),
        contentModifier = Modifier.fillMaxSize(),
        topBar = {
            SearchField(
                query = viewModel.query.value,
                onQueryChange = viewModel.query::value::set,
                onClose = popBackStack,
                modifier = Modifier
                    .animateEnterExit(
                        enter = SearchBarAnimation.enter,
                        exit = SearchBarAnimation.exit
                    )
                    .windowInsetsPadding(
                        insets = WindowInsets.statusBars.only(WindowInsetsSides.Top)
                    )
                    .padding(horizontal = 38.dp, vertical = 4.dp)
                    .fillMaxWidth()
            )
        },
        topBarScrollBehavior = CollapsingAppBarDefaults.pinnedScrollBehavior(topBarState),
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
        pullToRefreshEnabled = true,
        onRefresh = viewModel::refreshData,
        colors = CollapsingToolbarScaffoldDefaults.colors(
            toolbarContainerColor = MaterialTheme.colorScheme.background,
            transparentToolbarContainerColor = Color.Transparent,
        )
    ) {
        Crossfade(
            targetState = viewModel.listState.value,
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            label = "search_loading"
        ) { state ->
            when (state) {
                ListState.Loading -> LoadingInBox(
                    loadingModifier = Modifier.scale(1.4f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )

                ListState.Empty -> EmptyList(
                    text = when {
                        viewModel.query.value.isBlank() -> stringResource(R.string.empty_query)
                        else -> stringResource(R.string.search_no_results)
                    },
                    icon = {
                        if (viewModel.query.value.isNotBlank()) {
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

                ListState.Stable -> EntityScreen(
                    contentState = contentState,
                    topBarState = topBarState,
                    viewModel = viewModel,
                    navigateTo = navigateTo,
                    popBackStack = popBackStack,
                    showSnackbar = showSnackbar,
                    modifier = Modifier.fillMaxSize()
                )
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