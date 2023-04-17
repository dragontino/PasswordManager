package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.EnterContentAnimation
import com.security.passwordmanager.ExitContentAnimation
import com.security.passwordmanager.Loading
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.RaspberryLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import kotlin.math.roundToInt

/**
 * @param content the content to be displayed in the container.
 * Note: the content must be scrollable!
 */
@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun AnimatedVisibilityScope.ScrollableTopBarScaffold(
    modifier: Modifier = Modifier,
    topBarModifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    state: CollapsingToolbarScaffoldState = rememberCollapsingToolbarScaffoldState(),
    scrollStrategy: ScrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
    topBar: @Composable CollapsingToolbarScope.() -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentBorder: BorderStroke? = null,
    contentShape: Shape = MaterialTheme.shapes.medium.copy(
        bottomStart = CornerSize(0),
        bottomEnd = CornerSize(0)
    ),
    refreshing: Boolean = false,
    onRefresh: suspend () -> Unit = {},
    pullRefreshIndicator: @Composable (PullRefreshIndicatorScope.(progress: Float) -> Unit) = {},
    isPullRefreshEnabled: Boolean = true,
    enterContentAnimation: EnterTransition = EnterContentAnimation,
    exitContentAnimation: ExitTransition = ExitContentAnimation,
    colors: ScrollableTopBarScaffoldColors = ScrollableTopBarScaffoldDefaults.colors(),
    content: @Composable (ColumnScope.() -> Unit)
) {
    val refreshScope = rememberCoroutineScope()
    val threshold = with (LocalDensity.current) { 200.dp.toPx() }

    var currentDistance by remember { mutableStateOf(0f) }

    val progress = currentDistance / threshold


    fun onPull(pullDelta: Float): Float = when {
        refreshing || !isPullRefreshEnabled -> 0f
        else -> {
            val newOffset = (currentDistance + pullDelta).coerceAtLeast(0f)
            val dragConsumed = newOffset - currentDistance
            currentDistance = newOffset
            dragConsumed
        }
    }

    suspend fun onRelease(flingVelocity: Float): Float {
        val distance = currentDistance

        refreshScope.launch {
            if (distance > threshold && !refreshing) onRefresh()
        }

        animate(
            initialValue = currentDistance,
            targetValue = 0f,
            initialVelocity = flingVelocity
        ) { value, _ ->
            currentDistance = value
        }

        return currentDistance
    }


    val pullRefreshIndicatorScope = PullRefreshIndicatorScopeImpl {
        refreshScope.launch { onRefresh() }
    }



    val borderModifier = when (contentBorder) {
        null -> Modifier
        else -> Modifier.border(contentBorder, contentShape)
    }

    val offsetModifier = when {
        !isPullRefreshEnabled || currentDistance == 0f -> Modifier
        else -> Modifier.offset {
            IntOffset(x = 0, y = currentDistance.roundToInt())
        }
    }



    CompositionLocalProvider(LocalScrollableScaffoldColors provides colors) {
        Scaffold(
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            snackbarHost = snackbarHost,
            contentWindowInsets = WindowInsets
                .tappableElement
                .union(WindowInsets.ime)
                .only(WindowInsetsSides.Bottom),
            containerColor = colors.topBarColor,
            modifier = modifier,
        ) { contentPadding ->

            CollapsingToolbarScaffold(
                state = state,
                scrollStrategy = scrollStrategy,
                modifier = Modifier.padding(contentPadding),
                toolbarModifier = topBarModifier,
                toolbar = topBar,
            ) {
                Box(
                    Modifier.pullRefresh(
                        onPull = ::onPull,
                        onRelease = ::onRelease
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .then(offsetModifier)
                            .animateEnterExit(
                                enter = enterContentAnimation,
                                exit = exitContentAnimation
                            )
                            .clip(contentShape)
                            .background(
                                color = colors.containerColor,
                                shape = contentShape
                            )
                            .then(borderModifier)
                            .then(contentModifier)
                            .align(Alignment.TopCenter)
                    ) {
                        content()
                    }

                    Box(Modifier.align(Alignment.TopCenter)) {
                        pullRefreshIndicatorScope.pullRefreshIndicator(progress)
                    }
                }
            }
        }
    }
}




@ExperimentalMaterial3Api
@Composable
fun CollapsingToolbarScope.ScrollableTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationButton: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = navigationButton,
        actions = actions,
        colors = LocalScrollableScaffoldColors.current.convertToTopAppBarColors(),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        modifier = Modifier.parallax(ratio = 10.3f).then(modifier)
    )
}




@Composable
fun ToolbarButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    contentDescription: String = "",
    colors: ToolbarButtonColors = ToolbarButtonDefaults.colors(),
    onClick: () -> Unit
) {
    OutlinedIconButton(
        onClick = onClick,
        colors = colors.convertToIconButtonColors(),
        shape = CircleShape,
        border = BorderStroke(
            width = 1.1.dp,
            color = colors.borderColor
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = iconModifier
        )
    }
}




