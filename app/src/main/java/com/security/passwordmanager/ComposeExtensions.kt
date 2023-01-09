package com.security.passwordmanager

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp

val BottomAnimationSpec: AnimationSpec<Float> = tween(
    durationMillis = 350,
    easing = LinearOutSlowInEasing
)

fun LazyListState.isItemFullyVisible(index: Int): Boolean {
    with(layoutInfo) {
        val itemVisibleInfo = visibleItemsInfo.find { it.index == index }
        return if (itemVisibleInfo == null)
            false
        else firstVisibleItemScrollOffset > 0
            //viewportStartOffset - itemVisibleInfo.offset >= itemVisibleInfo.size
    }
}


@Immutable
internal data class SpanParagraphStyle(
    val spanStyle: SpanStyle,
    val paragraphStyle: ParagraphStyle
)


internal infix fun SpanStyle.and(paragraphStyle: ParagraphStyle) =
    SpanParagraphStyle(spanStyle = this, paragraphStyle)

internal infix fun ParagraphStyle.and(spanStyle: SpanStyle) =
    SpanParagraphStyle(spanStyle, paragraphStyle = this)

internal fun TextStyle.toSpanParagraphStyle() = SpanParagraphStyle(toSpanStyle(), toParagraphStyle())


internal inline fun <R : Any> AnnotatedString.Builder.withStyle(
    style: SpanParagraphStyle,
    crossinline block: AnnotatedString.Builder.() -> R
) : R {
    return withStyle(style.paragraphStyle) {
        withStyle(style.spanStyle) {
            block()
        }
    }
}


@Composable
fun Color.animate(durationMillis: Int = 600): Color =
    animateColorAsState(
        targetValue = this,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
    ).value


@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
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
    }.value
}


suspend fun LazyListState.smoothScrollToItem(targetPosition: Int) {
    // TODO: 11.11.2022 доделать
//    val firstVisibleItemPosition =
//        if (isItemFullyVisible(firstVisibleItemIndex)) {
//            firstVisibleItemIndex
//        } else {
//            firstVisibleItemIndex + 1
//        }

    val itemsToScroll = targetPosition - firstVisibleItemIndex

    Log.d("ComposeExtensions", "target = $targetPosition, itemsToScroll = $itemsToScroll, offset = $firstVisibleItemScrollOffset")
    val pixelsToScroll = layoutInfo
        .visibleItemsInfo
        .slice(toIndex = itemsToScroll)
        .sumOf { it.size } - firstVisibleItemScrollOffset

//    val duration = layoutInfo
//        .visibleItemsInfo
//        .slice(toIndex = itemsToScroll)
//        .fold(0) { acc, item ->
//            (acc + item.size * 1.2).roundToInt()
//        }


    Log.d("ComposeExtensions", "pixelsToScroll = $pixelsToScroll")

    animateScrollBy(
        value = pixelsToScroll.toFloat(),
        animationSpec = tween(
            durationMillis = 300 * itemsToScroll,//duration,
            delayMillis = 60,
            easing = FastOutSlowInEasing
        )
    )
}


@ExperimentalLayoutApi
@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = //WindowInsets.ime.getBottom(LocalDensity.current) > 0
    WindowInsets.isImeVisible
    
    return rememberUpdatedState(newValue = isImeVisible)
}



@ExperimentalAnimationApi
@Composable
fun Loading(isLoading: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(spring(stiffness = Spring.StiffnessLow)),
        exit = fadeOut(spring(stiffness = Spring.StiffnessLow)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.animate())
                .fillMaxSize(),
        ) {
            CircularProgressIndicator(
                strokeWidth = 3.5.dp,
                color = MaterialTheme.colorScheme.primary.animate(),
                modifier = Modifier
                    .animateEnterExit(
                        enter = slideInVertically(),
                        exit = slideOutVertically()
                    )
                    .scale(1.4f)
                    .align(Alignment.Center)
            )
        }
    }
}