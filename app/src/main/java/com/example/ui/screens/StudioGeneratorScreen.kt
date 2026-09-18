package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.ThemeManager

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

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    var textPrompt by remember {
        mutableStateOf("Cinematic drone flight over misty pine forests at golden hour with glowing sunbeams")
    }

    var imageMotionPrompt by remember {
        mutableStateOf("Animate realistic water flowing with dramatic sunset lighting and slow camera orbit push-in")
    }

    var selectedStyle by remember { mutableStateOf("Cinematic Film") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") }
    var selectedCameraMotion by remember { mutableStateOf("Cinematic Orbit") }
    var selectedLighting by remember { mutableStateOf("Golden Hour") }
    var selectedAudioMood by remember { mutableStateOf("ambient_synth") }
    var selectedDuration by remember { mutableIntStateOf(10) } // 5, 10, 15, 30, 60
    var selectedFps by remember { mutableIntStateOf(30) }
    var motionIntensity by remember { mutableFloatStateOf(0.75f) }
    var showAdvancedSettings by remember { mutableStateOf(false) }

    val styles = listOf(
        "Cinematic Film", "3D Anime Studio", "Indian Cartoon 3D Style",
        "Cyberpunk", "Anime", "Studio Ghibli",
        "Sci-Fi", "Nature Documentary", "Film Noir"
    )

    val aspectRatios = listOf(
        Pair("16:9", "YouTube/Cinema"),
        Pair("9:16", "Shorts/Reels"),
        Pair("1:1", "Square"),
        Pair("4:5", "Social"),
        Pair("21:9", "Scope")
    )

    val cameraMotions = listOf(
        "Cinematic Orbit", "Slow Pan Right", "Dynamic Zoom In",
        "Drone Flyover", "Dolly Forward", "Static Tripod"
    )

    val lightings = listOf(
        "Golden Hour", "Neon Noir", "Bioluminescent",
        "Volumetric Fog", "Dramatic Studio", "Pastel Sunrise"
    )

    val durationOptions = listOf(5, 10, 15, 30, 60)

    val imageMotionPresets = listOf(
        "3D Parallax Depth & Camera Push-In",
        "Gentle Floating Dust & Ambient Light Ray Sweep",
        "Liquid Water Wave Dynamics & Wind Sway",
        "Bioluminescent Pulsing Glow & Particle Burst",
        "Cinematic Slow Motion Drift"
    )

    val textPromptPresets = listOf(
        "Cyberpunk neon street market with flying cars in heavy rain",
        "Floating bioluminescent jellyfish in deep sapphire oceanic abyss",
        "Cosmic supernova birthing gold spiral nebula in deep space",
        "Samurai duel under falling cherry blossoms in misty bamboo grove",
        "Macro water droplet landing on radiant crystal flower petals"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeManager.backgroundColor)
            .testTag("generator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Theme Settings Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                ThemeManager.primaryAccent.copy(alpha = 0.18f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = ThemeManager.primaryAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Video Studio",
                            color = ThemeManager.textPrimaryColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Text-to-Video & Image-to-Video Engine",
                            color = ThemeManager.textSecondaryColor,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onSwitchToEditor != null) {
                        IconButton(
                            onClick = onSwitchToEditor,
                            modifier = Modifier
                                .size(36.dp)
                                .background(ThemeManager.primaryAccent.copy(alpha = 0.15f), CircleShape)
                                .testTag("switch_to_editor_header_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Open Creative Editor",
                                tint = ThemeManager.primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = onOpenThemeSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .background(ThemeManager.surfaceElevatedColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme Settings",
                            tint = ThemeManager.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Mode Switcher: Text-to-Video vs Image-to-Video
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = ThemeManager.surfaceColor,
                border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
            ) {
                TabRow(
                    selectedTabIndex = creationMode,
                    containerColor = Color.Transparent,
                    contentColor = ThemeManager.primaryAccent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[creationMode]),
                            color = ThemeManager.primaryAccent
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
                                Text("Image to Video", fontWeight = if (creationMode == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }
            }
        }

        // Image Picker Card (Only in Image-to-Video mode)
        if (creationMode == 1) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ThemeManager.surfaceColor,
                    border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SOURCE IMAGE",
                            color = ThemeManager.primaryAccent,
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
                                color = ThemeManager.surfaceElevatedColor,
                                border = BorderStroke(1.dp, ThemeManager.primaryAccent.copy(alpha = 0.5f)),
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
                                        tint = ThemeManager.primaryAccent,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap to Pick Photo from Device",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ThemeManager.textPrimaryColor
                                    )
                                    Text(
                                        text = "Animate any portrait, landscape, or artwork",
                                        fontSize = 11.sp,
                                        color = ThemeManager.textMutedColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Prompt Input Card (Text Prompt or Motion Prompt)
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
                            text = if (creationMode == 0) "SCENE PROMPT" else "MOTION ANIMATION PROMPT",
                            color = ThemeManager.primaryAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // AI Enhance Button
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
                                containerColor = ThemeManager.surfaceElevatedColor,
                                contentColor = ThemeManager.primaryAccent
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
                                    color = ThemeManager.primaryAccent,
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
                        value = if (creationMode == 0) textPrompt else imageMotionPrompt,
                        onValueChange = {
                            if (creationMode == 0) textPrompt = it else imageMotionPrompt = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("prompt_input_field"),
                        placeholder = {
                            Text(
                                if (creationMode == 0) "Describe your scene (subject, setting, mood, action)..."
                                else "Describe motion dynamics (e.g. animate camera orbit, water flowing, wind in hair)...",
                                color = ThemeManager.textMutedColor,
                                fontSize = 13.sp
                            )
                        },
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Inspirations / Motion Presets
                    Text(
                        text = if (creationMode == 0) "Prompt Inspirations:" else "Motion Dynamics Presets:",
                        color = ThemeManager.textMutedColor,
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
                        val presets = if (creationMode == 0) textPromptPresets else imageMotionPresets
                        presets.forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ThemeManager.surfaceElevatedColor)
                                    .clickable {
                                        if (creationMode == 0) textPrompt = preset else imageMotionPrompt = preset
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = preset.take(34) + "...",
                                    color = ThemeManager.textSecondaryColor,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Duration Selector (5s, 10s, 15s, 30s, 60s)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLIP DURATION",
                        color = ThemeManager.textSecondaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${selectedDuration} Seconds",
                        color = ThemeManager.primaryAccent,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    durationOptions.forEach { dur ->
                        val isSelected = selectedDuration == dur
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedDuration = dur }
                                .testTag("duration_$dur"),
                            color = if (isSelected) ThemeManager.primaryAccent.copy(alpha = 0.2f) else ThemeManager.surfaceColor,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ThemeManager.primaryAccent else ThemeManager.surfaceBorderColor
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
                                    color = if (isSelected) ThemeManager.primaryAccent else ThemeManager.textPrimaryColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // Camera Motion Selector (Orbit, Slow Pan, Zoom, Drone, Dolly, etc.)
        item {
            Column {
                Text(
                    text = "CAMERA CONTROLS",
                    color = ThemeManager.textSecondaryColor,
                    fontSize = 11.sp,
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
                                    if (isSelected) CinemaPurple.copy(alpha = 0.25f) else ThemeManager.surfaceColor
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) CinemaPurple else ThemeManager.surfaceBorderColor,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCameraMotion = motion }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("camera_$motion")
                        ) {
                            Text(
                                text = motion,
                                color = if (isSelected) CinemaPurple else ThemeManager.textPrimaryColor,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
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
                    color = ThemeManager.textSecondaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    aspectRatios.forEach { (ratio, label) ->
                        val isSelected = selectedAspectRatio == ratio
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedAspectRatio = ratio }
                                .testTag("ratio_button_$ratio"),
                            color = if (isSelected) ThemeManager.primaryAccent.copy(alpha = 0.2f) else ThemeManager.surfaceColor,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ThemeManager.primaryAccent else ThemeManager.surfaceBorderColor
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = ratio,
                                    color = if (isSelected) ThemeManager.primaryAccent else ThemeManager.textPrimaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = label.split("/").first(),
                                    color = ThemeManager.textMutedColor,
                                    fontSize = 9.sp,
                                    maxLines = 1
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
                    color = ThemeManager.textSecondaryColor,
                    fontSize = 11.sp,
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
                                    if (isSelected) ThemeManager.primaryAccent.copy(alpha = 0.2f) else ThemeManager.surfaceColor
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) ThemeManager.primaryAccent else ThemeManager.surfaceBorderColor,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedStyle = style }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = style,
                                color = if (isSelected) ThemeManager.primaryAccent else ThemeManager.textPrimaryColor,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Live Generation Progress View
        if (isGenerating) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ThemeManager.surfaceElevatedColor,
                    border = BorderStroke(1.dp, ThemeManager.primaryAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = ThemeManager.primaryAccent,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (creationMode == 0) "Synthesizing AI Video..." else "Animate Image to Video...",
                                color = ThemeManager.primaryAccent,
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
                            color = ThemeManager.primaryAccent,
                            trackColor = ThemeManager.surfaceBorderColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = generationStep,
                            color = ThemeManager.textSecondaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Primary Action: GENERATE VIDEO BUTTON
        item {
            val isEnabled = !isGenerating && (
                    (creationMode == 0 && textPrompt.isNotBlank()) ||
                            (creationMode == 1 && selectedImageUri != null && imageMotionPrompt.isNotBlank())
                    )

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
                    .height(54.dp)
                    .testTag("generate_video_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeManager.primaryAccent,
                    contentColor = Color.Black
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (creationMode == 0) Icons.Default.Movie else Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGenerating) "RENDERING VIDEO..." else if (creationMode == 0) "GENERATE VIDEO (${selectedDuration}s)" else "ANIMATE PHOTO TO VIDEO (${selectedDuration}s)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
