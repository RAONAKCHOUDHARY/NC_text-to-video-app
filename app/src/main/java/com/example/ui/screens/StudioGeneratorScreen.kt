package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Crop169
import androidx.compose.material.icons.filled.CropPortrait
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.audio.VoiceTone
import com.example.data.model.VideoProject
import com.example.ui.components.ApiKeySettingsDialog
import com.example.ui.components.CinematicVideoPlayer
import com.example.ui.components.PulseCraftLogo
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.ThemeManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * CapCut-inspired Creative Tool Item for the iconic circular/squircle icon hub
 */
data class CapCutToolItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val badge: String? = null
)

@Composable
fun StudioGeneratorScreen(
    activeProject: VideoProject?,
    projects: List<VideoProject> = emptyList(),
    onSelectProject: ((VideoProject) -> Unit)? = null,
    onDeleteProject: ((VideoProject) -> Unit)? = null,
    onDuplicateProject: ((VideoProject) -> Unit)? = null,
    isGenerating: Boolean,
    generationStep: String,
    generationProgress: Float,
    isEnhancingPrompt: Boolean,
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    onClearApiKey: () -> Unit,
    selectedVoiceTone: VoiceTone,
    onSelectVoiceTone: (VoiceTone) -> Unit,
    voicePitch: Float,
    onVoicePitchChange: (Float) -> Unit,
    voiceSpeed: Float,
    onVoiceSpeedChange: (Float) -> Unit,
    onPreviewVoice: (String) -> Unit,
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
        audioMood: String,
        sourceType: String,
        sourceImageUri: String?
    ) -> Unit,
    onOpenThemeSettings: () -> Unit,
    onSwitchToEditor: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var creationMode by remember { mutableIntStateOf(0) } // 0: Text-to-Video, 1: Image-to-Video
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        selectedImageUri = uri
        if (uri != null) {
            creationMode = 1
        }
    }

    var textPrompt by remember {
        mutableStateOf("Cinematic drone flight over misty pine forests at golden hour with glowing sunbeams")
    }

    var imageMotionPrompt by remember {
        mutableStateOf("Animate realistic water flowing with dramatic sunset lighting and slow camera orbit push-in")
    }

    // Styles: Cinematic, Horror, Anime, Realistic + others
    var selectedStyle by remember { mutableStateOf("Cinematic") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") }
    var selectedCameraMotion by remember { mutableStateOf("Cinematic Orbit") }
    var selectedLighting by remember { mutableStateOf("Golden Hour") }
    var selectedAudioMood by remember { mutableStateOf("ambient_synth") }
    var selectedDuration by remember { mutableIntStateOf(10) } // 5, 10, 15, 30
    var selectedFps by remember { mutableIntStateOf(30) }
    var motionIntensity by remember { mutableFloatStateOf(0.75f) }

    val styles = listOf(
        "Cinematic", "Horror", "Anime", "Realistic",
        "3D Anime Studio", "Cyberpunk", "Sci-Fi", "Studio Ghibli"
    )

    data class RatioOption(val id: String, val label: String, val icon: ImageVector, val ratioFloat: Float)
    val ratioOptions = listOf(
        RatioOption("16:9", "16:9 Cinema", Icons.Default.Crop169, 16f / 9f),
        RatioOption("9:16", "9:16 Shorts/Reels", Icons.Default.CropPortrait, 9f / 16f),
        RatioOption("1:1", "1:1 Square", Icons.Default.CropSquare, 1f),
        RatioOption("4:5", "4:5 Social", Icons.Default.CropPortrait, 4f / 5f),
        RatioOption("21:9", "21:9 Scope", Icons.Default.Crop169, 21f / 9f)
    )

    val cameraMotions = listOf(
        "Cinematic Orbit", "Slow Pan Right", "Dynamic Zoom In",
        "Drone Flyover", "Dolly Forward", "Static Tripod"
    )

    val lightings = listOf(
        "Golden Hour", "Volumetric Fog", "Neon Noir",
        "Chiaroscuro Shadow", "Bioluminescent", "Dramatic Studio"
    )

    val durationOptions = listOf(5, 10, 15, 30)

    val textPromptPresets = listOf(
        "Spine-chilling shadow apparition lurking in abandoned Victorian hallway",
        "Cyberpunk neon street market with hovercrafts in heavy rain",
        "Makoto Shinkai style twilight sky with sparkling shooting stars",
        "Hyper-realistic 8K cinematic portrait with golden hour rim lighting",
        "Macro water droplet creating ripples on glowing bioluminescent lotus"
    )

    // CapCut Iconic 10-Tool Quick Actions Hub
    val capCutTools = listOf(
        CapCutToolItem(
            id = "ai_video",
            title = "AI Video",
            icon = Icons.Default.Movie,
            gradientColors = listOf(Color(0xFF00E5FF), Color(0xFF0072FF)),
            badge = "AI"
        ),
        CapCutToolItem(
            id = "script_ai",
            title = "Script AI",
            icon = Icons.Default.Description,
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
            badge = "PRO"
        ),
        CapCutToolItem(
            id = "photo_motion",
            title = "Photo Motion",
            icon = Icons.Default.AddPhotoAlternate,
            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
        ),
        CapCutToolItem(
            id = "deep_horror",
            title = "Deep Horror",
            icon = Icons.Default.RecordVoiceOver,
            gradientColors = listOf(Color(0xFFDC2626), Color(0xFF7F1D1D)),
            badge = "HOT"
        ),
        CapCutToolItem(
            id = "voiceover",
            title = "Voiceover",
            icon = Icons.Default.Mic,
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF047857))
        ),
        CapCutToolItem(
            id = "autocut",
            title = "AutoCut",
            icon = Icons.Default.AutoAwesome,
            gradientColors = listOf(Color(0xFFFBBF24), Color(0xFFB45309)),
            badge = "FAST"
        ),
        CapCutToolItem(
            id = "captions",
            title = "Captions",
            icon = Icons.Default.ClosedCaption,
            gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
        ),
        CapCutToolItem(
            id = "bg_cutout",
            title = "Bg Cutout",
            icon = Icons.Default.ContentCut,
            gradientColors = listOf(Color(0xFFEC4899), Color(0xFFBE185D))
        ),
        CapCutToolItem(
            id = "soundtrack",
            title = "BGM Audio",
            icon = Icons.Default.MusicNote,
            gradientColors = listOf(Color(0xFFA855F7), Color(0xFF7E22CE))
        ),
        CapCutToolItem(
            id = "timeline",
            title = "Timeline",
            icon = Icons.Default.ViewTimeline,
            gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0E7490))
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E13))
            .testTag("generator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. CAPCUT-STYLE TOP HEADER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("top_action_bar"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Logo & Title with PRO Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulseCraftLogo(size = 38.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PulseCraft",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Glowing PRO badge like CapCut
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "PRO",
                                    color = Color(0xFF00E5FF),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "CapCut-Style AI Video & Audio Studio",
                            color = Color(0xFF8E95A5),
                            fontSize = 11.sp
                        )
                    }
                }

                // Top Icons: Timeline Editor, Theme, API Key
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (onSwitchToEditor != null) {
                        IconButton(
                            onClick = onSwitchToEditor,
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFF1B1D26), CircleShape)
                                .testTag("switch_to_editor_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Open Editor",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenThemeSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF1B1D26), CircleShape)
                            .testTag("theme_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme Engine",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    val hasCustomKey = currentApiKey.isNotBlank()
                    IconButton(
                        onClick = { showApiKeyDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (hasCustomKey) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF1B1D26),
                                CircleShape
                            )
                            .testTag("settings_gear_button")
                    ) {
                        Icon(
                            imageVector = if (hasCustomKey) Icons.Default.Key else Icons.Default.Settings,
                            contentDescription = "Gemini API Key",
                            tint = if (hasCustomKey) Color(0xFF00E5FF) else Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }

        // 2. CAPCUT SIGNATURE "NEW PROJECT" HERO CARD
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        creationMode = 0
                    }
                    .testTag("capcut_new_project_card"),
                color = Color(0xFF161822),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF00E5FF).copy(alpha = 0.6f),
                            Color(0xFF0072FF).copy(alpha = 0.3f)
                        )
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big '+' Plus icon with electric gradient
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF00F2FE), Color(0xFF4FACFE))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Project",
                            tint = Color.Black,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "New Project",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF00E5FF).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "AI STUDIO",
                                    color = Color(0xFF00E5FF),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Text & Photo to AI Video • Auto Voice • 4K",
                            color = Color(0xFF8E95A5),
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 3. CAPCUT ICON-BASED QUICK TOOLS HUB ("ICON WALA INTERFACE")
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capcut_icon_tools_section")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CAPCUT CREATIVE TOOLS",
                        color = Color(0xFF8E95A5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "10 Tools Available",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2 Rows of 5 vibrant tools (10 in total)
                val row1 = capCutTools.take(5)
                val row2 = capCutTools.drop(5)

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row1.forEach { tool ->
                            CapCutToolIconButton(
                                tool = tool,
                                onClick = {
                                    when (tool.id) {
                                        "ai_video" -> {
                                            creationMode = 0
                                        }
                                        "script_ai" -> {
                                            val currentP = if (creationMode == 0) textPrompt else imageMotionPrompt
                                            onEnhancePrompt(currentP, selectedStyle, selectedCameraMotion, selectedLighting) { enhanced ->
                                                if (creationMode == 0) textPrompt = enhanced else imageMotionPrompt = enhanced
                                            }
                                        }
                                        "photo_motion" -> {
                                            creationMode = 1
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        "deep_horror" -> {
                                            selectedStyle = "Horror"
                                            onSelectVoiceTone(VoiceTone.DEEP_HORROR)
                                            onVoicePitchChange(0.55f)
                                            onVoiceSpeedChange(0.85f)
                                            textPrompt = "Spine-chilling shadow apparition lurking in abandoned Victorian hallway at midnight with glowing red eyes"
                                        }
                                        "voiceover" -> {
                                            val script = if (creationMode == 0) textPrompt else imageMotionPrompt
                                            onPreviewVoice(script)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row2.forEach { tool ->
                            CapCutToolIconButton(
                                tool = tool,
                                onClick = {
                                    when (tool.id) {
                                        "autocut" -> {
                                            selectedDuration = 10
                                            selectedAspectRatio = "9:16"
                                            selectedStyle = "Cinematic"
                                            selectedLighting = "Golden Hour"
                                        }
                                        "captions" -> {
                                            textPrompt = "$textPrompt [Subtitle: Cinematic Suspense Flow]"
                                        }
                                        "bg_cutout" -> {
                                            selectedStyle = "Realistic"
                                            selectedLighting = "Dramatic Studio"
                                            textPrompt = "Chroma key transparent cutout silhouette of subject with high contrast neon rim lighting"
                                        }
                                        "soundtrack" -> {
                                            selectedAudioMood = if (selectedAudioMood == "ambient_synth") "action_beats" else "ambient_synth"
                                        }
                                        "timeline" -> {
                                            onSwitchToEditor?.invoke()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // 4. CAPCUT-STYLE QUICK FORMAT & STYLE RIBBON
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "QUICK FORMAT & STYLE RIBBON",
                    color = Color(0xFF8E95A5),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Aspect Ratio pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF161822),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                        modifier = Modifier.clickable {
                            // Cycle ratio
                            selectedAspectRatio = when (selectedAspectRatio) {
                                "16:9" -> "9:16"
                                "9:16" -> "1:1"
                                "1:1" -> "4:5"
                                "4:5" -> "21:9"
                                else -> "16:9"
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Crop169, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Ratio: $selectedAspectRatio", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Style pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF161822),
                        border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f)),
                        modifier = Modifier.clickable {
                            val nextIdx = (styles.indexOf(selectedStyle) + 1) % styles.size
                            selectedStyle = styles[nextIdx]
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Style: $selectedStyle", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Camera pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF161822),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                        modifier = Modifier.clickable {
                            val nextIdx = (cameraMotions.indexOf(selectedCameraMotion) + 1) % cameraMotions.size
                            selectedCameraMotion = cameraMotions[nextIdx]
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = selectedCameraMotion, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Duration pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF161822),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        modifier = Modifier.clickable {
                            val nextIdx = (durationOptions.indexOf(selectedDuration) + 1) % durationOptions.size
                            selectedDuration = durationOptions[nextIdx]
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "${selectedDuration}s @ ${selectedFps}fps", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. CREATION MODE: Text-to-Video vs Image-to-Video
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF161822),
                border = BorderStroke(1.dp, Color(0xFF222533))
            ) {
                TabRow(
                    selectedTabIndex = creationMode,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF00E5FF),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[creationMode]),
                            color = Color(0xFF00E5FF)
                        )
                    }
                ) {
                    Tab(
                        selected = creationMode == 0,
                        onClick = { creationMode = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Text to Video", fontWeight = if (creationMode == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = creationMode == 1,
                        onClick = { creationMode = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Photo to Video", fontWeight = if (creationMode == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }
            }
        }

        // Image Picker Card (When in Photo-to-Video mode)
        if (creationMode == 1) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF161822),
                    border = BorderStroke(1.dp, Color(0xFF222533))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SOURCE IMAGE",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Selected Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Black.copy(alpha = 0.75f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(10.dp)
                                        .height(34.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Change Photo", fontSize = 11.sp)
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("upload_photo_card"),
                                color = Color(0xFF1B1D26),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap to Pick Photo from Device",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Animate any portrait, landscape, or horror artwork",
                                        fontSize = 11.sp,
                                        color = Color(0xFF8E95A5)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. PROMPT COMPOSER WITH AI ENHANCE BUTTON
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prompt_composer_card"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF161822),
                border = BorderStroke(1.dp, Color(0xFF222533))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (creationMode == 0) "AI PROMPT COMPOSER" else "MOTION COMPOSER",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // AI Prompt Enhancement Button
                        Button(
                            onClick = {
                                val currentP = if (creationMode == 0) textPrompt else imageMotionPrompt
                                onEnhancePrompt(
                                    currentP,
                                    selectedStyle,
                                    selectedCameraMotion,
                                    selectedLighting
                                ) { enhanced ->
                                    if (creationMode == 0) textPrompt = enhanced else imageMotionPrompt = enhanced
                                }
                            },
                            enabled = !isEnhancingPrompt,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B1D26),
                                contentColor = CinemaAmber
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("enhance_prompt_button")
                        ) {
                            if (isEnhancingPrompt) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = CinemaAmber,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enhancing...", fontSize = 11.sp, color = CinemaAmber)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CinemaAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI Enhance",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CinemaAmber
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = if (creationMode == 0) textPrompt else imageMotionPrompt,
                        onValueChange = {
                            if (creationMode == 0) textPrompt = it else imageMotionPrompt = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(105.dp)
                            .testTag("prompt_input_field"),
                        placeholder = {
                            Text(
                                if (creationMode == 0) "Describe your scene (cinematic, camera motion, characters, lighting)..."
                                else "Describe motion dynamics (e.g. realistic water flow, slow orbit, creepy mist)...",
                                color = Color(0xFF6B7280),
                                fontSize = 13.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1B1D26),
                            unfocusedContainerColor = Color(0xFF1B1D26),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color(0xFF00E5FF),
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Inspirations
                    Text(
                        text = "Quick Inspirations:",
                        color = Color(0xFF8E95A5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        textPromptPresets.forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1B1D26))
                                    .clickable {
                                        if (creationMode == 0) textPrompt = preset else imageMotionPrompt = preset
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = preset.take(34) + "...",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. ASPECT RATIO SELECTOR
        item {
            Column(modifier = Modifier.testTag("aspect_ratio_section")) {
                Text(
                    text = "ASPECT RATIO",
                    color = Color(0xFF8E95A5),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ratioOptions.forEach { option ->
                        val isSelected = selectedAspectRatio == option.id
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedAspectRatio = option.id }
                                .testTag("ratio_chip_${option.id.replace(":", "_")}"),
                            color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF161822),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) Color(0xFF00E5FF) else Color(0xFF222533)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.label,
                                    tint = if (isSelected) Color(0xFF00E5FF) else Color(0xFF8E95A5),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = option.id,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = option.label.split(" ").last(),
                                    color = Color(0xFF6B7280),
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 9. VISUAL STYLE SELECTOR
        item {
            Column(modifier = Modifier.testTag("visual_style_section")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VISUAL STYLE",
                        color = Color(0xFF8E95A5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = selectedStyle.uppercase(),
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styles.forEach { style ->
                        val isSelected = selectedStyle == style
                        val isHorror = style == "Horror"
                        val isRealistic = style == "Realistic"
                        val isCinematic = style == "Cinematic"

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) {
                                        when {
                                            isHorror -> Color(0xFFDC2626).copy(alpha = 0.25f)
                                            isRealistic -> Color(0xFF0284C7).copy(alpha = 0.25f)
                                            else -> Color(0xFF00E5FF).copy(alpha = 0.22f)
                                        }
                                    } else {
                                        Color(0xFF161822)
                                    }
                                )
                                .border(
                                    1.5.dp,
                                    if (isSelected) {
                                        when {
                                            isHorror -> Color(0xFFDC2626)
                                            isRealistic -> Color(0xFF0284C7)
                                            else -> Color(0xFF00E5FF)
                                        }
                                    } else {
                                        Color(0xFF222533)
                                    },
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedStyle = style }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                                .testTag("style_chip_$style")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isHorror) {
                                    Text("🩸 ", fontSize = 11.sp)
                                } else if (isCinematic) {
                                    Text("🎬 ", fontSize = 11.sp)
                                } else if (isRealistic) {
                                    Text("📸 ", fontSize = 11.sp)
                                }
                                Text(
                                    text = style,
                                    color = if (isSelected) {
                                        when {
                                            isHorror -> Color(0xFFEF4444)
                                            isRealistic -> Color(0xFF38BDF8)
                                            else -> Color(0xFF00E5FF)
                                        }
                                    } else {
                                        Color.White
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // 10. REALISTIC HUMAN-LIKE & DEEP HORROR VOICE ENGINE
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_controls_card"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF161822),
                border = BorderStroke(1.dp, Color(0xFF222533))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.18f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REALISTIC VOICE ENGINE",
                                color = Color(0xFF00E5FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val script = if (creationMode == 0) textPrompt else imageMotionPrompt
                                onPreviewVoice(script)
                            },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("preview_voice_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Test Voice",
                                color = Color(0xFF00E5FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "VOICE TONE SELECTOR",
                        color = Color(0xFF8E95A5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        VoiceTone.entries.forEach { tone ->
                            val isSelected = selectedVoiceTone == tone
                            val isDeepHorror = tone == VoiceTone.DEEP_HORROR
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectVoiceTone(tone) }
                                    .testTag("voice_tone_${tone.id}"),
                                color = if (isSelected) {
                                    if (isDeepHorror) Color(0xFF450A0A) else Color(0xFF1B1D26)
                                } else {
                                    Color(0xFF1B1D26).copy(alpha = 0.6f)
                                },
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isSelected) {
                                        if (isDeepHorror) Color(0xFFEF4444) else Color(0xFF00E5FF)
                                    } else {
                                        Color(0xFF222533)
                                    }
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                if (isSelected) {
                                                    if (isDeepHorror) Color(0xFFEF4444) else Color(0xFF00E5FF)
                                                } else {
                                                    Color.Transparent
                                                },
                                                CircleShape
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) Color.Transparent else Color(0xFF6B7280),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tone.displayName,
                                            color = if (isSelected) {
                                                if (isDeepHorror) Color(0xFFFCA5A5) else Color(0xFF00E5FF)
                                            } else {
                                                Color.White
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = tone.description,
                                            color = Color(0xFF8E95A5),
                                            fontSize = 10.sp,
                                            lineHeight = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pitch Slider with Deep Horror indication
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PITCH (BASS RESONANCE)",
                            color = Color(0xFF8E95A5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = String.format(Locale.US, "%.2fx %s", voicePitch, if (voicePitch <= 0.7f) "• Extra Deep Bhari Aawaz" else ""),
                            color = if (voicePitch <= 0.7f) Color(0xFFEF4444) else Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = voicePitch,
                        onValueChange = onVoicePitchChange,
                        valueRange = 0.40f..1.40f,
                        steps = 20,
                        colors = SliderDefaults.colors(
                            thumbColor = if (voicePitch <= 0.7f) Color(0xFFEF4444) else Color(0xFF00E5FF),
                            activeTrackColor = if (voicePitch <= 0.7f) Color(0xFFEF4444) else Color(0xFF00E5FF),
                            inactiveTrackColor = Color(0xFF222533)
                        ),
                        modifier = Modifier.testTag("pitch_slider")
                    )
                    Text(
                        text = "Tip: Lower to 0.5x - 0.7x for extra deep, heavy, and scary bhari aawaz.",
                        color = Color(0xFF6B7280),
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Speed Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SPEECH SPEED (SUSPENSE PACING)",
                            color = Color(0xFF8E95A5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = String.format(Locale.US, "%.2fx %s", voiceSpeed, if (voiceSpeed <= 0.9f) "• Suspenseful Pace" else ""),
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = voiceSpeed,
                        onValueChange = onVoiceSpeedChange,
                        valueRange = 0.60f..1.30f,
                        steps = 14,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF00E5FF),
                            inactiveTrackColor = Color(0xFF222533)
                        ),
                        modifier = Modifier.testTag("speed_slider")
                    )
                }
            }
        }

        // 11. DURATION & CAMERA DYNAMICS
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLIP DURATION & SPEED",
                        color = Color(0xFF8E95A5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${selectedDuration}s @ ${selectedFps}fps",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durationOptions.forEach { dur ->
                        val isSelected = selectedDuration == dur
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedDuration = dur }
                                .testTag("duration_$dur"),
                            color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF161822),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF00E5FF) else Color(0xFF222533)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${dur}s",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 12. LIVE PROGRESS VIEW (WHEN GENERATING)
        if (isGenerating) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generation_progress_card"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF161822),
                    border = BorderStroke(1.5.dp, Color(0xFF00E5FF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF00E5FF),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (creationMode == 0) "Rendering AI Video..." else "Animating Photo to Video...",
                                color = Color(0xFF00E5FF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { generationProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF00E5FF),
                            trackColor = Color(0xFF222533)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = generationStep,
                            color = Color(0xFF8E95A5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 13. PROMINENT CAPCUT-STYLE "GENERATE VIDEO" ACTION BUTTON
        item {
            val isEnabled = !isGenerating && (
                    (creationMode == 0 && textPrompt.isNotBlank()) ||
                            (creationMode == 1 && selectedImageUri != null && imageMotionPrompt.isNotBlank())
                    )

            val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 0.95f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow_alpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Background subtle glow
                if (isEnabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF00E5FF).copy(alpha = glowAlpha * 0.45f),
                                        Color(0xFF0072FF).copy(alpha = glowAlpha * 0.45f)
                                    )
                                )
                            )
                    )
                }

                Button(
                    onClick = {
                        val promptToUse = if (creationMode == 0) textPrompt else imageMotionPrompt
                        onGenerateVideo(
                            promptToUse,
                            selectedStyle,
                            selectedAspectRatio,
                            selectedCameraMotion,
                            selectedLighting,
                            selectedDuration,
                            selectedFps,
                            motionIntensity,
                            selectedAudioMood,
                            if (creationMode == 0) "text" else "image",
                            selectedImageUri?.toString()
                        )
                    },
                    enabled = isEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("generate_video_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFF1B1D26),
                        disabledContentColor = Color(0xFF6B7280)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (creationMode == 0) Icons.Default.Movie else Icons.Default.Videocam,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isGenerating)
                                "RENDERING CINEMATIC VIDEO..."
                            else if (creationMode == 0)
                                "GENERATE VIDEO (${selectedDuration}s • $selectedStyle)"
                            else
                                "ANIMATE PHOTO TO VIDEO (${selectedDuration}s)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // 14. CAPCUT "DRAFTS / RECENT PROJECTS" SECTION
        if (projects.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().testTag("capcut_drafts_section")) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Drafts",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1B1D26)
                            ) {
                                Text(
                                    text = "${projects.size}",
                                    color = Color(0xFF8E95A5),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Recent Projects",
                            color = Color(0xFF00E5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(projects, key = { it.id }) { proj ->
                            CapCutDraftCard(
                                project = proj,
                                isSelected = activeProject?.id == proj.id,
                                onSelect = { onSelectProject?.invoke(proj) },
                                onDelete = { onDeleteProject?.invoke(proj) },
                                onDuplicate = { onDuplicateProject?.invoke(proj) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showApiKeyDialog) {
        ApiKeySettingsDialog(
            currentApiKey = currentApiKey,
            onSaveKey = { key ->
                onSaveApiKey(key)
                showApiKeyDialog = false
            },
            onClearKey = onClearApiKey,
            onDismiss = { showApiKeyDialog = false }
        )
    }
}

/**
 * CapCut-style vibrant squircle icon tool button with custom gradient and title
 */
@Composable
private fun CapCutToolIconButton(
    tool: CapCutToolItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(62.dp)
            .clickable { onClick() }
            .testTag("capcut_tool_${tool.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(tool.gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.title,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )

            // Optional Top-Right Badge (AI, PRO, HOT, FAST)
            if (tool.badge != null) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                ) {
                    Text(
                        text = tool.badge,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = tool.title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * CapCut-style Draft Project Card
 */
@Composable
private fun CapCutDraftCard(
    project: VideoProject,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .width(150.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("draft_card_${project.id}"),
        color = Color(0xFF161822),
        border = BorderStroke(
            1.2.dp,
            if (isSelected) Color(0xFF00E5FF) else Color(0xFF222533)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column {
            // Thumbnail container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(Color(0xFF090A0E)),
                contentAlignment = Alignment.Center
            ) {
                // Background artistic representation
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF00E5FF).copy(alpha = 0.15f),
                                Color(0xFF8B5CF6).copy(alpha = 0.25f)
                            )
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                )

                // Duration tag in bottom-right corner
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "00:%02d", project.durationSeconds),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }

                // Ratio tag in top-left
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.3f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = project.aspectRatio,
                        color = Color(0xFF00E5FF),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            // Info row
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color(0xFF8E95A5),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Open in Player") },
                                onClick = {
                                    showMenu = false
                                    onSelect()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                onClick = {
                                    showMenu = false
                                    onDuplicate()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = Color(0xFFEF4444)) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }

                val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
                Text(
                    text = dateFormat.format(Date(project.createdAt)),
                    color = Color(0xFF6B7280),
                    fontSize = 10.sp
                )
            }
        }
    }
}

