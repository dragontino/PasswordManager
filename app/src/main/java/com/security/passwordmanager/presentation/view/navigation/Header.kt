package com.security.passwordmanager.presentation.view.navigation

import androidx.compose.ui.text.AnnotatedString

sealed interface HeadingInterface {
    val title: CharSequence
    val subtitle: CharSequence
    val beautifulDesign: Boolean

    fun isEmpty(): Boolean = title.isBlank()
}



data class Header(
    override val title: String = "",
    override val subtitle: String = "",
    override val beautifulDesign: Boolean = false
) : HeadingInterface



data class AnnotatedHeader(
    override val title: AnnotatedString = AnnotatedString(""),
    override val subtitle: AnnotatedString = AnnotatedString(""),
    override val beautifulDesign: Boolean = false
) : HeadingInterface