package com.security.passwordmanager.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

//fun LazyListState.isItemFullyVisible(index: Int): Boolean {
//    with(layoutInfo) {
//        val itemVisibleInfo = visibleItemsInfo.find { it.index == index }
//        return if (itemVisibleInfo == null)
//            false
//        else firstVisibleItemScrollOffset > 0
//        //viewportStartOffset - itemVisibleInfo.offset >= itemVisibleInfo.size
//    }
//}


@Composable
fun Color.animate(durationMillis: Int = 600): Color =
    animateColorAsState(
        targetValue = this,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "colorAnimation"
    ).value


@Composable
fun Float.animate(durationMillis: Int = 600): Float =
    animateFloatAsState(
        targetValue = this,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "floatAnimation"
    ).value


@Composable
fun Dp.animate(durationMillis: Int = 600): Dp =
    animateDpAsState(
        targetValue = this,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "dpAnimation"
    ).value


@Composable
fun LazyListState.scrollingUpState(): State<Boolean> {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }
}


suspend fun LazyListState.smoothScrollToItem(targetPosition: Int) {
    val itemsToScroll = targetPosition - firstVisibleItemIndex
    val itemSize = with (layoutInfo.visibleItemsInfo) {
        if (isEmpty()) 0 else this[0].size
    }

    val pixelsToScroll = when {
        itemsToScroll < layoutInfo.visibleItemsInfo.size -> layoutInfo
            .visibleItemsInfo
            .slice(0..<itemsToScroll)
            .sumOf { it.size } - firstVisibleItemScrollOffset

        else -> itemSize * itemsToScroll
    }


    val duration = when {
        itemsToScroll < layoutInfo.visibleItemsInfo.size -> layoutInfo
            .visibleItemsInfo
            .slice(0..<itemsToScroll)
            .fold(0) { acc, item ->
                (acc + item.size * 1.2).roundToInt()
            }

        else -> (itemsToScroll * itemSize * 1.2).roundToInt()
    }

    animateScrollBy(
        value = pixelsToScroll.toFloat(),
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = 60,
            easing = FastOutSlowInEasing
        )
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.isImeVisible
    return rememberUpdatedState(newValue = isImeVisible)
}


@Composable
fun LoadingInBox(
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier.scale(1.4f),
    progress: Float? = null,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(containerColor.animate())
            .fillMaxSize(),
    ) {
        Loading(progress = progress, modifier = loadingModifier, color = contentColor)
    }
}


@Composable
fun Loading(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = MaterialTheme.colorScheme.primary
) = when (progress) {
    null -> {
        CircularProgressIndicator(
            color = color.animate(),
            strokeCap = StrokeCap.Round,
            strokeWidth = 3.5.dp,
            modifier = modifier
        )
    }

    else -> {
        CircularProgressIndicator(
            progress = { progress },
            modifier = modifier,
            color = color.animate(),
            strokeWidth = 3.5.dp,
            strokeCap = StrokeCap.Round,
        )
    }
}


fun String.convertToColor(): Color {
    val sum = sumOf { it.code }
    return Color(0xFF000000 + sum % 0xFFFFFF).run {
        copy(red = 1 - red, green = 1 - green, blue = 1 - blue)
    }
}


inline fun String.colorize(charColor: (Char) -> Color) = buildAnnotatedString {
    this@colorize.forEach { char ->
        withStyle(SpanStyle(color = charColor(char))) {
            append(char)
        }
    }
}


val Color.reversed: Color
    get() = copy(red = 1 - red, green = 1 - green, blue = 1 - blue)