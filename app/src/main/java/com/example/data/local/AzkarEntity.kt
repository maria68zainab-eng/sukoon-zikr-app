package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.SceneThemeIds

@Entity(tableName = "azkar")
data class AzkarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val arabicText: String,
    val transliteration: String = "",
    val urduTranslation: String,
    val englishTranslation: String,
    val benefitsAndReflection: String = "",
    val referenceText: String = "",
    val isSampleContent: Boolean = false,
    val targetCount: Int = 100,
    val currentCount: Int = 0,
    val themeId: String = SceneThemeIds.DEAD_TREE_TO_FRUITFUL,
    val isDailyZikr: Boolean = false,
    val isPublished: Boolean = true,
    val customStartImagePath: String? = null,
    val customEndImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
