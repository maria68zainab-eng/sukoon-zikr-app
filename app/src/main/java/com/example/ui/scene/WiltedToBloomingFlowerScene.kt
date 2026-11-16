package com.example.ui.scene

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawWiltedToBloomingFlowerScene(
    progress: Float,
    ambientPhase: Float,
    beadPulseAlpha: Float
) {
    val w = size.width
    val h = size.height

    // 1. Atmosphere: Muted Cool Dusk -> Soft Dawn Meadow Light
    val bgTop = multiColorLerp(
        listOf(Color(0xFF181621), Color(0xFF282B3A), Color(0xFF3E5F58), Color(0xFF72A78F)),
        progress
    )
    val bgMid = multiColorLerp(
        listOf(Color(0xFF231F2C), Color(0xFF363C4A), Color(0xFF6B8F78), Color(0xFFC5DEC0)),
        progress
    )
    val bgBottom = multiColorLerp(
        listOf(Color(0xFF1A171B), Color(0xFF292E2B), Color(0xFF425E48), Color(0xFF4C7C53)),
        progress
    )

    drawRect(
        brush = Brush.verticalGradient(listOf(bgTop, bgMid, bgBottom)),
        size = size
    )

    // 2. Radiant Warm Glow Behind Blossom (Stages 5-10)
    val glowAlpha = smoothStep(0.45f, 0.95f, progress)
    val blossomOrigin = Offset(
        lerpFloat(w * 0.62f, w * 0.50f, smoothStep(0.15f, 0.72f, progress)),
        lerpFloat(h * 0.58f, h * 0.42f, smoothStep(0.15f, 0.72f, progress))
    )
    if (glowAlpha > 0.01f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF0C9).copy(alpha = 0.65f * glowAlpha),
                    Color(0xFFF6B5B0).copy(alpha = 0.25f * glowAlpha),
                    Color.Transparent
                ),
                center = blossomOrigin,
                radius = w * 0.45f
            ),
            radius = w * 0.45f,
            center = blossomOrigin
        )
    }

    // 3. Soft Meadow Horizon & Earth Mound
    val earthColor = lerp(Color(0xFF251E1B), Color(0xFF355939), smoothStep(0.15f, 0.85f, progress))
    val meadowPath = Path().apply {
        moveTo(0f, h * 0.80f)
        quadraticTo(w * 0.5f, h * 0.74f, w, h * 0.80f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(meadowPath, earthColor)

    // 4. Straightening Stem (Cubic Bezier from bowed/drooping to upright)
    val straighten = smoothStep(0.12f, 0.72f, progress)
    val stemBase = Offset(w * 0.49f, h * 0.84f)
    val control1 = Offset(lerpFloat(w * 0.48f, w * 0.49f, straighten), h * 0.66f)
    val control2 = Offset(lerpFloat(w * 0.66f, w * 0.51f, straighten), lerpFloat(h * 0.48f, h * 0.52f, straighten))
    val stemColor = lerp(Color(0xFF565745), Color(0xFF4F945B), smoothStep(0.1f, 0.6f, progress))

    val stemPath = Path().apply {
        moveTo(stemBase.x, stemBase.y)
        cubicTo(
            control1.x, control1.y,
            control2.x, control2.y,
            blossomOrigin.x, blossomOrigin.y
        )
    }
    drawPath(
        path = stemPath,
        color = stemColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = w * 0.022f,
            cap = StrokeCap.Round
        )
    )

    // 5. Unfurling Leaves Along the Stem (Stages 2.5-7)
    val leafGrowth = smoothStep(0.22f, 0.65f, progress)
    if (leafGrowth > 0.02f) {
        val leftLeafAnchor = Offset(w * 0.495f, h * 0.69f)
        val rightLeafAnchor = Offset(lerpFloat(w * 0.53f, w * 0.505f, straighten), h * 0.62f)
        val leafLen = w * 0.16f * leafGrowth
        val leafColor = lerp(Color(0xFF4C6345), Color(0xFF57A362), leafGrowth)

        // Left leaf
        val leftLeaf = Path().apply {
            moveTo(leftLeafAnchor.x, leftLeafAnchor.y)
            quadraticTo(
                leftLeafAnchor.x - leafLen * 0.7f,
                leftLeafAnchor.y - leafLen * lerpFloat(0.05f, 0.55f, leafGrowth),
                leftLeafAnchor.x - leafLen,
                leftLeafAnchor.y - leafLen * lerpFloat(-0.25f, 0.35f, leafGrowth)
            )
            quadraticTo(
                leftLeafAnchor.x - leafLen * 0.45f,
                leftLeafAnchor.y + leafLen * 0.25f,
                leftLeafAnchor.x,
                leftLeafAnchor.y
            )
            close()
        }
        drawPath(leftLeaf, leafColor)

        // Right leaf
        val rightLeaf = Path().apply {
            moveTo(rightLeafAnchor.x, rightLeafAnchor.y)
            quadraticTo(
                rightLeafAnchor.x + leafLen * 0.7f,
                rightLeafAnchor.y - leafLen * lerpFloat(0.05f, 0.55f, leafGrowth),
                rightLeafAnchor.x + leafLen,
                rightLeafAnchor.y - leafLen * lerpFloat(-0.25f, 0.35f, leafGrowth)
            )
            quadraticTo(
                rightLeafAnchor.x + leafLen * 0.45f,
                rightLeafAnchor.y + leafLen * 0.25f,
                rightLeafAnchor.x,
                rightLeafAnchor.y
            )
            close()
        }
        drawPath(rightLeaf, leafColor)
    }

    // 6. Multi-Layered Wilted Bud -> Radiant Blooming Flower
    val bloomOpen = smoothStep(0.36f, 0.96f, progress)
    val tiltDegrees = lerpFloat(48f, 0f, smoothStep(0.15f, 0.72f, progress))
    val gentleSway = sin(ambientPhase * 2f * PI).toFloat() * 2.5f * bloomOpen

    rotate(degrees = tiltDegrees + gentleSway, pivot = blossomOrigin) {
        val petalScale = lerpFloat(0.42f, 1.0f, smoothStep(0.18f, 0.92f, progress))
        val outerPetalColor = lerp(Color(0xFF6B535B), Color(0xFFE7898F), smoothStep(0.2f, 0.85f, progress))
        val innerPetalColor = lerp(Color(0xFF7D626A), Color(0xFFFAD0CE), smoothStep(0.35f, 0.95f, progress))

        // Outer petals (open wide as bloomOpen increases)
        val outerCount = 8
        for (i in 0 until outerCount) {
            val spreadAngle = (i - (outerCount - 1) / 2f) * lerpFloat(5f, 26f, bloomOpen)
            rotate(degrees = spreadAngle, pivot = blossomOrigin) {
                val pW = w * 0.078f * petalScale
                val pH = w * 0.19f * petalScale
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(innerPetalColor, outerPetalColor),
                        startY = blossomOrigin.y - pH,
                        endY = blossomOrigin.y
                    ),
                    topLeft = Offset(blossomOrigin.x - pW / 2f, blossomOrigin.y - pH),
                    size = Size(pW, pH)
                )
            }
        }

        // Inner petals (stages 6-10)
        val innerVisibility = smoothStep(0.45f, 0.9f, progress)
        if (innerVisibility > 0.02f) {
            val innerCount = 5
            for (i in 0 until innerCount) {
                val spreadAngle = (i - (innerCount - 1) / 2f) * lerpFloat(4f, 18f, bloomOpen)
                rotate(degrees = spreadAngle, pivot = blossomOrigin) {
                    val pW = w * 0.056f * petalScale
                    val pH = w * 0.145f * petalScale
                    drawOval(
                        color = Color(0xFFFFEBEE).copy(alpha = 0.88f * innerVisibility),
                        topLeft = Offset(blossomOrigin.x - pW / 2f, blossomOrigin.y - pH),
                        size = Size(pW, pH)
                    )
                }
            }
        }

        // Golden luminous core
        val coreGlow = smoothStep(0.50f, 0.96f, progress)
        if (coreGlow > 0.02f) {
            drawCircle(
                color = Color(0xFFF6C65B).copy(alpha = coreGlow),
                radius = w * 0.032f * coreGlow,
                center = Offset(blossomOrigin.x, blossomOrigin.y - w * 0.055f)
            )
        }

        // Green calyx base
        drawOval(
            color = stemColor,
            topLeft = Offset(blossomOrigin.x - w * 0.035f, blossomOrigin.y - w * 0.02f),
            size = Size(w * 0.07f, w * 0.045f)
        )
    }

    // 7. Morning Dewdrops Shimmering (Stages 1-10)
    val dewVisibility = smoothStep(0.08f, 0.5f, progress)
    if (dewVisibility > 0.02f) {
        for (i in 0..5) {
            val fall = ((ambientPhase * 1.4f + i * 0.21f) % 1f)
            val dx = w * (0.28f + i * 0.085f)
            val dy = h * (0.20f + fall * 0.55f)
            val alpha = sin(fall * PI).toFloat() * 0.42f * dewVisibility
            drawCircle(
                color = Color(0xFFD5F5FA).copy(alpha = alpha.coerceIn(0f, 0.5f)),
                radius = 3.2f,
                center = Offset(dx, dy)
            )
        }
    }

    drawAmbientMotes(
        progress = progress,
        ambientPhase = ambientPhase,
        beadPulseAlpha = beadPulseAlpha,
        color = Color(0xFFFFDCE0)
    )
}
