package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.model.StockMediaItem
import com.example.data.model.StockMediaType
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaPink
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.ThemeManager

@Composable
fun StockLibraryScreen(
    stockItems: List<StockMediaItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: StockMediaType?,
    onSelectCategory: (StockMediaType?) -> Unit,
    onDownloadItem: (StockMediaItem) -> Unit,
    onImportToStudio: (StockMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var previewItem by remember { mutableStateOf<StockMediaItem?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeManager.backgroundColor)
            .testTag("stock_library_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
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
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = ThemeManager.primaryAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Royalty-Free Stock Media",
                        color = ThemeManager.textPrimaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "100% Free for Commercial Use • No Copyright",
                        color = ThemeManager.textSecondaryColor,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stock_search_field"),
                placeholder = {
                    Text("Search 4K videos, photos, and music tracks...", fontSize = 13.sp, color = ThemeManager.textMutedColor)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = ThemeManager.primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = ThemeManager.textMutedColor)
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ThemeManager.surfaceColor,
                    unfocusedContainerColor = ThemeManager.surfaceColor,
                    focusedTextColor = ThemeManager.textPrimaryColor,
                    unfocusedTextColor = ThemeManager.textPrimaryColor,
                    focusedIndicatorColor = ThemeManager.primaryAccent,
                    unfocusedIndicatorColor = ThemeManager.surfaceBorderColor
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Category Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" chip
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onSelectCategory(null) },
                    label = { Text("All Media", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ThemeManager.primaryAccent.copy(alpha = 0.2f),
                        selectedLabelColor = ThemeManager.primaryAccent,
                        containerColor = ThemeManager.surfaceColor,
                        labelColor = ThemeManager.textSecondaryColor
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (selectedCategory == null) ThemeManager.primaryAccent else ThemeManager.surfaceBorderColor,
                        selectedBorderColor = ThemeManager.primaryAccent,
                        enabled = true,
                        selected = selectedCategory == null
                    )
                )

                StockMediaType.values().forEach { type ->
                    val isSelected = selectedCategory == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(type) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (type) {
                                        StockMediaType.VIDEO -> Icons.Default.Movie
                                        StockMediaType.PHOTO -> Icons.Default.Image
                                        StockMediaType.AUDIO -> Icons.Default.MusicNote
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(type.label, fontSize = 12.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ThemeManager.primaryAccent.copy(alpha = 0.2f),
                            selectedLabelColor = ThemeManager.primaryAccent,
                            containerColor = ThemeManager.surfaceColor,
                            labelColor = ThemeManager.textSecondaryColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) ThemeManager.primaryAccent else ThemeManager.surfaceBorderColor,
                            selectedBorderColor = ThemeManager.primaryAccent,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
        }

        // Empty state
        if (stockItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No media found for '$searchQuery'",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.textPrimaryColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try searching 'drone', 'cyberpunk', 'space', 'ocean', or 'synth'.",
                            fontSize = 12.sp,
                            color = ThemeManager.textMutedColor
                        )
                    }
                }
            }
        }

        // Stock Items List
        items(stockItems, key = { it.id }) { item ->
            StockMediaCard(
                item = item,
                onPreview = { previewItem = item },
                onDownload = {
                    onDownloadItem(item)
                    Toast.makeText(context, "Downloaded '${item.title}' to device storage!", Toast.LENGTH_SHORT).show()
                },
                onImport = {
                    onImportToStudio(item)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // In-App Preview Modal
    previewItem?.let { item ->
        MediaPreviewDialog(
            item = item,
            onDismiss = { previewItem = null },
            onDownload = {
                onDownloadItem(item)
                Toast.makeText(context, "Downloaded '${item.title}' to device!", Toast.LENGTH_SHORT).show()
            },
            onImport = {
                onImportToStudio(item)
                previewItem = null
            }
        )
    }
}

@Composable
private fun StockMediaCard(
    item: StockMediaItem,
    onPreview: () -> Unit,
    onDownload: () -> Unit,
    onImport: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onPreview() }
            .testTag("stock_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        color = ThemeManager.surfaceColor,
        border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor)
    ) {
        Column {
            // Media Poster Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 8.5f)
                    .background(Color.Black)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val colors = item.previewGradientHexes.map {
                        try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { CinemaCyan }
                    }
                    drawRect(brush = Brush.verticalGradient(colors))
                }

                // Play / Inspect Icon Center
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(42.dp)
                        .background(Color.Black.copy(alpha = 0.65f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.type) {
                            StockMediaType.VIDEO -> Icons.Default.PlayArrow
                            StockMediaType.PHOTO -> Icons.Default.Image
                            StockMediaType.AUDIO -> Icons.Default.GraphicEq
                        },
                        contentDescription = null,
                        tint = ThemeManager.primaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Top Left: Type & Resolution Tag
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = item.type.label.uppercase(),
                            color = ThemeManager.primaryAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = item.resolution,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Bottom Right: Duration or Size Tag
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.75f)
                ) {
                    val label = if (item.durationSeconds > 0) {
                        "${item.durationSeconds}s • ${item.fileSizeMb}MB"
                    } else {
                        "${item.fileSizeMb}MB"
                    }
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Card Body Info
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThemeManager.textPrimaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // One-Tap Download Button
                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = ThemeManager.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.category} • by ${item.author}",
                        fontSize = 11.sp,
                        color = ThemeManager.textMutedColor
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ThemeManager.surfaceElevatedColor
                    ) {
                        Text(
                            text = "NO COPYRIGHT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CinemaAmber,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaPreviewDialog(
    item: StockMediaItem,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onImport: () -> Unit
) {
    var isAudioPlaying by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("media_preview_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThemeManager.textPrimaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = ThemeManager.textMutedColor)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Media Preview Stage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val colors = item.previewGradientHexes.map {
                            try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { CinemaCyan }
                        }
                        drawRect(brush = Brush.verticalGradient(colors))
                    }

                    if (item.type == StockMediaType.AUDIO) {
                        IconButton(
                            onClick = { isAudioPlaying = !isAudioPlaying },
                            modifier = Modifier
                                .size(52.dp)
                                .background(ThemeManager.primaryAccent.copy(alpha = 0.85f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.Black.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.type == StockMediaType.VIDEO) Icons.Default.PlayArrow else Icons.Default.Image,
                                contentDescription = null,
                                tint = ThemeManager.primaryAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata Details
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = ThemeManager.surfaceElevatedColor
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "SPECIFICATIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ThemeManager.primaryAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Format: ${item.type.label} • ${item.resolution}", fontSize = 11.sp, color = ThemeManager.textPrimaryColor)
                        Text(text = "File Size: ${item.fileSizeMb} MB", fontSize = 11.sp, color = ThemeManager.textSecondaryColor)
                        Text(text = "License: ${item.license}", fontSize = 11.sp, color = CinemaAmber)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ThemeManager.textPrimaryColor),
                        border = BorderStroke(1.dp, ThemeManager.surfaceBorderColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onImport,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeManager.primaryAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Use in Studio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = ThemeManager.surfaceColor,
        shape = RoundedCornerShape(18.dp)
    )
}
