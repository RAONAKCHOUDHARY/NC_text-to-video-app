package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SceneShot
import com.example.data.model.VideoProject
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaPink
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CinematicVideoPlayer(
    project: VideoProject,
    modifier: Modifier = Modifier,
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {},
    isMuted: Boolean = false,
    onToggleMute: () -> Unit = {}
) {
    val durationSeconds = project.durationSeconds.toFloat().coerceAtLeast(3f)
    var currentTimeSec by remember(project.id) { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(true) }
    var isLooping by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var showControls by remember { mutableStateOf(true) }

    val scenes = remember(project.scenesJson) { project.getScenes() }

    // Real-time animation loop
    LaunchedEffect(isPlaying, durationSeconds, playbackSpeed, isLooping) {
        var lastNano = System.nanoTime()
        while (isPlaying) {
            withFrameNanos { now ->
                val dt = (now - lastNano) / 1_000_000_000f
                lastNano = now
                currentTimeSec += dt * playbackSpeed
                if (currentTimeSec >= durationSeconds) {
                    if (isLooping) {
                        currentTimeSec %= durationSeconds
                    } else {
                        currentTimeSec = durationSeconds
                        isPlaying = false
                    }
                }
            }
        }
    }

    // Determine current scene
    val sceneFraction = (currentTimeSec / durationSeconds).coerceIn(0f, 0.999f)
    val activeSceneIndex = if (scenes.isNotEmpty()) {
        (sceneFraction * scenes.size).toInt().coerceIn(0, scenes.size - 1)
    } else 0
    val activeScene = scenes.getOrNull(activeSceneIndex)

    val aspectMultiplier = when (project.aspectRatio) {
        "9:16" -> 9f / 16f
        "1:1" -> 1f
        "4:3" -> 4f / 3f
        else -> 16f / 9f
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp))
            .background(Color.Black)
            .testTag("cinematic_video_player")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
    ) {
        // Video Canvas Aspect Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isFullscreen) Modifier.fillMaxSize()
                    else Modifier.aspectRatio(aspectMultiplier)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Generative Visual Rendering Engine
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("video_canvas")
            ) {
                drawCinematicScene(
                    project = project,
                    activeScene = activeScene,
                    timeSec = currentTimeSec,
                    durationSec = durationSeconds,
                    activeSceneIndex = activeSceneIndex,
                    totalScenes = scenes.size.coerceAtLeast(1)
                )
            }

            // Anamorphic Letterbox Cinema Bars if enabled in 16:9/landscape
            if (project.aspectRatio == "16:9" && !isFullscreen) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(Color.Black.copy(alpha = 0.85f))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(Color.Black.copy(alpha = 0.85f))
                    )
                }
            }

            // Live Camera Motion / Shot Telemetry HUD (Top left)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (isPlaying) Color.Red else CinemaAmber, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPlaying) "REC ${project.fps}FPS" else "PAUSED",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "•",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = activeScene?.shotType ?: project.cameraMotion,
                    color = CinemaCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Aspect Ratio & Resolution Pill (Top right)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${project.aspectRatio} | 4K AI",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Active Scene Title Overlay at bottom-left when playing
            if (activeScene != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 14.dp, bottom = if (showControls) 70.dp else 16.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text(
                            text = "SHOT ${activeSceneIndex + 1}/${scenes.size}: ${activeScene.title.uppercase()}",
                            color = CinemaCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = activeScene.cameraMovement,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Overlay Player Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                // Center Big Play/Pause Button
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                        .background(CinemaCyan.copy(alpha = 0.85f), CircleShape)
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = CinemaBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Bottom Control Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Timeline Scrubber Slider
                    Slider(
                        value = currentTimeSec,
                        onValueChange = {
                            currentTimeSec = it
                        },
                        valueRange = 0f..durationSeconds,
                        colors = SliderDefaults.colors(
                            thumbColor = CinemaCyan,
                            activeTrackColor = CinemaCyan,
                            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .testTag("timeline_slider")
                    )

                    // Control Buttons Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Play/Pause mini + Timecode
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Play",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Timecode
                            val currentMinutes = (currentTimeSec / 60).toInt()
                            val currentSecs = (currentTimeSec % 60).toInt()
                            val currentFrac = ((currentTimeSec % 1) * 10).toInt()
                            val totalMinutes = (durationSeconds / 60).toInt()
                            val totalSecs = (durationSeconds % 60).toInt()

                            val timecode = String.format(
                                "%02d:%02d.%d / %02d:%02d.0",
                                currentMinutes, currentSecs, currentFrac,
                                totalMinutes, totalSecs
                            )

                            Text(
                                text = timecode,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Right: Speed, Mute, Loop, Fullscreen
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Speed Toggle
                            Text(
                                text = "${playbackSpeed}x",
                                color = if (playbackSpeed != 1.0f) CinemaCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        playbackSpeed = when (playbackSpeed) {
                                            0.5f -> 1.0f
                                            1.0f -> 1.5f
                                            1.5f -> 2.0f
                                            else -> 0.5f
                                        }
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                                    .testTag("playback_speed_button")
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            // Loop Toggle
                            IconButton(
                                onClick = { isLooping = !isLooping },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = "Loop",
                                    tint = if (isLooping) CinemaCyan else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Mute Ambient Audio
                            IconButton(
                                onClick = onToggleMute,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Toggle Mute",
                                    tint = if (!isMuted) CinemaCyan else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Fullscreen Toggle
                            IconButton(
                                onClick = onToggleFullscreen,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Toggle Fullscreen",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * High-performance cinematic canvas rendering engine:
 * Simulates camera motion (zoom, pan, tilt, orbit),
 * lighting highlights, particle fields, atmospheric gradient skies,
 * and terrain/skyline geometry tailored to the video prompt & style!
 */
private fun DrawScope.drawCinematicScene(
    project: VideoProject,
    activeScene: SceneShot?,
    timeSec: Float,
    durationSec: Float,
    activeSceneIndex: Int,
    totalScenes: Int
) {
    val width = size.width
    val height = size.height
    if (width <= 0 || height <= 0) return

    val progress = (timeSec / durationSec).coerceIn(0f, 1f)
    val sceneProgress = ((timeSec % (durationSec / totalScenes.toFloat())) / (durationSec / totalScenes.toFloat())).coerceIn(0f, 1f)

    // Palette parsing
    val primaryColor = try {
        Color(android.graphics.Color.parseColor(activeScene?.colorHexes?.getOrNull(0) ?: "#00E5FF"))
    } catch (e: Exception) {
        CinemaCyan
    }

    val secondaryColor = try {
        Color(android.graphics.Color.parseColor(activeScene?.colorHexes?.getOrNull(1) ?: "#8B5CF6"))
    } catch (e: Exception) {
        CinemaPurple
    }

    val darkBaseColor = try {
        Color(android.graphics.Color.parseColor(activeScene?.colorHexes?.getOrNull(2) ?: "#090C15"))
    } catch (e: Exception) {
        CinemaBackground
    }

    // Camera Motion Transform Calculations:
    val motionIntensity = project.motionIntensity
    val cameraType = activeScene?.cameraMovement ?: project.cameraMotion

    var panX = 0f
    var panY = 0f
    var zoomScale = 1.0f

    when {
        cameraType.contains("Zoom", ignoreCase = true) -> {
            zoomScale = 1.0f + 0.35f * motionIntensity * sceneProgress
            panY = 15f * motionIntensity * sin(sceneProgress * 3.14159f)
        }
        cameraType.contains("Pan", ignoreCase = true) -> {
            panX = (sceneProgress - 0.5f) * width * 0.25f * motionIntensity
            zoomScale = 1.08f
        }
        cameraType.contains("Orbit", ignoreCase = true) -> {
            val angle = sceneProgress * 2.0 * Math.PI
            panX = (cos(angle) * 30f * motionIntensity).toFloat()
            panY = (sin(angle) * 18f * motionIntensity).toFloat()
            zoomScale = 1.05f + 0.1f * sin(sceneProgress * 3.14159f)
        }
        cameraType.contains("Drone", ignoreCase = true) || cameraType.contains("Flyover", ignoreCase = true) -> {
            panY = (sceneProgress - 0.5f) * height * 0.3f * motionIntensity
            zoomScale = 1.0f + 0.25f * motionIntensity * sceneProgress
            panX = sin(sceneProgress * 4.0).toFloat() * 20f
        }
        else -> { // Dolly / Dynamic
            zoomScale = 1.0f + 0.2f * motionIntensity * sceneProgress
            panX = (sceneProgress - 0.5f) * 20f
        }
    }

    // 1. Dynamic Atmosphere & Sky Gradient
    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            darkBaseColor,
            secondaryColor.copy(alpha = 0.7f),
            primaryColor.copy(alpha = 0.45f)
        ),
        startY = panY,
        endY = height * 0.85f + panY
    )
    drawRect(brush = skyGradient, size = size)

    // 2. Horizon / Celestial Body (Sun/Moon/Core)
    val sunRadius = (width * 0.14f) * zoomScale
    val sunCenterX = width * 0.5f + panX
    val sunCenterY = height * 0.42f + panY

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor,
                secondaryColor.copy(alpha = 0.6f),
                Color.Transparent
            ),
            center = Offset(sunCenterX, sunCenterY),
            radius = sunRadius * 2.2f
        ),
        radius = sunRadius * 2.2f,
        center = Offset(sunCenterX, sunCenterY)
    )

    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = sunRadius * 0.6f,
        center = Offset(sunCenterX, sunCenterY)
    )

    // 3. Volumetric Light Beams (Anamorphic sweep)
    val beamOffset = (sin((timeSec * 0.8).toDouble()) * width * 0.3).toFloat()
    val beamPath = Path().apply {
        moveTo(sunCenterX + beamOffset - 80f, 0f)
        lineTo(sunCenterX + beamOffset + 80f, 0f)
        lineTo(width * 0.9f + panX, height)
        lineTo(width * 0.1f + panX, height)
        close()
    }
    drawPath(
        path = beamPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.25f),
                secondaryColor.copy(alpha = 0.08f),
                Color.Transparent
            )
        )
    )

    // 4. Stylized Midground Silhouettes (Mountains or Futuristic Skyline depending on style)
    val isCyberpunkOrSciFi = project.style.contains("Cyberpunk", ignoreCase = true) ||
            project.style.contains("Sci-Fi", ignoreCase = true)

    if (isCyberpunkOrSciFi) {
        // Draw futuristic city silhouettes
        drawCyberpunkSkyline(width, height, panX, panY, zoomScale, primaryColor, secondaryColor)
    } else {
        // Draw cinematic organic terrain / mountain layers
        drawCinematicMountains(width, height, panX, panY, zoomScale, primaryColor, secondaryColor, darkBaseColor)
    }

    // 5. Floating Particles / Bokeh / Dust Motes
    val particleCount = 28
    for (i in 0 until particleCount) {
        val speedFactor = 0.3f + (i % 5) * 0.15f
        val particleX = ((i * 73f + timeSec * 45f * speedFactor + panX) % width + width) % width
        val particleY = ((i * 127f - timeSec * 25f * speedFactor + panY) % height + height) % height
        val particleRadius = (2f + (i % 4) * 2f) * zoomScale
        val particleAlpha = 0.3f + 0.4f * sin((timeSec + i).toDouble()).toFloat().coerceIn(0f, 1f)

        drawCircle(
            color = if (i % 2 == 0) primaryColor.copy(alpha = particleAlpha) else Color.White.copy(alpha = particleAlpha),
            radius = particleRadius,
            center = Offset(particleX, particleY)
        )
    }

    // 6. Foreground Focal Framing Elements
    val foregroundPath = Path().apply {
        val groundY = height * 0.82f - panY * 0.5f
        moveTo(0f, groundY)
        cubicTo(
            width * 0.25f, groundY - 20f * zoomScale,
            width * 0.75f, groundY + 30f * zoomScale,
            width, groundY - 10f * zoomScale
        )
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(
        path = foregroundPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                secondaryColor.copy(alpha = 0.85f),
                darkBaseColor
            ),
            startY = height * 0.8f,
            endY = height
        )
    )

    // 7. Cinematic Lens Flare Horizontal Streak (Anamorphic 2.39 look)
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                primaryColor.copy(alpha = 0.6f),
                Color.White.copy(alpha = 0.9f),
                primaryColor.copy(alpha = 0.6f),
                Color.Transparent
            )
        ),
        start = Offset(0f, sunCenterY),
        end = Offset(width, sunCenterY),
        strokeWidth = 2.5f
    )
}

