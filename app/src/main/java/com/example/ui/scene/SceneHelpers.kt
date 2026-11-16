package com.example.ui.scene

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
    if (edge1 <= edge0) return if (x >= edge1) 1f else 0f
    val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

fun lerpFloat(start: Float, stop: Float, fraction: Float): Float {
    val t = fraction.coerceIn(0f, 1f)
    return start + (stop - start) * t
}

fun multiColorLerp(colors: List<Color>, progress: Float): Color {
    if (colors.isEmpty()) return Color.Transparent
    if (colors.size == 1) return colors.first()
    val p = progress.coerceIn(0f, 1f)
    val scaled = p * (colors.size - 1)
    val index = scaled.toInt().coerceIn(0, colors.size - 2)
    val localT = scaled - index
    return lerp(colors[index], colors[index + 1], localT)
}

fun DrawScope.drawAmbientMotes(
    progress: Float,
    ambientPhase: Float,
    beadPulseAlpha: Float,
    color: Color,
    count: Int = 18
) {
    val visibility = smoothStep(0.15f, 0.9f, progress) * 0.75f + beadPulseAlpha * 0.35f
    if (visibility <= 0.01f) return
    val w = size.width
    val h = size.height
    for (i in 0 until count) {
        val seed = i * 1.6180339f
        val baseX = ((seed * 0.37f) % 1f) * w
        val driftX = sin((ambientPhase * 2f * PI + seed * 3f).toDouble()).toFloat() * w * 0.03f
        val riseProgress = ((ambientPhase + (seed * 0.23f)) % 1f)
        val y = h * (0.85f - riseProgress * 0.7f)
        val radius = (2.2f + (i % 3) * 1.4f) * (0.7f + 0.5f * progress)
        val alpha = (sin(riseProgress * PI).toFloat().coerceIn(0f, 1f)) * visibility * 0.65f
        drawCircle(
            color = color.copy(alpha = alpha.coerceIn(0f, 1f)),
            radius = radius,
            center = Offset(baseX + driftX, y)
        )
    }
}

fun DrawScope.drawSunRays(
    origin: Offset,
    rayColor: Color,
    alpha: Float,
    ambientPhase: Float,
    rayCount: Int = 6
) {
    if (alpha <= 0.01f) return
    val maxLen = maxOf(size.width, size.height) * 1.3f
    for (i in 0 until rayCount) {
        val baseAngle = (0.18f + i * 0.14f) * PI.toFloat()
        val sway = sin((ambientPhase * 2f * PI + i).toDouble()).toFloat() * 0.02f
        val angle = baseAngle + sway
        val end = Offset(
            origin.x + cos(angle.toDouble()).toFloat() * maxLen,
            origin.y + sin(angle.toDouble()).toFloat() * maxLen
        )
        drawLine(
            color = rayColor.copy(alpha = (alpha * (0.22f - i * 0.02f)).coerceIn(0f, 0.35f)),
            start = origin,
            end = end,
            strokeWidth = size.width * (0.045f + (i % 2) * 0.025f)
        )
    }
}
