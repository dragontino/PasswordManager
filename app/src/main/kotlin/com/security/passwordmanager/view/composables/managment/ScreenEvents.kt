package com.security.passwordmanager.view.composables.managment

import com.security.passwordmanager.view.composables.dialogs.DialogType

sealed interface ScreenEvents {
    data class OpenDialog(val type: DialogType) : ScreenEvents
    data object CloseDialog : ScreenEvents
    data class ShowSnackbar(val message: String) : ScreenEvents
    data class Navigate(val args: Any?) : ScreenEvents
}