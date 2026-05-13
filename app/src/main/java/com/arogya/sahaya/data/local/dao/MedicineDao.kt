package com.arogya.sahaya.data.local.dao

import androidx.room.*
import com.arogya.sahaya.data.local.entity.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines WHERE userId = :userId ORDER BY id ASC")
    fun getAllForUser(userId: String): Flow<List<MedicineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicine: MedicineEntity): Long

    @Query("DELETE FROM medicines WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: Int, userId: String)

    @Query("SELECT COUNT(*) FROM medicines WHERE userId = :userId")
    suspend fun countForUser(userId: String): Int

    @Query("DELETE FROM medicines WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("UPDATE medicines SET userId = :newId WHERE userId = :oldId")
    suspend fun migrateUser(oldId: String, newId: String)
}
