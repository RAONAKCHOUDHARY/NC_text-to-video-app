package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGradientEnd
import com.example.ui.theme.AccentGradientStart
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceBorder
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioGeneratorScreen(
    isGenerating: Boolean,
    generationStep: String,
    generationProgress: Float,
    isEnhancingPrompt: Boolean,
    onEnhancePrompt: (String, String, String, String, (String) -> Unit) -> Unit,
    onGenerateVideo: (
        prompt: String,
        style: String,
        aspectRatio: String,
        cameraMotion: String,
        lighting: String,
        duration: Int,
        fps: Int,
        motionIntensity: Float,
        audioMood: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var prompt by remember {
        mutableStateOf("Cinematic drone flight over misty pine forests at golden hour with glowing sunbeams")
    }

    var selectedStyle by remember { mutableStateOf("Cinematic Film") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") }
    var selectedCameraMotion by remember { mutableStateOf("Drone Flyover") }
    var selectedLighting by remember { mutableStateOf("Golden Hour") }
    var selectedAudioMood by remember { mutableStateOf("ambient_synth") }
    var selectedDuration by remember { mutableIntStateOf(8) }
    var selectedFps by remember { mutableIntStateOf(30) }
    var motionIntensity by remember { mutableFloatStateOf(0.75f) }
    var showAdvancedSettings by remember { mutableStateOf(false) }

    val styles = listOf(
        "Cinematic Film", "Cyberpunk", "Anime", "Studio Ghibli",
        "Sci-Fi", "Nature Documentary", "Film Noir"
    )

    val aspectRatios = listOf(
        Pair("16:9", "Cinema"),
        Pair("9:16", "Shorts/Reels"),
        Pair("1:1", "Square"),
        Pair("4:3", "Classic")
    )

    val cameraMotions = listOf(
        "Drone Flyover", "Cinematic Orbit", "Dynamic Zoom In",
        "Slow Pan Right", "Dolly Forward", "Static Tripod"
    )

    val lightings = listOf(
        "Golden Hour", "Neon Noir", "Bioluminescent",
        "Volumetric Fog", "Dramatic Studio", "Pastel Sunrise"
    )

    val audioMoods = listOf(
        Pair("ambient_synth", "Warm Synth Pad"),
        Pair("cinematic_drone", "Sub-Bass Drone"),
        Pair("cyber_pulse", "Cyberpunk Arp"),
        Pair("nature_wind", "Ambient Wind"),
        Pair("none", "Muted")
    )

    val promptPresets = listOf(
        "Cyberpunk neon street market with flying cars in heavy rain",
        "Floating bioluminescent jellyfish in deep sapphire oceanic abyss",
        "Cosmic supernova birthing gold spiral nebula in deep space",
        "Samurai duel under falling cherry blossoms in misty bamboo grove",
        "Macro water droplet landing on radiant crystal flower petals"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBackground)
            .testTag("generator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Brush.linearGradient(listOf(CinemaPurple, CinemaCyan)),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = CinemaBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Video Studio",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Describe your scene & render in 4K",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Prompt Input Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CinemaSurface,
                border = BorderStroke(1.dp, CinemaSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROMPT",
                            color = CinemaCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // AI Enhance Button
                        Button(
                            onClick = {
                                onEnhancePrompt(
                                    prompt,
                                    selectedStyle,
                                    selectedCameraMotion,
                                    selectedLighting
                                ) { enhanced ->
                                    prompt = enhanced
                                }
                            },
                            enabled = !isEnhancingPrompt && prompt.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CinemaSurfaceElevated,
                                contentColor = CinemaCyan
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("enhance_prompt_button")
                        ) {
                            if (isEnhancingPrompt) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = CinemaCyan,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enhancing...", fontSize = 11.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CinemaAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Enhance", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("prompt_input_field"),
                        placeholder = {
                            Text(
                                "Describe what you want to see in the video (subject, environment, mood, lighting)...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CinemaSurfaceElevated,
                            unfocusedContainerColor = CinemaSurfaceElevated,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = CinemaCyan,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset prompt chips
                    Text(
                        text = "Inspirations:",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        promptPresets.forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CinemaSurfaceElevated)
                                    .clickable { prompt = preset }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = preset.take(32) + "...",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Aspect Ratio Selector
        item {
            Column {
                Text(
                    text = "ASPECT RATIO",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    aspectRatios.forEach { (ratio, label) ->
                        val isSelected = selectedAspectRatio == ratio
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedAspectRatio = ratio }
                                .testTag("ratio_button_$ratio"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CinemaPurple.copy(alpha = 0.25f) else CinemaSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) CinemaCyan else CinemaSurfaceBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = ratio,
                                    color = if (isSelected) CinemaCyan else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = label,
                                    color = if (isSelected) TextPrimary else TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cinematic Style Selector
        item {
            Column {
                Text(
                    text = "VISUAL STYLE",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styles.forEach { style ->
                        val isSelected = selectedStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) CinemaCyan.copy(alpha = 0.2f) else CinemaSurface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) CinemaCyan else CinemaSurfaceBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedStyle = style }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("style_chip_$style")
                        ) {
                            Text(
                                text = style,
                                color = if (isSelected) CinemaCyan else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Camera Motion Selector
        item {
            Column {
                Text(
                    text = "CAMERA MOVEMENT",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cameraMotions.forEach { motion ->
                        val isSelected = selectedCameraMotion == motion
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) CinemaPurple.copy(alpha = 0.25f) else CinemaSurface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) CinemaPurple else CinemaSurfaceBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCameraMotion = motion }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = motion,
                                color = if (isSelected) CinemaPurple else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Lighting & Atmosphere
        item {
            Column {
                Text(
                    text = "LIGHTING & MOOD",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    lightings.forEach { light ->
                        val isSelected = selectedLighting == light
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) CinemaAmber.copy(alpha = 0.2f) else CinemaSurface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) CinemaAmber else CinemaSurfaceBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedLighting = light }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = light,
                                color = if (isSelected) CinemaAmber else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Audio Soundscape Selector
        item {
            Column {
                Text(
                    text = "AMBIENT SOUNDTRACK",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    audioMoods.forEach { (moodKey, moodLabel) ->
                        val isSelected = selectedAudioMood == moodKey
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedAudioMood = moodKey },
                            color = if (isSelected) CinemaCyan.copy(alpha = 0.2f) else CinemaSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) CinemaCyan else CinemaSurfaceBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = if (isSelected) CinemaCyan else TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = moodLabel,
                                    color = if (isSelected) CinemaCyan else TextPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Advanced Settings Collapsible Accordion
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CinemaSurface,
                border = BorderStroke(1.dp, CinemaSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAdvancedSettings = !showAdvancedSettings },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = CinemaCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Camera & Render Settings",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = if (showAdvancedSettings) "Hide" else "Show",
                            color = CinemaCyan,
                            fontSize = 12.sp
                        )
                    }

                    AnimatedVisibility(visible = showAdvancedSettings) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            // Duration
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Duration:", color = TextSecondary, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(4, 6, 8, 10).forEach { dur ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (selectedDuration == dur) CinemaCyan else CinemaSurfaceElevated
                                                )
                                                .clickable { selectedDuration = dur }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${dur}s",
                                                color = if (selectedDuration == dur) CinemaBackground else TextPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Framerate FPS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Frame Rate:", color = TextSecondary, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(24, 30, 60).forEach { fps ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (selectedFps == fps) CinemaPurple else CinemaSurfaceElevated
                                                )
                                                .clickable { selectedFps = fps }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "$fps fps",
                                                color = if (selectedFps == fps) TextPrimary else TextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Motion Intensity Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Motion Dynamics:", color = TextSecondary, fontSize = 12.sp)
                                    Text(
                                        "${(motionIntensity * 100).toInt()}%",
                                        color = CinemaCyan,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Slider(
                                    value = motionIntensity,
                                    onValueChange = { motionIntensity = it },
                                    valueRange = 0.2f..1.0f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = CinemaCyan,
                                        activeTrackColor = CinemaCyan,
                                        inactiveTrackColor = CinemaSurfaceBorder
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Generation Progress Indicator
        if (isGenerating) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CinemaSurfaceElevated,
                    border = BorderStroke(1.dp, CinemaCyan)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = CinemaCyan,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Generating Video...",
                                color = CinemaCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { generationProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CinemaCyan,
                            trackColor = CinemaSurfaceBorder
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = generationStep,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Primary Action Button: GENERATE VIDEO
        item {
            Button(
                onClick = {
                    onGenerateVideo(
                        prompt,
                        selectedStyle,
                        selectedAspectRatio,
                        selectedCameraMotion,
                        selectedLighting,
                        selectedDuration,
                        selectedFps,
                        motionIntensity,
                        selectedAudioMood
                    )
                },
                enabled = !isGenerating && prompt.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_video_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = CinemaSurfaceElevated
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(AccentGradientStart, AccentGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = CinemaBackground,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isGenerating) "RENDERING VIDEO..." else "GENERATE VIDEO",
                            color = CinemaBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
