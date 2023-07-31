package com.security.passwordmanager.presentation.view.composables.managment

import com.security.passwordmanager.presentation.view.composables.CollapsingToolbarDefaults

class OverContentToolbarState(
    statusBarHeightPx: Int,
    initialHeightPx: Float,
    maxAlpha: Float = CollapsingToolbarDefaults.maxAlpha,
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

    override val type: ToolbarType = ToolbarType.ToolbarOverContent
}