package com.security.passwordmanager.presentation.view.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.security.passwordmanager.Loading
import com.security.passwordmanager.animate
import com.security.passwordmanager.keyboardAsState
import com.security.passwordmanager.presentation.view.composables.managment.ScrollableScaffoldState
import com.security.passwordmanager.presentation.view.composables.managment.ToolbarState
import com.security.passwordmanager.presentation.view.composables.managment.ToolbarType
import com.security.passwordmanager.presentation.view.composables.managment.rememberScrollableScaffoldState
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
)
@Composable
fun ScrollableToolbarScaffold(
    state: ScrollableScaffoldState,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    topBar: @Composable (CollapsingToolbarScope.() -> Unit) = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentBorder: BorderStroke = ScrollableToolbarScaffoldDefaults.withoutBorder,
    contentShape: CornerBasedShape = ScrollableToolbarScaffoldDefaults.contentShape,
    refreshing: Boolean = false,
    onRefresh: suspend () -> Unit = {},
    pullRefreshIndicator: @Composable (PullRefreshIndicatorScope.(progress: Float) -> Unit) = {},
    isPullRefreshEnabled: Boolean = true,
    colors: ScrollableScaffoldColors = ScrollableToolbarScaffoldDefaults.colors(),
    content: @Composable (ColumnScope.() -> Unit)
) {
    val density = LocalDensity.current

    val refreshScope = rememberCoroutineScope()
    val toolbarScope = rememberCoroutineScope()
    val contentScope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { refreshScope.launch { onRefresh() } },
//        refreshingOffset = with(density) { scaffoldState.toolbarState.heightPx.toDp() }
    )


//    var currentDistance by remember { mutableStateOf(0f) }

