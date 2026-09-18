package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaPink
import com.example.ui.theme.CinemaPurple
import com.example.ui.theme.ThemeManager
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PulseCraftVectorBadge(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.46f

        // 1. Dark Curved Hexagon Base
        val hexPath = Path()
        val numSides = 6
        val angleStep = (2 * Math.PI / numSides).toFloat()
        val startAngle = (-Math.PI / 2.0).toFloat() // point at top

        for (i in 0 until numSides) {
            val angle = startAngle + i * angleStep
            val px = cx + r * cos(angle)
            val py = cy + r * sin(angle)
            if (i == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
        }
        hexPath.close()

        // Fill Hexagon
        drawPath(
            path = hexPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF030712))
            )
        )

        // Hexagon glowing cyan to neon-violet border
        drawPath(
            path = hexPath,
            brush = Brush.sweepGradient(
                colors = listOf(
                    ThemeManager.primaryAccent.copy(alpha = pulseGlow),
                    CinemaPurple,
                    CinemaPink,
                    ThemeManager.primaryAccent.copy(alpha = pulseGlow)
                ),
                center = Offset(cx, cy)
            ),
            style = Stroke(width = w * 0.045f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 2. Cinematic Film Reel Frame (Inner aperture)
        val reelW = w * 0.52f
        val reelH = h * 0.54f
        val reelLeft = cx - reelW / 2f
        val reelTop = cy - reelH / 2f
        val reelRadius = w * 0.08f

        val reelPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = reelLeft,
                    top = reelTop,
                    right = reelLeft + reelW,
                    bottom = reelTop + reelH,
                    cornerRadius = CornerRadius(reelRadius, reelRadius)
                )
            )
        }

        // Film Reel Body
        drawPath(
            path = reelPath,
            color = Color(0xFF130924)
        )
        drawPath(
            path = reelPath,
            color = CinemaPurple.copy(alpha = 0.8f),
            style = Stroke(width = w * 0.03f)
        )

        // Sprocket holes on left and right
        val sprocketW = w * 0.045f
        val sprocketH = h * 0.055f
        val sprocketSteps = 4
        for (i in 0 until sprocketSteps) {
            val spY = reelTop + (reelH / (sprocketSteps + 1)) * (i + 1) - sprocketH / 2f
            // Left sprocket
            drawRoundRect(
                color = ThemeManager.primaryAccent,
                topLeft = Offset(reelLeft + w * 0.02f, spY),
                size = androidx.compose.ui.geometry.Size(sprocketW, sprocketH),
                cornerRadius = CornerRadius(w * 0.01f, w * 0.01f)
            )
            // Right sprocket
            drawRoundRect(
                color = CinemaPink,
                topLeft = Offset(reelLeft + reelW - w * 0.02f - sprocketW, spY),
                size = androidx.compose.ui.geometry.Size(sprocketW, sprocketH),
                cornerRadius = CornerRadius(w * 0.01f, w * 0.01f)
            )
        }

        // 3. Dynamic Glowing Cyan & Neon-Violet Soundwave Intersecting the Reel
        val wavePath = Path().apply {
            moveTo(w * 0.12f, cy)
            quadraticTo(w * 0.25f, cy - h * 0.16f, w * 0.35f, cy + h * 0.18f)
            quadraticTo(w * 0.44f, cy - h * 0.32f, w * 0.54f, cy + h * 0.35f)
            quadraticTo(w * 0.65f, cy - h * 0.22f, w * 0.76f, cy + h * 0.14f)
            quadraticTo(w * 0.84f, cy - h * 0.06f, w * 0.88f, cy)
        }

        // Soft outer soundwave glow
        drawPath(
            path = wavePath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    ThemeManager.primaryAccent.copy(alpha = 0.35f),
                    CinemaPurple.copy(alpha = 0.4f),
                    CinemaPink.copy(alpha = 0.35f)
                )
            ),
            style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Sharp vibrant soundwave
        drawPath(
            path = wavePath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    ThemeManager.primaryAccent,
                    CinemaPurple,
                    CinemaPink
                )
            ),
            style = Stroke(width = w * 0.05f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Transient Crest Accent Dots
        drawCircle(
            color = ThemeManager.primaryAccent,
            radius = w * 0.038f,
            center = Offset(w * 0.44f, cy - h * 0.28f)
        )
        drawCircle(
            color = CinemaPink,
            radius = w * 0.038f,
            center = Offset(w * 0.54f, cy + h * 0.30f)
        )
    }
}

@Composable
fun PulseCraftHeader(
    modifier: Modifier = Modifier,
    subtitle: String = "Creative Motion & Audio Suite",
    actions: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PulseCraftVectorBadge(size = 38.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Pulse",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ThemeManager.textPrimaryColor
                    )
                    Text(
                        text = "Craft",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ThemeManager.primaryAccent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "STUDIO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = CinemaPurple
                    )
                }
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = ThemeManager.textMutedColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        actions()
    }
}
