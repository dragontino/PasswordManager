package com.security.passwordmanager.presentation.model.data

import com.security.passwordmanager.data.UID

sealed interface ComposableChild : ComposableData, Comparable<ComposableChild> {
    val uid: UID

    var isNameRenaming: Boolean

    override fun compareTo(other: ComposableChild) =
        this.uid.compareTo(other.uid)
}