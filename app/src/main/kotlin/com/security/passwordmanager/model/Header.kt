package com.security.passwordmanager.model

data class Header(
    val title: CharSequence = "",
    val subtitle: CharSequence = "",
    val beautifulDesign: Boolean = false
) {
    fun isEmpty(): Boolean = title.isBlank()
}