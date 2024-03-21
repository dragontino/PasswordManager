package com.security.passwordmanager.model

import com.security.passwordmanager.domain.model.UID

sealed interface ComposableChild : ComposableData, Comparable<ComposableChild> {
    val uid: UID

    var isNameRenaming: Boolean

    override fun compareTo(other: ComposableChild) =
        this.uid.compareTo(other.uid)
}