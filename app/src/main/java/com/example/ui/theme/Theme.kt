package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = ThemeManager.isDark,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val primary = ThemeManager.primaryAccent
    val secondary = ThemeManager.secondaryAccent
    val bg = ThemeManager.backgroundColor
    val surface = ThemeManager.surfaceColor
    val surfaceElevated = ThemeManager.surfaceElevatedColor
    val outline = ThemeManager.surfaceBorderColor
    val textPrimary = ThemeManager.textPrimaryColor
    val textSecondary = ThemeManager.textSecondaryColor

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = bg,
            primaryContainer = surfaceElevated,
            onPrimaryContainer = primary,
            secondary = secondary,
            onSecondary = textPrimary,
            secondaryContainer = surfaceElevated,
            onSecondaryContainer = secondary,
            tertiary = CinemaAmber,
            onTertiary = bg,
            background = bg,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceElevated,
            onSurfaceVariant = textSecondary,
            outline = outline
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = bg,
            primaryContainer = surfaceElevated,
            onPrimaryContainer = primary,
            secondary = secondary,
            onSecondary = textPrimary,
            secondaryContainer = surfaceElevated,
            onSecondaryContainer = secondary,
            tertiary = CinemaAmber,
            onTertiary = textPrimary,
            background = bg,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceElevated,
            onSurfaceVariant = textSecondary,
            outline = outline
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = bg.toArgb()
                window.navigationBarColor = bg.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
