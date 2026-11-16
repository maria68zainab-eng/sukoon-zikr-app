package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.AzkarEntity
import com.example.domain.SceneThemeRegistry
import com.example.ui.scene.SceneTransformationCanvas
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.DeepSanctuary
import com.example.ui.theme.SageMist
import com.example.ui.theme.SoftGoldLight
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmParchment
import kotlinx.coroutines.delay

@Composable
fun TransformationPreviewDialog(
    zikr: AzkarEntity,
    onDismiss: () -> Unit
) {
    val safeTarget = zikr.targetCount.coerceAtLeast(1)
    var previewCount by remember { mutableIntStateOf(0) }
    var isAutoPlaying by remember { mutableStateOf(true) }
    var pulseCount by remember { mutableIntStateOf(0) }

    val stageInfo = SceneThemeRegistry.calculateStageProgress(
        currentCount = previewCount,
        targetCount = safeTarget,
        themeId = zikr.themeId
    )

    LaunchedEffect(isAutoPlaying, safeTarget) {
        if (isAutoPlaying) {
            while (true) {
                delay(480L)
                val step = (safeTarget / 10).coerceAtLeast(1)
                previewCount = if (previewCount >= safeTarget) 0 else (previewCount + step).coerceAtMost(safeTarget)
                pulseCount += 1
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SceneTransformationCanvas(
                themeId = zikr.themeId,
                targetProgress = stageInfo.normalizedProgress,
                beadPulseCount = pulseCount,
                customStartImagePath = zikr.customStartImagePath,
                customEndImagePath = zikr.customEndImagePath,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.border(1.dp, WarmGold.copy(alpha = 0.4f), RoundedCornerShape(50))
                    ) {
                        Text(
                            text = "Admin Preview · Stage ${stageInfo.stage} / 10",
                            style = MaterialTheme.typography.labelLarge,
                            color = SoftGoldLight,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("preview_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close preview",
                            tint = WarmParchment
                        )
                    }
                }

                // Sample Zikr overlay
                Surface(
                    color = Color(0xFF0A1713).copy(alpha = 0.58f),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = zikr.arabicText,
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = WarmParchment,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = zikr.englishTranslation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmParchment.copy(alpha = 0.86f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Scrubber Controls
                Surface(
                    color = Color(0xFF0A1713).copy(alpha = 0.78f),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, WarmGold.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stageInfo.stageDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftGoldLight,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$previewCount / $safeTarget",
                                style = MaterialTheme.typography.labelLarge,
                                color = WarmGold
                            )
                        }

                        Slider(
                            value = previewCount.toFloat(),
                            onValueChange = {
                                isAutoPlaying = false
                                previewCount = it.toInt()
                                pulseCount += 1
                            },
                            valueRange = 0f..safeTarget.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = WarmGold,
                                activeTrackColor = WarmGold,
                                inactiveTrackColor = SageMist.copy(alpha = 0.35f)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isAutoPlaying = false
                                    val step = (safeTarget / 10).coerceAtLeast(1)
                                    previewCount = (previewCount - step).coerceAtLeast(0)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("-1 Stage", color = WarmParchment)
                            }
                            Button(
                                onClick = { isAutoPlaying = !isAutoPlaying },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WarmGold,
                                    contentColor = DeepSanctuary
                                ),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(
                                    imageVector = if (isAutoPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = null
                                )
                                Text(if (isAutoPlaying) " Pause" else " Auto-Play")
                            }
                            OutlinedButton(
                                onClick = {
                                    isAutoPlaying = false
                                    val step = (safeTarget / 10).coerceAtLeast(1)
                                    previewCount = (previewCount + step).coerceAtMost(safeTarget)
                                    pulseCount += 1
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+1 Stage", color = WarmParchment)
                            }
                        }
                    }
                }
            }
        }
    }
}
