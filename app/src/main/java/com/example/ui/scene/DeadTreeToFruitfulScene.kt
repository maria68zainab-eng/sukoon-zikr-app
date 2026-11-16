package com.example.ui.scene

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawDeadTreeToFruitfulScene(
    progress: Float,
    ambientPhase: Float,
    beadPulseAlpha: Float
) {
    val w = size.width
    val h = size.height

    // 1. Progressive Sky Gradient
    val skyTop = multiColorLerp(
        listOf(Color(0xFF0E131B), Color(0xFF1F333B), Color(0xFF427A76), Color(0xFF7BC2AE)),
        progress
    )
    val skyMid = multiColorLerp(
        listOf(Color(0xFF1B222C), Color(0xFF354D4D), Color(0xFF78A88D), Color(0xFFCDE6BA)),
        progress
    )
    val skyHorizon = multiColorLerp(
        listOf(Color(0xFF282B30), Color(0xFF565D4F), Color(0xFFD8B878), Color(0xFFF9E5A7)),
        progress
    )

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(skyTop, skyMid, skyHorizon)
        ),
        size = size
    )

    // 2. Soft Dawn Sun Glow (Stages 4-10)
    val sunAlpha = smoothStep(0.35f, 0.95f, progress)
    val sunCenter = Offset(w * 0.78f, lerpFloat(h * 0.42f, h * 0.22f, progress))
    if (sunAlpha > 0.01f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF2C2).copy(alpha = 0.65f * sunAlpha),
                    Color(0xFFF3C66E).copy(alpha = 0.22f * sunAlpha),
                    Color.Transparent
                ),
                center = sunCenter,
                radius = w * 0.42f
            ),
            radius = w * 0.42f,
            center = sunCenter
        )
        drawSunRays(
            origin = sunCenter,
            rayColor = Color(0xFFFFF0BD),
            alpha = sunAlpha * 0.75f,
            ambientPhase = ambientPhase
        )
    }

    // 3. Distant Hills & Lush Meadow Ground
    val backHillColor = multiColorLerp(
        listOf(Color(0xFF1B1E20), Color(0xFF2B3831), Color(0xFF3B6649), Color(0xFF4D8A58)),
        progress
    )
    val frontHillColor = multiColorLerp(
        listOf(Color(0xFF211C18), Color(0xFF2E3325), Color(0xFF2D593A), Color(0xFF3B7448)),
        progress
    )

    val backHill = Path().apply {
        moveTo(0f, h * 0.74f)
        quadraticTo(w * 0.28f, h * 0.64f, w * 0.58f, h * 0.71f)
        quadraticTo(w * 0.82f, h * 0.76f, w, h * 0.67f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(backHill, color = backHillColor)

    val frontGround = Path().apply {
        moveTo(0f, h * 0.79f)
        quadraticTo(w * 0.42f, h * 0.72f, w * 0.76f, h * 0.78f)
        quadraticTo(w * 0.9f, h * 0.81f, w, h * 0.77f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(frontGround, color = frontHillColor)

    // Quiet garden pond reflection appearing in stages 6-10
    val pondAlpha = smoothStep(0.55f, 0.95f, progress)
    if (pondAlpha > 0.01f) {
        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF4D8B94).copy(alpha = 0.5f * pondAlpha),
                    Color(0xFFAAD8C8).copy(alpha = 0.65f * pondAlpha),
                    Color(0xFF4D8B94).copy(alpha = 0.45f * pondAlpha)
                )
            ),
            topLeft = Offset(w * 0.56f, h * 0.84f),
            size = Size(w * 0.36f, h * 0.065f)
        )
    }

    // 4. Tree Trunk & Branches (Organic bark warms as life returns)
    val barkColor = lerp(Color(0xFF2B2320), Color(0xFF563C2B), smoothStep(0.1f, 0.75f, progress))
    val sway = sin(ambientPhase * 2f * PI).toFloat() * w * 0.004f * smoothStep(0.2f, 1f, progress)

    val base = Offset(w * 0.46f, h * 0.82f)
    val forkMain = Offset(w * 0.47f + sway * 0.3f, h * 0.56f)

    // Main trunk
    drawLine(
        color = barkColor,
        start = base,
        end = forkMain,
        strokeWidth = w * 0.052f,
        cap = StrokeCap.Round
    )
    // Root flares
    drawLine(barkColor, Offset(w * 0.44f, h * 0.81f), Offset(w * 0.37f, h * 0.84f), strokeWidth = w * 0.024f, cap = StrokeCap.Round)
    drawLine(barkColor, Offset(w * 0.48f, h * 0.81f), Offset(w * 0.56f, h * 0.84f), strokeWidth = w * 0.024f, cap = StrokeCap.Round)

    data class Branch(val start: Offset, val end: Offset, val thickness: Float)
    val primaryBranches = listOf(
        Branch(forkMain, Offset(w * 0.28f + sway, h * 0.44f), w * 0.028f),
        Branch(forkMain, Offset(w * 0.48f + sway, h * 0.36f), w * 0.030f),
        Branch(forkMain, Offset(w * 0.67f + sway, h * 0.43f), w * 0.026f),
        Branch(Offset(w * 0.36f + sway * 0.6f, h * 0.49f), Offset(w * 0.21f + sway, h * 0.35f), w * 0.017f),
        Branch(Offset(w * 0.37f + sway * 0.6f, h * 0.48f), Offset(w * 0.37f + sway, h * 0.30f), w * 0.016f),
        Branch(Offset(w * 0.47f + sway * 0.7f, h * 0.42f), Offset(w * 0.45f + sway, h * 0.24f), w * 0.017f),
        Branch(Offset(w * 0.48f + sway * 0.7f, h * 0.40f), Offset(w * 0.58f + sway, h * 0.27f), w * 0.016f),
        Branch(Offset(w * 0.59f + sway * 0.6f, h * 0.47f), Offset(w * 0.74f + sway, h * 0.33f), w * 0.016f),
        Branch(Offset(w * 0.61f + sway * 0.6f, h * 0.48f), Offset(w * 0.78f + sway, h * 0.45f), w * 0.014f),
        Branch(Offset(w * 0.29f + sway * 0.8f, h * 0.42f), Offset(w * 0.17f + sway, h * 0.44f), w * 0.013f)
    )

    primaryBranches.forEach { b ->
        drawLine(barkColor, b.start, b.end, strokeWidth = b.thickness, cap = StrokeCap.Round)
    }

    // 5. Progressive Green Leaves (Stages 1.5 -> 6)
    val leafCenters = listOf(
        Offset(0.20f, 0.35f), Offset(0.28f, 0.31f), Offset(0.36f, 0.28f),
        Offset(0.45f, 0.23f), Offset(0.55f, 0.25f), Offset(0.64f, 0.30f),
        Offset(0.73f, 0.33f), Offset(0.79f, 0.42f), Offset(0.17f, 0.43f),
        Offset(0.26f, 0.40f), Offset(0.34f, 0.37f), Offset(0.42f, 0.32f),
        Offset(0.51f, 0.31f), Offset(0.60f, 0.36f), Offset(0.69f, 0.39f),
        Offset(0.39f, 0.41f), Offset(0.49f, 0.38f), Offset(0.56f, 0.42f),
        Offset(0.24f, 0.46f), Offset(0.73f, 0.47f), Offset(0.47f, 0.27f)
    )

    val foliagePresence = smoothStep(0.12f, 0.65f, progress)
    if (foliagePresence > 0.01f) {
        leafCenters.forEachIndexed { idx, norm ->
            val threshold = 0.10f + (idx % 7) * 0.065f
            val localGrowth = smoothStep(threshold, threshold + 0.22f, progress)
            if (localGrowth > 0.01f) {
                val center = Offset(w * norm.x + sway, h * norm.y)
                val baseRadius = w * (0.07f + (idx % 3) * 0.02f) * localGrowth
                val leafColor = lerp(
                    Color(0xFF47784C),
                    if (idx % 2 == 0) Color(0xFF63A864) else Color(0xFF4E9055),
                    smoothStep(0.3f, 0.9f, progress)
                )
                drawCircle(
                    color = leafColor.copy(alpha = (0.88f * localGrowth).coerceIn(0f, 0.92f)),
                    radius = baseRadius,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF8FD082).copy(alpha = (0.35f * localGrowth).coerceIn(0f, 0.45f)),
                    radius = baseRadius * 0.62f,
                    center = Offset(center.x - baseRadius * 0.2f, center.y - baseRadius * 0.22f)
                )
            }
        }
    }

    // 6. Delicate Blossoms (Stages 5-8)
    val blossomPresence = smoothStep(0.44f, 0.78f, progress)
    if (blossomPresence > 0.01f) {
        val blossomSpots = listOf(
            Offset(0.24f, 0.37f), Offset(0.35f, 0.30f), Offset(0.47f, 0.25f),
            Offset(0.58f, 0.30f), Offset(0.70f, 0.36f), Offset(0.31f, 0.41f),
            Offset(0.52f, 0.36f), Offset(0.64f, 0.41f), Offset(0.20f, 0.44f),
            Offset(0.76f, 0.44f)
        )
        blossomSpots.forEachIndexed { i, norm ->
            val local = smoothStep(0.42f + (i % 4) * 0.06f, 0.72f + (i % 4) * 0.05f, progress)
            if (local > 0.01f) {
                val c = Offset(w * norm.x + sway, h * norm.y)
                val r = w * 0.016f * local
                for (p in 0 until 5) {
                    val ang = p * (2f * PI.toFloat() / 5f)
                    drawCircle(
                        color = Color(0xFFFFEBF0).copy(alpha = 0.9f * local),
                        radius = r * 0.65f,
                        center = Offset(c.x + cos(ang.toDouble()).toFloat() * r, c.y + sin(ang.toDouble()).toFloat() * r)
                    )
                }
                drawCircle(color = Color(0xFFF6C85F).copy(alpha = local), radius = r * 0.55f, center = c)
            }
        }
    }

    // 7. Abundant Ripe Fruits (Stages 7-10)
    val fruitPresence = smoothStep(0.66f, 0.96f, progress)
    if (fruitPresence > 0.01f) {
        val fruitSpots = listOf(
            Offset(0.29f, 0.35f), Offset(0.41f, 0.29f), Offset(0.53f, 0.27f),
            Offset(0.65f, 0.35f), Offset(0.73f, 0.40f), Offset(0.24f, 0.42f),
            Offset(0.46f, 0.36f), Offset(0.59f, 0.40f), Offset(0.36f, 0.43f)
        )
        fruitSpots.forEachIndexed { i, norm ->
            val local = smoothStep(0.66f + (i % 3) * 0.07f, 0.95f, progress)
            if (local > 0.01f) {
                val c = Offset(w * norm.x + sway, h * norm.y)
                val r = w * 0.020f * local
                val fruitColor = if (i % 2 == 0) Color(0xFFE06D53) else Color(0xFFEBB44D)
                drawCircle(
                    color = fruitColor.copy(alpha = 0.95f * local),
                    radius = r,
                    center = c
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f * local),
                    radius = r * 0.32f,
                    center = Offset(c.x - r * 0.28f, c.y - r * 0.28f)
                )
            }
        }
    }

    // 8. Ground Meadow Wildflowers (Stages 6-10)
    val meadowFlowers = smoothStep(0.55f, 0.95f, progress)
    if (meadowFlowers > 0.01f) {
        for (i in 0..14) {
            val fx = w * (0.06f + i * 0.062f)
            val fy = h * (0.81f + (i % 3) * 0.035f)
            if (fx in (w * 0.56f)..(w * 0.92f) && fy > h * 0.83f) continue
            val flowerColor = when (i % 3) {
                0 -> Color(0xFFF7D560)
                1 -> Color(0xFFF6C2C2)
                else -> Color(0xFFFFFFFF)
            }
            drawCircle(
                color = flowerColor.copy(alpha = 0.85f * meadowFlowers),
                radius = w * 0.008f * meadowFlowers,
                center = Offset(fx, fy)
            )
        }
    }

    // 9. Ambient Floating Sun Motes
    drawAmbientMotes(
        progress = progress,
        ambientPhase = ambientPhase,
        beadPulseAlpha = beadPulseAlpha,
        color = Color(0xFFFFEFA8)
    )
}
