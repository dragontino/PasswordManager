package com.security.passwordmanager.presentation.viewmodel

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.security.passwordmanager.animationTimeMillis
import com.security.passwordmanager.presentation.model.DataUI

class NotesViewModel : ViewModel() {

    val enterFabAnimation = fadeIn(
        animationSpec = tween(
            durationMillis = 700,
            easing = LinearEasing
        )
    ) + slideInVertically(
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    ) { it / 2 }

    val exitFabAnimation = fadeOut(
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearEasing
        )
    ) + slideOutVertically(
        animationSpec = tween(
            durationMillis = 700,
            easing = FastOutSlowInEasing
        )
    ) { it / 2 }



    val enterDataItemAnimation = fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing,
        ),
    ) + expandVertically(
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing,
        ),
    )

    val exitDataItemAnimation = fadeOut(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutLinearInEasing
        )
    ) + shrinkVertically(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutLinearInEasing
        )
    )



    val enterScreenFabAnimation = slideInHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    )

    val exitScreenFabAnimation = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    )




    @Composable
    fun screenShape() = if (showTopBar) {
        MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        )
    } else {
        RoundedCornerShape(0)
    }



    var isLoading by mutableStateOf(false)

    var isSearching by mutableStateOf(false)

    var query by mutableStateOf("")

    var showFab by mutableStateOf(true)

    var showTopBar by mutableStateOf(true)

    var dataList = listOf<DataUI>()
}