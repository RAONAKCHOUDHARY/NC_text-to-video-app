package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class ThemeMode(val label: String) {
    AMOLED("AMOLED Black"),
    DARK("Dark Studio"),
    LIGHT("Light Studio")
}

enum class AccentPalette(val label: String, val primary: Color, val secondary: Color) {
    CYAN("Cinema Cyan", Color(0xFF00E5FF), Color(0xFF8B5CF6)),
    PURPLE("Cyber Purple", Color(0xFF8B5CF6), Color(0xFFEC4899)),
    AMBER("Electric Amber", Color(0xFFF59E0B), Color(0xFFEF4444)),
    EMERALD("Emerald Glow", Color(0xFF10B981), Color(0xFF00E5FF)),
    ROSE("Rose Neon", Color(0xFFF43F5E), Color(0xFF8B5CF6)),
    BLUE("Sapphire Blue", Color(0xFF3B82F6), Color(0xFF00E5FF))
}

/**
 * 5-Color Dynamic Theme Engine:
 * 1. OLED Midnight (Pure deep black + electric cyan highlights)
 * 2. Crisp Studio Light (Clean minimal off-white + slate charcoal)
 * 3. Cyberpunk Prism (Deep purple + glowing neon magenta & electric blue)
 * 4. Cinematic Amber (Dark film-grade charcoal + warm golden amber)
 * 5. Aurora Emerald (Matte deep jade + mint green accents)
 */
enum class StudioTheme(
    val displayName: String,
    val description: String,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceBorder: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color
) {
    OLED_MIDNIGHT(
        displayName = "OLED Midnight",
        description = "Pure deep black with electric cyan highlights",
        isDark = true,
        background = Color(0xFF000000),
        surface = Color(0xFF0D0D0D),
        surfaceElevated = Color(0xFF171717),
        surfaceBorder = Color(0xFF262626),
        primaryAccent = Color(0xFF00E5FF),
        secondaryAccent = Color(0xFF8B5CF6),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8),
        textMuted = Color(0xFF64748B)
    ),
    CRISP_STUDIO_LIGHT(
        displayName = "Crisp Studio Light",
        description = "Clean minimal off-white with slate charcoal",
        isDark = false,
        background = Color(0xFFF8FAFC),
        surface = Color(0xFFFFFFFF),
        surfaceElevated = Color(0xFFF1F5F9),
        surfaceBorder = Color(0xFFE2E8F0),
        primaryAccent = Color(0xFF0284C7),
        secondaryAccent = Color(0xFF6366F1),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF475569),
        textMuted = Color(0xFF94A3B8)
    ),
    CYBERPUNK_PRISM(
        displayName = "Cyberpunk Prism",
        description = "Deep purple with glowing neon magenta & electric blue",
        isDark = true,
        background = Color(0xFF0D061F),
        surface = Color(0xFF160D33),
        surfaceElevated = Color(0xFF23144D),
        surfaceBorder = Color(0xFF3B1E78),
        primaryAccent = Color(0xFFF43F5E),
        secondaryAccent = Color(0xFF00E5FF),
        textPrimary = Color(0xFFFDF4FF),
        textSecondary = Color(0xFFE879F9),
        textMuted = Color(0xFFA855F7)
    ),
    CINEMATIC_AMBER(
        displayName = "Cinematic Amber",
        description = "Dark film-grade charcoal with warm golden amber",
        isDark = true,
        background = Color(0xFF121212),
        surface = Color(0xFF1C1917),
        surfaceElevated = Color(0xFF292524),
        surfaceBorder = Color(0xFF44403C),
        primaryAccent = Color(0xFFF59E0B),
        secondaryAccent = Color(0xFFEA580C),
        textPrimary = Color(0xFFFFFBEB),
        textSecondary = Color(0xFFD6D3D1),
        textMuted = Color(0xFFA8A29E)
    ),
    AURORA_EMERALD(
        displayName = "Aurora Emerald",
        description = "Matte deep jade with mint green accents",
        isDark = true,
        background = Color(0xFF041812),
        surface = Color(0xFF08271E),
        surfaceElevated = Color(0xFF0F382C),
        surfaceBorder = Color(0xFF1B5543),
        primaryAccent = Color(0xFF10B981),
        secondaryAccent = Color(0xFF34D399),
        textPrimary = Color(0xFFECFDF5),
        textSecondary = Color(0xFFA7F3D0),
        textMuted = Color(0xFF6EE7B7)
    )
}

object ThemeManager {
    var activeStudioTheme by mutableStateOf(StudioTheme.OLED_MIDNIGHT)
    var currentThemeMode by mutableStateOf(ThemeMode.AMOLED)
    var currentAccent by mutableStateOf(AccentPalette.CYAN)

    fun selectStudioTheme(theme: StudioTheme) {
        activeStudioTheme = theme
        currentThemeMode = when (theme) {
            StudioTheme.OLED_MIDNIGHT -> ThemeMode.AMOLED
            StudioTheme.CRISP_STUDIO_LIGHT -> ThemeMode.LIGHT
            StudioTheme.CYBERPUNK_PRISM -> ThemeMode.DARK
            StudioTheme.CINEMATIC_AMBER -> ThemeMode.DARK
            StudioTheme.AURORA_EMERALD -> ThemeMode.DARK
        }
        currentAccent = when (theme) {
            StudioTheme.OLED_MIDNIGHT -> AccentPalette.CYAN
            StudioTheme.CRISP_STUDIO_LIGHT -> AccentPalette.BLUE
            StudioTheme.CYBERPUNK_PRISM -> AccentPalette.PURPLE
            StudioTheme.CINEMATIC_AMBER -> AccentPalette.AMBER
            StudioTheme.AURORA_EMERALD -> AccentPalette.EMERALD
        }
    }

    val isDark: Boolean
        get() = activeStudioTheme.isDark

    val currentTheme: StudioTheme
        get() = activeStudioTheme

    val isDarkTheme: Boolean
        get() = isDark

    fun toggleThemeMode() {
        if (isDark) {
            selectStudioTheme(StudioTheme.CRISP_STUDIO_LIGHT)
        } else {
            selectStudioTheme(StudioTheme.OLED_MIDNIGHT)
        }
    }

    val backgroundColor: Color
        get() = activeStudioTheme.background

    val surfaceColor: Color
        get() = activeStudioTheme.surface

    val surfaceElevatedColor: Color
        get() = activeStudioTheme.surfaceElevated

    val surfaceBorderColor: Color
        get() = activeStudioTheme.surfaceBorder

    val textPrimaryColor: Color
        get() = activeStudioTheme.textPrimary

    val textSecondaryColor: Color
        get() = activeStudioTheme.textSecondary

    val textMutedColor: Color
        get() = activeStudioTheme.textMuted

    val primaryAccent: Color
        get() = activeStudioTheme.primaryAccent

    val secondaryAccent: Color
        get() = activeStudioTheme.secondaryAccent
}