object ScrollableTopBarScaffoldDefaults {
    @Composable
    fun colors(
        topBarColor: Color = MaterialTheme.colorScheme.primary.animate(),
        topBarNavigationIconContentColor: Color = contentColorFor(topBarColor).animate(),
        topBarTitleContentColor: Color = contentColorFor(topBarColor).animate(),
        topBarActionIconContentColor: Color = contentColorFor(topBarColor).animate(),
        containerColor: Color = MaterialTheme.colorScheme.background.animate()

    ) = ScrollableTopBarScaffoldColors(
        topBarColor,
        topBarNavigationIconContentColor,
        topBarTitleContentColor,
        topBarActionIconContentColor,
        containerColor
    )
}




private val LocalScrollableScaffoldColors = compositionLocalOf {
    ScrollableTopBarScaffoldColors(
        topBarColor = RaspberryLight,
        topBarNavigationIconContentColor = Color.White,
        topBarTitleContentColor = Color.White,
        topBarActionIconContentColor = Color.White,
        containerColor = Color.White
    )
}




@ExperimentalMaterial3Api
@Composable
private fun ScrollableTopBarScaffoldColors.convertToTopAppBarColors() =
    TopAppBarDefaults.topAppBarColors(
        containerColor = topBarColor,
        navigationIconContentColor = topBarNavigationIconContentColor,
        titleContentColor = topBarTitleContentColor,
        actionIconContentColor = topBarActionIconContentColor
    )






data class ScrollableTopBarScaffoldColors internal constructor(
    internal val topBarColor: Color,
    internal val topBarNavigationIconContentColor: Color,
    internal val topBarTitleContentColor: Color,
    internal val topBarActionIconContentColor: Color,
    internal val containerColor: Color
)






object ToolbarButtonDefaults {
    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = LocalContentColor.current,
        disabledContainerColor: Color = Color.Transparent,
        disabledContentColor: Color = contentColor.copy(alpha = 0.38f),
        borderColor: Color = Color.Transparent
    ) = ToolbarButtonColors(
        containerColor,
        contentColor,
        disabledContainerColor,
        disabledContentColor,
        borderColor
    )
}



data class ToolbarButtonColors internal constructor(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val borderColor: Color
)


@Composable
private fun ToolbarButtonColors.convertToIconButtonColors() =
    IconButtonDefaults.outlinedIconButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )



sealed interface PullRefreshIndicatorScope {

    fun onRefresh()

    @ExperimentalMaterialApi
    @Composable
    fun PullRefreshIndicator(
        refreshing: Boolean,
        modifier: Modifier = Modifier,
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = contentColorFor(containerColor)
    ) {
        androidx.compose.material.pullrefresh.PullRefreshIndicator(
            refreshing = refreshing,
            state = rememberPullRefreshState(
                refreshing = refreshing,
                onRefresh = this::onRefresh
            ),
            backgroundColor = containerColor.animate(),
            contentColor = contentColor.animate(),
            scale = true,
            modifier = modifier
        )
    }
}


@ExperimentalMaterialApi
private class PullRefreshIndicatorScopeImpl(private val onRefresh: () -> Unit)
    : PullRefreshIndicatorScope {
    override fun onRefresh() {
        this.onRefresh.invoke()
    }
}






@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalToolbarApi
@Preview
@Composable
private fun ToolbarScaffoldPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by remember { mutableStateOf(false) }

    suspend fun refresh() {
        isLoading = true
        delay(3000)
        isLoading = false
    }

    LaunchedEffect(key1 = isLoading) {
        snackbarHostState.showSnackbar("Wrecked")
    }

    PasswordManagerTheme(isDarkTheme = false, dynamicColor = true) {
        AnimatedVisibility(visible = true) {
            ScrollableTopBarScaffold(
                state = rememberCollapsingToolbarScaffoldState(),
                scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
                topBar = {
                    ScrollableTopBar(title = "Imagine Dragons")
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        Snackbar(snackbarData = it)
                    }
                },
                colors = ScrollableTopBarScaffoldDefaults.colors(
                    topBarColor = MaterialTheme.colorScheme.primary.animate(),
                    containerColor = MaterialTheme.colorScheme.background.animate()
                ),
                contentBorder = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onBackground.animate()
                ),
                refreshing = isLoading,
                onRefresh = ::refresh,
                pullRefreshIndicator = {
                    Loading(
                        progress = it,
                        modifier = Modifier.padding(top = 64.dp),
                        color = MaterialTheme.colorScheme.onBackground.animate()
                    )
                },
                contentModifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Sharks",
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        }
    }
}