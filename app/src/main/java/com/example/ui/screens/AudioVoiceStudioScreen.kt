package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VoiceProfile
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaPink
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.ThemeManager
import java.util.Locale
import kotlin.math.sin

@Composable
fun AudioVoiceStudioScreen(
    isSpeaking: Boolean,
    speechProgress: Float,
    onSpeak: (text: String, profile: VoiceProfile, pitch: Float, speed: Float) -> Unit,
    onStop: () -> Unit,
    onAttachToVideo: (script: String, profile: VoiceProfile) -> Unit,
    onGenerateAIScript: (topic: String, onDone: (String) -> Unit) -> Unit,
    isGeneratingScript: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedProfile by remember { mutableStateOf(VoiceProfile.ADAM_DEEP_MALE) }
    var scriptText by remember {
        mutableStateOf("In a world sculpted by shadows, the dawn of cinematic imagination awakens with breathtaking power.")
    }

    var pitchSlider by remember { mutableFloatStateOf(selectedProfile.defaultPitch) }
    var speedSlider by remember { mutableFloatStateOf(selectedProfile.defaultSpeed) }
    var voiceVolumeBalance by remember { mutableFloatStateOf(0.8f) } // Voice vs BG music
    var selectedBgMusic by remember { mutableStateOf("ambient_synth") }

    val bgTracks = listOf(
        Pair("ambient_synth", "Warm Analog Synth"),
        Pair("cinematic_drone", "Cinematic Sub-Drone"),
        Pair("cyber_pulse", "Cyberpunk Arp Beat"),
        Pair("nature_wind", "Gentle Rain & Wind"),
        Pair("none", "No Music (Voice Only)")
    )

    val scriptPresets = listOf(
        Pair("Cinematic Trailer", "In the silence between the stars, a new legend begins. Witness the journey of discovery."),
        Pair("Hindi Poetry & Nature", "सूरज की पहली किरण जब बादलों को चीरती हुई धरती पर पड़ती है, तो जीवन की एक नई दास्तान शुरू होती है।"),
        Pair("Rajasthani Heritage", "खम्मा घणी सा! मरुधरा री माटी और किलों री अमर शान, PulseCraft सूं बणायो बेमिसाल नजरो।"),
        Pair("Punjabi Energy", "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ ਜੀ! ਪੰਜਾਬ ਦੇ ਰੰਗ ਅਤੇ ਧੜਕਣ ਨਾਲ ਭਰਪੂਰ ਇਹ ਸਿਨੇਮੈਟਿਕ ਕਹਾਣੀ ਦੇਖੋ।"),
        Pair("Haryanvi Bold", "राम राम भाई सारे ने! हरियाणा का दम और देसी ठाठ, पूरा सिनेमैटिक स्वैग!"),
        Pair("ASMR Gentle Rain", "Close your eyes. Soft raindrops fall upon the calm water, creating endless gentle ripples.")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeManager.backgroundColor)
            .testTag("audio_voice_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(ThemeManager.primaryAccent.copy(alpha = 0.18f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = ThemeManager.primaryAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Voice & Audio Studio",
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cinematic TTS Narration & Soundtrack Mixing",
                        color = ThemeManager.textSecondaryColor,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Live Audio Waveform Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ThemeManager.surfaceColor,
                border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
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
                                    .size(8.dp)
                                    .background(if (isSpeaking) ThemeManager.primaryAccent else ThemeManager.textMutedColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSpeaking) "SYNTHESIS ACTIVE" else "AUDIO ENGINE READY",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpeaking) ThemeManager.primaryAccent else ThemeManager.textMutedColor
                            )
                        }

                        Text(
                            text = selectedProfile.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ThemeManager.primaryAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Waveform Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ThemeManager.surfaceElevatedColor)
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val w = size.width
                            val h = size.height
                            val barCount = 36
                            val barWidth = w / barCount

                            for (i in 0 until barCount) {
                                val barFraction = if (isSpeaking) {
                                    val wave = sin((speechProgress * 20.0 + i * 0.4)).toFloat()
                                    0.25f + 0.65f * Math.abs(wave)
                                } else {
                                    0.15f + 0.15f * sin((i * 0.3)).toFloat()
                                }

                                val barHeight = h * barFraction
                                val barTop = (h - barHeight) / 2f
                                val barColor = if (isSpeaking) {
                                    if (i % 2 == 0) ThemeManager.primaryAccent else CinemaPurple
                                } else {
                                    ThemeManager.textMutedColor.copy(alpha = 0.4f)
                                }

                                drawRect(
                                    color = barColor,
                                    topLeft = Offset(i * barWidth + 2f, barTop),
                                    size = Size(barWidth - 4f, barHeight)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Playback Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isSpeaking) {
                                    onStop()
                                } else {
                                    onSpeak(scriptText, selectedProfile, pitchSlider, speedSlider)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSpeaking) CinemaPink else ThemeManager.primaryAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(42.dp).testTag("speak_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSpeaking) "STOP NARRATION" else "PLAY VOICEOVER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Attach to Video Button
                        OutlinedButton(
                            onClick = {
                                onAttachToVideo(scriptText, selectedProfile)
                                Toast.makeText(context, "Voiceover attached to current video project!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ThemeManager.textPrimaryColor
                            ),
                            border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Attach to Video", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Voice Profiles Selector
        item {
            Column {
                Text(
                    text = "SELECT VOICE PROFILE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeManager.textSecondaryColor,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VoiceProfile.values().forEach { profile ->
                        val isSelected = selectedProfile == profile
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedProfile = profile
                                    pitchSlider = profile.defaultPitch
                                    speedSlider = profile.defaultSpeed
                                }
                                .testTag("voice_${profile.id}"),
                            color = if (isSelected) ThemeManager.primaryAccent.copy(alpha = 0.15f) else ThemeManager.surfaceColor,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ThemeManager.primaryAccent else ThemeManager.surfaceBorderColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                if (isSelected) ThemeManager.primaryAccent else ThemeManager.surfaceElevatedColor,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.Black else ThemeManager.textSecondaryColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = profile.displayName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) ThemeManager.primaryAccent else ThemeManager.textPrimaryColor
                                        )
                                        Text(
                                            text = profile.description,
                                            fontSize = 11.sp,
                                            color = ThemeManager.textMutedColor
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        onSpeak(profile.sampleText, profile, profile.defaultPitch, profile.defaultSpeed)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Sample",
                                        tint = if (isSelected) ThemeManager.primaryAccent else ThemeManager.textMutedColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Script Composer Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ThemeManager.surfaceColor,
                border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VOICEOVER SCRIPT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.primaryAccent,
                            letterSpacing = 1.sp
                        )

                        // AI Write Script Button
                        Button(
                            onClick = {
                                onGenerateAIScript("cinematic") { generated ->
                                    scriptText = generated
                                }
                            },
                            enabled = !isGeneratingScript,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeManager.surfaceElevatedColor,
                                contentColor = ThemeManager.primaryAccent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            if (isGeneratingScript) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Scriptwriter", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = scriptText,
                        onValueChange = { scriptText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("script_input_field"),
                        placeholder = { Text("Write narration script or use presets below...", fontSize = 12.sp, color = ThemeManager.textMutedColor) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ThemeManager.surfaceElevatedColor,
                            unfocusedContainerColor = ThemeManager.surfaceElevatedColor,
                            focusedTextColor = ThemeManager.textPrimaryColor,
                            unfocusedTextColor = ThemeManager.textPrimaryColor,
                            focusedIndicatorColor = ThemeManager.primaryAccent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Script Pills
                    Text(
                        text = "Script Templates:",
                        fontSize = 11.sp,
                        color = ThemeManager.textMutedColor,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scriptPresets.forEach { (title, script) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ThemeManager.surfaceElevatedColor)
                                    .clickable { scriptText = script }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    color = ThemeManager.textSecondaryColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // Audio Controls (Pitch, Speed, Balance, BG Music)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ThemeManager.surfaceColor,
                border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "AUDIO FINE-TUNING & MIXING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThemeManager.secondaryAccent,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pitch Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voice Pitch:", fontSize = 12.sp, color = ThemeManager.textSecondaryColor)
                        Text(
                            String.format(Locale.US, "%.2fx", pitchSlider),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.primaryAccent
                        )
                    }
                    Slider(
                        value = pitchSlider,
                        onValueChange = { pitchSlider = it },
                        valueRange = 0.5f..1.8f,
                        colors = SliderDefaults.colors(
                            thumbColor = ThemeManager.primaryAccent,
                            activeTrackColor = ThemeManager.primaryAccent,
                            inactiveTrackColor = ThemeManager.surfaceBorderColor
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Speed Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Speech Cadence / Speed:", fontSize = 12.sp, color = ThemeManager.textSecondaryColor)
                        Text(
                            String.format(Locale.US, "%.2fx", speedSlider),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.primaryAccent
                        )
                    }
                    Slider(
                        value = speedSlider,
                        onValueChange = { speedSlider = it },
                        valueRange = 0.6f..1.8f,
                        colors = SliderDefaults.colors(
                            thumbColor = ThemeManager.primaryAccent,
                            activeTrackColor = ThemeManager.primaryAccent,
                            inactiveTrackColor = ThemeManager.surfaceBorderColor
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Volume Balance (Voice vs Music)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mix Balance (Voice vs Music):", fontSize = 12.sp, color = ThemeManager.textSecondaryColor)
                        Text(
                            "${(voiceVolumeBalance * 100).toInt()}% Voice",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.secondaryAccent
                        )
                    }
                    Slider(
                        value = voiceVolumeBalance,
                        onValueChange = { voiceVolumeBalance = it },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = ThemeManager.secondaryAccent,
                            activeTrackColor = ThemeManager.secondaryAccent,
                            inactiveTrackColor = ThemeManager.surfaceBorderColor
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Background Music selector
                    Text(
                        text = "Background Music Bed:",
                        fontSize = 12.sp,
                        color = ThemeManager.textSecondaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bgTracks.forEach { (key, label) ->
                            val isSelected = selectedBgMusic == key
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedBgMusic = key },
                                color = if (isSelected) ThemeManager.primaryAccent.copy(alpha = 0.18f) else ThemeManager.surfaceElevatedColor,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) ThemeManager.primaryAccent else ThemeManager.surfaceBorderColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = if (isSelected) ThemeManager.primaryAccent else ThemeManager.textMutedColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        color = if (isSelected) ThemeManager.primaryAccent else ThemeManager.textPrimaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
