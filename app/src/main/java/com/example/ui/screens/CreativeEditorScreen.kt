package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BeatTransient
import com.example.data.model.CaptionStyle
import com.example.data.model.EqualizerPreset
import com.example.data.model.FilterGrading
import com.example.data.model.TransitionType
import com.example.data.model.VideoProject
import com.example.ui.components.CinematicVideoPlayer
import com.example.ui.components.PulseCraftVectorBadge
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaPink
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VideoViewModel

@Composable
fun CreativeEditorScreen(
    viewModel: VideoViewModel,
    onOpenGenerator: () -> Unit
) {
    val context = LocalContext.current
    val project by viewModel.selectedProject.collectAsStateWithLifecycle()
    val isAudioMuted by viewModel.isAudioMuted.collectAsStateWithLifecycle()
    val beatTransients by viewModel.beatTransients.collectAsStateWithLifecycle()
    val waveformAmplitudes by viewModel.waveformAmplitudes.collectAsStateWithLifecycle()

    val isRecordingMic by viewModel.micRecorder.isRecording.collectAsStateWithLifecycle()
    val micDurationMs by viewModel.micRecorder.recordedDurationMs.collectAsStateWithLifecycle()
    val micAmplitude by viewModel.micRecorder.audioAmplitude.collectAsStateWithLifecycle()

    // Sub-tool section tabs: 0: Canvas & Transforms, 1: Grading & FX, 2: Beats & Captions, 3: Audio Suite
    var activeToolTab by remember { mutableIntStateOf(0) }

    // Media Pickers
    val bgMediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = resolveFileName(context, it) ?: "Imported_Media"
            val isVideo = name.endsWith(".mp4", ignoreCase = true) || name.endsWith(".mkv", ignoreCase = true)
            viewModel.importBackgroundMedia(it.toString(), name, isVideo)
        }
    }

    val overlayMediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = resolveFileName(context, it) ?: "Overlay_Track"
            viewModel.importOverlayMedia(it.toString(), name)
        }
    }

    // Permission launcher for Microphone recording
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceoverRecording()
        }
    }

    if (project == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CinemaBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PulseCraftVectorBadge(size = 80.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Project Active",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onOpenGenerator,
                    colors = ButtonDefaults.buttonColors(containerColor = CinemaCyan)
                ) {
                    Text("Create AI Video", color = CinemaBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val currentProj = project!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CinemaBackground)
            .testTag("creative_editor_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Top Branding & Project Header
        item {
            EditorHeader(
                project = currentProj,
                onSwitchToGenerator = onOpenGenerator
            )
        }

        // 2. Dual-Track Live Canvas Player with gesture transform support
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                CinematicVideoPlayer(
                    project = currentProj,
                    isMuted = isAudioMuted,
                    onToggleMute = { viewModel.toggleMute() },
                    enableTransformGestures = true,
                    onTransformChange = { scale, panX, panY, rot ->
                        viewModel.updateCanvasTransforms(scale, panX, panY, rot)
                    },
                    beatTransients = beatTransients
                )
            }
        }

        // 3. Dual-Track Timeline Bar (Track 1 Main + Track 2 Overlay)
        item {
            DualTrackTimelineCard(
                project = currentProj,
                onImportBackground = { bgMediaPicker.launch("video/*") },
                onImportOverlay = { overlayMediaPicker.launch("image/*") },
                onClearOverlay = { viewModel.clearOverlayMedia() }
            )
        }

        // 4. Studio Module Selection Chips
        item {
            StudioToolTabSelector(
                selectedTab = activeToolTab,
                onSelectTab = { activeToolTab = it }
            )
        }

        // 5. Active Tool Module Content
        item {
            when (activeToolTab) {
                0 -> {
                    CanvasTransformsToolSection(
                        project = currentProj,
                        onScaleChange = { viewModel.updateCanvasTransforms(it, currentProj.canvasPanX, currentProj.canvasPanY, currentProj.canvasRotation) },
                        onRotationChange = { viewModel.updateCanvasTransforms(currentProj.canvasScale, currentProj.canvasPanX, currentProj.canvasPanY, it) },
                        onPanXChange = { viewModel.updateCanvasTransforms(currentProj.canvasScale, it, currentProj.canvasPanY, currentProj.canvasRotation) },
                        onPanYChange = { viewModel.updateCanvasTransforms(currentProj.canvasScale, currentProj.canvasPanX, it, currentProj.canvasRotation) },
                        onReset = { viewModel.resetCanvasTransforms() },
                        onSelectAspectRatio = { viewModel.updateAspectRatio(it) }
                    )
                }

                1 -> {
                    GradingAndTransitionsToolSection(
                        project = currentProj,
                        onSelectFilter = { viewModel.updateFilterGrading(it) },
                        onSelectTransition = { viewModel.updateTransitionEffect(it) }
                    )
                }

                2 -> {
                    BeatAndCaptionsToolSection(
                        project = currentProj,
                        beatTransients = beatTransients,
                        waveform = waveformAmplitudes,
                        onBpmChange = { viewModel.updateBpm(it) },
                        onSpeedChange = { viewModel.updatePlaybackSpeed(it) },
                        onToggleSnap = { viewModel.toggleSnapToTransients(it) },
                        onGenerateCaptions = { viewModel.generateAutoCaptions(it) },
                        onSelectCaptionStyle = { viewModel.updateCaptionStyle(it) },
                        onToggleCaptions = { viewModel.toggleCaptions(it) }
                    )
                }

                3 -> {
                    AudioEditingSuiteSection(
                        project = currentProj,
                        isRecordingMic = isRecordingMic,
                        micDurationMs = micDurationMs,
                        micAmplitude = micAmplitude,
                        onRecordMicClick = {
                            if (isRecordingMic) {
                                viewModel.stopVoiceoverRecording()
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onBgVolumeChange = { viewModel.updateBgVolume(it) },
                        onVoiceoverVolumeChange = { viewModel.updateVoiceoverVolume(it) },
                        onAiVoiceVolumeChange = { viewModel.updateAiVoiceVolume(it) },
                        onFadeChange = { fIn, fOut -> viewModel.updateFadeDurations(fIn, fOut) },
                        onPitchChange = { viewModel.updatePitchSemitones(it) },
                        onEqualizerChange = { viewModel.updateEqualizerPreset(it) },
                        onNoiseSuppressionChange = { viewModel.toggleNoiseSuppression(it) }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// EDITOR HEADER
// -------------------------------------------------------------
@Composable
private fun EditorHeader(
    project: VideoProject,
    onSwitchToGenerator: () -> Unit
) {
    Surface(
        color = CinemaSurface.copy(alpha = 0.85f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PulseCraftVectorBadge(size = 40.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PULSECRAFT",
                            color = CinemaCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STUDIO",
                            color = CinemaPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = project.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick Switch to AI Generator
            IconButton(
                onClick = onSwitchToGenerator,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CinemaCyan.copy(alpha = 0.15f))
                    .testTag("open_ai_generator_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Open AI Generator",
                    tint = CinemaCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// DUAL-TRACK TIMELINE CARD (Track 1 Main + Track 2 Overlay)
// -------------------------------------------------------------
@Composable
private fun DualTrackTimelineCard(
    project: VideoProject,
    onImportBackground: () -> Unit,
    onImportOverlay: () -> Unit,
    onClearOverlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("dual_track_timeline_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Timeline Tracks",
                        tint = CinemaCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DUAL-TRACK TIMELINE",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = "${project.durationSeconds}s • ${project.fps} FPS",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Track 1: Main Media (Canvas / Imported Video)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImportBackground() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(CinemaCyan, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "TRACK 1: MAIN CANVAS",
                                color = CinemaCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = project.backgroundMediaName ?: "AI Generative Scene (${project.style})",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }

                    Text(
                        text = "TAP TO IMPORT",
                        color = CinemaCyan.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Track 2: Overlay Media (PIP / B-Roll)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1E1B4B),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImportOverlay() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(CinemaPink, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "TRACK 2: OVERLAY (PIP)",
                                color = CinemaPink,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = project.overlayMediaName ?: "Empty (Tap to add video/image)",
                                color = if (project.overlayMediaName != null) Color.White else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }

                    if (project.overlayMediaName != null) {
                        IconButton(
                            onClick = onClearOverlay,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Overlay",
                                tint = CinemaPink,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "+ ADD B-ROLL",
                            color = CinemaPink.copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STUDIO TOOL TAB SELECTOR
// -------------------------------------------------------------
@Composable
private fun StudioToolTabSelector(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit
) {
    val tabs = listOf(
        "Canvas" to Icons.Default.AspectRatio,
        "Grading & FX" to Icons.Default.Tune,
        "Beats & Subs" to Icons.Default.Subtitles,
        "Audio Suite" to Icons.Default.GraphicEq
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, (label, icon) ->
            val isSelected = selectedTab == index
            val bgColor by animateColorAsState(
                if (isSelected) CinemaCyan.copy(alpha = 0.2f) else CinemaSurface
            )
            val strokeColor by animateColorAsState(
                if (isSelected) CinemaCyan else Color.Transparent
            )

            Surface(
                onClick = { onSelectTab(index) },
                shape = RoundedCornerShape(10.dp),
                color = bgColor,
                modifier = Modifier
                    .border(1.dp, strokeColor, RoundedCornerShape(10.dp))
                    .testTag("tool_tab_$index")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) CinemaCyan else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        color = if (isSelected) CinemaCyan else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MODULE 0: CANVAS TRANSFORMS & ASPECT RATIO CROP
// -------------------------------------------------------------
@Composable
private fun CanvasTransformsToolSection(
    project: VideoProject,
    onScaleChange: (Float) -> Unit,
    onRotationChange: (Float) -> Unit,
    onPanXChange: (Float) -> Unit,
    onPanYChange: (Float) -> Unit,
    onReset: () -> Unit,
    onSelectAspectRatio: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CANVAS TRANSFORMS",
                    color = CinemaCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Aspect Ratio Selector
            Text("Aspect Ratio Crop", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("9:16", "16:9", "1:1", "4:5", "21:9").forEach { ratio ->
                    val isSelected = project.aspectRatio == ratio
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectAspectRatio(ratio) },
                        label = { Text(ratio, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CinemaCyan.copy(alpha = 0.25f),
                            selectedLabelColor = CinemaCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-touch pinch scale slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Scale Zoom", color = TextSecondary, fontSize = 12.sp)
                Text("${String.format("%.2f", project.canvasScale)}x", color = CinemaCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = project.canvasScale,
                onValueChange = onScaleChange,
                valueRange = 0.5f..3.0f,
                colors = SliderDefaults.colors(thumbColor = CinemaCyan, activeTrackColor = CinemaCyan)
            )

            // Canvas Rotation slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rotation", color = TextSecondary, fontSize = 12.sp)
                Text("${project.canvasRotation.toInt()}°", color = CinemaCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = project.canvasRotation,
                onValueChange = onRotationChange,
                valueRange = -180f..180f,
                colors = SliderDefaults.colors(thumbColor = CinemaCyan, activeTrackColor = CinemaCyan)
            )

            // Pan X/Y offsets
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pan Offset (X / Y)", color = TextSecondary, fontSize = 12.sp)
                Text("(${project.canvasPanX.toInt()}px, ${project.canvasPanY.toInt()}px)", color = CinemaCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Slider(
                    value = project.canvasPanX,
                    onValueChange = onPanXChange,
                    valueRange = -300f..300f,
                    colors = SliderDefaults.colors(thumbColor = CinemaCyan, activeTrackColor = CinemaCyan),
                    modifier = Modifier.weight(1f)
                )
                Slider(
                    value = project.canvasPanY,
                    onValueChange = onPanYChange,
                    valueRange = -300f..300f,
                    colors = SliderDefaults.colors(thumbColor = CinemaPurple, activeTrackColor = CinemaPurple),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// MODULE 1: FILTER GRADING & TRANSITIONS
// -------------------------------------------------------------
@Composable
private fun GradingAndTransitionsToolSection(
    project: VideoProject,
    onSelectFilter: (String) -> Unit,
    onSelectTransition: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Filter Grading
            Text(
                text = "CINEMATIC FILTER GRADING",
                color = CinemaCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val filters = listOf(
                FilterGrading.NONE,
                FilterGrading.TEAL_ORANGE,
                FilterGrading.MOODY_MONO,
                FilterGrading.CYBERPUNK_NEON,
                FilterGrading.VINTAGE_VHS
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = project.filterGrading == filter.displayName
                    Surface(
                        onClick = { onSelectFilter(filter.displayName) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) CinemaCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                        modifier = Modifier.border(1.dp, if (isSelected) CinemaCyan else Color.Transparent, RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = filter.displayName,
                                color = if (isSelected) CinemaCyan else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = filter.subtitle,
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scene Transitions
            Text(
                text = "TRANSITION & KEYFRAME ANIMATION",
                color = CinemaPink,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val transitions = listOf(
                TransitionType.FADE,
                TransitionType.WHIP_PAN,
                TransitionType.ZOOM_PUNCH,
                TransitionType.GLITCH_WARP,
                TransitionType.RGB_SPLIT
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                transitions.forEach { trans ->
                    val isSelected = project.transitionEffect == trans.displayName
                    Surface(
                        onClick = { onSelectTransition(trans.displayName) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) CinemaPink.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                        modifier = Modifier.border(1.dp, if (isSelected) CinemaPink else Color.Transparent, RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = trans.displayName,
                                color = if (isSelected) CinemaPink else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${trans.durationMs}ms dynamic cut",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MODULE 2: BEAT DETECTION, FLOW SYNC & AUTO-CAPTIONS
// -------------------------------------------------------------
@Composable
private fun BeatAndCaptionsToolSection(
    project: VideoProject,
    beatTransients: List<BeatTransient>,
    waveform: List<Float>,
    onBpmChange: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onToggleSnap: (Boolean) -> Unit,
    onGenerateCaptions: (String) -> Unit,
    onSelectCaptionStyle: (String) -> Unit,
    onToggleCaptions: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Audio Visualizer with Beat-Marker Transients
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Equalizer, contentDescription = "Beats", tint = CinemaCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BEAT DETECTION & FLOW SYNC",
                        color = CinemaCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "${beatTransients.size} BEATS",
                    color = CinemaPink,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Waveform with beat transients canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0B0F19))
            ) {
                val w = size.width
                val h = size.height
                val count = waveform.size.coerceAtLeast(1)
                val step = w / count.toFloat()

                waveform.forEachIndexed { i, amp ->
                    val barH = (amp * (h * 0.85f)).coerceAtLeast(4f)
                    val barX = i * step
                    val barY = (h - barH) / 2f
                    drawRoundRect(
                        color = if (i % 4 == 0) CinemaCyan else CinemaPurple.copy(alpha = 0.6f),
                        topLeft = Offset(barX, barY),
                        size = Size(step * 0.7f, barH),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                }

                // Draw Beat-Marker Transient points
                beatTransients.forEach { transient ->
                    val tX = (transient.timestampMs / (project.durationSeconds * 1000f).coerceAtLeast(1f)) * w
                    drawLine(
                        color = CinemaPink,
                        start = Offset(tX, 0f),
                        end = Offset(tX, h),
                        strokeWidth = 2f
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Flow Tempo BPM Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Flow Tempo (BPM)", color = TextSecondary, fontSize = 12.sp)
                Text("${project.bpm} BPM", color = CinemaCyan, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = project.bpm.toFloat(),
                onValueChange = { onBpmChange(it.toInt()) },
                valueRange = 60f..180f,
                colors = SliderDefaults.colors(thumbColor = CinemaCyan, activeTrackColor = CinemaCyan)
            )

            // Playback Speed Slider (0.5x to 3.0x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Playback Speed Stretch", color = TextSecondary, fontSize = 12.sp)
                Text("${project.playbackSpeed}x", color = CinemaCyan, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = project.playbackSpeed,
                onValueChange = onSpeedChange,
                valueRange = 0.5f..3.0f,
                colors = SliderDefaults.colors(thumbColor = CinemaCyan, activeTrackColor = CinemaCyan)
            )

            // Snap Cuts to Transients Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Snap Cuts to Transients", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Rhythmic pulse sync along the timeline", color = TextMuted, fontSize = 10.sp)
                }
                Switch(
                    checked = project.snapToTransients,
                    onCheckedChange = onToggleSnap,
                    colors = SwitchDefaults.colors(checkedThumbColor = CinemaPink, checkedTrackColor = CinemaPink.copy(alpha = 0.35f))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AUTO-CAPTIONS GENERATOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Subtitles, contentDescription = "Captions", tint = CinemaPink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DYNAMIC AUTO-CAPTIONS",
                        color = CinemaPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Switch(
                    checked = project.hasCaptions,
                    onCheckedChange = onToggleCaptions,
                    colors = SwitchDefaults.colors(checkedThumbColor = CinemaCyan, checkedTrackColor = CinemaCyan.copy(alpha = 0.35f))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Captions Style Selector: Karaoke, Word-by-Word, Bold Cinematic
            val captionStyles = listOf(
                CaptionStyle.KARAOKE,
                CaptionStyle.WORD_POP,
                CaptionStyle.BOLD_CINEMATIC
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                captionStyles.forEach { style ->
                    val isSelected = project.captionStyle == style.displayName
                    Surface(
                        onClick = { onSelectCaptionStyle(style.displayName) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) CinemaCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isSelected) CinemaCyan else Color.Transparent, RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = style.displayName,
                                color = if (isSelected) CinemaCyan else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onGenerateCaptions(project.captionStyle) },
                colors = ButtonDefaults.buttonColors(containerColor = CinemaPink.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Auto Transcribe", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate AI Speech Captions", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// MODULE 3: AUDIO EDITING SUITE & MULTI-LAYER MIXING
// -------------------------------------------------------------
@Composable
private fun AudioEditingSuiteSection(
    project: VideoProject,
    isRecordingMic: Boolean,
    micDurationMs: Long,
    micAmplitude: Float,
    onRecordMicClick: () -> Unit,
    onBgVolumeChange: (Float) -> Unit,
    onVoiceoverVolumeChange: (Float) -> Unit,
    onAiVoiceVolumeChange: (Float) -> Unit,
    onFadeChange: (Float, Float) -> Unit,
    onPitchChange: (Int) -> Unit,
    onEqualizerChange: (String) -> Unit,
    onNoiseSuppressionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "MULTI-LAYER AUDIO MIXER",
                color = CinemaCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Microphone Voiceover Recorder
            Surface(
                color = if (isRecordingMic) Color(0xFF3B0764) else Color(0xFF0F172A),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(if (isRecordingMic) Color.Red else CinemaCyan, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isRecordingMic) "RECORDING MIC..." else "VOICEOVER MIC CAPTURE",
                                color = if (isRecordingMic) Color.Red else CinemaCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isRecordingMic) "${String.format("%.1f", micDurationMs / 1000f)}s"
                                else (if (project.recordedVoiceUri != null) "Recorded: ${String.format("%.1f", project.recordedDurationSec)}s" else "Tap Mic to record"),
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = onRecordMicClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecordingMic) Color.Red else CinemaPink
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isRecordingMic) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Record",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isRecordingMic) "STOP" else "RECORD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Volume Sliders: Background Beat, Mic Voiceover, AI Narration
            Text("Layer Volume Automation", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Background Beat Volume", color = TextSecondary, fontSize = 11.sp)
                Text("${(project.bgVolume * 100).toInt()}%", color = CinemaCyan, fontSize = 11.sp)
            }
            Slider(
                value = project.bgVolume,
                onValueChange = onBgVolumeChange,
                valueRange = 0f..1.5f,
                colors = SliderDefaults.colors(thumbColor = CinemaCyan, activeTrackColor = CinemaCyan)
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Recorded Voiceover Volume", color = TextSecondary, fontSize = 11.sp)
                Text("${(project.voiceoverVolume * 100).toInt()}%", color = CinemaPink, fontSize = 11.sp)
            }
            Slider(
                value = project.voiceoverVolume,
                onValueChange = onVoiceoverVolumeChange,
                valueRange = 0f..1.5f,
                colors = SliderDefaults.colors(thumbColor = CinemaPink, activeTrackColor = CinemaPink)
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("AI Voice Narration Volume", color = TextSecondary, fontSize = 11.sp)
                Text("${(project.aiVoiceVolume * 100).toInt()}%", color = CinemaPurple, fontSize = 11.sp)
            }
            Slider(
                value = project.aiVoiceVolume,
                onValueChange = onAiVoiceVolumeChange,
                valueRange = 0f..1.5f,
                colors = SliderDefaults.colors(thumbColor = CinemaPurple, activeTrackColor = CinemaPurple)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pitch Shifting (-12 to +12 semitones)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Pitch Shifting", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${if (project.pitchSemitones > 0) "+" else ""}${project.pitchSemitones} semitones",
                    color = CinemaCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Slider(
                value = project.pitchSemitones.toFloat(),
                onValueChange = { onPitchChange(it.toInt()) },
                valueRange = -12f..12f,
                steps = 23,
                colors = SliderDefaults.colors(thumbColor = CinemaCyan, activeTrackColor = CinemaCyan)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Equalizer Presets
            Text("Equalizer Presets", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            val eqPresets = listOf(
                EqualizerPreset.STUDIO_WARMTH,
                EqualizerPreset.BASS_BOOST,
                EqualizerPreset.VOCAL_CLARITY,
                EqualizerPreset.FLAT
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                eqPresets.forEach { preset ->
                    val isSelected = project.equalizerPreset == preset.displayName
                    FilterChip(
                        selected = isSelected,
                        onClick = { onEqualizerChange(preset.displayName) },
                        label = { Text(preset.displayName, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CinemaCyan.copy(alpha = 0.25f),
                            selectedLabelColor = CinemaCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Noise Suppression Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Background Noise Suppression", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Adaptive noise-gate spectral filtering", color = TextMuted, fontSize = 10.sp)
                }
                Switch(
                    checked = project.noiseSuppression,
                    onCheckedChange = onNoiseSuppressionChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = CinemaCyan, checkedTrackColor = CinemaCyan.copy(alpha = 0.35f))
                )
            }
        }
    }
}

// Utility to extract display name from a content Uri
private fun resolveFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
    } catch (e: Exception) {
        // Fallback
    }
    return name ?: uri.lastPathSegment
}