private fun DrawScope.drawCinematicMountains(
    width: Float,
    height: Float,
    panX: Float,
    panY: Float,
    zoomScale: Float,
    primaryColor: Color,
    secondaryColor: Color,
    darkBaseColor: Color
) {
    // Back Layer Mountains
    val backPath = Path().apply {
        moveTo(0f, height * 0.68f)
        lineTo(width * 0.2f + panX * 0.4f, height * 0.52f)
        lineTo(width * 0.45f + panX * 0.4f, height * 0.62f)
        lineTo(width * 0.7f + panX * 0.4f, height * 0.48f)
        lineTo(width, height * 0.65f)
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(
        path = backPath,
        color = secondaryColor.copy(alpha = 0.55f)
    )

    // Mid Layer Mountains
    val midPath = Path().apply {
        moveTo(0f, height * 0.75f)
        lineTo(width * 0.32f + panX * 0.7f, height * 0.58f)
        lineTo(width * 0.6f + panX * 0.7f, height * 0.72f)
        lineTo(width * 0.85f + panX * 0.7f, height * 0.56f)
        lineTo(width, height * 0.74f)
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(
        path = midPath,
        color = darkBaseColor.copy(alpha = 0.85f)
    )
}

private fun DrawScope.drawCyberpunkSkyline(
    width: Float,
    height: Float,
    panX: Float,
    panY: Float,
    zoomScale: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    val buildingCount = 14
    val buildingWidth = width / (buildingCount - 2)

    for (i in 0 until buildingCount) {
        val bHeight = ((i * 37) % 180 + 120) * zoomScale
        val bLeft = (i - 1) * buildingWidth + panX * 0.5f
        val bTop = height * 0.8f - bHeight

        // Building silhouette
        drawRect(
            color = Color(0xFF0A0F1D).copy(alpha = 0.95f),
            topLeft = Offset(bLeft, bTop),
            size = Size(buildingWidth * 0.9f, bHeight + 40f)
        )

        // Neon Edge Accent
        drawLine(
            color = if (i % 2 == 0) primaryColor else secondaryColor,
            start = Offset(bLeft, bTop),
            end = Offset(bLeft + buildingWidth * 0.9f, bTop),
            strokeWidth = 2.dp.toPx()
        )

        // Building Window Grid Glow
        val windowRows = 5
        val windowCols = 3
        for (r in 0 until windowRows) {
            for (c in 0 until windowCols) {
                if ((r + c + i) % 3 != 0) {
                    drawRect(
                        color = if ((r * c + i) % 2 == 0) primaryColor.copy(alpha = 0.45f) else CinemaAmber.copy(alpha = 0.4f),
                        topLeft = Offset(bLeft + 6f + c * 8f, bTop + 12f + r * 14f),
                        size = Size(4f, 6f)
                    )
                }
            }
        }
    }
}
