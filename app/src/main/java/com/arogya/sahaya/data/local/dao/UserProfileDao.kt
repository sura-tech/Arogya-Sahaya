package com.arogya.sahaya.data.local.dao

import androidx.room.*
import com.arogya.sahaya.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE userId = :userId")
    fun getProfileForUser(userId: String): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("SELECT COUNT(*) FROM user_profile WHERE userId = :userId")
    suspend fun countForUser(userId: String): Int

    @Query("DELETE FROM user_profile WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("UPDATE user_profile SET userId = :newId WHERE userId = :oldId")
    suspend fun migrateUser(oldId: String, newId: String)
}
