package com.security.passwordmanager.view.screens.datascreens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DataArray
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.util.LoadingInBox
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.util.getActivity
import com.security.passwordmanager.util.reversed
import com.security.passwordmanager.util.scrollingUpState
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffold
import com.security.passwordmanager.view.composables.scaffold.DpShape
import com.security.passwordmanager.view.composables.scaffold.ToolbarButton
import com.security.passwordmanager.view.composables.scaffold.ToolbarButtonDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingAppBarDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingToolbar
import com.security.passwordmanager.view.composables.scaffold.toolbar.rememberAppBarState
import com.security.passwordmanager.view.composables.sheets.ModalBottomSheet
import com.security.passwordmanager.view.composables.sheets.ModalSheetItems.IconTextItem
import com.security.passwordmanager.view.navigation.EditScreen
import com.security.passwordmanager.view.navigation.HomeScreen
import com.security.passwordmanager.view.theme.NotesScreenAnimations
import com.security.passwordmanager.view.theme.ScreenContentAnimation
import com.security.passwordmanager.view.theme.ScreenToolbarAnimation
import com.security.passwordmanager.view.theme.screenBorderThickness
import com.security.passwordmanager.viewmodel.AllNotesViewModel
import com.security.passwordmanager.viewmodel.ListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AnimatedVisibilityScope.AllNotesScreen(
    title: String,
    isDarkTheme: Boolean,
    viewModel: AllNotesViewModel,
    openDrawer: () -> Unit,
    navigateTo: (route: String) -> Unit,
    isDarkStatusBarIcons: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val snackbarHostState = remember(::SnackbarHostState)
    val contentState = rememberLazyListState()
    val topBarState = rememberAppBarState()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showMainBottomSheet by rememberSaveable { mutableStateOf(false) }


    val showSnackbar = remember {
        fun(message: String) {
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }


    val scrollingUpState = contentState.scrollingUpState()
    LaunchedEffect(scrollingUpState.value) {
        delay(60)
        viewModel.showFab = scrollingUpState.value
    }

    LaunchedEffect(topBarState.isVisible) {
        isDarkStatusBarIcons(!topBarState.isVisible && !isDarkTheme)
    }


    BackHandler {
        with(viewModel) {
            when {
                notifyUserAboutFinishApp -> {
                    viewModel.showSnackbar(context.getString(R.string.close_app))
                    scope.launch {
                        notifyUserAboutFinishApp = false
                        delay(5000)
                        notifyUserAboutFinishApp = true
                    }
                }

                else -> viewModel.navigateTo(null)
            }
        }
    }



    if (showMainBottomSheet) {
        ModalBottomSheet(
            state = bottomSheetState,
            onClose = { showMainBottomSheet = false },
            title = stringResource(R.string.new_note),
            beautifulDesign = true,
        ) {
            IconTextItem(
                text = EditScreen.Website.title(),
                icon = Icons.Outlined.AccountCircle,
                iconTintColor = MaterialTheme.colorScheme.primary
            ) {
                showMainBottomSheet = false
                navigateTo(EditScreen.Website.createUrl())
            }

            /*ScreenTypeItem(screen = AppScreens.BankEdit) {
                    navigateTo(it.createUrl())
                    hideBottomSheet()
                }*/
        }
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
                title = title,
                navigationButton = {
                    ToolbarButton(
                        icon = Icons.Rounded.Menu,
                        contentDescription = title,
                        colors = ToolbarButtonDefaults.colors(
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            transparentContentColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = openDrawer
                    )
                },
                actions = {
                    ToolbarButton(
                        icon = Icons.Rounded.Search,
                        colors = ToolbarButtonDefaults.colors(
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            transparentContentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        navigateTo(HomeScreen.Search.createUrl(entityType = EntityType.All))
                    }
                },
                modifier = Modifier.animateEnterExit(
                    enter = ScreenToolbarAnimation.enter,
                    exit = ScreenToolbarAnimation.exit
                )
            )
        },
        topBarScrollBehavior = CollapsingAppBarDefaults.underContentScrollBehavior(state = topBarState),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    actionOnNewLine = true,
                    shape = MaterialTheme.shapes.medium,
                    actionColor = MaterialTheme.colorScheme.primary.animate(),
                    containerColor = MaterialTheme.colorScheme.background.reversed.animate(),
                    contentColor = MaterialTheme.colorScheme.onBackground.reversed.animate()
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = viewModel.showFab || !contentState.canScrollForward,
                enter = NotesScreenAnimations.ScrollFabAnimation.enter,
                exit = NotesScreenAnimations.ScrollFabAnimation.exit,
                modifier = Modifier.animateEnterExit(
                    enter = NotesScreenAnimations.ScreenFabAnimation.enter,
                    exit = NotesScreenAnimations.ScreenFabAnimation.exit
                )
            ) {
                FloatingActionButton(
                    onClick = { showMainBottomSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary.animate(),
                    contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    elevation = FloatingActionButtonDefaults.loweredElevation(),
                    /*modifier = Modifier.windowInsetsPadding(
                            WindowInsets.tappableElement
                                .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                        )*/
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "add new note"
                    )
                }
            }
        },
        contentBorder = BorderStroke(
            width = screenBorderThickness,
            brush = Brush.verticalGradient(
                0.1f to MaterialTheme.colorScheme.primary.animate(),
                0.6f to MaterialTheme.colorScheme.background.animate()
            ),
        ),
        contentShape = DpShape(top = 13.dp),
        onRefresh = viewModel::refreshData,
        pullToRefreshEnabled = viewModel.settings.value.pullToRefresh
    ) {
        Crossfade(
            targetState = viewModel.listState.value,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "state",
        ) { state ->
            when (state) {
                ListState.Loading -> {
                    LoadingInBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )
                }

                ListState.Empty -> EmptyList(
                    text = stringResource(R.string.empty_data_list),
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.DataArray,
                            contentDescription = "empty list",
                            tint = MaterialTheme.colorScheme.onBackground.animate(),
                            modifier = Modifier.scale(2.5f)
                        )
                    }
                )

                ListState.Stable -> {
                    EntityScreen(
                        contentState = contentState,
                        topBarState = topBarState,
                        viewModel = viewModel,
                        navigateTo = navigateTo,
                        popBackStack = { context.getActivity()?.finish() },
                        showSnackbar = showSnackbar,
                        modifier = Modifier.fillMaxSize()
                    )
                }
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .align(Alignment.Center)
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
}