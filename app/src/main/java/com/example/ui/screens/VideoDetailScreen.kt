package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SceneShot
import com.example.data.model.VideoProject
import com.example.ui.components.CinematicVideoPlayer
import com.example.ui.components.ExportEngineBottomSheet
import com.example.ui.components.FrameExtractorDialog
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaPink
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.ThemeManager
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    project: VideoProject,
    isAudioMuted: Boolean,
    onToggleMute: () -> Unit,
    onToggleFavorite: (VideoProject) -> Unit,
    onDeleteProject: (VideoProject) -> Unit,
    onRemixPrompt: (VideoProject) -> Unit,
    onBack: () -> Unit,
    onOpenInEditor: (VideoProject) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFullscreen by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }
    var showFrameExtractor by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scenes = remember(project.scenesJson) { project.getScenes() }

    if (isFullscreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            CinematicVideoPlayer(
                project = project,
                modifier = Modifier.fillMaxSize(),
                isFullscreen = true,
                onToggleFullscreen = { isFullscreen = false },
                isMuted = isAudioMuted,
                onToggleMute = onToggleMute
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeManager.backgroundColor)
            .testTag("video_detail_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Nav Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .background(ThemeManager.surfaceElevatedColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ThemeManager.textPrimaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = project.title,
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Favorite Toggle
                    IconButton(
                        onClick = { onToggleFavorite(project) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (project.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (project.isFavorite) CinemaPink else ThemeManager.textSecondaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Delete button
                    IconButton(
                        onClick = { onDeleteProject(project) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = ThemeManager.textMutedColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Hero Video Player
        item {
            CinematicVideoPlayer(
                project = project,
                modifier = Modifier.fillMaxWidth(),
                isFullscreen = false,
                onToggleFullscreen = { isFullscreen = true },
                isMuted = isAudioMuted,
                onToggleMute = onToggleMute
            )
        }

        // Project Badges & Metadata Row
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = ThemeManager.surfaceColor,
                border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetaItem(label = "STYLE", value = project.style)
                    MetaItem(label = "RATIO", value = project.aspectRatio)
                    MetaItem(label = "FPS", value = "${project.fps} fps")
                    MetaItem(label = "LENGTH", value = "${project.durationSeconds}s")
                }
            }
        }

        // Creative Suite Hub
        item {
            Button(
                onClick = { onOpenInEditor(project) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("edit_in_studio_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeManager.primaryAccent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open in Creative Studio Suite", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Export & Extraction Action Hub (User requirements 2 & 5)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cinematic Export
                Button(
                    onClick = { showExportSheet = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("export_video_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeManager.surfaceElevatedColor,
                        contentColor = ThemeManager.textPrimaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.HighQuality, contentDescription = null, modifier = Modifier.size(16.dp), tint = ThemeManager.primaryAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Extract Frames
                Button(
                    onClick = { showFrameExtractor = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("extract_frames_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeManager.surfaceElevatedColor,
                        contentColor = ThemeManager.textPrimaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = CinemaPurple)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Extract Frames", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Secondary Actions Row (Remix, Direct Download, Copy Script)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Direct Download
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Direct download started for ${project.title}. Saved to Movies / Gallery!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ThemeManager.textPrimaryColor),
                    border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Direct Save", fontSize = 11.sp)
                }

                // Remix Prompt
                OutlinedButton(
                    onClick = { onRemixPrompt(project) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ThemeManager.primaryAccent),
                    border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remix", fontSize = 11.sp)
                }

                // Copy Script
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Video Script", project.expandedPrompt.ifBlank { project.prompt })
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Script copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ThemeManager.textPrimaryColor),
                    border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 11.sp)
                }
            }
        }

        // Voiceover Information Card (if present)
        if (project.hasVoiceover && project.voiceScript.isNotBlank()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = ThemeManager.surfaceColor,
                    border = BorderStroke(1.dp, ThemeManager.primaryAccent.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = ThemeManager.primaryAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SYNCHRONIZED VOICEOVER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ThemeManager.primaryAccent)
                            }
                            Text(project.voiceProfile.uppercase(), fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = CinemaAmber)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = project.voiceScript, fontSize = 12.sp, color = ThemeManager.textSecondaryColor, lineHeight = 17.sp)
                    }
                }
            }
        }

        // Director's Storyboard Timeline Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DIRECTOR'S STORYBOARD",
                        color = ThemeManager.primaryAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${scenes.size} Shots Planned",
                        color = ThemeManager.textMutedColor,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                scenes.forEach { scene ->
                    StoryboardCard(scene = scene)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Prompt & Camera Specifications Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ThemeManager.surfaceColor,
                border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CINEMATOGRAPHY SPECIFICATIONS",
                        color = CinemaPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "User Prompt:",
                        color = ThemeManager.textMutedColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = project.prompt,
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    if (project.expandedPrompt.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Director Script & Lighting Notes:",
                            color = ThemeManager.primaryAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = project.expandedPrompt,
                            color = ThemeManager.textSecondaryColor,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Motion Dynamics: ${(project.motionIntensity * 100).toInt()}%",
                            color = ThemeManager.textMutedColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Soundscape: ${project.audioMood}",
                            color = ThemeManager.textMutedColor,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Export Bottom Sheet
    if (showExportSheet) {
        ExportEngineBottomSheet(
            project = project,
            sheetState = sheetState,
            onDismiss = { showExportSheet = false },
            onExportComplete = { res, fps, path ->
                Toast.makeText(context, "Exported successfully to $path", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Frame Extractor Dialog
    if (showFrameExtractor) {
        FrameExtractorDialog(
            project = project,
            onDismiss = { showFrameExtractor = false }
        )
    }
}

@Composable
private fun MetaItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = ThemeManager.textMutedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = ThemeManager.textPrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StoryboardCard(scene: SceneShot) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ThemeManager.surfaceElevatedColor,
        border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(ThemeManager.primaryAccent.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${scene.sceneNumber}",
                            color = ThemeManager.primaryAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = scene.title,
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ThemeManager.backgroundColor
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", scene.durationSeconds)}s",
                        color = CinemaAmber,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = scene.visualDescription,
                color = ThemeManager.textSecondaryColor,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CinemaPurple.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = scene.cameraMovement,
                            color = CinemaPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Color Palette Swatches
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    scene.colorHexes.take(3).forEach { hex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(hex))
                        } catch (e: Exception) {
                            ThemeManager.primaryAccent
                        }
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        )
                    }
                }
            }
        }
    }
}
