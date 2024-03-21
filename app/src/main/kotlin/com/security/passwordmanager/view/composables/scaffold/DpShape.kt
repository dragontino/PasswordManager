package com.security.passwordmanager.view.composables.scaffold

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

data class DpShape(
    val topStart: Dp = 0.dp,
    val topEnd: Dp = 0.dp,
    val bottomStart: Dp = 0.dp,
    val bottomEnd: Dp = 0.dp
) : Shape {

    constructor(top: Dp = 0.dp, bottom: Dp = 0.dp) : this(
        topStart = top,
        topEnd = top,
        bottomStart = bottom,
        bottomEnd = bottom
    )

    constructor(all: Dp = 0.dp) : this(
        top = all,
        bottom = all
    )

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val roundedCornerShape = RoundedCornerShape(
            topStart = topStart,
            topEnd = topEnd,
            bottomStart = bottomStart,
            bottomEnd = bottomEnd
        )
        return roundedCornerShape.createOutline(size, layoutDirection, density)
    }

    fun animate(percent: Float, density: Density): Shape = GenericShape { size, layoutDirection ->
        val topStartRadiusPx = with(density) { (topStart * percent).toPx() }
        val topEndRadiusPx = with(density) { (topEnd * percent).toPx() }
        val bottomStartRadiusPx = with(density) { (bottomStart * percent).toPx() }
        val bottomEndRadiusPx = with(density) { (bottomEnd * percent).toPx() }

        val roundRect = when (layoutDirection) {
            LayoutDirection.Ltr -> RoundRect(
                rect = size.toRect(),
                topLeft = CornerRadius(topStartRadiusPx),
                topRight = CornerRadius(topEndRadiusPx),
                bottomLeft = CornerRadius(bottomStartRadiusPx),
                bottomRight = CornerRadius(bottomEndRadiusPx)
            )

            LayoutDirection.Rtl -> RoundRect(
                rect = size.toRect(),
                topLeft = CornerRadius(topEndRadiusPx),
                topRight = CornerRadius(topStartRadiusPx),
                bottomLeft = CornerRadius(bottomEndRadiusPx),
                bottomRight = CornerRadius(bottomStartRadiusPx)
            )
        }

        addRoundRect(roundRect)
    }

}