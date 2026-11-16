package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.domain.SceneThemeRegistry
import com.example.ui.components.InteractiveTasbeehBead
import com.example.ui.components.ReferenceAuthenticityBadge
import com.example.ui.scene.SceneTransformationCanvas
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.DeepSanctuary
import com.example.ui.theme.MidnightPine
import com.example.ui.theme.SageMist
import com.example.ui.theme.SoftGoldLight
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmParchment
import com.example.ui.viewmodel.TasbeehViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen(
    viewModel: TasbeehViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val zikr = state.zikr
    var showResetDialog by remember { mutableStateOf(false) }
    var showReflectionSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full-screen animated progressive landscape
        if (zikr != null) {
            SceneTransformationCanvas(
                themeId = zikr.themeId,
                targetProgress = state.stageProgress?.normalizedProgress ?: 0f,
                beadPulseCount = state.beadPulseCount,
                customStartImagePath = zikr.customStartImagePath,
                customEndImagePath = zikr.customEndImagePath,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepSanctuary)
            )
        }

        // Subtle readability vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.42f),
                            Color.Black.copy(alpha = 0.14f),
                            Color.Black.copy(alpha = 0.48f)
                        )
                    )
                )
        )

        if (zikr != null) {
            val stageInfo = state.stageProgress
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 2. Top Controls Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TasbeehControlIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        onClick = onNavigateBack,
                        testTag = "tasbeeh_back_button"
                    )

                    Surface(
                        color = Color.Black.copy(alpha = 0.42f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.border(
                            1.dp,
                            SoftGoldLight.copy(alpha = 0.25f),
                            RoundedCornerShape(50)
                        )
                    ) {
                        Text(
                            text = "Stage ${stageInfo?.stage ?: 0} / 10",
                            style = MaterialTheme.typography.labelLarge,
                            color = SoftGoldLight,
                            modifier = Modifier
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("tasbeeh_stage_badge")
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TasbeehControlIconButton(
                            icon = if (state.soundEnabled) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                            contentDescription = if (state.soundEnabled) "Mute sound" else "Enable sound",
                            isActive = state.soundEnabled,
                            onClick = { viewModel.toggleSound() },
                            testTag = "tasbeeh_sound_toggle"
                        )
                        TasbeehControlIconButton(
                            icon = if (state.vibrationEnabled) Icons.Rounded.Vibration else Icons.Rounded.PhoneAndroid,
                            contentDescription = if (state.vibrationEnabled) "Disable vibration" else "Enable vibration",
                            isActive = state.vibrationEnabled,
                            onClick = { viewModel.toggleVibration() },
                            testTag = "tasbeeh_vibration_toggle"
                        )
                        TasbeehControlIconButton(
                            icon = if (state.isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                            contentDescription = if (state.isPaused) "Resume session" else "Pause session",
                            isActive = !state.isPaused,
                            onClick = { viewModel.togglePause() },
                            testTag = "tasbeeh_pause_toggle"
                        )
                        TasbeehControlIconButton(
                            icon = Icons.Rounded.Refresh,
                            contentDescription = "Reset session",
                            onClick = { showResetDialog = true },
                            testTag = "tasbeeh_reset_button"
                        )
                    }
                }

                // Stage milestone toast banner
                AnimatedVisibility(
                    visible = state.milestoneBanner != null,
                    enter = fadeIn() + slideInVertically { -20 },
                    exit = fadeOut() + slideOutVertically { -20 }
                ) {
                    Surface(
                        color = MidnightPine.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .border(1.dp, WarmGold.copy(alpha = 0.45f), RoundedCornerShape(50))
                    ) {
                        Text(
                            text = state.milestoneBanner.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftGoldLight,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.55f))

                // 3. Sacred Zikr Card (Arabic + Urdu + English + Reference)
                Surface(
                    color = Color(0xFF0A1713).copy(alpha = 0.56f),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.16f),
                            RoundedCornerShape(26.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ReferenceAuthenticityBadge(isSampleContent = zikr.isSampleContent)

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .clickable { showReflectionSheet = true }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoStories,
                                    contentDescription = "Reflection and reference",
                                    tint = SoftGoldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Reflection & Ref",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SoftGoldLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Arabic Zikr (RTL)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = zikr.arabicText,
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                lineHeight = 46.sp,
                                color = WarmParchment,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (zikr.transliteration.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = zikr.transliteration,
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                color = SoftGoldLight.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Urdu Translation (RTL)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = zikr.urduTranslation,
                                fontFamily = AmiriFontFamily,
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                color = WarmParchment.copy(alpha = 0.94f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // English Translation
                        Text(
                            text = zikr.englishTranslation,
                            style = MaterialTheme.typography.bodyLarge,
                            color = WarmParchment.copy(alpha = 0.86f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stageInfo?.stageDescription ?: SceneThemeRegistry.getTheme(zikr.themeId).shortSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = SageMist.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 4. Interactive Digital Tasbeeh Bead at Bottom
                InteractiveTasbeehBead(
                    currentCount = state.currentCount,
                    targetCount = state.targetCount,
                    isPaused = state.isPaused,
                    onTap = { viewModel.onBeadTapped() }
                )

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = MidnightPine,
            titleContentColor = WarmParchment,
            textContentColor = WarmParchment.copy(alpha = 0.85f),
            title = { Text("Reset Zikr Counter?") },
            text = { Text("Your current landscape and bead count will return to 0. Any previously completed sessions remain safely saved.") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetProgress()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmGold, contentColor = DeepSanctuary)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = SageMist)
                }
            }
        )
    }

    if (showReflectionSheet && zikr != null) {
        ModalBottomSheet(
            onDismissRequest = { showReflectionSheet = false },
            containerColor = MidnightPine,
            contentColor = WarmParchment
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReferenceAuthenticityBadge(isSampleContent = zikr.isSampleContent)
                    Text(
                        text = zikr.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftGoldLight
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Reflection & Spiritual Grounding",
                    style = MaterialTheme.typography.labelLarge,
                    color = WarmGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = zikr.benefitsAndReflection.ifBlank {
                        "A quiet moment of remembrance to center the heart and nurture gratitude."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = WarmParchment.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Reference",
                    style = MaterialTheme.typography.labelLarge,
                    color = WarmGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = zikr.referenceText.ifBlank { "No reference specified" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SageMist
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.spiritual_disclaimer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmParchment.copy(alpha = 0.68f)
                )
            }
        }
    }

    if (state.showCompletionModal && zikr != null) {
        val mins = state.elapsedSeconds / 60
        val secs = state.elapsedSeconds % 60
        val durationFormatted = if (mins > 0) "${mins}m ${secs}s" else "${secs}s"

        AlertDialog(
            onDismissRequest = { viewModel.dismissCompletionModal() },
            containerColor = MidnightPine,
            titleContentColor = SoftGoldLight,
            textContentColor = WarmParchment,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Spa,
                    contentDescription = "Session completed",
                    tint = WarmGold,
                    modifier = Modifier.size(34.dp)
                )
            },
            title = {
                Text(
                    text = "Alhamdulillah · Journey Complete",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "You completed ${state.targetCount} beads of ${zikr.title} in $durationFormatted.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.spiritual_disclaimer),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SageMist,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissCompletionModal()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmGold, contentColor = DeepSanctuary)
                ) {
                    Text("Return to Sanctuary")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissCompletionAndRestart() }) {
                    Text("Begin Again", color = SoftGoldLight)
                }
            }
        )
    }
}

@Composable
private fun TasbeehControlIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    testTag: String,
    isActive: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.42f))
            .border(
                1.dp,
                if (isActive) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
                CircleShape
            )
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) WarmParchment else WarmParchment.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp)
        )
    }
}
