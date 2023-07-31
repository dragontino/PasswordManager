package com.security.passwordmanager.presentation.view.composables.managment

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.security.passwordmanager.presentation.view.composables.CollapsingToolbarDefaults

@Stable
sealed class ScrollableScaffoldState(
    private val contentState: ScrollableState,
    val toolbarState: ToolbarState,
) : ScrollableState by contentState {

    class LazyListContent(
        val contentState: LazyListState,
        toolbarState: ToolbarState
    ) : ScrollableScaffoldState(contentState, toolbarState)

    class ScrollContent(
        val contentState: ScrollState,
        toolbarState: ToolbarState
    ) : ScrollableScaffoldState(contentState, toolbarState)
}



@Composable
fun rememberScrollableScaffoldState(
    lazyListState: LazyListState,
    toolbarState: ToolbarState = rememberToolbarState()
) = remember {
    ScrollableScaffoldState.LazyListContent(lazyListState, toolbarState)
}



@Composable
fun rememberScrollableScaffoldState(
    scrollState: ScrollState,
    toolbarState: ToolbarState = rememberToolbarState()
) = remember {
    ScrollableScaffoldState.ScrollContent(scrollState, toolbarState)
}



@Composable
fun rememberScrollableScaffoldState(
    lazyListState: LazyListState,
    toolbarType: ToolbarType = CollapsingToolbarDefaults.type,
    initialToolbarHeightDp: Int = CollapsingToolbarDefaults.heightDp,
    maxToolbarAlpha: Float = CollapsingToolbarDefaults.maxAlpha
) = rememberScrollableScaffoldState(
    lazyListState = lazyListState,
    toolbarState = rememberToolbarState(
        type = toolbarType,
        initialHeightDp = initialToolbarHeightDp,
        maxAlpha = maxToolbarAlpha
    )
)



@Composable
fun rememberScrollableScaffoldState(
    scrollState: ScrollState,
    toolbarType: ToolbarType = CollapsingToolbarDefaults.type,
    initialToolbarHeightDp: Int = CollapsingToolbarDefaults.heightDp,
    maxToolbarAlpha: Float = CollapsingToolbarDefaults.maxAlpha
) = rememberScrollableScaffoldState(
    scrollState = scrollState,
    toolbarState = rememberToolbarState(
        type = toolbarType,
        initialHeightDp = initialToolbarHeightDp,
        maxAlpha = maxToolbarAlpha
    )
)