//    val pullRefreshProgress = with(scaffoldState.toolbarState) {
//        (currentDistance - heightPx) / threshold
//    }


    fun onPull(pullDelta: Float): Float {
        println("onPull = $pullDelta")
        return when {
            refreshing || !isPullRefreshEnabled || state.toolbarState.contentScrollProgress < 1 -> 0f
            else -> {
                state.toolbarState.contentOffsetPx += pullDelta
                /*val newOffset = (state.toolbarState.contentOffsetPx + pullDelta).coerceAtLeast(0f)
                val dragConsumed = newOffset - state.toolbarState.contentOffsetPx
                state.toolbarState.contentOffsetPx = newOffset*/
                pullDelta
            }
        }
    }


    suspend fun onRelease(flingVelocity: Float): Float = with(state.toolbarState) {
        println("onRelease = $flingVelocity")

        if (contentOffsetPx < heightPx)
            return 0f


        if (contentOffsetPx > 2.5f * heightPx && !refreshing) {
            withContext(Dispatchers.Main) { onRefresh() }
        }

        val oldOffset = contentOffsetPx
        animate(
            initialValue = oldOffset,
            targetValue = heightPx,
            initialVelocity = flingVelocity,
            animationSpec = tween(
                durationMillis = 600,
                easing = LinearEasing
            )
        ) { value, _ ->
            contentOffsetPx = value
        }

        return heightPx - oldOffset
    }


    val pullRefreshIndicatorScope = PullRefreshIndicatorScopeImpl {
        refreshScope.launch { onRefresh() }
    }


    val collapsingToolbarScope = object : CollapsingToolbarScope {
        override val state: ToolbarState = state.toolbarState
    }

    val isKeyboardOpen by keyboardAsState()
    state.toolbarState.isImeVisible = isKeyboardOpen




    val pullRefreshGraphicsLayer = 4f


    /*fun CornerSize.toPx(): Float = toPx(
        shapeSize = Size(
            width = configuration.screenWidthDp.toFloat(),
            height = configuration.screenHeightDp.toFloat()
        ),
        density = density
    )*/


    val animatedContentShape = contentShape.animate(percent = state.toolbarState.contentScrollProgress)
    /*RoundedCornerShape(
        topStart = contentShape.topStart.toPx() * state.toolbarState.contentScrollProgress,
        *//*when {
            state.toolbarState.isVisible -> contentShape.topStart.toPx()
            else -> 0f
        }.animate(400),*//*
        topEnd = contentShape.topEnd.toPx() * state.
        *//*when {
            state.toolbarState.isVisible -> contentShape.topEnd.toPx()
            else -> 0f
        }.animate(400)*//*
    )*/


    val defaultBorderWidthPx = with(density) { contentBorder.width.toPx() }
    var contentBorderWidthPx by rememberSaveable { mutableStateOf(defaultBorderWidthPx) }


    val contentBorderBrush = when {
        state.toolbarState.isVisible -> contentBorder.brush
        else -> SolidColor(Color.Transparent)
    }


    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                println("onPreScroll = $delta")

                with(state) {
                    toolbarState.scrollTopLimitReached = !canScrollBackward

                    if (
                        delta > 0 && !canScrollBackward ||
                        delta < 0 && canScrollForward
                    ) {
                        toolbarState.contentOffsetPx += delta
                    }

                    toolbarState.offsetPx += delta

                    toolbarState.updateValue()
                    toolbarState.updateZIndex()

                    contentBorderWidthPx = defaultBorderWidthPx * toolbarState.contentScrollProgress

                    return when {
                        !canScrollBackward && toolbarState.contentOffsetPx > 0 -> Offset(x = 0f, y = delta)
                        else -> Offset.Zero
                    }
                }
            }


            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset = with(state) {
                if (
                    !canScrollBackward &&
                    toolbarState.contentOffsetPx == 0f &&
                    canScrollForward
                ) {
                    val oldOffset = toolbarState.contentOffsetPx
                    toolbarState.apply {
                        contentOffsetPx = contentOffsetPx
                            .plus(available.y)
                            .coerceAtLeast(10f)
                    }
                    return Offset(0f, y = toolbarState.contentOffsetPx - oldOffset)
                } else return Offset.Zero
            }


            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                with(state.toolbarState) {
                    toolbarScope.launch {
                        animate(
                            initialValue = offsetPx,
                            initialVelocity = available.y,
                            targetValue = calculateRoundedToolbarOffset(),
                            animationSpec = tween(
                                durationMillis = ScrollableToolbarScaffoldDefaults.animationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ) { value, _ ->
                            offsetPx = value
                        }

                        updateValue()
                        updateZIndex()
                    }


                    val initialContentOffset = contentOffsetPx
                    val initialBorderWidth = contentBorderWidthPx
                    val targetContentOffset = calculateRoundedContentOffset()

                    contentScope.launch {
                        if (contentOffsetPx != targetContentOffset) {
                            animate(
                                initialValue = initialContentOffset,
                                initialVelocity = available.y,
                                targetValue = targetContentOffset,
                                animationSpec = tween(
                                    durationMillis = ScrollableToolbarScaffoldDefaults.animationDurationMillis,
                                    easing = FastOutSlowInEasing
                                )
                            ) { value, _ ->
                                contentOffsetPx = value

                                val percent =
                                    (value - initialContentOffset) / (targetContentOffset - initialContentOffset)
                                val targetBorderWidth = when (targetContentOffset) {
                                    0f -> 0f
                                    else -> defaultBorderWidthPx
                                }

                                contentBorderWidthPx =
                                    initialBorderWidth + (targetBorderWidth - initialBorderWidth) * percent
                            }
                        }

                        updateValue()
                        updateZIndex()
                    }
                }

                return Velocity.Zero
            }
        }
    }



    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen) {
            toolbarScope.launch {
                animate(
                    initialValue = state.toolbarState.offsetPx,
                    targetValue = -state.toolbarState.heightPx,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                ) { value, _ ->
                    state.toolbarState.offsetPx = value
                }

                nestedScrollConnection.onPostFling(
                    consumed = Velocity(
                        x = 0f,
                        y = -state.toolbarState.heightPx - state.toolbarState.offsetPx
                    ),
                    available = Velocity.Zero
                )
            }
        }
    }


    /*if (state.contentState is LazyListState) {
        LaunchedEffect(state.contentState.isScrollInProgress) {
            var initialVisibleItemIndex by mutableStateOf(0)
            var initialVisibleItemScrollOffset by mutableStateOf(0)

            if (state.contentState.isScrollInProgress) {
                initialVisibleItemIndex = state.contentState.firstVisibleItemIndex
                initialVisibleItemScrollOffset =
                    state.contentState.firstVisibleItemScrollOffset
            }
            else {
                val finalVisibleItemIndex = state.contentState.firstVisibleItemIndex
                val finalVisibleItemScrollOffset = state.contentState.firstVisibleItemScrollOffset

                val scrolledPixels = (finalVisibleItemIndex - initialVisibleItemIndex) *
                        state.contentState.layoutInfo.viewportSize.height +
                        (finalVisibleItemScrollOffset - initialVisibleItemScrollOffset)

                println("scrolled Pixels = $scrolledPixels")



//                nestedScrollConnection.onPostFling(
//                    consumed = Velocity(x = 0f, y = scrolledPixels.toFloat()),
//                    available = Velocity.Zero
//                )
            }
        }
    }*/


    val topBarOffsetModifier = when {
        state.toolbarState.zIndex > 0 -> Modifier.offset {
            IntOffset(x = 0, y = state.toolbarState.offsetPx.roundToInt())
        }
        else -> Modifier
    }




    CompositionLocalProvider(LocalScrollableScaffoldColors provides colors) {
        Scaffold(
            modifier = modifier,
            floatingActionButton = floatingActionButton,
            /*{
                Box(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.tappableElement
                            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                    )
                ) {
                    floatingActionButton()
                }
            },*/
            floatingActionButtonPosition = floatingActionButtonPosition,
            snackbarHost = snackbarHost,
            /*{
                Box(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.tappableElement
                            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                    )
                ) {
                    snackbarHost()
                }
            },*/
            contentWindowInsets = when {
                isKeyboardOpen -> WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                else -> WindowInsets.tappableElement
                    .only(WindowInsetsSides.Bottom)
                    .union(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            },
            containerColor = colors.toolbarColors.containerColor.animate()
        ) { contentPadding ->

            /*val horizontalPadding = with(contentPadding) {
                maxOf(
                    calculateStartPadding(LocalLayoutDirection.current),
                    calculateEndPadding(LocalLayoutDirection.current)
                )
            }*/

            val horizontalContentPadding = with(WindowInsets.tappableElement.asPaddingValues()) {
                maxOf(
                    calculateStartPadding(LocalLayoutDirection.current),
                    calculateEndPadding(LocalLayoutDirection.current)
                )
            }


            Box(
                Modifier
                    .padding(contentPadding)
                    .pullRefresh(
                        onPull = ::onPull,
                        onRelease = ::onRelease
                    )
                    .nestedScroll(nestedScrollConnection)
            ) {
                Box(
                    Modifier
                        .offset {
                            IntOffset(
                                x = 0,
                                y = state.toolbarState.offsetPx.roundToInt()
                            )
                        }
                        /*.then(topBarOffsetModifier)*/

                        .zIndex(state.toolbarState.zIndex)
                        .onGloballyPositioned {
                            state.toolbarState.heightPx = it.size.height.toFloat()
                        }
                        .padding(horizontal = horizontalContentPadding)
                ) {
                    topBar(collapsingToolbarScope)
                }


                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = 0,
                                y = state.toolbarState.contentOffsetPx.roundToInt()
                            )
                        }
                        /*.background(
                            color = colors.toolbarColors.transparentContainerColor.copy(
                                alpha = state.toolbarState.alpha
                            )
                        )*/
                        .align(Alignment.TopCenter)
                        .zIndex(0f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
//                            .windowInsetsPadding(WindowInsets.ime.only(WindowInsetsSides.Bottom))
//                            .offset {
//                                IntOffset(
//                                    x = 0,
//                                    y = state.toolbarState.contentOffsetPx.roundToInt()
//                                )
//                            }
                            .then(contentModifier)
                            .background(
                                color = colors.containerColor,
                                shape = animatedContentShape
                            )
                            .clip(animatedContentShape)
                            .border(
                                border = contentBorder.copy(
                                    width = with(density) { contentBorderWidthPx.toDp() },
                                    brush = contentBorderBrush
                                ),
                                shape = animatedContentShape
                            )
                            .padding(horizontal = horizontalContentPadding)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        arrayOf(toolbarScope, contentScope).forEach {
                                            it.coroutineContext.cancelChildren()
                                        }
                                    }
                                )
                            }
                    ) {
                        content()
                    }
                }


