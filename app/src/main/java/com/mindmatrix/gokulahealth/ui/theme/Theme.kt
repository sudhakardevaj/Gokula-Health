package com.mindmatrix.gokulahealth.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MeadowGreen,
    secondary = SkyBlue,
    tertiary = SunflowerAmber,
    background = LightMeadow,
    surface = MilkWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = EarthBrown,
    onSurface = EarthBrown
)

// We focus on a bright, friendly light theme
private val DarkColorScheme = darkColorScheme(
    primary = MeadowGreen,
    secondary = SkyBlue,
    background = Color(0xFF1B1C1B),
    surface = Color(0xFF2C2D2C)
)

@Composable
fun GokulaHealthTheme(
    darkTheme: Boolean = false, // Force light theme for the friendly look
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
