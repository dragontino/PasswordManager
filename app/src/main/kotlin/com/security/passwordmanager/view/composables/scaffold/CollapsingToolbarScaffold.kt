package com.security.passwordmanager.view.composables.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.security.passwordmanager.util.Loading
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.util.keyboardAsState
import com.security.passwordmanager.util.reversed
import com.security.passwordmanager.view.composables.scaffold.toolbar.AppBarScrollBehavior
import com.security.passwordmanager.view.composables.scaffold.toolbar.AppBarState
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingAppBarDefaults
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingAppBarScope
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingToolbar
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingToolbarColors
import com.security.passwordmanager.view.theme.PasswordManagerTheme
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt


@ExperimentalMaterial3Api
@Composable
fun CollapsingToolbarScaffold(
    contentState: ScrollableState,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    topBar: @Composable (CollapsingAppBarScope.() -> Unit)? = null,
    topBarScrollBehavior: AppBarScrollBehavior =
        CollapsingAppBarDefaults.onTopOfContentScrollBehavior(),
    snackbarHost: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentBorder: BorderStroke = CollapsingToolbarScaffoldDefaults.noBorder,
    contentShape: DpShape = CollapsingToolbarScaffoldDefaults.contentShape,
    contentWindowInsets: WindowInsets = CollapsingToolbarScaffoldDefaults.contentWindowInsets,
    pullToRefreshEnabled: Boolean = true,
    onRefresh: suspend () -> Unit = {},
    pullToRefreshIndicator: @Composable ((state: PullToRefreshState) -> Unit) = {
        PullToRefreshDefaults.Indicator(state = it)
    },
    colors: CollapsingToolbarScaffoldColors = CollapsingToolbarScaffoldDefaults.colors(),
    content: @Composable (ColumnScope.() -> Unit)
) {
    val density = LocalDensity.current

    val toolbarScope = rememberCoroutineScope()
    val contentScope = rememberCoroutineScope()

    val collapsingAppBarScope = remember {
        object : CollapsingAppBarScope {
            override val state: AppBarState = topBarScrollBehavior.state
        }
    }

    val pullRefreshGraphicsLayer = remember { 4f }

    val animatedContentShape = remember(density, topBarScrollBehavior.state.overlappedFraction) {
        contentShape.animate(
            percent = 1 - topBarScrollBehavior.state.overlappedFraction,
            density = density
        )
    }


    val defaultBorderWidthPx = remember(density) {
        with(density) { contentBorder.width.toPx() }
    }
    val animatedBorderWidth = remember(topBarScrollBehavior.state.overlappedFraction) {
        derivedStateOf { defaultBorderWidthPx * (1 - topBarScrollBehavior.state.overlappedFraction) }
    }


    val contentBorderBrush = remember(topBarScrollBehavior.state.isVisible) {
        when {
            topBarScrollBehavior.state.isVisible -> contentBorder.brush
            else -> SolidColor(Color.Transparent)
        }
    }


    val pullToRefreshState = remember {
        object : PullToRefreshState {
            override val positionalThreshold: Float = with(density) { 100.dp.toPx() }
            override var verticalOffset: Float by mutableFloatStateOf(0f)
            override var isRefreshing: Boolean by mutableStateOf(false)
            private var distancePulled by mutableFloatStateOf(0f)
            private val adjustedDistancePulled: Float get() = distancePulled * .5f

            override val progress: Float get() = adjustedDistancePulled / positionalThreshold


            override fun startRefresh() {
                isRefreshing = true
            }

            override fun endRefresh() {
                isRefreshing = false
            }

            override var nestedScrollConnection: NestedScrollConnection =
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val pullToRefreshOffset = when {
                            !pullToRefreshEnabled -> Offset.Zero
                            // Swiping up
                            source == NestedScrollSource.Drag && available.y < 0 ->
                                consumeAvailablePullOffset(available)
                            else -> Offset.Zero
                        }

                        val topBarOffset = with (topBarScrollBehavior) {
                            state.scrollTopLimitReached = !contentState.canScrollBackward
                            state.scrollDownLimitReached = !contentState.canScrollForward
                            state.contentOffsetBottomLimit = verticalOffset
                            return@with nestedScrollConnection.onPreScroll(available, source)
                        }

                        println("pull refresh offset = $pullToRefreshOffset, topBarOffset = $topBarOffset")
                        return Offset(
                            x = 0f,
                            y = when {
                                available.y < 0 -> minOf(pullToRefreshOffset.y, topBarOffset.y)
                                else -> maxOf(pullToRefreshOffset.y, topBarOffset.y)
                            }
                        )
                    }

                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val pullToRefreshOffset = when {
                            !pullToRefreshEnabled -> Offset.Zero
                            // Swiping down
                            source == NestedScrollSource.Drag && available.y > 0 ->
                                consumeAvailablePullOffset(available)

                            else -> Offset.Zero
                        }

                        val topBarOffset = with(topBarScrollBehavior) {
                            state.scrollTopLimitReached = !contentState.canScrollBackward
                            state.scrollDownLimitReached = !contentState.canScrollForward
                            state.contentOffsetBottomLimit = verticalOffset
                            return@with nestedScrollConnection.onPostScroll(consumed, available, source)
                        }

                        println("state offset = ${topBarScrollBehavior.state.contentOffset}, pull offset = $verticalOffset")
                        return Offset(
                            x = 0f,
                            y = when {
                                available.y < 0 -> minOf(pullToRefreshOffset.y, topBarOffset.y)
                                else -> maxOf(pullToRefreshOffset.y, topBarOffset.y)
                            }
                        )
                    }


                    override suspend fun onPreFling(available: Velocity): Velocity {
                        topBarScrollBehavior.nestedScrollConnection.onPreFling(available)

                        if (isRefreshing || !pullToRefreshEnabled) return Velocity.Zero
                        if (adjustedDistancePulled > positionalThreshold) {
                            startRefresh()
                            animateVerticalOffset(positionalThreshold)
                            onRefresh()
                            animateVerticalOffset(0f)
                            endRefresh()
                        }
                        else {
                            animateVerticalOffset(0f)
                        }

                        val consumed = when {
                            distancePulled == 0f || available.y < 0f -> 0f
                            else -> available.y
                        }
                        distancePulled = 0f

                        return Velocity(0f, consumed)
                    }


                    override suspend fun onPostFling(
                        consumed: Velocity,
                        available: Velocity
                    ): Velocity {
                        return topBarScrollBehavior
                            .nestedScrollConnection
                            .onPostFling(consumed, available)
                    }
                }


            fun consumeAvailablePullOffset(available: Offset): Offset {
                val y = if (isRefreshing) 0f else {
                    val newOffset = (distancePulled + available.y).coerceAtLeast(0f)
                    val dragConsumed = newOffset - distancePulled
                    distancePulled = newOffset
                    verticalOffset = calculateVerticalOffset()
                    dragConsumed
                }
                return Offset(0f, y)
            }


            fun calculateVerticalOffset(): Float = when {
                // If drag hasn't gone past the threshold, the position is the adjustedDistancePulled.
                adjustedDistancePulled <= positionalThreshold -> adjustedDistancePulled
                else -> {
                    // How far beyond the threshold pull has gone, as a percentage of the threshold.
                    val overshootPercent = abs(progress) - 1.0f
                    // Limit the overshoot to 200%. Linear between 0 and 200.
                    val linearTension = overshootPercent.coerceIn(0f, 2f)
                    // Non-linear tension. Increases with linearTension, but at a decreasing rate.
                    val tensionPercent = linearTension - linearTension.pow(2) / 4
                    // The additional offset beyond the threshold.
                    val extraOffset = positionalThreshold * tensionPercent
                    positionalThreshold + extraOffset
                }
            }

            private suspend fun animateVerticalOffset(targetOffset: Float) {
                animate(
                    initialValue = verticalOffset,
                    targetValue = targetOffset,
                    animationSpec = tween(
                        durationMillis = CollapsingToolbarScaffoldDefaults.animationDurationMillis,
                        easing = FastOutSlowInEasing
                    )
                ) { value, _ ->
                    verticalOffset = value
                }
            }
        }
    }

    val keyboardState = keyboardAsState()
    val imeInsets = WindowInsets.ime
    LaunchedEffect(Unit) {
        snapshotFlow { keyboardState.value }.collect { isKeyboardVisible ->
            delay(200)
            if (isKeyboardVisible) {
                with(topBarScrollBehavior.state) {
                    val keyboardPadding = imeInsets.getBottom(density)
                    val prevContentOffset = contentOffset
                    val contentOffsetScrollPixels = keyboardPadding - (contentOffset - offsetTopLimit)
                    animate(
                        initialValue = prevContentOffset,
                        targetValue = prevContentOffset - contentOffsetScrollPixels,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    ) { value, _ ->
                        contentOffset = value
                    }

                    contentState.animateScrollBy(
                        value = keyboardPadding - contentOffsetScrollPixels,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }


    CompositionLocalProvider(LocalCollapsingToolbarScaffoldColors provides colors) {
        Scaffold(
            modifier = modifier,
            floatingActionButton = {
                if (floatingActionButton == null) return@Scaffold
                val fabInsets = when (floatingActionButtonPosition) {
                    FabPosition.Center -> contentWindowInsets.only(WindowInsetsSides.Bottom)
                    else -> contentWindowInsets
                }
                Box(
                    modifier = Modifier.windowInsetsPadding(fabInsets),
                    contentAlignment = Alignment.Center
                ) {
                    floatingActionButton()
                }
            },
            floatingActionButtonPosition = floatingActionButtonPosition,
            snackbarHost = {
                val snackbarInsets = when {
                    snackbarHost == null -> return@Scaffold
                    floatingActionButton == null -> contentWindowInsets
                    else -> contentWindowInsets.only(WindowInsetsSides.Horizontal)
                }
                Box(
                    modifier = Modifier.windowInsetsPadding(snackbarInsets),
                    contentAlignment = Alignment.Center
                ) {
                    snackbarHost()
                }
            },
            contentWindowInsets = WindowInsets(0),
            containerColor = colors.toolbarColors.containerColor.animate()
        ) { contentPadding ->
            val horizontalContentPadding = with(contentWindowInsets.asPaddingValues()) {
                maxOf(
                    calculateStartPadding(LocalLayoutDirection.current),
                    calculateEndPadding(LocalLayoutDirection.current)
                )
            }


            val scaleFraction = when {
                pullToRefreshState.isRefreshing -> 1f
                else -> FastOutSlowInEasing.transform(pullToRefreshState.progress).coerceIn(0f, 1f)
            }
            Box(
                Modifier
                    .padding(contentPadding)
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
            ) {
                Box(
                    Modifier
                        .offset {
                            IntOffset(
                                x = 0,
                                y = topBarScrollBehavior.state.heightOffset.roundToInt()
                            )
                        }
                        .zIndex(topBarScrollBehavior.zIndex.floatValue)
                        .onGloballyPositioned {
                            topBarScrollBehavior.state.offsetTopLimit = -it.size.height.toFloat()
                        }
                        .padding(horizontal = horizontalContentPadding)
                ) {
                    topBar?.invoke(collapsingAppBarScope)
                }


                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = 0,
                                y = with(topBarScrollBehavior.state) {
                                    (contentOffset - offsetTopLimit).roundToInt()
                                }
                            )
                        }
                        .align(Alignment.TopCenter)
                        .zIndex(.5f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .then(contentModifier)
                            .windowInsetsPadding(contentWindowInsets)
                            .background(
                                color = colors.containerColor,
                                shape = animatedContentShape
                            )
                            .clip(animatedContentShape)
                            .border(
                                border = contentBorder.copy(
                                    width = with(density) { animatedBorderWidth.value.toDp() },
                                    brush = contentBorderBrush
                                ),
                                shape = animatedContentShape
                            )
                            .imePadding()
                            .padding(horizontal = horizontalContentPadding)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        arrayOf(toolbarScope, contentScope).forEach {
                                            it.coroutineContext.cancelChildren()
                                        }
                                    }
                                )
                            },
                        content = content
                    )
                }

                if (pullToRefreshEnabled) {
                    PullToRefreshContainer(
                        state = pullToRefreshState,
                        indicator = pullToRefreshIndicator,
                        containerColor = MaterialTheme.colorScheme.background.reversed,
                        contentColor = MaterialTheme.colorScheme.onBackground.reversed,
                        modifier = Modifier
                            .zIndex(pullRefreshGraphicsLayer)
                            .align(Alignment.TopCenter)
                            .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction)
                    )
                }
            }
        }
    }
}





