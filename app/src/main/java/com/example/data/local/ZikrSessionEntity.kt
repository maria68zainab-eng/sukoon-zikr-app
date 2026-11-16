package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zikr_sessions")
data class ZikrSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val zikrId: Long,
    val zikrTitle: String,
    val arabicText: String,
    val themeId: String,
    val completedCount: Int,
    val targetCount: Int,
    val durationSeconds: Long,
    val completedAt: Long = System.currentTimeMillis(),
    val isTargetCompleted: Boolean = true
)
