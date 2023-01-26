package com.security.passwordmanager

import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import me.onebone.toolbar.CollapsingToolbarState

internal fun Bundle?.getInt(key: String, defaultValue: Int) =
    this?.getInt(key) ?: defaultValue


internal fun Bundle?.getString(key: String, defaultValue: String = "") =
    this?.getString(key) ?: defaultValue


@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun <D : Enum<*>> Bundle?.getEnum(key: String, defaultValue: D): D =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            this?.getSerializable(key, defaultValue::class.java) ?: defaultValue
        }
        else -> {
            this?.getSerializable(key) as D? ?: defaultValue
        }
    }



fun showToast(context: Context?, text: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, text, duration).show()



fun showToast(context: Context?, @StringRes text: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, text, duration).show()



inline fun buildString(initString: String = "", builderAction: StringBuilder.() -> Unit) =
    StringBuilder(initString).apply(builderAction).toString()



fun StringBuilder.deleteFromLast(count: Int = 1) =
    deleteRange(length - count, length)



fun <E> List<E>.slice(fromIndex: Int = 0, toIndex: Int = size) =
    subList(fromIndex, toIndex)



fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}



fun Parcel.getString(defaultValue: String = "") = readString() ?: defaultValue



fun String.isValidUrl() = URLUtil.isValidUrl(this)



fun <T> MutableList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}


fun Context.checkNetworkConnection(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val capabilities = connectivityManager
        .getNetworkCapabilities(connectivityManager.activeNetwork)

    return capabilities != null
}



// Compose functions



val BottomAnimationSpec: AnimationSpec<Float> = tween(
    durationMillis = 350,
    easing = LinearOutSlowInEasing
)



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
fun CollapsingToolbarState.progress(): Float =
    ((height - minHeight).toFloat() / (maxHeight - minHeight)).coerceIn(0f, 1f)

val CollapsingToolbarState.scrollProgress: Float
get() = ((height - minHeight).toFloat() / (maxHeight - minHeight)).coerceIn(0f, 1f)



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


@Composable
fun Loading(modifier: Modifier = Modifier, progress: Float? = null) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background.animate())
            .fillMaxSize(),
    ) {
        if (progress == null) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary.animate(),
                strokeCap = StrokeCap.Round,
                strokeWidth = 3.5.dp,
                modifier = Modifier
                    .scale(1.4f)
                    .align(Alignment.Center)
            )
        } else {
            CircularProgressIndicator(
                progress = progress,
                strokeWidth = 3.5.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.primary.animate(),
                modifier = Modifier
                    .scale(1.4f)
                    .align(Alignment.Center)
            )
        }
    }
}


const val animationTimeMillis = 800
const val screenEnterExitRatio = 1.0 / 1
const val contentDurationRatio = 7.0 / 8



val EnterScreenAnimation = fadeIn(
    animationSpec = tween(
        durationMillis = ((animationTimeMillis * screenEnterExitRatio) / (1 + screenEnterExitRatio)).toInt(),
        delayMillis = (animationTimeMillis / (screenEnterExitRatio + 1)).toInt(),
        easing = LinearEasing
    )
)

val ExitScreenAnimation = fadeOut(
    animationSpec = tween(
        durationMillis = (animationTimeMillis / (screenEnterExitRatio + 1)).toInt(),
        easing = LinearEasing
    )
)



val EnterContentAnimation = slideInVertically(
    animationSpec = tween(
        durationMillis = ((animationTimeMillis * screenEnterExitRatio) / (1 + screenEnterExitRatio)).toInt(),
        delayMillis = (animationTimeMillis / (screenEnterExitRatio + 1)).toInt(),
        easing = LinearOutSlowInEasing
    )
) { it / 2 }

val ExitContentAnimation = slideOutVertically(
    animationSpec = tween(
        durationMillis = (contentDurationRatio * animationTimeMillis / (screenEnterExitRatio + 1)).toInt(),
        easing = LinearOutSlowInEasing
    )
) { it / 2 }
