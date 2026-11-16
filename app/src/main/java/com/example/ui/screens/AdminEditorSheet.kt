package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.AzkarEntity
import com.example.domain.SceneThemeIds
import com.example.domain.SceneThemeRegistry
import com.example.ui.theme.DeepSanctuary
import com.example.ui.theme.ElevatedSage
import com.example.ui.theme.ForestCard
import com.example.ui.theme.MidnightPine
import com.example.ui.theme.SageMist
import com.example.ui.theme.SoftGoldLight
import com.example.ui.theme.TerracottaRose
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmParchment
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditorSheet(
    initialZikr: AzkarEntity?,
    onImportImage: suspend (Uri) -> String?,
    onPreview: (AzkarEntity) -> Unit,
    onSave: (AzkarEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(initialZikr?.title ?: "") }
    var arabicText by remember { mutableStateOf(initialZikr?.arabicText ?: "") }
    var transliteration by remember { mutableStateOf(initialZikr?.transliteration ?: "") }
    var urduTranslation by remember { mutableStateOf(initialZikr?.urduTranslation ?: "") }
    var englishTranslation by remember { mutableStateOf(initialZikr?.englishTranslation ?: "") }
    var benefitsAndReflection by remember { mutableStateOf(initialZikr?.benefitsAndReflection ?: "") }
    var referenceText by remember { mutableStateOf(initialZikr?.referenceText ?: "") }
    var isSampleContent by remember { mutableStateOf(initialZikr?.isSampleContent ?: false) }
    var targetCountText by remember { mutableStateOf((initialZikr?.targetCount ?: 100).toString()) }
    var selectedThemeId by remember { mutableStateOf(initialZikr?.themeId ?: SceneThemeIds.DEAD_TREE_TO_FRUITFUL) }
    var isDailyZikr by remember { mutableStateOf(initialZikr?.isDailyZikr ?: false) }
    var isPublished by remember { mutableStateOf(initialZikr?.isPublished ?: true) }
    var customStartImagePath by remember { mutableStateOf(initialZikr?.customStartImagePath) }
    var customEndImagePath by remember { mutableStateOf(initialZikr?.customEndImagePath) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val startImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val path = onImportImage(uri)
                if (path != null) customStartImagePath = path
            }
        }
    }

    val endImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val path = onImportImage(uri)
                if (path != null) customEndImagePath = path
            }
        }
    }

    fun buildDraftEntity(): AzkarEntity {
        val target = targetCountText.toIntOrNull()?.coerceIn(1, 10000) ?: 100
        return AzkarEntity(
            id = initialZikr?.id ?: 0L,
            title = title.trim().ifBlank { "Untitled Zikr" },
            arabicText = arabicText.trim(),
            transliteration = transliteration.trim(),
            urduTranslation = urduTranslation.trim(),
            englishTranslation = englishTranslation.trim(),
            benefitsAndReflection = benefitsAndReflection.trim(),
            referenceText = referenceText.trim(),
            isSampleContent = isSampleContent,
            targetCount = target,
            currentCount = initialZikr?.currentCount ?: 0,
            themeId = selectedThemeId,
            isDailyZikr = isDailyZikr,
            isPublished = isPublished,
            customStartImagePath = customStartImagePath,
            customEndImagePath = customEndImagePath,
            createdAt = initialZikr?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MidnightPine,
        contentColor = WarmParchment
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (initialZikr == null) "Add New Zikr" else "Edit Zikr",
                style = MaterialTheme.typography.headlineMedium,
                color = SoftGoldLight
            )

            AdminTextField(
                value = title,
                onValueChange = { title = it },
                label = "Zikr Title (e.g., SubhanAllah wa bihamdihi)",
                testTag = "admin_field_title"
            )
            AdminTextField(
                value = arabicText,
                onValueChange = { arabicText = it },
                label = "Arabic Text *",
                testTag = "admin_field_arabic"
            )
            AdminTextField(
                value = transliteration,
                onValueChange = { transliteration = it },
                label = "Transliteration (Optional)",
                testTag = "admin_field_transliteration"
            )
            AdminTextField(
                value = urduTranslation,
                onValueChange = { urduTranslation = it },
                label = "Urdu Translation *",
                testTag = "admin_field_urdu"
            )
            AdminTextField(
                value = englishTranslation,
                onValueChange = { englishTranslation = it },
                label = "English Translation *",
                testTag = "admin_field_english"
            )
            AdminTextField(
                value = benefitsAndReflection,
                onValueChange = { benefitsAndReflection = it },
                label = "Spiritual Reflection & Benefits",
                singleLine = false,
                testTag = "admin_field_benefits"
            )
            AdminTextField(
                value = referenceText,
                onValueChange = { referenceText = it },
                label = "Authentic Reference (e.g., Sahih al-Bukhari 6405)",
                testTag = "admin_field_reference"
            )

            // Target Count Presets & Input
            Text(
                text = "Target Bead Count",
                style = MaterialTheme.typography.labelLarge,
                color = WarmGold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("33", "99", "100").forEach { preset ->
                    FilterChip(
                        selected = targetCountText == preset,
                        onClick = { targetCountText = preset },
                        label = { Text(preset) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WarmGold,
                            selectedLabelColor = DeepSanctuary,
                            containerColor = ForestCard,
                            labelColor = WarmParchment
                        )
                    )
                }
                OutlinedTextField(
                    value = targetCountText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 4) {
                            targetCountText = input
                        }
                    },
                    label = { Text("Custom") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Transformation Theme Selector
            Text(
                text = "Transformation Scene Theme",
                style = MaterialTheme.typography.labelLarge,
                color = WarmGold
            )
            SceneThemeRegistry.themes.forEach { spec ->
                val selected = spec.id == selectedThemeId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) ElevatedSage else ForestCard)
                        .border(
                            1.dp,
                            if (selected) WarmGold else Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedThemeId = spec.id }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(spec.previewColors))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = spec.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selected) SoftGoldLight else WarmParchment
                        )
                        Text(
                            text = spec.shortSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SageMist
                        )
                    }
                }
            }

            // Upload or Replace Custom Scene Images (Zero-permission Photo Picker)
            Text(
                text = "Custom Scene Images (Optional)",
                style = MaterialTheme.typography.labelLarge,
                color = WarmGold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SceneImageUploadSlot(
                    label = "Start Scene",
                    imagePath = customStartImagePath,
                    onPick = {
                        startImagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onClear = { customStartImagePath = null },
                    modifier = Modifier.weight(1f)
                )
                SceneImageUploadSlot(
                    label = "Final Scene",
                    imagePath = customEndImagePath,
                    onPick = {
                        endImagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onClear = { customEndImagePath = null },
                    modifier = Modifier.weight(1f)
                )
            }

            // Toggles
            AdminToggleRow(
                title = "Mark as Sample Demonstration Content",
                subtitle = "Clearly distinguishes demo entries from verified references",
                checked = isSampleContent,
                onCheckedChange = { isSampleContent = it }
            )
            AdminToggleRow(
                title = "Set as Today's Daily Zikr",
                subtitle = "Highlights this Zikr on the Sanctuary home screen",
                checked = isDailyZikr,
                onCheckedChange = { isDailyZikr = it }
            )
            AdminToggleRow(
                title = "Published to Users",
                subtitle = "Unpublished drafts remain hidden from Explore",
                checked = isPublished,
                onCheckedChange = { isPublished = it }
            )

            if (validationError != null) {
                Text(
                    text = validationError.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TerracottaRose
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onPreview(buildDraftEntity())
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("admin_preview_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = SoftGoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Preview", color = SoftGoldLight)
                }

                Button(
                    onClick = {
                        if (arabicText.isBlank() || urduTranslation.isBlank() || englishTranslation.isBlank()) {
                            validationError = "Please enter Arabic text, Urdu translation, and English translation."
                            return@Button
                        }
                        val target = targetCountText.toIntOrNull()
                        if (target == null || target < 1) {
                            validationError = "Target count must be at least 1."
                            return@Button
                        }
                        validationError = null
                        onSave(buildDraftEntity())
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(50.dp)
                        .testTag("admin_save_zikr_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmGold,
                        contentColor = DeepSanctuary
                    )
                ) {
                    Text(if (initialZikr == null) "Save Zikr" else "Update Zikr")
                }
            }
        }
    }
}

