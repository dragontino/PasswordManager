package com.security.passwordmanager.domain.model

data class IconSite(
    val url: String,
    val icons: List<Icon>
)

data class Icon(
    val url: String,
    val width: Int,
    val height: Int,
    val format: String,
    val bytes: Int,
    val error: String?,
    val sha1sum: String
)