//                PullRefreshIndicator(
//                    refreshing = refreshing,
//                    state = pullRefreshState,
//                    modifier = Modifier
//                        .pullRefreshIndicatorTransform(pullRefreshState)
//                        .zIndex(pullRefreshGraphicsLayer)
//                        .align(Alignment.TopCenter)
//                )
                Box(
                    modifier = Modifier
                        .offset(
                            y = with(density) {
                                state.toolbarState
                                    .contentOffsetPx
                                    .coerceAtMost(1.5f * state.toolbarState.heightPx)
                                    .toDp()
//                                    .animate(topBarAnimationDurationMillis)
                            }
                        )
                        .pullRefreshIndicatorTransform(pullRefreshState)
                        .zIndex(pullRefreshGraphicsLayer)
                        .align(Alignment.TopCenter)
                ) {
                    PullRefreshIndicator(
                        refreshing = refreshing,
                        state = pullRefreshState,
                        scale = true,
                        backgroundColor = MaterialTheme.colorScheme.onBackground.animate()
                    )
//                    pullRefreshIndicatorScope.pullRefreshIndicator(scaffoldState.toolbarState.contentScrollProgress)
                }
            }
        }
    }
}


@Composable
private fun CornerBasedShape.animate(percent: Float): CornerBasedShape {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    fun CornerSize.toPx(): Float = toPx(
        shapeSize = Size(
            width = configuration.screenWidthDp.toFloat(),
            height = configuration.screenHeightDp.toFloat()
        ),
        density = density
    )


    return RoundedCornerShape(
        topStart = topStart.toPx() * percent,
        /*when {
            state.toolbarState.isVisible -> contentShape.topStart.toPx()
            else -> 0f
        }.animate(400),*/
        topEnd = topEnd.toPx() * percent,
        /*when {
            state.toolbarState.isVisible -> contentShape.topEnd.toPx()
            else -> 0f
        }.animate(400)*/
        bottomStart = bottomStart.toPx() * percent,
        bottomEnd = bottomEnd.toPx() * percent
    )
}





