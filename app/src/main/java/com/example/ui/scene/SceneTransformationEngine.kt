package com.example.ui.scene

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.domain.SceneThemeIds
import java.io.File

@Composable
fun SceneTransformationCanvas(
    themeId: String,
    targetProgress: Float,
    beadPulseCount: Int,
    modifier: Modifier = Modifier,
    customStartImagePath: String? = null,
    customEndImagePath: String? = null
) {
    val smoothProgress by animateFloatAsState(
        targetValue = targetProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "scene_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_motion")
    val ambientPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_phase"
    )

    // Subtle per-bead ripple + light surge on every bead tap
    val beadRipple = remember { Animatable(0f) }
    LaunchedEffect(beadPulseCount) {
        if (beadPulseCount > 0) {
            beadRipple.snapTo(0f)
            beadRipple.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
        }
    }
    val pulseAlpha = if (beadPulseCount > 0) (1f - beadRipple.value).coerceIn(0f, 1f) else 0f

    val hasStartImage = !customStartImagePath.isNullOrBlank() && File(customStartImagePath).exists()
    val hasEndImage = !customEndImagePath.isNullOrBlank() && File(customEndImagePath).exists()

    Box(modifier = modifier.fillMaxSize()) {
        if (hasStartImage || hasEndImage) {
            CustomImageTransformationLayer(
                startImagePath = if (hasStartImage) customStartImagePath else customEndImagePath,
                endImagePath = if (hasEndImage) customEndImagePath else customStartImagePath,
                progress = smoothProgress,
                ambientPhase = ambientPhase,
                pulseAlpha = pulseAlpha,
                rippleFraction = beadRipple.value,
                hasPulse = beadPulseCount > 0
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                when (themeId) {
                    SceneThemeIds.DARKNESS_TO_SUNRISE ->
                        drawDarknessToSunriseScene(smoothProgress, ambientPhase, pulseAlpha)
                    SceneThemeIds.WILTED_TO_BLOOMING_FLOWER ->
                        drawWiltedToBloomingFlowerScene(smoothProgress, ambientPhase, pulseAlpha)
                    SceneThemeIds.DRY_DESERT_TO_GARDEN ->
                        drawDryDesertToGardenScene(smoothProgress, ambientPhase, pulseAlpha)
                    else ->
                        drawDeadTreeToFruitfulScene(smoothProgress, ambientPhase, pulseAlpha)
                }

                // Subtle expanding ripple from the Tasbeeh bead area on every tap
                if (beadPulseCount > 0 && pulseAlpha > 0.01f) {
                    val rippleCenter = Offset(size.width * 0.5f, size.height * 0.84f)
                    val radius = size.width * (0.08f + beadRipple.value * 0.58f)
                    drawCircle(
                        color = Color(0xFFFFEFC2).copy(alpha = pulseAlpha * 0.34f),
                        radius = radius,
                        center = rippleCenter,
                        style = Stroke(width = 3.5f * (1f - beadRipple.value * 0.6f))
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomImageTransformationLayer(
    startImagePath: String?,
    endImagePath: String?,
    progress: Float,
    ambientPhase: Float,
    pulseAlpha: Float,
    rippleFraction: Float,
    hasPulse: Boolean
) {
    val saturationMatrix = remember(progress) {
        ColorMatrix().apply {
            setToSaturation(lerpFloat(0.25f, 1.1f, progress))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (startImagePath != null) {
            AsyncImage(
                model = File(startImagePath),
                contentDescription = "Initial scene atmosphere",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(saturationMatrix),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = if (endImagePath != null && endImagePath != startImagePath) {
                            1f - progress
                        } else {
                            1f
                        }
                    }
            )
        }
        if (endImagePath != null && endImagePath != startImagePath) {
            AsyncImage(
                model = File(endImagePath),
                contentDescription = "Transformed scene atmosphere",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = progress }
            )
        }

        // Progressive twilight -> golden light atmospheric veil + motes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val duskVeilAlpha = lerpFloat(0.55f, 0.08f, progress)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1412).copy(alpha = duskVeilAlpha),
                        Color(0xFF142822).copy(alpha = duskVeilAlpha * 0.8f)
                    )
                )
            )
            if (progress > 0.35f) {
                drawSunRays(
                    origin = Offset(size.width * 0.5f, size.height * 0.15f),
                    rayColor = Color(0xFFFFECC0),
                    alpha = smoothStep(0.35f, 1f, progress) * 0.65f,
                    ambientPhase = ambientPhase
                )
            }
            drawAmbientMotes(
                progress = progress,
                ambientPhase = ambientPhase,
                beadPulseAlpha = pulseAlpha,
                color = Color(0xFFFFF0C2)
            )
            if (hasPulse && pulseAlpha > 0.01f) {
                val rippleCenter = Offset(size.width * 0.5f, size.height * 0.84f)
                val radius = size.width * (0.08f + rippleFraction * 0.55f)
                drawCircle(
                    color = Color(0xFFFFEFC2).copy(alpha = pulseAlpha * 0.35f),
                    radius = radius,
                    center = rippleCenter,
                    style = Stroke(width = 3.5f)
                )
            }
        }
    }
}
