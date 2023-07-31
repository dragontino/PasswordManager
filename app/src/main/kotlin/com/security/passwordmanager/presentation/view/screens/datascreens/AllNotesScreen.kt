package com.security.passwordmanager.presentation.view.screens.datascreens

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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DataArray
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.LoadingInBox
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.getActivity
import com.security.passwordmanager.isScrollingUp
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.composables.CollapsingToolbar
import com.security.passwordmanager.presentation.view.composables.ScrollableToolbarScaffold
import com.security.passwordmanager.presentation.view.composables.ToolbarButton
import com.security.passwordmanager.presentation.view.composables.ToolbarButtonDefaults
import com.security.passwordmanager.presentation.view.composables.managment.ToolbarType
import com.security.passwordmanager.presentation.view.composables.managment.rememberScrollableScaffoldState
import com.security.passwordmanager.presentation.view.navigation.AppScreens
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.ModalSheetDefaults
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.ScreenTypeItem
import com.security.passwordmanager.presentation.view.theme.NotesScreenAnimations
import com.security.passwordmanager.presentation.view.theme.ScreenContentAnimation
import com.security.passwordmanager.presentation.view.theme.ScreenToolbarAnimation
import com.security.passwordmanager.presentation.view.theme.screenBorderThickness
import com.security.passwordmanager.presentation.viewmodel.AllNotesViewModel
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun AnimatedVisibilityScope.AllNotesScreen(
    title: String,
    isDarkTheme: Boolean,
    viewModel: AllNotesViewModel,
    settings: Settings,
    openDrawer: () -> Unit,
    navigateTo: (route: String) -> Unit,
    isDarkStatusBarIcons: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val snackbarHostState = remember(::SnackbarHostState)
    val lazyListState = rememberLazyListState()
    val scaffoldState = rememberScrollableScaffoldState(
        lazyListState = lazyListState,
        toolbarType = ToolbarType.ToolbarBelowContent
    )
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = ModalSheetDefaults.AnimationSpec,
        skipHalfExpanded = true
    )

    isDarkStatusBarIcons(!scaffoldState.toolbarState.isVisible && !isDarkTheme)


    val showBottomSheet = {
        scope.launch {
            delay(50)
            bottomSheetState.show()
        }
    }

    val hideBottomSheet = {
        scope.launch { bottomSheetState.hide() }
    }


    fun showSnackbar(message: String, actionLabel: String? = null) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel
            )
        }
    }


    val snackbarHost: @Composable () -> Unit = {
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
    }


    lazyListState.isScrollingUp().let { scrollUp ->
        LaunchedEffect(scrollUp) {
            delay(60)
            viewModel.showFab = scrollUp
        }
    }


    BackHandler {
        with(viewModel) {
            when {
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


    val mainBottomSheetContent: @Composable (ColumnScope.() -> Unit) = {
        BottomSheetContent(
            title = stringResource(R.string.new_note),
            beautifulDesign = true
        ) {
            ScreenTypeItem(
                screen = AppScreens.WebsiteEdit,
                icon = Icons.Outlined.AccountCircle
            ) {
                navigateTo(it.createUrl())
                hideBottomSheet()
            }

            /*ScreenTypeItem(screen = AppScreens.BankEdit) {
                navigateTo(it.createUrl())
                hideBottomSheet()
            }*/
        }
    }



    ModalBottomSheetLayout(
        sheetContent = viewModel.bottomSheetContent,
        sheetState = bottomSheetState,
        sheetShape = ModalSheetDefaults.Shape,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface.animate(),
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
                            navigateTo(AppScreens.Search.createUrl(dataType = DataType.All))
                        }
                    },
                    modifier = Modifier.animateEnterExit(
                        enter = ScreenToolbarAnimation.enter,
                        exit = ScreenToolbarAnimation.exit
                    )
                )
            },
            snackbarHost = snackbarHost,
            floatingActionButton = {
                AnimatedVisibility(
                    visible = viewModel.showFab || !scaffoldState.canScrollForward,
                    enter = NotesScreenAnimations.ScrollFabAnimation.enter,
                    exit = NotesScreenAnimations.ScrollFabAnimation.exit,
                    modifier = Modifier.animateEnterExit(
                        enter = NotesScreenAnimations.ScreenFabAnimation.enter,
                        exit = NotesScreenAnimations.ScreenFabAnimation.exit
                    )
                ) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.bottomSheetContent = mainBottomSheetContent
                            showBottomSheet()
                        },
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
            contentShape = viewModel.screenShape(),
            refreshing = viewModel.viewModelState == DataViewModel.DataViewModelState.Loading,
            onRefresh = {
                viewModel.refreshData {
                    if (it != null) {
                        showSnackbar(it)
                    }
                }
            },
            pullRefreshIndicator = {
                PullRefreshIndicator(
                    refreshing = viewModel.viewModelState == DataViewModel.DataViewModelState.Loading,
                    contentColor = MaterialTheme.colorScheme.background
                )
            },
            isPullRefreshEnabled = settings.pullToRefresh
        ) {
            Crossfade(
                targetState = viewModel.viewModelState,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "state",
            ) {
                when (it) {
                    DataViewModel.DataViewModelState.PreLoading, DataViewModel.DataViewModelState.Loading -> {
                        LoadingInBox(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }

                    DataViewModel.DataViewModelState.EmptyList -> EmptyList(
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

                    DataViewModel.DataViewModelState.Ready -> {
                        DataScreen(
                            scaffoldState = scaffoldState,
                            viewModel = viewModel,
                            useBeautifulFont = settings.beautifulFont,
                            loadIcons = settings.loadIcons,
                            navigateTo = navigateTo,
                            showBottomSheet = { showBottomSheet() },
                            hideBottomSheet = { hideBottomSheet() },
                            showSnackbar = { msg -> showSnackbar(msg) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
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