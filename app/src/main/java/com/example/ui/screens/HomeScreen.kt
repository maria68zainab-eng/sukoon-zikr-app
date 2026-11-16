package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.AzkarEntity
import com.example.data.local.ZikrSessionEntity
import com.example.data.preferences.UserPreferences
import com.example.notifications.DailyReminderScheduler
import com.example.ui.components.ReferenceAuthenticityBadge
import com.example.ui.components.ThemeSwatchChip
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.DeepSanctuary
import com.example.ui.theme.ElevatedSage
import com.example.ui.theme.ForestCard
import com.example.ui.theme.MidnightPine
import com.example.ui.theme.SageMist
import com.example.ui.theme.SoftGoldLight
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmParchment
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    dailyZikr: AzkarEntity?,
    publishedAzkarCount: Int,
    recentSessions: List<ZikrSessionEntity>,
    completedTargetCount: Int,
    preferences: UserPreferences,
    onStartZikr: (Long) -> Unit,
    onExploreAll: () -> Unit,
    onViewProgress: () -> Unit,
    onUpdateReminder: (Boolean, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            DailyReminderScheduler.scheduleDailyReminder(
                context,
                preferences.reminderHour,
                preferences.reminderMinute
            )
            onUpdateReminder(true, preferences.reminderHour, preferences.reminderMinute)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepSanctuary, MidnightPine, DeepSanctuary)
                )
            ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Sanctuary Editorial Hero Banner
        item {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .height(172.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(1.dp, WarmGold.copy(alpha = 0.28f), RoundedCornerShape(26.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_sanctuary_hero),
                    contentDescription = "Sukoon sanctuary dawn landscape",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color(0xFF0A1914).copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "SUKOON",
                        style = MaterialTheme.typography.displayMedium,
                        color = SoftGoldLight,
                        letterSpacing = 3.sp
                    )
                    Text(
                        text = "A Journey of Zikr · Calm Remembrance",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WarmParchment.copy(alpha = 0.88f)
                    )
                }
            }
        }

        // 2. Today's Zikr Card
        item {
            Surface(
                color = ForestCard,
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .border(1.dp, WarmGold.copy(alpha = 0.28f), RoundedCornerShape(26.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = WarmGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.todays_zikr),
                                style = MaterialTheme.typography.labelLarge,
                                color = WarmGold
                            )
                        }
                        if (dailyZikr != null) {
                            ReferenceAuthenticityBadge(isSampleContent = dailyZikr.isSampleContent)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (dailyZikr != null) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = dailyZikr.arabicText,
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                lineHeight = 42.sp,
                                color = WarmParchment,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = dailyZikr.urduTranslation,
                                fontFamily = AmiriFontFamily,
                                fontSize = 17.sp,
                                color = WarmParchment.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = dailyZikr.englishTranslation,
                            style = MaterialTheme.typography.bodyLarge,
                            color = SageMist,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        ThemeSwatchChip(themeId = dailyZikr.themeId)

                        Spacer(modifier = Modifier.height(14.dp))

                        val progress = (dailyZikr.currentCount.toFloat() / dailyZikr.targetCount.coerceAtLeast(1))
                            .coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Current Progress",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SageMist
                            )
                            Text(
                                text = "${dailyZikr.currentCount} / ${dailyZikr.targetCount} beads",
                                style = MaterialTheme.typography.labelLarge,
                                color = SoftGoldLight
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(50)),
                            color = WarmGold,
                            trackColor = ElevatedSage
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { onStartZikr(dailyZikr.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("start_todays_zikr_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarmGold,
                                contentColor = DeepSanctuary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (dailyZikr.currentCount > 0) stringResource(R.string.continue_zikr)
                                else stringResource(R.string.start_zikr),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = "Select any Zikr from Explore or create one in Admin Studio.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SageMist
                        )
                    }
                }
            }
        }

        // 3. Quick Navigation Cards: Explore & Progress
        item {
            Row(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = ForestCard,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .clickable { onExploreAll() }
                        .testTag("home_explore_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Explore,
                            contentDescription = "Explore all Azkar",
                            tint = WarmGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "$publishedAzkarCount Azkar",
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmParchment
                        )
                        Text(
                            text = "Explore all",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SageMist
                        )
                    }
                }

                Surface(
                    color = ForestCard,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .clickable { onViewProgress() }
                        .testTag("home_progress_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = "My progress",
                            tint = WarmGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${preferences.totalLifetimeBeads} Beads",
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmParchment
                        )
                        Text(
                            text = "$completedTargetCount completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SageMist
                        )
                    }
                }
            }
        }

        // 4. Recent Completed Sessions Preview
        if (recentSessions.isNotEmpty()) {
            item {
                Surface(
                    color = ForestCard,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Completed Sessions",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmParchment
                            )
                            TextButton(onClick = onViewProgress) {
                                Text("View All", color = WarmGold)
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    tint = WarmGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        recentSessions.take(2).forEach { session ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MidnightPine)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.zikrTitle,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = WarmParchment,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${session.completedCount} / ${session.targetCount} beads",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SageMist
                                    )
                                }
                                Text(
                                    text = if (session.isTargetCompleted) "Complete" else "Partial",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SoftGoldLight
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Optional Daily Reminder Card
        item {
            Surface(
                color = ForestCard,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (preferences.reminderEnabled) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsNone,
                            contentDescription = "Daily reminder",
                            tint = WarmGold
                        )
                        Column {
                            Text(
                                text = "Daily Zikr Reminder",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmParchment
                            )
                            val formattedTime = String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                preferences.reminderHour,
                                preferences.reminderMinute
                            )
                            Row(
                                modifier = Modifier.clickable { showTimePicker = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Schedule,
                                    contentDescription = "Select reminder time",
                                    tint = SageMist,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (preferences.reminderEnabled) "Scheduled daily at $formattedTime (Tap to change)"
                                    else "Optional gentle reminder · $formattedTime",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SageMist
                                )
                            }
                        }
                    }

                    Switch(
                        checked = preferences.reminderEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    DailyReminderScheduler.scheduleDailyReminder(
                                        context,
                                        preferences.reminderHour,
                                        preferences.reminderMinute
                                    )
                                    onUpdateReminder(true, preferences.reminderHour, preferences.reminderMinute)
                                }
                            } else {
                                DailyReminderScheduler.cancelDailyReminder(context)
                                onUpdateReminder(false, preferences.reminderHour, preferences.reminderMinute)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepSanctuary,
                            checkedTrackColor = WarmGold
                        ),
                        modifier = Modifier.testTag("daily_reminder_switch")
                    )
                }
            }
        }

        // 6. Local Storage Status & Spiritual Grounding Disclaimer
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Storage,
                        contentDescription = null,
                        tint = SageMist.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.local_mode_notice),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SageMist.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = stringResource(R.string.spiritual_disclaimer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmParchment.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showTimePicker) {
        val pickerState = rememberTimePickerState(
            initialHour = preferences.reminderHour,
            initialMinute = preferences.reminderMinute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = MidnightPine,
            title = { Text("Reminder Time", color = WarmParchment) },
            text = { TimePicker(state = pickerState) },
            confirmButton = {
                Button(
                    onClick = {
                        showTimePicker = false
                        if (preferences.reminderEnabled) {
                            DailyReminderScheduler.scheduleDailyReminder(
                                context,
                                pickerState.hour,
                                pickerState.minute
                            )
                        }
                        onUpdateReminder(preferences.reminderEnabled, pickerState.hour, pickerState.minute)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmGold, contentColor = DeepSanctuary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = SageMist)
                }
            }
        )
    }
}
