package com.example.ui.scene

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawDryDesertToGardenScene(
    progress: Float,
    ambientPhase: Float,
    beadPulseAlpha: Float
) {
    val w = size.width
    val h = size.height

    // 1. Arid Twilight Sky -> Clear Turquoise Oasis Morning
    val skyTop = multiColorLerp(
        listOf(Color(0xFF1F1614), Color(0xFF3E322D), Color(0xFF42848C), Color(0xFF68B6C9)),
        progress
    )
    val skyHorizon = multiColorLerp(
        listOf(Color(0xFF473124), Color(0xFF8C6245), Color(0xFFE6C892), Color(0xFFF6E7B9)),
        progress
    )
    drawRect(brush = Brush.verticalGradient(listOf(skyTop, skyHorizon)), size = size)

    // Warm sun glow in stages 5-10
    val sunAlpha = smoothStep(0.45f, 0.95f, progress)
    if (sunAlpha > 0.02f) {
        val sunCenter = Offset(w * 0.25f, lerpFloat(h * 0.44f, h * 0.24f, progress))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF4C4).copy(alpha = 0.72f * sunAlpha),
                    Color(0xFFF2C879).copy(alpha = 0.25f * sunAlpha),
                    Color.Transparent
                ),
                center = sunCenter,
                radius = w * 0.36f
            ),
            radius = w * 0.36f,
            center = sunCenter
        )
    }

    // 2. Barren Dunes Transforming into Lush Terraced Meadows
    val backDuneColor = multiColorLerp(
        listOf(Color(0xFF4A3324), Color(0xFF6B533B), Color(0xFF4E7F54), Color(0xFF4B945A)),
        progress
    )
    val midDuneColor = multiColorLerp(
        listOf(Color(0xFF5B3F2C), Color(0xFF755D41), Color(0xFF42734B), Color(0xFF3C824B)),
        progress
    )
    val foregroundColor = multiColorLerp(
        listOf(Color(0xFF3B281B), Color(0xFF524432), Color(0xFF2D5B38), Color(0xFF2E6B3E)),
        progress
    )

    val backDune = Path().apply {
        moveTo(0f, h * 0.62f)
        quadraticTo(w * 0.32f, h * 0.52f, w * 0.64f, h * 0.60f)
        quadraticTo(w * 0.85f, h * 0.65f, w, h * 0.57f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(backDune, backDuneColor)

    val midDune = Path().apply {
        moveTo(0f, h * 0.69f)
        quadraticTo(w * 0.28f, h * 0.74f, w * 0.56f, h * 0.65f)
        quadraticTo(w * 0.82f, h * 0.59f, w, h * 0.68f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(midDune, midDuneColor)

    drawRect(
        color = foregroundColor,
        topLeft = Offset(0f, h * 0.76f),
        size = Size(w, h * 0.24f)
    )

    // 3. Flowing Oasis Water Stream (Stages 2-10)
    val streamProgress = smoothStep(0.18f, 0.72f, progress)
    if (streamProgress > 0.02f) {
        val streamWidthFactor = lerpFloat(0.25f, 1.0f, smoothStep(0.2f, 0.9f, progress))
        val streamPath = Path().apply {
            moveTo(w * 0.56f, h * 0.66f)
            cubicTo(
                w * 0.38f, h * 0.73f,
                w * 0.68f, h * 0.82f,
                w * 0.42f, h
            )
            lineTo(w * (0.42f + 0.26f * streamWidthFactor), h)
            cubicTo(
                w * (0.68f + 0.14f * streamWidthFactor), h * 0.82f,
                w * (0.44f + 0.06f * streamWidthFactor), h * 0.73f,
                w * 0.58f, h * 0.66f
            )
            close()
        }
        drawPath(
            path = streamPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4CA5B3).copy(alpha = 0.88f * streamProgress),
                    Color(0xFF7FD6DB).copy(alpha = 0.92f * streamProgress)
                ),
                startY = h * 0.66f,
                endY = h
            )
        )

        // Animated water shimmer ripples
        for (i in 0..5) {
            val rippleT = ((ambientPhase * 1.2f + i * 0.18f) % 1f)
            val ry = lerpFloat(h * 0.71f, h * 0.96f, rippleT)
            val rx = w * (0.50f + sin(rippleT * 5f) * 0.05f)
            val rw = w * (0.04f + rippleT * 0.07f) * streamWidthFactor
            drawLine(
                color = Color.White.copy(alpha = (0.42f * streamProgress * (1f - rippleT)).coerceIn(0f, 0.5f)),
                start = Offset(rx - rw, ry),
                end = Offset(rx + rw, ry),
                strokeWidth = 2.6f,
                cap = StrokeCap.Round
            )
        }
    }

    // 4. Oasis Palm & Garden Trees Growing (Stages 4-10)
    val treeGrowth = smoothStep(0.38f, 0.88f, progress)
    if (treeGrowth > 0.02f) {
        val sway = sin(ambientPhase * 2f * PI).toFloat() * w * 0.008f
        drawPalmTree(
            base = Offset(w * 0.20f, h * 0.77f),
            top = Offset(w * 0.16f + sway, lerpFloat(h * 0.76f, h * 0.50f, treeGrowth)),
            scale = treeGrowth,
            alpha = treeGrowth
        )
        drawPalmTree(
            base = Offset(w * 0.82f, h * 0.76f),
            top = Offset(w * 0.86f + sway, lerpFloat(h * 0.75f, h * 0.47f, treeGrowth)),
            scale = treeGrowth * 1.05f,
            alpha = treeGrowth
        )
    }

    // 5. Lush Shrubs & Wildflowers Along Stream Banks (Stages 6-10)
    val gardenBloom = smoothStep(0.58f, 0.96f, progress)
    if (gardenBloom > 0.02f) {
        val shrubSpots = listOf(
            Offset(0.10f, 0.82f), Offset(0.28f, 0.79f), Offset(0.34f, 0.86f),
            Offset(0.73f, 0.83f), Offset(0.88f, 0.81f), Offset(0.79f, 0.90f)
        )
        shrubSpots.forEachIndexed { i, norm ->
            val c = Offset(w * norm.x, h * norm.y)
            val r = w * 0.055f * gardenBloom
            drawCircle(color = Color(0xFF3E8E52).copy(alpha = 0.9f * gardenBloom), radius = r, center = c)
            val flowerColor = if (i % 2 == 0) Color(0xFFF6C2C7) else Color(0xFFF7DC6F)
            drawCircle(
                color = flowerColor.copy(alpha = gardenBloom),
                radius = r * 0.24f,
                center = Offset(c.x - r * 0.25f, c.y - r * 0.25f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.85f * gardenBloom),
                radius = r * 0.18f,
                center = Offset(c.x + r * 0.28f, c.y - r * 0.12f)
            )
        }
    }

    drawAmbientMotes(
        progress = progress,
        ambientPhase = ambientPhase,
        beadPulseAlpha = beadPulseAlpha,
        color = Color(0xFFC8F7DC)
    )
}

