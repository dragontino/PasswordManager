package com.security.passwordmanager.view.composables.scaffold

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.view.composables.scaffold.toolbar.CollapsingAppBarScope


@Composable
fun CollapsingAppBarScope.ToolbarButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    contentDescription: String = "",
    enabled: Boolean = true,
    colors: ToolbarButtonColors = ToolbarButtonDefaults.colors(),
    onClick: () -> Unit
) {

    @Composable
    fun ToolbarButtonColors.convertToIconButtonColors(alpha: Float) =
        IconButtonDefaults.outlinedIconButtonColors(
            containerColor = containerColor + transparentContainerColor ratio alpha,
            contentColor = contentColor + transparentContentColor ratio alpha,
        )

    val borderColor =
        colors.transparentBorderColor + colors.borderColor ratio state.overlappedFraction

    OutlinedIconButton(
        onClick = onClick,
        colors = colors.convertToIconButtonColors(alpha = 1 - state.overlappedFraction),
        shape = CircleShape,
        enabled = enabled,
        border = when (borderColor) {
            Color.Transparent -> null
            else -> BorderStroke(width = 1.1.dp, color = borderColor)
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = iconModifier
        )
    }
}


object ToolbarButtonDefaults {
    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = LocalContentColor.current,
        borderColor: Color = Color.Transparent,
        transparentContainerColor: Color = containerColor,
        transparentContentColor: Color = contentColor,
        transparentBorderColor: Color = borderColor
    ) = ToolbarButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        transparentContainerColor = transparentContainerColor,
        transparentContentColor = transparentContentColor,
        borderColor = borderColor,
        transparentBorderColor = transparentBorderColor
    )
}


data class ToolbarButtonColors internal constructor(
    val containerColor: Color,
    val contentColor: Color,
    val transparentContainerColor: Color,
    val transparentContentColor: Color,
    val borderColor: Color,
    val transparentBorderColor: Color,
)