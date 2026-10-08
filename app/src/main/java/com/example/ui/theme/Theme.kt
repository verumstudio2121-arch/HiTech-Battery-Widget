package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HiTechDarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color(0xFF030712),
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = PurpleAccent,
    onSecondary = Color(0xFF1E1B4B),
    secondaryContainer = Color(0xFF581C87),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = EmeraldAccent,
    onTertiary = Color(0xFF064E3B),
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder
)

@Composable
fun HiTechTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HiTechDarkColorScheme,
        typography = Typography,
        content = content
    )
}
