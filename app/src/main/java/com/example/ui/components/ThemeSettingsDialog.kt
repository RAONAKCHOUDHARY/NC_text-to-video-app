package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentPalette
import com.example.ui.theme.StudioTheme
import com.example.ui.theme.ThemeManager
import com.example.ui.theme.ThemeMode

@Composable
fun ThemeSettingsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_settings_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = ThemeManager.primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Studio Appearance",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.textPrimaryColor
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ThemeManager.textMutedColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5-Color Dynamic Theme Engine
                Text(
                    text = "DYNAMIC THEME ENGINE (5 PRESETS)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeManager.textSecondaryColor,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StudioTheme.entries.forEach { studioTheme ->
                        val isSelected = ThemeManager.activeStudioTheme == studioTheme
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { ThemeManager.selectStudioTheme(studioTheme) }
                                .testTag("studio_theme_${studioTheme.name}"),
                            color = if (isSelected) studioTheme.primaryAccent.copy(alpha = 0.16f) else ThemeManager.surfaceElevatedColor,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) studioTheme.primaryAccent else ThemeManager.surfaceBorderColor
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(studioTheme.background, CircleShape)
                                            .border(1.5.dp, studioTheme.primaryAccent, CircleShape)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(studioTheme.secondaryAccent, CircleShape)
                                                .align(Alignment.Center)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = studioTheme.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) studioTheme.primaryAccent else ThemeManager.textPrimaryColor
                                        )
                                        Text(
                                            text = studioTheme.description,
                                            fontSize = 10.sp,
                                            color = ThemeManager.textMutedColor,
                                            maxLines = 1
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = studioTheme.primaryAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Theme Mode Selector
                Text(
                    text = "THEME MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeManager.textSecondaryColor,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.values().forEach { mode ->
                        val isSelected = ThemeManager.currentThemeMode == mode
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { ThemeManager.currentThemeMode = mode }
                                .testTag("theme_mode_${mode.name}"),
                            color = if (isSelected) ThemeManager.primaryAccent.copy(alpha = 0.18f) else ThemeManager.surfaceElevatedColor,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ThemeManager.primaryAccent else ThemeManager.surfaceBorderColor
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = mode.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ThemeManager.primaryAccent else ThemeManager.textPrimaryColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Accent Color Selector
                Text(
                    text = "ACCENT PALETTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeManager.textSecondaryColor,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val paletteRows = AccentPalette.entries.chunked(3)
                    for (rowPalettes in paletteRows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (palette in rowPalettes) {
                                val isSelected = ThemeManager.currentAccent == palette
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { ThemeManager.currentAccent = palette }
                                        .testTag("accent_${palette.name}"),
                                    color = if (isSelected) palette.primary.copy(alpha = 0.18f) else ThemeManager.surfaceElevatedColor,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) palette.primary else ThemeManager.surfaceBorderColor
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(palette.primary, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = palette.label.split(" ").last(),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) palette.primary else ThemeManager.textPrimaryColor,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = ThemeManager.surfaceColor,
        shape = RoundedCornerShape(20.dp)
    )
}
