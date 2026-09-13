package com.pandora.app.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val PandoraShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

val CardShape = RoundedCornerShape(20.dp)
val PillShape = RoundedCornerShape(9999.dp)
val CapsuleShape = RoundedCornerShape(28.dp)
val BottomSheetTopShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
