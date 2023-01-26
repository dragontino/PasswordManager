package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.animationTimeMillis
import com.security.passwordmanager.buildString
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.deleteFromLast
import com.security.passwordmanager.presentation.model.ObservableWebsite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WebsiteViewModel : ViewModel() {


    enum class ViewModelState {
        Loading,
        Ready
    }



    val enterFabAnimation = slideInHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    ) { it / 2 }


    val exitFabAnimation = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    ) { it / 2 }



    var viewModelState by mutableStateOf(ViewModelState.Loading)


    val accountList = mutableStateListOf(ObservableWebsite())
    val itemsToDelete = mutableStateListOf<Website>()

    var newWebsites by mutableStateOf(0)


    var showDialog by mutableStateOf(false)
        private set

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


    init {
        viewModelScope.launch {
            delay(5000)
            viewModelState = ViewModelState.Ready
        }
    }


    @Composable
    fun screenShape() = MaterialTheme.shapes.medium.copy(
        bottomStart = CornerSize(0),
        bottomEnd = CornerSize(0)
    )


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