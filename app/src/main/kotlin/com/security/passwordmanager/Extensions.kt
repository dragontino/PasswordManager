package com.security.passwordmanager

import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun Bundle?.getInt(key: String, defaultValue: Int) =
    this?.getInt(key) ?: defaultValue


internal fun Bundle?.getString(key: String, defaultValue: String = "") =
    this?.getString(key)?.takeIf { it != "null" } ?: defaultValue


@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun <D : Enum<*>> Bundle?.getEnum(key: String, defaultValue: D): D =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            this?.getSerializable(key, defaultValue::class.java) ?: defaultValue
        }

        else -> this?.getSerializable(key) as D? ?: defaultValue
    }


inline fun buildString(initString: String = "", builderAction: StringBuilder.() -> Unit) =
    StringBuilder(initString).apply(builderAction).toString()


fun <E> List<E>.slice(fromIndex: Int = 0, toIndex: Int = size) =
    slice(fromIndex until toIndex)


fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}


fun Parcel.getString(defaultValue: String = "") = readString() ?: defaultValue


fun String.isValidUrl() = URLUtil.isValidUrl(this)


fun Context.checkNetworkConnection(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val capabilities = connectivityManager
        .getNetworkCapabilities(connectivityManager.activeNetwork)

    return capabilities != null
}


// Compose functions


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
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
    ).value


@Composable
fun Float.animate(durationMillis: Int = 600): Float =
    animateFloatAsState(
        targetValue = this,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
    ).value


@Composable
fun Dp.animate(durationMillis: Int = 600): Dp =
    animateDpAsState(
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

    Log.d(
        "ComposeExtensions",
        "target = $targetPosition, itemsToScroll = $itemsToScroll, offset = $firstVisibleItemScrollOffset"
    )
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
            progress = progress,
            strokeWidth = 3.5.dp,
            strokeCap = StrokeCap.Round,
            color = color.animate(),
            modifier = modifier
        )
    }
}


fun String.convertToColor(): Color {
    val sum = this.fold(0) { acc, c -> acc + c.code }
    return Color(0xFF000000 + sum % 0xFFFFFF).run {
        copy(red = 1 - red, green = 1 - green, blue = 1 - blue)
    }
}