package com.pandora.app.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Editorial Porcelain Mathematical Spacing Tokens (4dp / 8dp baseline grid).
 */
object Spacing {
    val None: Dp = 0.dp
    val ExtraSmall: Dp = 4.dp      // 4dp - Tight element pairing & micro padding
    val Small: Dp = 8.dp           // 8dp - Inner badge padding, small gaps
    val MediumSmall: Dp = 12.dp    // 12dp - Compact container padding
    val Medium: Dp = 16.dp         // 16dp - Standard card padding & screen gutter
    val Large: Dp = 20.dp          // 20dp - Section gaps
    val ExtraLarge: Dp = 24.dp     // 24dp - Major section separators
    val Huge: Dp = 32.dp           // 32dp - Header negative space
    val Massive: Dp = 48.dp        // 48dp - Empty state offsets
}
