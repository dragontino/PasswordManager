package com.security.passwordmanager.viewmodel

import com.security.passwordmanager.view.composables.dialogs.DialogType
import com.security.passwordmanager.view.composables.managment.ScreenEvents
import kotlinx.coroutines.flow.SharedFlow

interface EventsFlow {
    val eventsFlow: SharedFlow<ScreenEvents>

    fun openDialog(type: DialogType)

    fun closeDialog()

    fun navigateTo(args: Any?)

    fun showSnackbar(message: String)
}