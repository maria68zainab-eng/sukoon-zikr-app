package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.SceneThemeRegistry
import com.example.ui.theme.AmberSample
import com.example.ui.theme.DeepSanctuary
import com.example.ui.theme.SoftGoldLight
import com.example.ui.theme.VerifiedEmerald
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmParchment

@Composable
fun ReferenceAuthenticityBadge(
    isSampleContent: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSampleContent) AmberSample.copy(alpha = 0.18f) else VerifiedEmerald.copy(alpha = 0.18f)
    val borderColor = if (isSampleContent) AmberSample.copy(alpha = 0.55f) else VerifiedEmerald.copy(alpha = 0.55f)
    val contentColor = if (isSampleContent) AmberSample else VerifiedEmerald
    val label = if (isSampleContent) "Sample Demo" else "Verified Reference"
    val icon = if (isSampleContent) Icons.Rounded.AutoAwesome else Icons.Rounded.Verified

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ThemeSwatchChip(
    themeId: String,
    modifier: Modifier = Modifier
) {
    val spec = SceneThemeRegistry.getTheme(themeId)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(spec.previewColors))
        )
        Text(
            text = spec.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = WarmParchment.copy(alpha = 0.9f),
            maxLines = 1
        )
    }
}

@Composable
fun InteractiveTasbeehBead(
    currentCount: Int,
    targetCount: Int,
    isPaused: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safeTarget = targetCount.coerceAtLeast(1)
    val progress = (currentCount.toFloat() / safeTarget.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bead_arc_progress"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isPaused) 0.91f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "bead_tap_scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(134.dp)
        ) {
            // Orbital track + golden progress arc
            Canvas(modifier = Modifier.size(134.dp)) {
                val strokeWidth = 5.dp.toPx()
                val arcPadding = strokeWidth / 2f + 4.dp.toPx()
                val arcSize = Size(size.width - arcPadding * 2, size.height - arcPadding * 2)
                val topLeft = Offset(arcPadding, arcPadding)

                drawArc(
                    color = Color.White.copy(alpha = 0.18f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(WarmGold, SoftGoldLight, WarmGold)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Central luminous Tasbeeh bead
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(98.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isPaused) {
                                listOf(Color(0xFF56635E), Color(0xFF283530), Color(0xFF141E1A))
                            } else {
                                listOf(SoftGoldLight, WarmGold, Color(0xFF7A5B22))
                            },
                            center = Offset(34f, 30f),
                            radius = 120f
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (isPaused) Color.White.copy(alpha = 0.25f) else SoftGoldLight.copy(alpha = 0.75f),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onTap
                    )
                    .testTag("tasbeeh_bead_button")
            ) {
                if (isPaused) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Pause,
                            contentDescription = "Session paused",
                            tint = WarmParchment,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "PAUSED",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmParchment,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentCount.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = DeepSanctuary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("tasbeeh_count_text")
                        )
                        Text(
                            text = "TAP",
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepSanctuary.copy(alpha = 0.7f),
                            fontSize = 9.sp,
                            letterSpacing = 1.4.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            color = Color.Black.copy(alpha = 0.42f),
            shape = RoundedCornerShape(50),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(50))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$currentCount / $safeTarget",
                    style = MaterialTheme.typography.labelLarge,
                    color = SoftGoldLight,
                    modifier = Modifier.testTag("tasbeeh_target_label")
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(12.dp)
                        .background(Color.White.copy(alpha = 0.25f))
                )
                Text(
                    text = if (isPaused) "Tap Resume above" else "Tap bead to remember",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmParchment.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
