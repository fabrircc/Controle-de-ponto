package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Slate950,
    primaryContainer = AccentBlueContainer,
    onPrimaryContainer = Color.White,
    secondary = EntradaGreen,
    onSecondary = Color.White,
    secondaryContainer = EntradaGreenContainer,
    onSecondaryContainer = EntradaGreenLight,
    tertiary = RetroAmber,
    onTertiary = Slate950,
    tertiaryContainer = RetroAmberContainer,
    onTertiaryContainer = RetroAmberLight,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    outlineVariant = Slate800,
    error = SaidaRed,
    onError = Color.White,
    errorContainer = SaidaRedContainer,
    onErrorContainer = SaidaRedLight
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = EntradaGreenDark,
    onSecondary = Color.White,
    secondaryContainer = EntradaGreenLight,
    onSecondaryContainer = EntradaGreenContainer,
    tertiary = RetroAmberDark,
    onTertiary = Color.White,
    tertiaryContainer = RetroAmberLight,
    onTertiaryContainer = Color(0xFF78350F),
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate300,
    outlineVariant = Slate200,
    error = SaidaRedDark,
    onError = Color.White,
    errorContainer = SaidaRedLight,
    onErrorContainer = Color(0xFF991B1B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark totem kiosk look
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
