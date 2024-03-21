package com.security.passwordmanager.view.composables.scaffold.toolbar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.security.passwordmanager.view.composables.scaffold.LocalCollapsingToolbarScaffoldColors
import com.security.passwordmanager.view.theme.RaspberryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingAppBarScope.CollapsingToolbar(
    title: String,
    modifier: Modifier = Modifier,
    navigationButton: @Composable (() -> Unit) = {},
    textStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold
    ),
    colors: CollapsingToolbarColors = LocalCollapsingToolbarScaffoldColors.current.toolbarColors,
    actions: @Composable (RowScope.() -> Unit) = {}
) {

    @Composable
    fun CollapsingToolbarColors.convertToTopAppBarColors(
        alpha: Float,
        contentColorPercent: Float
    ) = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = transparentContainerColor.copy(alpha = alpha),
        titleContentColor = titleContentColor + transparentTitleContentColor ratio contentColorPercent,
        navigationIconContentColor = navigationIconContentColor + transparentNavigationIconContentColor ratio contentColorPercent,
        actionIconContentColor = actionIconContentColor + transparentActionIconContentColor ratio contentColorPercent
    )



    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = textStyle
            )
        },
        navigationIcon = navigationButton,
        actions = actions,
        colors = colors.convertToTopAppBarColors(
            alpha = state.alpha,
            contentColorPercent = 1 - state.overlappedFraction
        ),
        modifier = modifier
    )
}


data class CollapsingToolbarColors internal constructor(
    val containerColor: Color,
    val titleContentColor: Color,
    val navigationIconContentColor: Color,
    val actionIconContentColor: Color,
    val transparentContainerColor: Color,
    val transparentTitleContentColor: Color,
    val transparentNavigationIconContentColor: Color,
    val transparentActionIconContentColor: Color
) {
    companion object {
        val Default = CollapsingToolbarColors(
            containerColor = RaspberryLight,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White,
            transparentContainerColor = Color.White,
            transparentTitleContentColor = Color.Black,
            transparentNavigationIconContentColor = RaspberryLight,
            transparentActionIconContentColor = RaspberryLight,
        )
    }
}


object CollapsingAppBarDefaults {
    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        titleContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        transparentContainerColor: Color = MaterialTheme.colorScheme.background,
        transparentTitleContentColor: Color = MaterialTheme.colorScheme.onBackground,
        transparentNavigationIconContentColor: Color = MaterialTheme.colorScheme.onBackground,
        transparentActionIconContentColor: Color = MaterialTheme.colorScheme.onBackground
    ) =
        CollapsingToolbarColors(
            containerColor = containerColor,
            titleContentColor = titleContentColor,
            navigationIconContentColor = navigationIconContentColor,
            actionIconContentColor = actionIconContentColor,
            transparentContainerColor = transparentContainerColor,
            transparentTitleContentColor = transparentTitleContentColor,
            transparentNavigationIconContentColor = transparentNavigationIconContentColor,
            transparentActionIconContentColor = transparentActionIconContentColor
        )


    const val offsetLimitDp = -95
    const val maxAlpha = .83f

    @Composable
    fun onTopOfContentScrollBehavior(
        state: AppBarState = rememberAppBarState()
    ) : AppBarScrollBehavior = OnTopOfContentScrollBehavior(state)

    @Composable
    fun underContentScrollBehavior(
        state: AppBarState = rememberAppBarState()
    ): AppBarScrollBehavior = UnderContentScrollBehavior(state)

    @Composable
    fun pinnedScrollBehavior(
        state: AppBarState = rememberAppBarState()
    ): AppBarScrollBehavior = PinnedScrollBehavior(state)
}


interface CollapsingAppBarScope {
    val state: AppBarState


    /**
     * Функция, которая смешивает 2 цвета. Пример использования:
     * ```
     * val grayColor = Color.Black + Color.White ratio 0.5f
     * ```
     * Эта функция является первой из двух в процессе смешивания цветов.
     * Для получения финального цвета необходимо определить пропорцию, с которой нужно смешать 2 цвета —
     * используйте для этого функцию [ratio]
     * @return объект класса [ColorPair], содержащий в себе переданные цвета
     * @see ratio
     */
    operator fun Color.plus(otherColor: Color) = ColorPair(
        firstColor = this,
        secondColor = otherColor,
    )

    /**
     * Промежуточный data класс, используемый в процессе смешения цветов
     */
    @Stable
    data class ColorPair(val firstColor: Color, val secondColor: Color)

    /**
     * Функция, которая добавляет пропорцию между двумя цветами и высчитывает итоговый цвет.
     * Пример использования:
     * ```
     * val grayColor = Color.Black + Color.White ratio 0.5f
     * ```
     * Эта функция является вторай из двух, которые позволяют смешать 2 цвета.
     * Она нужна для определения того, сколько процентов в итоговом цвете будет от первого цвета, а сколько от второго.
     * Для определения пары цветов, которые будут участвовать в сумме, используйте функцию [Color.plus].
     * @param ratio отношение количества первого цвета к количеству итогового цвета.
     * Исходные цвета будут смешаны в пропорции ratio / (1 - ratio)
     * К примеру, если [ratio] = 0.5f, то исходные цвета будут смешаны в пропорции 1 : 1.
     * @see Color.plus
     */
    @Stable
    infix fun ColorPair.ratio(ratio: Float): Color {
        val fraction = FastOutSlowInEasing.transform(ratio)
        return Color(
            alpha = linearInterpolate(firstColor.alpha, secondColor.alpha, fraction),
            red = linearInterpolate(firstColor.red, secondColor.red, fraction),
            green = linearInterpolate(firstColor.green, secondColor.green, fraction),
            blue = linearInterpolate(firstColor.blue, secondColor.blue, fraction)
        )
    }

    private fun linearInterpolate(start: Float, stop: Float, fraction: Float): Float {
        return start * fraction + stop * (1 - fraction)
    }
}