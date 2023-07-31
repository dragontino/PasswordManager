package com.security.passwordmanager.presentation.view.composables.managment

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.presentation.view.composables.CollapsingToolbarDefaults
import kotlin.reflect.KProperty

sealed class ToolbarState(
    internal val statusBarHeightPx: Int,
    initialHeightPx: Float,
    initialOffsetPx: Float,
    initialContentOffsetPx: Float,
    private val defaultZIndex: Float,
    private val maxAlpha: Float
) {

    companion object {
        val Saver: Saver<ToolbarState, Any>
            get() {
                val statusBarHeightKey = "statusBarHeight"
                val heightKey = "height"
                val offsetKey = "offset"
                val contentOffsetKey = "contentOffset"
                val toolbarValueKey = "toolbarValue"
                val maxAlphaKey = "maxAlpha"
                val typeKey = "stateType"
                val zIndexKey = "zIndex"

                return mapSaver(
                    save = {
                        mapOf(
                            statusBarHeightKey to it.statusBarHeightPx,
                            heightKey to it.heightPx,
                            offsetKey to it.offsetPx,
                            contentOffsetKey to it.contentOffsetPx,
                            toolbarValueKey to it.toolbarValue,
                            maxAlphaKey to it.maxAlpha,
                            typeKey to it.type,
                            zIndexKey to it.zIndex
                        )
                    },
                    restore = { savedMap ->
                        ToolbarState(
                            type = savedMap[typeKey] as ToolbarType,
                            statusBarHeightPx = savedMap[statusBarHeightKey] as Int,
                            initialHeightPx = savedMap[heightKey] as Float,
                            initialOffsetPx = savedMap[offsetKey] as Float,
                            initialContentOffsetPx = savedMap[contentOffsetKey] as Float,
                            maxAlpha = savedMap[maxAlphaKey] as Float
                        ).also {
                            it.zIndex = savedMap[zIndexKey] as Float
                            it.toolbarValue = savedMap[toolbarValueKey] as ToolbarValue
                        }
                    }
                )
            }
    }


    protected abstract val type: ToolbarType


    private var _heightPx by mutableStateOf(initialHeightPx)
    protected var mutableOffsetPx by mutableStateOf(initialOffsetPx)
    protected var mutableContentOffsetPx by mutableStateOf(initialContentOffsetPx)


    var scrollTopLimitReached = true
    var isImeVisible: Boolean = false


    var heightPx: Float
        get() = _heightPx
        set(value) {
            mutableOffsetPx = mutableOffsetPx.coerceIn(-value, 0f)
            mutableContentOffsetPx = when (mutableContentOffsetPx) {
                _heightPx -> value
                else -> mutableContentOffsetPx.coerceIn(0f, value)
            }
            _heightPx = value
        }

    open var offsetPx: Float
        get() = mutableOffsetPx
        set(value) {
            mutableOffsetPx = when {
//                isImeVisible -> -_heightPx
                else -> value.coerceIn(-_heightPx, 0f)
            }
        }


    open var contentOffsetPx: Float
        get() = mutableContentOffsetPx
        set(value) {
            mutableContentOffsetPx = when {
//                isImeVisible -> 0f
                else -> value.coerceIn(0f, _heightPx)
            }
        }


    var toolbarValue by mutableStateOf(calculateToolbarValue())
        private set

    var zIndex: Float by mutableStateOf(defaultZIndex)
        private set


    val contentScrollProgress: Float
        get() =
            (contentOffsetPx / heightPx).coerceIn(0f..1f)

    val alpha: Float get() = maxAlpha * (1 - contentScrollProgress)

    val isVisible get() = toolbarValue == ToolbarValue.Visible


    fun updateValue() {
        toolbarValue = calculateToolbarValue()
    }


    private fun calculateToolbarValue() = when {
        contentOffsetPx > statusBarHeightPx -> ToolbarValue.Visible
        contentOffsetPx == 0f && offsetPx == -heightPx -> ToolbarValue.Hidden
        else -> ToolbarValue.Transparent
    }


    fun updateZIndex() {
        if (contentScrollProgress > 0f && contentScrollProgress < .8f) return

        zIndex = when (contentScrollProgress) {
            0f -> 1f
            else -> defaultZIndex
        }
    }


    /**
     * Функция, округляющая [contentOffsetPx] к минимальному или максимальному значению
     * @return новое значение [contentOffsetPx]
     */
    open fun calculateRoundedContentOffset() = when {
        contentOffsetPx > heightPx / 2 -> heightPx
        else -> 0f
    }


    /**
     * Функция, округляющая [offsetPx] к минимальному или максимальному значению
     * @return новое значение [offsetPx]
     */
    open fun calculateRoundedToolbarOffset() = when {
        contentOffsetPx > heightPx / 2 -> 0f
        offsetPx > -heightPx / 2 -> 0f
        else -> -heightPx
    }


    operator fun getValue(thisObj: Any?, property: KProperty<*>) = toolbarValue

    operator fun setValue(thisObj: Any?, property: KProperty<*>, value: ToolbarValue) {
        toolbarValue = value
    }
}


fun ToolbarState(
    type: ToolbarType,
    statusBarHeightPx: Int,
    initialHeightPx: Float,
    maxAlpha: Float,
    initialOffsetPx: Float = 0f,
    initialContentOffsetPx: Float = initialHeightPx,
) = when (type) {
    ToolbarType.ToolbarOverContent -> OverContentToolbarState(
        statusBarHeightPx = statusBarHeightPx,
        initialHeightPx = initialHeightPx,
        maxAlpha = maxAlpha,
        initialOffsetPx = initialOffsetPx,
        initialContentOffsetPx = initialContentOffsetPx
    )

    ToolbarType.ToolbarBelowContent -> BelowContentToolbarState(
        statusBarHeightPx = statusBarHeightPx,
        initialHeightPx = initialHeightPx,
        maxAlpha = maxAlpha,
        initialOffsetPx = initialOffsetPx,
        initialContentOffsetPx = initialContentOffsetPx
    )

    ToolbarType.PinnedToolbar -> PinnedToolbarState(
        statusBarHeightPx = statusBarHeightPx,
        initialHeightPx = initialHeightPx,
        maxAlpha = maxAlpha,
        initialOffsetPx = initialOffsetPx,
        initialContentOffsetPx = initialContentOffsetPx
    )
}


enum class ToolbarValue {
    Hidden,
    Transparent,
    Visible
}


enum class ToolbarType {
    ToolbarOverContent,
    ToolbarBelowContent,
    PinnedToolbar
}


@Composable
fun rememberToolbarState(
    type: ToolbarType = CollapsingToolbarDefaults.type,
    initialHeightDp: Int = CollapsingToolbarDefaults.heightDp,
    maxAlpha: Float = CollapsingToolbarDefaults.maxAlpha
): ToolbarState {

    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density)
    val initialHeightPx = with(density) { initialHeightDp.dp.toPx() }

    return rememberSaveable(saver = ToolbarState.Saver) {
        ToolbarState(type, statusBarHeightPx, initialHeightPx, maxAlpha)
    }
}