@Composable
private fun SceneImageUploadSlot(
    label: String,
    imagePath: String?,
    onPick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exists = !imagePath.isNullOrBlank() && File(imagePath).exists()
    Box(
        modifier = modifier
            .height(92.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(ForestCard)
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
            .clickable { onPick() },
        contentAlignment = Alignment.Center
    ) {
        if (exists) {
            AsyncImage(
                model = File(imagePath!!),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
            IconButton(
                onClick = onClear,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Remove $label image",
                    tint = WarmParchment
                )
            }
            Text(
                text = "Replace $label",
                style = MaterialTheme.typography.labelSmall,
                color = WarmParchment,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.AddPhotoAlternate,
                    contentDescription = "Upload $label image",
                    tint = WarmGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Upload $label",
                    style = MaterialTheme.typography.labelSmall,
                    color = SageMist
                )
            }
        }
    }
}

@Composable
private fun AdminToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ForestCard)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = WarmParchment)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = SageMist)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DeepSanctuary,
                checkedTrackColor = WarmGold
            )
        )
    }
}

@Composable
private fun AdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    testTag: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(14.dp),
        colors = adminFieldColors(),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

@Composable
private fun adminFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = WarmGold,
    unfocusedBorderColor = Color.White.copy(alpha = 0.16f),
    focusedContainerColor = ForestCard,
    unfocusedContainerColor = ForestCard,
    focusedTextColor = WarmParchment,
    unfocusedTextColor = WarmParchment,
    focusedLabelColor = WarmGold,
    unfocusedLabelColor = SageMist
)
