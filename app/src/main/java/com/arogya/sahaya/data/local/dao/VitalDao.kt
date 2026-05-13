package com.arogya.sahaya.data.local.dao

import androidx.room.*
import com.arogya.sahaya.data.local.entity.VitalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalDao {
    @Query("SELECT * FROM vital_entries WHERE userId = :userId ORDER BY id ASC")
    fun getAllForUser(userId: String): Flow<List<VitalEntryEntity>>

    @Query("SELECT * FROM vital_entries WHERE userId = :userId ORDER BY id DESC LIMIT 7")
    fun getLast7ForUser(userId: String): Flow<List<VitalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: VitalEntryEntity): Long

    @Query("DELETE FROM vital_entries WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("UPDATE vital_entries SET userId = :newId WHERE userId = :oldId")
    suspend fun migrateUser(oldId: String, newId: String)
}
