package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AzkarDao {
    @Query("SELECT * FROM azkar WHERE isPublished = 1 ORDER BY isDailyZikr DESC, updatedAt DESC")
    fun observePublishedAzkar(): Flow<List<AzkarEntity>>

    @Query("SELECT * FROM azkar ORDER BY isDailyZikr DESC, updatedAt DESC")
    fun observeAllAzkar(): Flow<List<AzkarEntity>>

    @Query("SELECT * FROM azkar WHERE isDailyZikr = 1 AND isPublished = 1 LIMIT 1")
    fun observeDailyZikr(): Flow<AzkarEntity?>

    @Query("SELECT * FROM azkar WHERE id = :id LIMIT 1")
    fun observeZikrById(id: Long): Flow<AzkarEntity?>

    @Query("SELECT * FROM azkar WHERE id = :id LIMIT 1")
    suspend fun getZikrById(id: Long): AzkarEntity?

    @Query("SELECT COUNT(*) FROM azkar")
    suspend fun getAzkarCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZikr(zikr: AzkarEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AzkarEntity>)

    @Update
    suspend fun updateZikr(zikr: AzkarEntity)

    @Query("UPDATE azkar SET currentCount = :count, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateProgress(id: Long, count: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE azkar SET isPublished = :published, updatedAt = :timestamp WHERE id = :id")
    suspend fun updatePublishedStatus(id: Long, published: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE azkar SET isDailyZikr = 0")
    suspend fun clearDailyZikrFlags()

    @Query("UPDATE azkar SET isDailyZikr = 1, isPublished = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun markAsDailyZikr(id: Long, timestamp: Long = System.currentTimeMillis())

    @Transaction
    suspend fun setDailyZikr(id: Long) {
        clearDailyZikrFlags()
        markAsDailyZikr(id)
    }

    @Query("DELETE FROM azkar WHERE id = :id")
    suspend fun deleteZikrById(id: Long)

    // Sessions
    @Query("SELECT * FROM zikr_sessions ORDER BY completedAt DESC")
    fun observeCompletedSessions(): Flow<List<ZikrSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ZikrSessionEntity): Long

    @Query("SELECT COALESCE(SUM(completedCount), 0) FROM zikr_sessions")
    fun observeTotalSessionBeads(): Flow<Int>

    @Query("SELECT COUNT(*) FROM zikr_sessions WHERE isTargetCompleted = 1")
    fun observeCompletedTargetCount(): Flow<Int>
}