object CollapsingToolbarScaffoldDefaults {
    val noBorder = BorderStroke(width = 0.dp, color = Color.Transparent)

    val contentShape = DpShape(0.dp)

    val contentWindowInsets: WindowInsets
        @Composable
        get() = WindowInsets.tappableElement.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.background,
        toolbarColors: CollapsingToolbarColors = CollapsingAppBarDefaults.colors(),
    ) =
        CollapsingToolbarScaffoldColors(containerColor, toolbarColors)


    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.background,
        toolbarContainerColor: Color = MaterialTheme.colorScheme.primary,
        toolbarTitleContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        toolbarNavigationIconContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        toolbarActionIconContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        transparentToolbarContainerColor: Color = MaterialTheme.colorScheme.background,
        transparentToolbarTitleContentColor: Color = MaterialTheme.colorScheme.onBackground,
        transparentToolbarNavigationIconContentColor: Color = MaterialTheme.colorScheme.onBackground,
        transparentToolbarActionIconContentColor: Color = MaterialTheme.colorScheme.onBackground
    ) = colors(
        containerColor = containerColor,
        toolbarColors = CollapsingAppBarDefaults.colors(
            containerColor = toolbarContainerColor,
            titleContentColor = toolbarTitleContentColor,
            navigationIconContentColor = toolbarNavigationIconContentColor,
            actionIconContentColor = toolbarActionIconContentColor,
            transparentContainerColor = transparentToolbarContainerColor,
            transparentTitleContentColor = transparentToolbarTitleContentColor,
            transparentNavigationIconContentColor = transparentToolbarNavigationIconContentColor,
            transparentActionIconContentColor = transparentToolbarActionIconContentColor
        )
    )


    internal const val animationDurationMillis = 600
}


internal val LocalCollapsingToolbarScaffoldColors = compositionLocalOf {
    CollapsingToolbarScaffoldColors(
        containerColor = Color.White,
        toolbarColors = CollapsingToolbarColors.Default,
    )
}


data class CollapsingToolbarScaffoldColors internal constructor(
    internal val containerColor: Color,
    internal val toolbarColors: CollapsingToolbarColors
)


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun CollapsingToolbarScaffoldPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

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
            CollapsingToolbarScaffold(
                contentState = scrollState,
                contentModifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                topBar = {
                    CollapsingToolbar(title = "Imagine Dragons")
                },
                topBarScrollBehavior = CollapsingAppBarDefaults.onTopOfContentScrollBehavior(),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        Snackbar(snackbarData = it)
                    }
                },
                contentBorder = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onBackground.animate()
                ),
                onRefresh = ::refresh,
                pullToRefreshIndicator = {
                    Loading(
                        progress = it.progress,
                        modifier = Modifier.padding(top = 64.dp),
                        color = MaterialTheme.colorScheme.onBackground.animate()
                    )
                },
                colors = CollapsingToolbarScaffoldDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    toolbarColors = CollapsingAppBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center
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