package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.TransformOrigin
import androidx.lifecycle.ViewModel
import com.security.passwordmanager.R
import com.security.passwordmanager.buildString
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.deleteFromLast
import com.security.passwordmanager.presentation.model.ObservableWebsite

class WebsiteViewModel : ViewModel() {

    @ExperimentalAnimationApi
    val enterScreenAnimation = slideInHorizontally(
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    ) { it / 2 } + scaleIn(
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing,
        ),
        initialScale = .5f,
        transformOrigin = TransformOrigin(0.5f, 1f),
    )

    val enterFabAnimation = slideInHorizontally(
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 100,
            easing = FastOutSlowInEasing
        )
    ) { it / 2 } + fadeIn(
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = 100,
            easing = LinearEasing
        )
    )


    @ExperimentalAnimationApi
    val exitScreenAnimation = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    ) { it / 2 } + scaleOut(
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        targetScale = .5f,
        transformOrigin = TransformOrigin(.5f, 1f)
    )


    val exitFabAnimation = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    ) { it / 2 } + fadeOut(
        animationSpec = tween(
            durationMillis = 100,
            easing = LinearEasing
        )
    )


    val accountList = mutableStateListOf(ObservableWebsite())
    val itemsToDelete = mutableStateListOf<Website>()

    var newWebsites by mutableStateOf(0)


    var showDialog by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)

    var dialogContent: @Composable () -> Unit by mutableStateOf({})
        private set

    var showErrors by mutableStateOf(false)


    var isNameRenaming by mutableStateOf(false)

    fun getEditItemName(context: Context) = if (isNameRenaming) {
        context.getString(R.string.cancel_renaming_data)
    } else {
        context.getString(R.string.rename_data)
    }


    var currentWebsitePosition by mutableStateOf(-1)
        private set

    val currentWebsite get() = accountList.getOrElse(currentWebsitePosition) { ObservableWebsite() }


    fun openDialog(content: @Composable () -> Unit) {
        dialogContent = content
        showDialog = true
    }

    fun closeDialog() {
        dialogContent = {}
        showDialog = false
    }

    @ExperimentalMaterialApi
    suspend fun openBottomSheet(sheetState: ModalBottomSheetState, position: Int) {
        currentWebsitePosition = position
        sheetState.show()
    }


    fun generateNameWebsite() = buildString(accountList.firstOrNull()?.address ?: "") {
        val firstWebsite = accountList[0]
        if ("www." in firstWebsite.address)
            deleteRange(0, 4)
        if (".com" in firstWebsite.address || ".org" in firstWebsite.address)
            deleteFromLast(4)
        if (".ru" in firstWebsite.address)
            deleteFromLast(3)
        this[0] = this[0].uppercaseChar()
    }


    fun checkWebsites(): Boolean =
        accountList.all { !it.haveErrors }

    fun updateErrorMessages(context: Context) =
        accountList.forEach { it.updateErrors(context) }
}