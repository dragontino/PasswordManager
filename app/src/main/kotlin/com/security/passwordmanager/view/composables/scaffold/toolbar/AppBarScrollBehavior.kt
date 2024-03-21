package com.security.passwordmanager.view.composables.scaffold.toolbar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import com.security.passwordmanager.view.composables.scaffold.CollapsingToolbarScaffoldDefaults

@Stable
sealed interface AppBarScrollBehavior {
    val state: AppBarState

    val zIndex: MutableFloatState

    val nestedScrollConnection: NestedScrollConnection
}



internal class OnTopOfContentScrollBehavior(override val state: AppBarState) : AppBarScrollBehavior {
    override val zIndex: MutableFloatState = mutableFloatStateOf(1f)

    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y

            with(state) {
                val prevOffset = contentOffset
                if (
                    delta > 0 && scrollTopLimitReached ||
                    delta < 0
                ) {
                    contentOffset += delta
                }

                if (contentOffset <= 0) {
                    heightOffset += delta
                }
                state.updateValue()

                return when {
                    state.scrollTopLimitReached && state.overlappedFraction < 1f -> Offset(
                        x = 0f,
                        y = contentOffset - prevOffset
                    )
                    else -> Offset.Zero
                }
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return consumeOnPostScroll(available)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val initialHeightOffset = state.heightOffset
            val initialContentOffset = state.contentOffset
            val heightOffsetDiff = state.calculateRoundedAppBarOffset() - initialHeightOffset
            val contentOffsetDiff = state.calculateRoundedContentOffset() - initialContentOffset

            animate(
                initialValue = 0f,
                initialVelocity = available.y,
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = CollapsingToolbarScaffoldDefaults.animationDurationMillis,
                    easing = FastOutSlowInEasing
                )
            ) { fraction, _ ->
                state.heightOffset = initialHeightOffset + heightOffsetDiff * fraction
                state.contentOffset = initialContentOffset + contentOffsetDiff * fraction
                state.updateValue()
            }

            return Velocity.Zero
        }
    }
}



internal class PinnedScrollBehavior(override val state: AppBarState) : AppBarScrollBehavior {
    override val zIndex: MutableFloatState = mutableFloatStateOf(1f)

    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val prevOffset = state.contentOffset
            if (available.y > 0 && state.scrollTopLimitReached || available.y < 0) {
                state.contentOffset += available.y
            }
            return when {
                state.scrollTopLimitReached && state.overlappedFraction < 1f ->
                    Offset(x = 0f, y = state.contentOffset - prevOffset)

                else -> Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return consumeOnPostScroll(available)
        }
    }
}



internal class UnderContentScrollBehavior(override val state: AppBarState) : AppBarScrollBehavior {
    override val zIndex = mutableFloatStateOf(1f)

    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y

            with(state) {
                val prevOffset = contentOffset
                if (
                    delta > 0 && scrollTopLimitReached ||
                    delta < 0
                ) {
                    contentOffset += delta
                }

                if (contentOffset <= 0) {
                    heightOffset = when {
                        delta < 0f -> consumeHeightOffsetInScrollingUp(delta)
                        else -> consumeHeightOffsetInScrollingDown(delta)
                    }
                }

                state.updateValue()
                calculateZIndex(delta)?.let { zIndex.floatValue = it }

                return when {
                    state.scrollTopLimitReached && state.overlappedFraction < 1f ->
                        Offset(x = 0f, y = contentOffset - prevOffset)

                    else -> Offset.Zero
                }
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return consumeOnPostScroll(available)
        }


        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val initialHeightOffset = state.heightOffset
            val initialContentOffset = state.contentOffset
            val heightOffsetDiff = calculateRoundedHeightOffset() - initialHeightOffset
            val contentOffsetDiff = state.calculateRoundedContentOffset() - initialContentOffset

            animate(
                initialValue = 0f,
                initialVelocity = available.y,
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = CollapsingToolbarScaffoldDefaults.animationDurationMillis,
                    easing = FastOutSlowInEasing
                )
            ) { fraction, _ ->
                state.heightOffset = initialHeightOffset + heightOffsetDiff * fraction
                state.contentOffset = initialContentOffset + contentOffsetDiff * fraction
                state.updateValue()
                calculateZIndex(available.y)?.let { zIndex.floatValue = it }
            }

            return Velocity.Zero
        }
    }


    private fun consumeHeightOffsetInScrollingUp(delta: Float): Float {
        with(state) {
            return when {
                overlappedFraction < 1f -> 0f
                scrollTopLimitReached && contentOffset <= offsetTopLimit + 0.1f -> offsetTopLimit
                else -> heightOffset + delta
            }
        }
    }

    private fun consumeHeightOffsetInScrollingDown(delta: Float): Float {
        with(state) {
            if (overlappedFraction == 1f && scrollTopLimitReached && collapsedFraction == .5f) {
                return 0f
            } else {
                return heightOffset + delta
            }
        }
    }


    private fun calculateZIndex(delta: Float): Float? = when {
        delta > 0 && state.overlappedFraction == 1f && !state.scrollTopLimitReached -> 1f
        delta < 0 && state.overlappedFraction != 1f && state.collapsedFraction < .5f -> -1f
        else -> null
    }


    private fun calculateRoundedHeightOffset(): Float {
        val currentContentOffset = state.contentOffset
        val roundedContentOffset = state.calculateRoundedContentOffset()
        val roundedAppBarOffset = state.calculateRoundedAppBarOffset()
        with(state) {
            return when {
                currentContentOffset > offsetTopLimit && roundedContentOffset == offsetTopLimit -> offsetTopLimit
                else -> roundedAppBarOffset
            }
        }
    }
}



private fun AppBarScrollBehavior.consumeOnPostScroll(available: Offset): Offset = with(state) {
    return when {
        !scrollTopLimitReached -> Offset.Zero
        scrollDownLimitReached -> Offset.Zero
        overlappedFraction < 1f -> Offset.Zero
        else -> {
            val prevOffset = contentOffset
            contentOffset += available.y.coerceAtLeast(10f)
            Offset(x = 0f, y = contentOffset - prevOffset)
        }
    }
}


