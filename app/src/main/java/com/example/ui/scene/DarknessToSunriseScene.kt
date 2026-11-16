package com.example.ui.scene

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawDarknessToSunriseScene(
    progress: Float,
    ambientPhase: Float,
    beadPulseAlpha: Float
) {
    val w = size.width
    val h = size.height

    // 1. Progressive Night -> Pre-dawn -> Sunrise Sky
    val skyTop = multiColorLerp(
        listOf(Color(0xFF060913), Color(0xFF192244), Color(0xFF474E7D), Color(0xFF5D9FC7)),
        progress
    )
    val skyMid = multiColorLerp(
        listOf(Color(0xFF0E1629), Color(0xFF333663), Color(0xFF9F6B7E), Color(0xFF9BD4DF)),
        progress
    )
    val skyHorizon = multiColorLerp(
        listOf(Color(0xFF172034), Color(0xFF53405E), Color(0xFFE79862), Color(0xFFFCE29C)),
        progress
    )

    drawRect(
        brush = Brush.verticalGradient(listOf(skyTop, skyMid, skyHorizon)),
        size = size
    )

    // 2. Twinkling Stars Fading with Dawn (Stages 0-5)
    val starAlpha = 1f - smoothStep(0.12f, 0.62f, progress)
    if (starAlpha > 0.01f) {
        for (i in 0 until 32) {
            val sx = w * (((i * 0.618f) % 1f))
            val sy = h * (0.06f + ((i * 0.37f) % 0.45f))
            val twinkle = 0.55f + 0.45f * sin((ambientPhase * 2f * PI + i).toDouble()).toFloat()
            drawCircle(
                color = Color(0xFFF6F5E8).copy(alpha = (starAlpha * twinkle).coerceIn(0f, 1f)),
                radius = (1.5f + (i % 3) * 0.9f),
                center = Offset(sx, sy)
            )
        }
    }

    // 3. Rising Golden Sun (Stages 3.5-10)
    val sunProgress = smoothStep(0.32f, 0.96f, progress)
    val sunY = lerpFloat(h * 0.72f, h * 0.28f, sunProgress)
    val sunCenter = Offset(w * 0.5f, sunY)
    val sunVisibility = smoothStep(0.25f, 0.75f, progress)

    if (sunVisibility > 0.01f) {
        // Outer sunrise halo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF5C7).copy(alpha = 0.75f * sunVisibility),
                    Color(0xFFF7AE59).copy(alpha = 0.35f * sunVisibility),
                    Color.Transparent
                ),
                center = sunCenter,
                radius = w * 0.5f
            ),
            radius = w * 0.5f,
            center = sunCenter
        )
        drawSunRays(
            origin = sunCenter,
            rayColor = Color(0xFFFFE599),
            alpha = sunVisibility * 0.85f,
            ambientPhase = ambientPhase,
            rayCount = 7
        )
        // Sun disc
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFBE6), Color(0xFFFAD06B)),
                center = sunCenter,
                radius = w * 0.11f
            ),
            radius = w * 0.105f,
            center = sunCenter
        )
    }

    // 4. Heavy Clouds Gradually Parting & Brightening (Stages 0-8)
    val cloudDensity = 1f - smoothStep(0.15f, 0.88f, progress)
    if (cloudDensity > 0.02f) {
        val cloudColor = multiColorLerp(
            listOf(Color(0xFF1D2436), Color(0xFF3A3F59), Color(0xFFD5A69E), Color(0xFFFFF3E0)),
            progress
        ).copy(alpha = (0.75f * cloudDensity).coerceIn(0f, 0.8f))

        val partOffset = w * 0.35f * smoothStep(0.1f, 0.9f, progress)
        val drift = sin(ambientPhase * 2f * PI).toFloat() * w * 0.018f

        // Left cloud bank
        drawOval(cloudColor, Offset(-w * 0.1f - partOffset + drift, h * 0.22f), Size(w * 0.65f, h * 0.12f))
        drawOval(cloudColor, Offset(w * 0.05f - partOffset + drift, h * 0.18f), Size(w * 0.45f, h * 0.10f))
        // Right cloud bank
        drawOval(cloudColor, Offset(w * 0.45f + partOffset - drift, h * 0.26f), Size(w * 0.65f, h * 0.12f))
        drawOval(cloudColor, Offset(w * 0.52f + partOffset - drift, h * 0.21f), Size(w * 0.48f, h * 0.095f))
    }

    // 5. Layered Mountain Silhouettes
    val farMountainColor = multiColorLerp(
        listOf(Color(0xFF121828), Color(0xFF282E49), Color(0xFF535474), Color(0xFF6A8EA2)),
        progress
    )
    val nearMountainColor = multiColorLerp(
        listOf(Color(0xFF0A0F1A), Color(0xFF1B2335), Color(0xFF2C424D), Color(0xFF2B5C54)),
        progress
    )

    val farMountain = Path().apply {
        moveTo(0f, h * 0.68f)
        lineTo(w * 0.22f, h * 0.56f)
        lineTo(w * 0.44f, h * 0.64f)
        lineTo(w * 0.68f, h * 0.53f)
        lineTo(w, h * 0.65f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(farMountain, farMountainColor)

    val nearMountain = Path().apply {
        moveTo(0f, h * 0.72f)
        lineTo(w * 0.18f, h * 0.63f)
        lineTo(w * 0.40f, h * 0.71f)
        lineTo(w * 0.72f, h * 0.61f)
        lineTo(w, h * 0.70f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(nearMountain, nearMountainColor)

    // 6. Calm Reflective Lake at Base
    val lakeTop = multiColorLerp(
        listOf(Color(0xFF0B1220), Color(0xFF242D45), Color(0xFF7B697A), Color(0xFF83BAC4)),
        progress
    )
    val lakeBottom = multiColorLerp(
        listOf(Color(0xFF070B14), Color(0xFF192236), Color(0xFF474961), Color(0xFF4A808D)),
        progress
    )
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(lakeTop, lakeBottom),
            startY = h * 0.76f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.76f),
        size = Size(w, h * 0.24f)
    )

    // Sun shimmer reflection on water
    if (sunVisibility > 0.05f) {
        for (i in 0 until 7) {
            val ry = h * (0.79f + i * 0.025f)
            val rw = w * (0.28f - i * 0.025f)
            val shift = sin((ambientPhase * 2f * PI + i).toDouble()).toFloat() * w * 0.015f
            drawLine(
                color = Color(0xFFFFE59E).copy(alpha = (0.45f * sunVisibility * (1f - i * 0.1f)).coerceIn(0f, 0.55f)),
                start = Offset(w * 0.5f - rw * 0.5f + shift, ry),
                end = Offset(w * 0.5f + rw * 0.5f + shift, ry),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
        }
    }

    // 7. Gentle Soaring Birds (Stages 7-10)
    val birdsAlpha = smoothStep(0.68f, 0.96f, progress)
    if (birdsAlpha > 0.02f) {
        val birdOffsets = listOf(
            Offset(0.28f, 0.34f),
            Offset(0.35f, 0.31f),
            Offset(0.67f, 0.29f),
            Offset(0.73f, 0.33f)
        )
        birdOffsets.forEachIndexed { idx, norm ->
            val bx = w * norm.x + sin((ambientPhase * 2f * PI + idx).toDouble()).toFloat() * w * 0.012f
            val by = h * norm.y + cos((ambientPhase * 2f * PI + idx).toDouble()).toFloat() * h * 0.008f
            val bw = w * 0.022f
            val birdPath = Path().apply {
                moveTo(bx - bw, by + bw * 0.25f)
                quadraticTo(bx - bw * 0.45f, by - bw * 0.35f, bx, by)
                quadraticTo(bx + bw * 0.45f, by - bw * 0.35f, bx + bw, by + bw * 0.25f)
            }
            drawPath(
                path = birdPath,
                color = Color(0xFF283546).copy(alpha = 0.7f * birdsAlpha),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }
    }

    drawAmbientMotes(
        progress = progress,
        ambientPhase = ambientPhase,
        beadPulseAlpha = beadPulseAlpha,
        color = Color(0xFFFFF3C4)
    )
}
