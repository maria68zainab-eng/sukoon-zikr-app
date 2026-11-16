package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.PublicOff
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AzkarEntity
import com.example.ui.components.ReferenceAuthenticityBadge
import com.example.ui.components.ThemeSwatchChip
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.DeepSanctuary
import com.example.ui.theme.ForestCard
import com.example.ui.theme.MidnightPine
import com.example.ui.theme.SageMist
import com.example.ui.theme.SoftGoldLight
import com.example.ui.theme.TerracottaRose
import com.example.ui.theme.VerifiedEmerald
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmParchment

@Composable
fun AdminScreen(
    allAzkar: List<AzkarEntity>,
    hasAdminPasscode: Boolean,
    isUnlocked: Boolean,
    authError: String?,
    onSetupPasscode: (String) -> Unit,
    onVerifyPasscode: (String) -> Unit,
    onLockAdmin: () -> Unit,
    onClearError: () -> Unit,
    onSaveZikr: (AzkarEntity) -> Unit,
    onSetDailyZikr: (Long) -> Unit,
    onTogglePublished: (AzkarEntity) -> Unit,
    onDeleteZikr: (Long) -> Unit,
    onImportImage: suspend (Uri) -> String?,
    modifier: Modifier = Modifier
) {
    if (!isUnlocked) {
        AdminPasscodeGate(
            hasPasscode = hasAdminPasscode,
            authError = authError,
            onSetupPasscode = onSetupPasscode,
            onVerifyPasscode = onVerifyPasscode,
            onClearError = onClearError,
            modifier = modifier
        )
        return
    }

    var editingZikr by remember { mutableStateOf<AzkarEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }
    var previewingZikr by remember { mutableStateOf<AzkarEntity?>(null) }
    var pendingDelete by remember { mutableStateOf<AzkarEntity?>(null) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Studio",
                            style = MaterialTheme.typography.displayMedium,
                            color = SoftGoldLight
                        )
                        Text(
                            text = "Curate daily Azkar, scene themes, custom artwork & preview transformations.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SageMist
                        )
                    }
                    IconButton(
                        onClick = onLockAdmin,
                        modifier = Modifier.testTag("admin_lock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Lock Admin Studio",
                            tint = WarmGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { isCreatingNew = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_add_zikr_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmGold,
                        contentColor = DeepSanctuary
                    )
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Zikr", fontWeight = FontWeight.Bold)
                }
            }
        }

        items(allAzkar, key = { it.id }) { zikr ->
            AdminZikrCard(
                zikr = zikr,
                onSetDaily = { onSetDailyZikr(zikr.id) },
                onTogglePublished = { onTogglePublished(zikr) },
                onPreview = { previewingZikr = zikr },
                onEdit = { editingZikr = zikr },
                onDelete = { pendingDelete = zikr },
                modifier = Modifier.widthIn(max = 600.dp)
            )
        }
    }

    if (isCreatingNew || editingZikr != null) {
        AdminEditorSheet(
            initialZikr = editingZikr,
            onImportImage = onImportImage,
            onPreview = { draft -> previewingZikr = draft },
            onSave = { entity ->
                onSaveZikr(entity)
                isCreatingNew = false
                editingZikr = null
            },
            onDismiss = {
                isCreatingNew = false
                editingZikr = null
            }
        )
    }

    if (previewingZikr != null) {
        TransformationPreviewDialog(
            zikr = previewingZikr!!,
            onDismiss = { previewingZikr = null }
        )
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = MidnightPine,
            titleContentColor = WarmParchment,
            textContentColor = SageMist,
            title = { Text("Delete Zikr?") },
            text = { Text("Remove \"${pendingDelete!!.title}\" from Sukoon? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteZikr(pendingDelete!!.id)
                        pendingDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaRose, contentColor = DeepSanctuary)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel", color = SageMist)
                }
            }
        )
    }
}

@Composable
private fun AdminPasscodeGate(
    hasPasscode: Boolean,
    authError: String?,
    onSetupPasscode: (String) -> Unit,
    onVerifyPasscode: (String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DeepSanctuary, MidnightPine, DeepSanctuary))
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = ForestCard,
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .border(1.dp, WarmGold.copy(alpha = 0.3f), RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = "Protected Admin Studio",
                    tint = WarmGold,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (hasPasscode) "Protected Admin Studio" else "Create Your Private Admin PIN",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SoftGoldLight,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (hasPasscode) {
                        "Enter your private administrator PIN to manage Azkar, daily selections, and scene artwork."
                    } else {
                        "Set a private 4–8 digit PIN on this device to protect official Zikr editing and publishing."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SageMist,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() } && it.length <= 8) {
                            pin = it
                            localError = null
                            onClearError()
                        }
                    },
                    label = { Text(if (hasPasscode) "Admin PIN" else "New PIN (4–8 digits)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = WarmParchment,
                        unfocusedTextColor = WarmParchment
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (!hasPasscode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() } && it.length <= 8) {
                                confirmPin = it
                                localError = null
                            }
                        },
                        label = { Text("Confirm PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = WarmParchment,
                            unfocusedTextColor = WarmParchment
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_confirm_input")
                    )
                }

                val errorMsg = localError ?: authError
                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TerracottaRose,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (hasPasscode) {
                            onVerifyPasscode(pin)
                        } else {
                            if (pin.length < 4) {
                                localError = "PIN must be at least 4 digits."
                            } else if (pin != confirmPin) {
                                localError = "PINs do not match."
                            } else {
                                onSetupPasscode(pin)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_unlock_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmGold,
                        contentColor = DeepSanctuary
                    )
                ) {
                    Icon(imageVector = Icons.Rounded.LockOpen, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (hasPasscode) "Unlock Admin Studio" else "Save PIN & Enter")
                }
            }
        }
    }
}

@Composable
private fun AdminZikrCard(
    zikr: AzkarEntity,
    onSetDaily: () -> Unit,
    onTogglePublished: () -> Unit,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ForestCard,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (zikr.isDailyZikr) WarmGold.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(20.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                    Surface(
                        color = if (zikr.isPublished) VerifiedEmerald.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (zikr.isPublished) "Published" else "Draft",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (zikr.isPublished) VerifiedEmerald else SageMist,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (zikr.isDailyZikr) {
                    Text(
                        text = "★ Daily Zikr",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarmGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = zikr.title,
                style = MaterialTheme.typography.titleMedium,
                color = SoftGoldLight
            )

            Spacer(modifier = Modifier.height(6.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = zikr.arabicText,
                    fontFamily = AmiriFontFamily,
                    fontSize = 21.sp,
                    color = WarmParchment,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemeSwatchChip(themeId = zikr.themeId)
                Text(
                    text = "Target: ${zikr.targetCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SageMist
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onPreview,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Visibility,
                        contentDescription = "Preview",
                        tint = SoftGoldLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Preview", color = SoftGoldLight, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onSetDaily,
                    enabled = !zikr.isDailyZikr,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Set Daily",
                        tint = if (zikr.isDailyZikr) SageMist else WarmGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (zikr.isDailyZikr) "Daily" else "Set Daily", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onTogglePublished) {
                    Icon(
                        imageVector = if (zikr.isPublished) Icons.Rounded.PublicOff else Icons.Rounded.Public,
                        contentDescription = null,
                        tint = SageMist,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (zikr.isPublished) "Unpublish" else "Publish", color = SageMist)
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Zikr",
                            tint = SoftGoldLight
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Delete Zikr",
                            tint = TerracottaRose
                        )
                    }
                }
            }
        }
    }
}
