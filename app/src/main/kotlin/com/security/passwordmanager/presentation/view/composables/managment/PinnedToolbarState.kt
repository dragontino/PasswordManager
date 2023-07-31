package com.security.passwordmanager.presentation.view.composables.managment

class PinnedToolbarState(
    statusBarHeightPx: Int,
    initialHeightPx: Float,
    maxAlpha: Float = 1f,
    initialOffsetPx: Float = 0f,
    initialContentOffsetPx: Float = initialHeightPx
) : ToolbarState(
    statusBarHeightPx = statusBarHeightPx,
    initialHeightPx = initialHeightPx,
    defaultZIndex = 1f,
    initialOffsetPx = initialOffsetPx,
    initialContentOffsetPx = initialContentOffsetPx,
    maxAlpha = maxAlpha
) {

    override val type: ToolbarType = ToolbarType.PinnedToolbar


    @Suppress("UNUSED_PARAMETER")
    override var offsetPx: Float
        get() = super.offsetPx
        set(value) {
            mutableOffsetPx = 0f
        }


    override var contentOffsetPx: Float
        get() = super.contentOffsetPx
        set(value) {
            mutableContentOffsetPx = value.coerceIn(0f, heightPx)
        }


    override fun calculateRoundedToolbarOffset(): Float = 0f

    override fun calculateRoundedContentOffset(): Float = contentOffsetPx
}