package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AzkarDao
import com.example.data.local.AzkarEntity
import com.example.data.local.ZikrSessionEntity
import com.example.data.preferences.PreferencesManager
import com.example.data.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class SukoonRepository(
    private val context: Context,
    private val dao: AzkarDao,
    private val prefs: PreferencesManager
) {
    val publishedAzkar: Flow<List<AzkarEntity>> = dao.observePublishedAzkar()
    val allAzkar: Flow<List<AzkarEntity>> = dao.observeAllAzkar()
    val dailyZikr: Flow<AzkarEntity?> = dao.observeDailyZikr()
    val completedSessions: Flow<List<ZikrSessionEntity>> = dao.observeCompletedSessions()
    val completedTargetCount: Flow<Int> = dao.observeCompletedTargetCount()
    val totalSessionBeads: Flow<Int> = dao.observeTotalSessionBeads()
    val userPreferences: Flow<UserPreferences> = prefs.preferencesFlow

    fun observeZikrById(id: Long): Flow<AzkarEntity?> = dao.observeZikrById(id)

    suspend fun getZikrById(id: Long): AzkarEntity? = dao.getZikrById(id)

    suspend fun saveBeadProgress(zikrId: Long, newCount: Int, incrementLifetime: Boolean) {
        dao.updateProgress(zikrId, newCount)
        if (incrementLifetime) {
            prefs.incrementLifetimeBeads(1)
        }
    }

    suspend fun recordSession(
        zikr: AzkarEntity,
        completedCount: Int,
        durationSeconds: Long,
        isTargetCompleted: Boolean
    ) {
        if (completedCount <= 0) return
        dao.insertSession(
            ZikrSessionEntity(
                zikrId = zikr.id,
                zikrTitle = zikr.title,
                arabicText = zikr.arabicText,
                themeId = zikr.themeId,
                completedCount = completedCount,
                targetCount = zikr.targetCount,
                durationSeconds = durationSeconds.coerceAtLeast(1L),
                completedAt = System.currentTimeMillis(),
                isTargetCompleted = isTargetCompleted
            )
        )
    }

    suspend fun upsertZikr(zikr: AzkarEntity): Long {
        val now = System.currentTimeMillis()
        val entity = if (zikr.id == 0L) {
            zikr.copy(createdAt = now, updatedAt = now)
        } else {
            zikr.copy(updatedAt = now)
        }
        val savedId = dao.insertZikr(entity)
        val effectiveId = if (entity.id == 0L) savedId else entity.id
        if (entity.isDailyZikr) {
            dao.setDailyZikr(effectiveId)
        }
        return effectiveId
    }

    suspend fun setDailyZikr(zikrId: Long) = dao.setDailyZikr(zikrId)

    suspend fun setPublished(zikrId: Long, published: Boolean) =
        dao.updatePublishedStatus(zikrId, published)

    suspend fun deleteZikr(zikrId: Long) = dao.deleteZikrById(zikrId)

    suspend fun setSoundEnabled(enabled: Boolean) = prefs.setSoundEnabled(enabled)
    suspend fun setVibrationEnabled(enabled: Boolean) = prefs.setVibrationEnabled(enabled)
    suspend fun setReminder(enabled: Boolean, hour: Int, minute: Int) =
        prefs.setReminder(enabled, hour, minute)

    suspend fun setAdminPasscode(pin: String) = prefs.setAdminPasscode(pin)
    suspend fun verifyAdminPasscode(pin: String): Boolean = prefs.verifyAdminPasscode(pin)

    suspend fun copyImageToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, "scene_images").apply { mkdirs() }
            val dest = File(dir, "scene_${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                dest.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext null
            dest.absolutePath
        } catch (_: Exception) {
            null
        }
    }
}
