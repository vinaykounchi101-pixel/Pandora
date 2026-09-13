package com.pandora.app.core.designsystem.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PorcelainColorScheme = lightColorScheme(
    primary = IrisPrimary,
    onPrimary = PorcelainSheetWhite,
    primaryContainer = IrisFixed,
    onPrimaryContainer = OnIrisFixedVariant,
    secondary = ApricotOrange,
    onSecondary = PorcelainSheetWhite,
    secondaryContainer = ApricotContainer,
    onSecondaryContainer = OnApricotFixed,
    tertiary = CeruleanTertiary,
    onTertiary = PorcelainSheetWhite,
    tertiaryContainer = CeruleanDark,
    onTertiaryContainer = CeruleanFixed,
    background = PorcelainCanvas,
    onBackground = TextPrimary,
    surface = PorcelainCanvas,
    onSurface = TextPrimary,
    surfaceVariant = PorcelainContainer,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = PorcelainSheetWhite,
    surfaceContainerLow = PorcelainContainerLow,
    surfaceContainer = PorcelainContainer,
    surfaceContainerHigh = PorcelainContainerHigh,
    surfaceContainerHighest = PorcelainContainerHighest,
    outline = OutlineHairline,
    outlineVariant = OutlineVariant,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface
)

@Composable
fun PandoraTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = PorcelainColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PandoraTypography,
        shapes = PandoraShapes,
        content = content
    )
}
