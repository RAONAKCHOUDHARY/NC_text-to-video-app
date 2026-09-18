package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ApiKeySettingsDialog
import com.example.ui.components.PulseCraftLogo
import com.example.ui.components.ThemeSettingsDialog
import com.example.ui.theme.StudioTheme
import com.example.ui.theme.ThemeManager

@Composable
fun SettingsScreen(
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    onClearApiKey: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeManager.backgroundColor)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(ThemeManager.primaryAccent.copy(alpha = 0.18f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = ThemeManager.primaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Studio Settings",
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "API keys, visual themes, and audio engine preferences",
                        color = ThemeManager.textSecondaryColor,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Section 1: Gemini AI Key Setup (Prominent Card)
        item {
            val hasKey = currentApiKey.isNotBlank()
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_api_key_card"),
                shape = RoundedCornerShape(16.dp),
                color = ThemeManager.surfaceColor,
                border = BorderStroke(
                    1.5.dp,
                    if (hasKey) Color(0xFF10B981).copy(alpha = 0.5f) else ThemeManager.primaryAccent.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (hasKey) Color(0xFF10B981).copy(alpha = 0.15f) else ThemeManager.primaryAccent.copy(alpha = 0.15f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (hasKey) Icons.Default.CheckCircle else Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = if (hasKey) Color(0xFF10B981) else ThemeManager.primaryAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Gemini API Key",
                                    color = ThemeManager.textPrimaryColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (hasKey) "Active & Saved in SharedPreferences" else "Not Configured (Fallback Active)",
                                    color = if (hasKey) Color(0xFF10B981) else ThemeManager.textSecondaryColor,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (hasKey)
                            "Key: ••••••••••••••••${currentApiKey.takeLast(4)}"
                        else
                            "Connect your Google AI Studio API key to enable live Gemini prompt expansions, cinematic storyboard choreography, and deep scriptwriting.",
                        color = ThemeManager.textSecondaryColor,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showApiKeyDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("configure_api_key_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeManager.primaryAccent
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasKey) "Update Key" else "Set Up API Key",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (hasKey) {
                            OutlinedButton(
                                onClick = onClearApiKey,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF4444)
                                )
                            ) {
                                Text("Remove", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Theme Engine
        item {
            SettingsCategoryCard(
                title = "VISUAL & THEME ENGINE",
                items = listOf(
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Color Theme (${ThemeManager.currentTheme.displayName})",
                        subtitle = "Choose from 5 studio color themes (OLED Midnight, Crisp Studio, Cyberpunk, Amber, Emerald)",
                        onClick = { showThemeDialog = true }
                    ),
                    SettingsItem(
                        icon = Icons.Default.DarkMode,
                        title = "Theme Mode (${if (ThemeManager.isDarkTheme) "Dark" else "Light"})",
                        subtitle = "Toggle between deep cinematic dark and studio light modes",
                        onClick = { ThemeManager.toggleThemeMode() }
                    )
                )
            )
        }

        // Section 3: Audio & Voice Engine
        item {
            SettingsCategoryCard(
                title = "AUDIO & VOICE OVER ENGINE",
                items = listOf(
                    SettingsItem(
                        icon = Icons.Default.RecordVoiceOver,
                        title = "Realistic Voice Profiles",
                        subtitle = "Deep Horror, Cinematic Thriller, Realistic Storyteller & Dramatic Shocking",
                        onClick = { /* informative */ }
                    ),
                    SettingsItem(
                        icon = Icons.Default.Tune,
                        title = "Studio Acoustic Enhancements",
                        subtitle = "Sub-bass resonance, neural pitch modulation & suspense pacing",
                        onClick = { /* informative */ }
                    )
                )
            )
        }

        // Section 4: App Information & Logo
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ThemeManager.surfaceColor,
                border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PulseCraftLogo(size = 56.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "PulseCraft Studio",
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version 2.0 (Cinematic Production Edition)",
                        color = ThemeManager.textSecondaryColor,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All-in-One AI Video Generator, Deep Horror Voice Engine & Creative Suite",
                        color = ThemeManager.textMutedColor,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    if (showApiKeyDialog) {
        ApiKeySettingsDialog(
            currentApiKey = currentApiKey,
            onSaveKey = { key ->
                onSaveApiKey(key)
                showApiKeyDialog = false
            },
            onClearKey = {
                onClearApiKey()
            },
            onDismiss = { showApiKeyDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeSettingsDialog(onDismiss = { showThemeDialog = false })
    }
}

private data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Composable
private fun SettingsCategoryCard(
    title: String,
    items: List<SettingsItem>
) {
    Column {
        Text(
            text = title,
            color = ThemeManager.textMutedColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = ThemeManager.surfaceColor,
            border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = item.onClick)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ThemeManager.surfaceElevatedColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = ThemeManager.primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = ThemeManager.textPrimaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = item.subtitle,
                                color = ThemeManager.textSecondaryColor,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = ThemeManager.textMutedColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (index < items.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(ThemeManager.surfaceBorderColor)
                        )
                    }
                }
            }
        }
    }
}
