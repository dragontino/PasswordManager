package com.security.passwordmanager.view.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import com.security.passwordmanager.view.theme.AnimationConstants.animationTimeMillis
import com.security.passwordmanager.view.theme.AnimationConstants.contentDurationRatio
import com.security.passwordmanager.view.theme.AnimationConstants.screenEnterExitRatio

sealed interface EnterExitAnimation {

    /**
     * Время анимации появления объекта, включая все задержки.
     * Гарантируется, что время работы [enter] анимации соответствует времени [enterTimeMillis]
     */
    val enterTimeMillis: Int

    /**
     * Время анимации исчезновения объекта, включая все задержки.
     * Гарантируется, что время работы [exit] анимации соответствует времени [exitTimeMillis]
     */
    val exitTimeMillis: Int

    val enter: EnterTransition
    val exit: ExitTransition
}


object AnimationConstants {
    const val animationTimeMillis = 800
    const val screenEnterExitRatio = 1.0 / 1
    const val contentDurationRatio = 7.0 / 8
}


data object AnyScreenAnimation : EnterExitAnimation {
    override val enterTimeMillis = animationTimeMillis

    override val exitTimeMillis = (animationTimeMillis / (screenEnterExitRatio + 1)).toInt()

    override val enter = fadeIn(
        animationSpec = tween(
            durationMillis = ((animationTimeMillis * screenEnterExitRatio) / (1 + screenEnterExitRatio)).toInt(),
            delayMillis = exitTimeMillis,
            easing = LinearEasing
        )
    )

    override val exit = fadeOut(
        animationSpec = tween(
            durationMillis = exitTimeMillis,
            easing = LinearEasing
        )
    )

}


data object ScreenContentAnimation : EnterExitAnimation {
    override val enterTimeMillis = animationTimeMillis

    override val exitTimeMillis =
        (contentDurationRatio * animationTimeMillis / (screenEnterExitRatio + 1)).toInt()

    override val enter = slideInVertically(
        animationSpec = tween(
            durationMillis = ((animationTimeMillis * screenEnterExitRatio) / (1 + screenEnterExitRatio)).toInt(),
            delayMillis = AnyScreenAnimation.exitTimeMillis,
            easing = LinearOutSlowInEasing
        )
    ) { it / 2 }

    override val exit = slideOutVertically(
        animationSpec = tween(
            durationMillis = exitTimeMillis,
            easing = LinearOutSlowInEasing
        )
    ) { it / 2 }
}


data object ScreenToolbarAnimation : EnterExitAnimation {
    override val enterTimeMillis = ScreenContentAnimation.enterTimeMillis
    override val exitTimeMillis = ScreenContentAnimation.exitTimeMillis

    override val enter: EnterTransition = slideInVertically(
        animationSpec = tween(
            durationMillis = ((animationTimeMillis * screenEnterExitRatio) / (1 + screenEnterExitRatio)).toInt(),
            delayMillis = AnyScreenAnimation.exitTimeMillis,
            easing = LinearOutSlowInEasing
        )
    )

    override val exit: ExitTransition = slideOutVertically(
        animationSpec = tween(
            durationMillis = exitTimeMillis,
            easing = LinearOutSlowInEasing
        )
    )
}


object NotesScreenAnimations {
    data object ScrollFabAnimation : EnterExitAnimation {
        override val enterTimeMillis = animationTimeMillis / 2
        override val exitTimeMillis = animationTimeMillis / 2

        override val enter = fadeIn(
            animationSpec = tween(
                durationMillis = enterTimeMillis,
                easing = LinearEasing
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = enterTimeMillis,
                easing = FastOutSlowInEasing
            )
        ) { it / 2 }

        override val exit = fadeOut(
            animationSpec = tween(
                durationMillis = exitTimeMillis / 2,
                delayMillis = exitTimeMillis / 2,
                easing = LinearEasing
            )
        ) + slideOutVertically(
            animationSpec = tween(
                durationMillis = exitTimeMillis,
                easing = FastOutSlowInEasing
            )
        ) { it / 2 }
    }


    data object ScreenFabAnimation : EnterExitAnimation {
        override val enterTimeMillis = animationTimeMillis
        override val exitTimeMillis = animationTimeMillis

        override val enter = slideInHorizontally(
            animationSpec = tween(
                durationMillis = enterTimeMillis,
                easing = LinearEasing
            )
        )

        override val exit = slideOutHorizontally(
            animationSpec = tween(
                durationMillis = exitTimeMillis,
                easing = LinearEasing
            )
        )
    }


    data object ListItemAnimation : EnterExitAnimation {
        override val enterTimeMillis = animationTimeMillis / 2
        override val exitTimeMillis = animationTimeMillis / 2


        override val enter = fadeIn(
            animationSpec = tween(
                durationMillis = animationTimeMillis / 3,
                easing = LinearOutSlowInEasing,
            ),
        ) + expandVertically(
            animationSpec = tween(
                durationMillis = animationTimeMillis / 2,
                easing = LinearOutSlowInEasing,
            ),
        )

        override val exit = fadeOut(
            animationSpec = tween(
                durationMillis = animationTimeMillis / 3,
                easing = FastOutLinearInEasing
            )
        ) + shrinkVertically(
            animationSpec = tween(
                durationMillis = animationTimeMillis / 2,
                easing = FastOutLinearInEasing
            )
        )
    }
}


data object SearchBarAnimation : EnterExitAnimation {
    override val enterTimeMillis = animationTimeMillis * 2
    override val exitTimeMillis = animationTimeMillis

    override val enter = slideInVertically(
        animationSpec = tween(
            durationMillis = 2 * enterTimeMillis / 3,
            delayMillis = enterTimeMillis / 3,
            easing = LinearOutSlowInEasing
        ),
        initialOffsetY = { it / 3 }
    )

    override val exit = slideOutVertically(
        animationSpec = tween(
            durationMillis = exitTimeMillis,
            easing = LinearOutSlowInEasing
        ),
        targetOffsetY = { it / 3 }
    )
}


data object WebsiteScreenFabAnimation : EnterExitAnimation {
    override val enterTimeMillis = animationTimeMillis
    override val exitTimeMillis = animationTimeMillis

    override val enter = slideInHorizontally(
        animationSpec = tween(
            durationMillis = enterTimeMillis,
            easing = LinearEasing
        )
    ) { it / 2 }

    override val exit = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = exitTimeMillis,
            easing = LinearEasing
        )
    ) { it / 2 }
}