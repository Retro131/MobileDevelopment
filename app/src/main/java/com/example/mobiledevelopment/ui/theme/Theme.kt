package com.example.mobiledevelopment.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealLight,
    onPrimaryContainer = TealDark,
    secondary = GreenGray,
    onSecondary = Color.White,
    secondaryContainer = GreenLight,
    onSecondaryContainer = GreenDark,
    background = AppBackground,
    onBackground = AppText,
    surface = AppBackground,
    onSurface = AppText,
    onSurfaceVariant = MutedText,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = CardLow,
    surfaceContainer = CardNormal,
    surfaceContainerHigh = CardHigh,
    surfaceContainerHighest = CardHighest,
    outline = Border,
    outlineVariant = BorderLight
)

@Composable
fun MobileDevelopmentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}