object ScrollableToolbarScaffoldDefaults {
    val withoutBorder = BorderStroke(width = 0.dp, color = Color.Transparent)

    val contentShape = CutCornerShape(0)

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.background,
        toolbarColors: CollapsingToolbarColors = CollapsingToolbarDefaults.colors(),
    ) =
        ScrollableScaffoldColors(containerColor, toolbarColors)


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
        toolbarColors = CollapsingToolbarDefaults.colors(
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


internal val LocalScrollableScaffoldColors = compositionLocalOf {
    ScrollableScaffoldColors(
        containerColor = Color.White,
        toolbarColors = CollapsingToolbarColors.Default,
    )
}


data class ScrollableScaffoldColors internal constructor(
    internal val containerColor: Color,
    internal val toolbarColors: CollapsingToolbarColors
)


sealed interface PullRefreshIndicatorScope {

    fun onRefresh()

    @ExperimentalMaterialApi
    @Composable
    fun PullRefreshIndicator(
        refreshing: Boolean,
        modifier: Modifier = Modifier,
        progress: Float? = null,
        containerColor: Color = Color.Transparent,
        contentColor: Color = MaterialTheme.colorScheme.onBackground
    ) {
        PullRefreshIndicator(
            refreshing = refreshing,
            state = rememberPullRefreshState(
                refreshing = refreshing,
                onRefresh = this::onRefresh
            ),
            backgroundColor = containerColor.animate(),
            contentColor = contentColor.animate(),
            scale = true,
            modifier = when (progress) {
                null -> modifier
                else -> modifier.progressSemantics(progress)
            }
        )
    }
}


@ExperimentalMaterialApi
private class PullRefreshIndicatorScopeImpl(private val onRefresh: () -> Unit) :
    PullRefreshIndicatorScope {
    override fun onRefresh() {
        this.onRefresh.invoke()
    }
}


@Preview
@Composable
private fun ToolbarScaffoldPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val scaffoldState = rememberScrollableScaffoldState(
        scrollState = scrollState,
        toolbarType = ToolbarType.ToolbarOverContent
    )

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
            ScrollableToolbarScaffold(
                state = scaffoldState,
                contentModifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                topBar = {
                    CollapsingToolbar(title = "Imagine Dragons")
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        Snackbar(snackbarData = it)
                    }
                },
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
                colors = ScrollableToolbarScaffoldDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    toolbarColors = CollapsingToolbarDefaults.colors(
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