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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AzkarEntity
import com.example.domain.SceneThemeRegistry
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

private enum class ExploreFilter(val label: String) {
    ALL("All"),
    VERIFIED("Verified References"),
    SAMPLE("Sample Demos")
}

@Composable
fun ExploreScreen(
    azkarList: List<AzkarEntity>,
    onStartZikr: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(ExploreFilter.ALL) }
    var selectedThemeFilter by remember { mutableStateOf<String?>(null) }

    val filteredList = remember(azkarList, searchQuery, activeFilter, selectedThemeFilter) {
        azkarList.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.englishTranslation.contains(searchQuery, ignoreCase = true) ||
                item.urduTranslation.contains(searchQuery, ignoreCase = true) ||
                item.arabicText.contains(searchQuery, ignoreCase = true)
            val matchesAuthenticity = when (activeFilter) {
                ExploreFilter.ALL -> true
                ExploreFilter.VERIFIED -> !item.isSampleContent
                ExploreFilter.SAMPLE -> item.isSampleContent
            }
            val matchesTheme = selectedThemeFilter == null || item.themeId == selectedThemeFilter
            matchesQuery && matchesAuthenticity && matchesTheme
        }
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
                    text = "Explore Azkar",
                    style = MaterialTheme.typography.displayMedium,
                    color = SoftGoldLight
                )
                Text(
                    text = "Choose a remembrance and journey through its progressive landscape.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SageMist
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Arabic, Urdu, English, or title") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search Azkar",
                            tint = WarmGold
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.16f),
                        focusedContainerColor = ForestCard,
                        unfocusedContainerColor = ForestCard,
                        focusedTextColor = WarmParchment,
                        unfocusedTextColor = WarmParchment
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ExploreFilter.entries) { filter ->
                        FilterChip(
                            selected = activeFilter == filter,
                            onClick = { activeFilter = filter },
                            label = { Text(filter.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarmGold,
                                selectedLabelColor = DeepSanctuary,
                                containerColor = ForestCard,
                                labelColor = WarmParchment
                            )
                        )
                    }
                    items(SceneThemeRegistry.themes) { theme ->
                        FilterChip(
                            selected = selectedThemeFilter == theme.id,
                            onClick = {
                                selectedThemeFilter = if (selectedThemeFilter == theme.id) null else theme.id
                            },
                            label = { Text(theme.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElevatedSage,
                                selectedLabelColor = SoftGoldLight,
                                containerColor = ForestCard,
                                labelColor = SageMist
                            )
                        )
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Surface(
                    color = ForestCard,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Spa,
                            contentDescription = null,
                            tint = WarmGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No matching Azkar found",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmParchment
                        )
                        Text(
                            text = "Try clearing your search or filter selection.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SageMist
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { zikr ->
                ExploreAzkarCard(
                    zikr = zikr,
                    onStart = { onStartZikr(zikr.id) },
                    modifier = Modifier.widthIn(max = 600.dp)
                )
            }
        }
    }
}

@Composable
private fun ExploreAzkarCard(
    zikr: AzkarEntity,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (zikr.currentCount.toFloat() / zikr.targetCount.coerceAtLeast(1)).coerceIn(0f, 1f)

    Surface(
        color = ForestCard,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (zikr.isDailyZikr) WarmGold.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(22.dp)
            )
            .clickable { onStart() }
            .testTag("explore_zikr_card_${zikr.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReferenceAuthenticityBadge(isSampleContent = zikr.isSampleContent)
                    if (zikr.isDailyZikr) {
                        Text(
                            text = "Today's Zikr",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "${zikr.currentCount}/${zikr.targetCount}",
                    style = MaterialTheme.typography.labelLarge,
                    color = SoftGoldLight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = zikr.title,
                style = MaterialTheme.typography.titleMedium,
                color = SoftGoldLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = zikr.arabicText,
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 36.sp,
                    color = WarmParchment,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = zikr.urduTranslation,
                    fontFamily = AmiriFontFamily,
                    fontSize = 15.sp,
                    color = WarmParchment.copy(alpha = 0.85f),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = zikr.englishTranslation,
                style = MaterialTheme.typography.bodyMedium,
                color = SageMist
            )

            Spacer(modifier = Modifier.height(12.dp))

            ThemeSwatchChip(themeId = zikr.themeId)

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50)),
                color = WarmGold,
                trackColor = ElevatedSage
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = zikr.referenceText.ifBlank { "Personal entry" },
                    style = MaterialTheme.typography.labelSmall,
                    color = SageMist.copy(alpha = 0.8f),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmGold,
                        contentColor = DeepSanctuary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (zikr.currentCount > 0) "Resume" else "Begin")
                }
            }
        }
    }
}
