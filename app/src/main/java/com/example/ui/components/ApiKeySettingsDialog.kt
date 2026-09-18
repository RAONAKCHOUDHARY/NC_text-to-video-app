package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiVideoService
import com.example.ui.theme.ThemeManager
import kotlinx.coroutines.launch

@Composable
fun ApiKeySettingsDialog(
    currentApiKey: String,
    onSaveKey: (String) -> Unit,
    onClearKey: () -> Unit,
    onDismiss: () -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var testStatus by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = null,
        containerColor = ThemeManager.surfaceColor,
        shape = RoundedCornerShape(20.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("api_key_settings_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ThemeManager.primaryAccent.copy(alpha = 0.18f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = ThemeManager.primaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Gemini API Key",
                                color = ThemeManager.textPrimaryColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Unlock full production AI capabilities",
                                color = ThemeManager.textSecondaryColor,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ThemeManager.textSecondaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Banner
                val isConfigured = currentApiKey.isNotBlank()
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isConfigured) Color(0xFF10B981).copy(alpha = 0.12f) else ThemeManager.surfaceElevatedColor,
                    border = BorderStroke(
                        1.dp,
                        if (isConfigured) Color(0xFF10B981).copy(alpha = 0.4f) else ThemeManager.surfaceBorderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isConfigured) Icons.Default.CheckCircle else Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = if (isConfigured) Color(0xFF10B981) else ThemeManager.primaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isConfigured)
                                "Custom API Key Active (Securely Persisted)"
                            else
                                "No Custom Key Set (Using intelligent local fallback)",
                            color = if (isConfigured) Color(0xFF10B981) else ThemeManager.textSecondaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Text Field for API Key
                Text(
                    text = "API KEY (Google AI Studio)",
                    color = ThemeManager.textMutedColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = {
                        apiKeyInput = it
                        testStatus = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_text_input"),
                    placeholder = {
                        Text(
                            text = "AIzaSy...",
                            color = ThemeManager.textMutedColor,
                            fontSize = 13.sp
                        )
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPasswordVisible) "Hide key" else "Show key",
                                    tint = ThemeManager.textSecondaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val clip = clipboardManager.getText()?.text
                                    if (!clip.isNullOrBlank()) {
                                        apiKeyInput = clip.trim()
                                        testStatus = null
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste from clipboard",
                                    tint = ThemeManager.primaryAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = ThemeManager.textPrimaryColor,
                        unfocusedTextColor = ThemeManager.textPrimaryColor,
                        focusedContainerColor = ThemeManager.surfaceElevatedColor,
                        unfocusedContainerColor = ThemeManager.surfaceElevatedColor,
                        focusedIndicatorColor = ThemeManager.primaryAccent,
                        unfocusedIndicatorColor = ThemeManager.surfaceBorderColor
                    ),
                    singleLine = true
                )

                // Test Status Message
                if (testStatus != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = testStatus!!,
                        color = if (testStatus!!.startsWith("✓")) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Info text
                Text(
                    text = "Your API key is stored locally in device private storage (SharedPreferences) and sent directly to Google Gemini endpoints.",
                    color = ThemeManager.textSecondaryColor,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Test Button
                    OutlinedButton(
                        onClick = {
                            if (apiKeyInput.isBlank()) {
                                testStatus = "Please enter an API key first"
                                return@OutlinedButton
                            }
                            isTesting = true
                            coroutineScope.launch {
                                val service = GeminiVideoService()
                                val res = service.enhancePrompt(
                                    prompt = "Epic cinematic ocean",
                                    style = "Cinematic",
                                    cameraMotion = "Orbit",
                                    lighting = "Golden Hour",
                                    customApiKey = apiKeyInput.trim()
                                )
                                isTesting = false
                                testStatus = if (res.isSuccess) "✓ Key validated successfully with Gemini!" else "✗ Key test notice (saved key will still be used)"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ThemeManager.textPrimaryColor
                        )
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = ThemeManager.primaryAccent
                            )
                        } else {
                            Text("Test Key", fontSize = 12.sp)
                        }
                    }

                    // Clear Button
                    if (currentApiKey.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                apiKeyInput = ""
                                onClearKey()
                                testStatus = "Key removed"
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear key",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Save Button
                    Button(
                        onClick = {
                            onSaveKey(apiKeyInput.trim())
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("save_api_key_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeManager.primaryAccent
                        )
                    ) {
                        Text(
                            text = "Save Key",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    )
}
