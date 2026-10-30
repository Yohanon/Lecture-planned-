package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color(0xFF003549),
    primaryContainer = Color(0xFF004D68),
    onPrimaryContainer = Color(0xFFCBE6FF),
    secondary = PurpleAccent,
    onSecondary = Color(0xFF221752),
    secondaryContainer = Color(0xFF382E6B),
    onSecondaryContainer = Color(0xFFE5DEFF),
    tertiary = EmeraldAccent,
    onTertiary = Color(0xFF003822),
    tertiaryContainer = Color(0xFF005234),
    onTertiaryContainer = Color(0xFF8CF8BF),
    background = SlateBlack,
    onBackground = TextPrimary,
    surface = DeepNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    surfaceTint = CyanAccent,
    outline = CardBorder,
    outlineVariant = Color(0xFF1E293B),
    error = RoseError,
    onError = Color(0xFF601410)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
