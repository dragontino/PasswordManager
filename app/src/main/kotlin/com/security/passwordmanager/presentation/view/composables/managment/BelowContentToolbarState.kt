package com.security.passwordmanager.presentation.view.composables.managment

import com.security.passwordmanager.presentation.view.composables.CollapsingToolbarDefaults

class BelowContentToolbarState(
    statusBarHeightPx: Int,
    initialHeightPx: Float,
    maxAlpha: Float = CollapsingToolbarDefaults.maxAlpha,
    initialOffsetPx: Float = 0f,
    initialContentOffsetPx: Float = initialHeightPx
) : ToolbarState(
    statusBarHeightPx = statusBarHeightPx,
    initialHeightPx = initialHeightPx,
    defaultZIndex = -1f,
    initialOffsetPx = initialOffsetPx,
    initialContentOffsetPx = initialContentOffsetPx,
    maxAlpha = maxAlpha
) {

    override val type: ToolbarType = ToolbarType.ToolbarBelowContent

    override var offsetPx: Float
        get() = super.offsetPx
        set(value) {
            val incrementedValue = value - super.offsetPx
            when {
                incrementedValue < 0 -> {
                    if (contentOffsetPx > 0) super.offsetPx = 0f
                    else if (scrollTopLimitReached && super.contentOffsetPx <= 0.1f) super.offsetPx = -heightPx
                    else super.offsetPx = value
                }
                else -> {
                    if (contentOffsetPx == 0f && scrollTopLimitReached && super.offsetPx == -heightPx / 2) super.offsetPx = 0f
                    else super.offsetPx = value
                }
            }
        }


    override fun calculateRoundedToolbarOffset(): Float {
        val currentContentOffset = contentOffsetPx
        val roundedToolbarOffset = super.calculateRoundedToolbarOffset()
        val roundedContentOffset = super.calculateRoundedContentOffset()
        return when {
            currentContentOffset > 0f && roundedContentOffset == 0f -> -heightPx
            else -> roundedToolbarOffset
        }
    }
}