package com.security.passwordmanager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val BottomSheetShape: RoundedCornerShape
    get() = RoundedCornerShape(
    topStart = bottomSheetCornerRadius,
    topEnd = bottomSheetCornerRadius
)

val bottomSheetCornerRadius = 18.dp

val DataCardShape = RoundedCornerShape(12.dp)

val ButtonShape = RoundedCornerShape(17.dp)

val BottomAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessLow)

data class TitleWithSubtitle(val title: String = "", val subtitle: String = "")