private fun DrawScope.drawPalmTree(
    base: Offset,
    top: Offset,
    scale: Float,
    alpha: Float
) {
    val trunkPath = Path().apply {
        moveTo(base.x, base.y)
        quadraticTo(
            (base.x + top.x) * 0.5f + size.width * 0.02f,
            (base.y + top.y) * 0.5f,
            top.x,
            top.y
        )
    }
    drawPath(
        path = trunkPath,
        color = Color(0xFF6B4D36).copy(alpha = alpha),
        style = Stroke(width = size.width * 0.022f * scale.coerceAtLeast(0.4f), cap = StrokeCap.Round)
    )

    val frondCount = 6
    val frondLen = size.width * 0.14f * scale
    for (i in 0 until frondCount) {
        val angle = (-0.85f + i * 0.34f) * PI.toFloat() - (PI.toFloat() / 2f)
        val tip = Offset(
            top.x + cos(angle.toDouble()).toFloat() * frondLen,
            top.y + sin(angle.toDouble()).toFloat() * frondLen * 0.7f + frondLen * 0.22f
        )
        val frondPath = Path().apply {
            moveTo(top.x, top.y)
            quadraticTo(
                (top.x + tip.x) * 0.5f,
                top.y - frondLen * 0.28f,
                tip.x,
                tip.y
            )
        }
        drawPath(
            path = frondPath,
            color = lerp(Color(0xFF3A7B48), Color(0xFF67B36D), (i % 2) * 0.5f).copy(alpha = alpha),
            style = Stroke(width = size.width * 0.015f * scale.coerceAtLeast(0.3f), cap = StrokeCap.Round)
        )
    }
}
