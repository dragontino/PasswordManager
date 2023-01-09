package com.security.passwordmanager.presentation.viewmodel

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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



    var isLoading by mutableStateOf(false)

    var isSearching by mutableStateOf(false)

    var query by mutableStateOf("")

    var showFab by mutableStateOf(true)

    var dataList = listOf<DataUI>()
}