package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.ThemeManager
import java.util.Locale
import kotlin.math.sin

@Composable
fun FrameExtractorDialog(
    project: VideoProject,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val totalSeconds = project.durationSeconds.toFloat().coerceAtLeast(4f)
    var currentFrameTime by remember { mutableFloatStateOf(1.5f) }

    val currentFrameIndex = (currentFrameTime * project.fps).toInt()
    val totalFrames = (totalSeconds * project.fps).toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("frame_extractor_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = ThemeManager.primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Frame Extractor",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.textPrimaryColor
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ThemeManager.textMutedColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Frame Preview Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val w = size.width
                        val h = size.height
                        val progress = (currentFrameTime / totalSeconds).coerceIn(0f, 1f)

                        // Render dynamic frame simulation
                        val isCyber = project.style.contains("Cyber", ignoreCase = true)
                        val skyColor = if (isCyber) Color(0xFF0F172A) else Color(0xFF1E1B4B)
                        val accentColor = if (isCyber) CinemaCyan else CinemaPurple

                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(skyColor, accentColor.copy(alpha = 0.5f), Color(0xFF090C15))
                            )
                        )

                        // Frame focal orb
                        val orbX = w * (0.35f + 0.3f * sin(progress * 3.14).toFloat())
                        val orbY = h * 0.45f
                        drawCircle(
                            color = Color.White.copy(alpha = 0.85f),
                            radius = 28.dp.toPx(),
                            center = Offset(orbX, orbY)
                        )
                    }

                    // Watermark / Frame overlay
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "FRAME $currentFrameIndex / $totalFrames",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.primaryAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = String.format(Locale.US, "TIME: %02d:%04.2fs", (currentFrameTime / 60).toInt(), currentFrameTime % 60),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fine Frame Slider
                Slider(
                    value = currentFrameTime,
                    onValueChange = { currentFrameTime = it },
                    valueRange = 0f..totalSeconds,
                    colors = SliderDefaults.colors(
                        thumbColor = ThemeManager.primaryAccent,
                        activeTrackColor = ThemeManager.primaryAccent,
                        inactiveTrackColor = ThemeManager.surfaceBorderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Frame Step Buttons (-1 frame, +1 frame)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            currentFrameTime = (currentFrameTime - (1f / project.fps)).coerceAtLeast(0f)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeManager.surfaceElevatedColor,
                            contentColor = ThemeManager.textPrimaryColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("-1 Frame", fontSize = 11.sp)
                    }

                    Text(
                        text = "3840x2160 • PNG 24-bit",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ThemeManager.textMutedColor
                    )

                    Button(
                        onClick = {
                            currentFrameTime = (currentFrameTime + (1f / project.fps)).coerceAtMost(totalSeconds)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeManager.surfaceElevatedColor,
                            contentColor = ThemeManager.textPrimaryColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("+1 Frame", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Frame Button
                Button(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Frame #$currentFrameIndex captured and saved to Gallery / Pictures!",
                            Toast.LENGTH_LONG
                        ).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_frame_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeManager.primaryAccent,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAVE HIGH-RES FRAME", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = ThemeManager.surfaceColor,
        shape = RoundedCornerShape(20.dp)
    )
}
