package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SukoonDarkColorScheme = darkColorScheme(
    primary = WarmGold,
    onPrimary = DeepSanctuary,
    primaryContainer = DeepGold,
    onPrimaryContainer = SoftGoldLight,
    secondary = SageMist,
    onSecondary = DeepSanctuary,
    secondaryContainer = ElevatedSage,
    onSecondaryContainer = WarmParchment,
    tertiary = TerracottaRose,
    onTertiary = DeepSanctuary,
    background = DeepSanctuary,
    onBackground = WarmParchment,
    surface = MidnightPine,
    onSurface = WarmParchment,
    surfaceVariant = ForestCard,
    onSurfaceVariant = SageMist,
    outline = MutedMoss
)

private val SukoonLightColorScheme = lightColorScheme(
    primary = Color(0xFF1B4D3E),
    onPrimary = ParchmentWhite,
    primaryContainer = SoftGoldLight,
    onPrimaryContainer = Color(0xFF2D2006),
    secondary = Color(0xFF476B5B),
    onSecondary = ParchmentWhite,
    secondaryContainer = Color(0xFFDCE8E0),
    onSecondaryContainer = Color(0xFF142B22),
    tertiary = Color(0xFF9C5B4B),
    onTertiary = ParchmentWhite,
    background = WarmParchment,
    onBackground = DeepSanctuary,
    surface = ParchmentWhite,
    onSurface = DeepSanctuary,
    surfaceVariant = SoftCream,
    onSurfaceVariant = Color(0xFF455D52),
    outline = Color(0xFF8AA195)
)

@Composable
fun SukoonTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SukoonDarkColorScheme else SukoonLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SukoonTypography,
        content = content
    )
}
