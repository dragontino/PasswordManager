package com.security.passwordmanager.view.composables.scaffold.toolbar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.reflect.KProperty

class AppBarState
internal constructor(
    internal val statusBarHeight: Int,
    initialOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float,
    private val maxAlpha: Float
) {
    companion object {
        val Saver: Saver<AppBarState, Any>
            get() {
                val statusBarHeightKey = "statusBarHeight"
                val heightKey = "height"
                val offsetKey = "offset"
                val contentOffsetKey = "contentOffset"
                val toolbarValueKey = "toolbarValue"
                val maxAlphaKey = "maxAlpha"

                return mapSaver(
                    save = {
                        mapOf(
                            statusBarHeightKey to it.statusBarHeight,
                            heightKey to it.offsetTopLimit,
                            offsetKey to it.heightOffset,
                            contentOffsetKey to it.contentOffset,
                            toolbarValueKey to it.appBarValue,
                            maxAlphaKey to it.maxAlpha
                        )
                    },
                    restore = { savedMap ->
                        AppBarState(
                            statusBarHeight = savedMap[statusBarHeightKey] as Int,
                            initialOffsetLimit = savedMap[heightKey] as Float,
                            initialHeightOffset = savedMap[offsetKey] as Float,
                            initialContentOffset = savedMap[contentOffsetKey] as Float,
                            maxAlpha = savedMap[maxAlphaKey] as Float
                        ).also {
                            it.appBarValue = savedMap[toolbarValueKey] as AppBarValue
                        }
                    }
                )
            }
    }

    private var _heightOffset by mutableFloatStateOf(initialHeightOffset)
    private var _contentOffset by mutableFloatStateOf(initialContentOffset)


    var scrollTopLimitReached = true

    var scrollDownLimitReached = true


    /**
     * The top app bar's height offset limit in pixels, which represents the limit that a top app
     * bar is allowed to collapse to.
     *
     * Use this limit to coerce the [heightOffset] and [contentOffset] values when they is updated.
     */
    var offsetTopLimit: Float = initialOffsetLimit
        set(value) {
            if (value != field) {
                require(value <= 0 && value.isFinite()) { "Offset limit value must be finite and <= 0" }
                val prevLimit = field
                heightOffset = when (heightOffset) {
                    prevLimit -> value
                    else -> heightOffset
                }
                contentOffset = when (contentOffset) {
                    prevLimit -> value
                    else -> contentOffset
                }
                field = value
            }
        }

    var contentOffsetBottomLimit: Float = 0f
        set(value) {
            if (value != field) {
                require(value > offsetTopLimit && value.isFinite()) {
                    """
                        Content offset bottom limit must be finite and more than top limit.
                        Top offset limit is $offsetTopLimit pixels, your value: $value.
                        """.trimIndent()
                }
                val prevLimit = field
                contentOffset = when (contentOffset) {
                    prevLimit -> value
                    else -> contentOffset
                }
                field = value
            }
        }

    var heightOffset: Float
        get() = _heightOffset
        set(value) {
            _heightOffset = value.coerceIn(offsetTopLimit, 0f)
        }


    var contentOffset: Float
        get() = _contentOffset
        set(value) {
            _contentOffset = value.coerceIn(offsetTopLimit, contentOffsetBottomLimit)
        }


    var appBarValue by mutableStateOf(calculateAppBarValue())
        private set


    /**
     * A value that represents the collapsed height percentage of the app bar.
     *
     * A `0.0` represents a fully expanded bar, and `1.0` represents a fully collapsed bar (computed
     * as [heightOffset] / [offsetTopLimit]).
     */
    val collapsedFraction: Float
        get() = when (offsetTopLimit) {
            0f -> 0f
            else -> heightOffset / offsetTopLimit
        }


    /**
     * A value that represents the percentage of the app bar area that is overlapping with the
     * content scrolled behind it.
     *
     * A `0.0` indicates that the app bar does not overlap any content, while `1.0` indicates that
     * the entire visible app bar area overlaps the scrolled content.
     */
    val overlappedFraction: Float
        get() = when (offsetTopLimit) {
            0f -> 0f
            else -> (contentOffset / offsetTopLimit).coerceIn(0f..1f)
        }

    val alpha: Float get() = maxAlpha * overlappedFraction

    val isVisible get() = appBarValue == AppBarValue.Visible


    fun updateValue() {
        appBarValue = calculateAppBarValue()
    }


    private fun calculateAppBarValue() = when {
        contentOffset > statusBarHeight + offsetTopLimit -> AppBarValue.Visible
        overlappedFraction == 1f && collapsedFraction == 1f -> AppBarValue.Hidden
        else -> AppBarValue.Transparent
    }





    /**
     * Функция, округляющая [contentOffset] к минимальному или максимальному значению
     * @return новое значение [contentOffset]
     */
    fun calculateRoundedContentOffset() = when {
        overlappedFraction < .5f -> 0f
        else -> offsetTopLimit
    }


    /**
     * Функция, округляющая [heightOffset] к минимальному или максимальному значению
     * @return новое значение [heightOffset]
     */
    fun calculateRoundedAppBarOffset() = when {
        overlappedFraction < .5f -> 0f
        collapsedFraction < .5f -> 0f
        else -> offsetTopLimit
    }


    operator fun getValue(thisObj: Any?, property: KProperty<*>) = appBarValue

    operator fun setValue(thisObj: Any?, property: KProperty<*>, value: AppBarValue) {
        appBarValue = value
    }
}


enum class AppBarValue {
    Hidden,
    Transparent,
    Visible
}


@Composable
fun rememberAppBarState(
    initialOffsetLimitDp: Int = CollapsingAppBarDefaults.offsetLimitDp,
    maxAlpha: Float = CollapsingAppBarDefaults.maxAlpha
): AppBarState {
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)
    val initialOffsetLimit = with(density) { initialOffsetLimitDp.dp.toPx() }

    return rememberSaveable(saver = AppBarState.Saver) {
        AppBarState(
            statusBarHeight = statusBarHeight,
            initialOffsetLimit = initialOffsetLimit,
            initialHeightOffset = 0f,
            initialContentOffset = 0f,
            maxAlpha = maxAlpha
        )
    }
}