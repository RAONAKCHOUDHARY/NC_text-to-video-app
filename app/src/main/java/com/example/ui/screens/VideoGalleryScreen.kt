package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.components.ExportEngineBottomSheet
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaPink
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.ThemeManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoGalleryScreen(
    projects: List<VideoProject>,
    onSelectProject: (VideoProject) -> Unit,
    onToggleFavorite: (VideoProject) -> Unit,
    onNewVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var exportProjectTarget by remember { mutableStateOf<VideoProject?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filteredProjects = remember(projects, showOnlyFavorites) {
        if (showOnlyFavorites) projects.filter { it.isFavorite } else projects
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeManager.backgroundColor)
            .testTag("gallery_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Studio Gallery & Vault",
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${projects.size} AI Projects • Downloads & Exports",
                        color = ThemeManager.textSecondaryColor,
                        fontSize = 12.sp
                    )
                }

                // Filter chip
                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = { showOnlyFavorites = !showOnlyFavorites },
                    label = { Text("Favorites (${projects.count { it.isFavorite }})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CinemaPink.copy(alpha = 0.2f),
                        selectedLabelColor = CinemaPink,
                        containerColor = ThemeManager.surfaceColor,
                        labelColor = ThemeManager.textSecondaryColor
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (showOnlyFavorites) CinemaPink else ThemeManager.surfaceBorderColor,
                        selectedBorderColor = CinemaPink,
                        enabled = true,
                        selected = showOnlyFavorites
                    )
                )
            }
        }

        // Empty State
        if (filteredProjects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(ThemeManager.surfaceElevatedColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = ThemeManager.primaryAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (showOnlyFavorites) "No Favorite Videos Yet" else "No Videos Generated Yet",
                            color = ThemeManager.textPrimaryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (showOnlyFavorites) "Tap the heart icon on any video to bookmark it here."
                            else "Type any prompt or pick a photo in the Video Studio tab to generate your first AI video clip.",
                            color = ThemeManager.textMutedColor,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNewVideoClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeManager.primaryAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create New Video", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Projects List
        items(filteredProjects, key = { it.id }) { project ->
            VideoProjectCard(
                project = project,
                onClick = { onSelectProject(project) },
                onToggleFavorite = { onToggleFavorite(project) },
                onExportClick = { exportProjectTarget = project }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Export Bottom Sheet
    exportProjectTarget?.let { proj ->
        ExportEngineBottomSheet(
            project = proj,
            sheetState = sheetState,
            onDismiss = { exportProjectTarget = null },
            onExportComplete = { res, fps, path ->
                Toast.makeText(context, "Exported successfully to $path", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun VideoProjectCard(
    project: VideoProject,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onExportClick: () -> Unit
) {
    val dateStr = remember(project.createdAt) {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        sdf.format(Date(project.createdAt))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("video_card_${project.id}"),
        shape = RoundedCornerShape(16.dp),
        color = ThemeManager.surfaceColor,
        border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
    ) {
        Column {
            // Thumbnail Canvas Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 8.5f)
                    .background(Color.Black)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val isCyber = project.style.contains("Cyber", ignoreCase = true)

                    val topColor = if (isCyber) Color(0xFF0F172A) else Color(0xFF1E1B4B)
                    val midColor = if (isCyber) ThemeManager.primaryAccent.copy(alpha = 0.6f) else CinemaPurple.copy(alpha = 0.5f)
                    val bottomColor = if (isCyber) CinemaPink.copy(alpha = 0.3f) else CinemaAmber.copy(alpha = 0.4f)

                    drawRect(
                        brush = Brush.verticalGradient(listOf(topColor, midColor, bottomColor))
                    )

                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = 24.dp.toPx(),
                        center = Offset(w * 0.5f, h * 0.45f)
                    )
                }

                // Play Button Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.65f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = ThemeManager.primaryAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Top Left: Badges (Style, Aspect, Image/Text)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = if (project.sourceType == "image") "PHOTO ANIMATION" else project.style.uppercase(),
                            color = ThemeManager.primaryAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = project.aspectRatio,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    if (project.hasVoiceover) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CinemaPurple.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("VOICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Bottom Right: Duration Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.75f)
                ) {
                    Text(
                        text = "00:${String.format(Locale.US, "%02d", project.durationSeconds)}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Card Body Info
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.title,
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onExportClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export",
                                tint = ThemeManager.primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (project.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (project.isFavorite) CinemaPink else ThemeManager.textMutedColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = project.prompt,
                    color = ThemeManager.textSecondaryColor,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${project.cameraMotion} • ${project.fps} FPS",
                        color = ThemeManager.textMutedColor,
                        fontSize = 11.sp
                    )

                    Text(
                        text = dateStr,
                        color = ThemeManager.textMutedColor,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
