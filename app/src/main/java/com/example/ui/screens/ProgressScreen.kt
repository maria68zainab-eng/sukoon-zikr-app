package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ZikrSessionEntity
import com.example.ui.components.ThemeSwatchChip
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.DeepSanctuary
import com.example.ui.theme.ForestCard
import com.example.ui.theme.MidnightPine
import com.example.ui.theme.SageMist
import com.example.ui.theme.SoftGoldLight
import com.example.ui.theme.VerifiedEmerald
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmParchment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    totalLifetimeBeads: Int,
    completedTargetCount: Int,
    sessions: List<ZikrSessionEntity>,
    onOpenZikr: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember {
        SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DeepSanctuary, MidnightPine, DeepSanctuary))
            ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "My Progress",
                    style = MaterialTheme.typography.displayMedium,
                    color = SoftGoldLight
                )
                Text(
                    text = "A quiet record of your moments of remembrance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SageMist
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProgressStatCard(
                        value = totalLifetimeBeads.toString(),
                        label = "Total Beads",
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        value = completedTargetCount.toString(),
                        label = "Completed Journeys",
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        value = sessions.size.toString(),
                        label = "Logged Sessions",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Previous Completed Sessions",
                style = MaterialTheme.typography.titleLarge,
                color = WarmParchment,
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }

        if (sessions.isEmpty()) {
            item {
                Surface(
                    color = ForestCard,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = null,
                            tint = WarmGold,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No completed sessions yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmParchment
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Begin a Zikr journey from the Sanctuary or Explore tab to see your completed sessions here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SageMist,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                Surface(
                    color = ForestCard,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .clickable { onOpenZikr(session.zikrId) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = session.zikrTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = SoftGoldLight,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (session.isTargetCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.Spa,
                                    contentDescription = null,
                                    tint = if (session.isTargetCompleted) VerifiedEmerald else WarmGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${session.completedCount} / ${session.targetCount}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = WarmParchment
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = session.arabicText,
                                fontFamily = AmiriFontFamily,
                                fontSize = 20.sp,
                                color = WarmParchment.copy(alpha = 0.9f),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ThemeSwatchChip(themeId = session.themeId)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateFormatter.format(Date(session.completedAt)),
                                style = MaterialTheme.typography.labelSmall,
                                color = SageMist
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Timer,
                                    contentDescription = null,
                                    tint = SageMist,
                                    modifier = Modifier.size(13.dp)
                                )
                                val mins = session.durationSeconds / 60
                                val secs = session.durationSeconds % 60
                                Text(
                                    text = if (mins > 0) "${mins}m ${secs}s" else "${secs}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SageMist
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ForestCard,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.border(1.dp, WarmGold.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = SoftGoldLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SageMist,
                textAlign = TextAlign.Center
            )
        }
    }
}
