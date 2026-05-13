package com.arogya.sahaya.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary            = Primary700,
    onPrimary          = Surface,
    primaryContainer   = Primary100,
    onPrimaryContainer = OnSurface,
    secondary          = Teal500,
    onSecondary        = Surface,
    secondaryContainer = Teal100,
    onSecondaryContainer = OnSurface,
    tertiary           = PurpleAI,
    tertiaryContainer  = PurpleLight,
    error              = RedSOS,
    errorContainer     = RedLight,
    background         = Background,
    onBackground       = OnSurface,
    surface            = Surface,
    onSurface          = OnSurface,
    surfaceVariant     = Primary50,
    onSurfaceVariant   = SubText,
    outline            = Divider,
)

@Composable
fun ArogyaSahayaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
