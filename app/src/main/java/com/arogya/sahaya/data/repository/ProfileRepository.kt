package com.arogya.sahaya.data.repository

import com.arogya.sahaya.data.local.dao.MedicineDao
import com.arogya.sahaya.data.local.dao.UserProfileDao
import com.arogya.sahaya.data.local.dao.VitalDao
import com.arogya.sahaya.data.local.entity.UserProfileEntity
import com.arogya.sahaya.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val profileDao: UserProfileDao,
    private val vitalDao: VitalDao,
    private val medicineDao: MedicineDao
) {

    fun getProfile(userId: String): Flow<UserProfile> =
        profileDao.getProfileForUser(userId).map { entity ->
            entity?.toUserProfile() ?: UserProfile()
        }

    suspend fun save(userId: String, profile: UserProfile) =
        profileDao.upsert(UserProfileEntity.fromUserProfile(userId, profile))

    suspend fun hasProfile(userId: String): Boolean =
        profileDao.countForUser(userId) > 0

    suspend fun deleteProfile(userId: String) {
        vitalDao.deleteAllForUser(userId)
        medicineDao.deleteAllForUser(userId)
        profileDao.deleteByUserId(userId)
    }

    suspend fun migrateGuestData(newId: String) {
        val guestId = "guest"
        vitalDao.migrateUser(guestId, newId)
        medicineDao.migrateUser(guestId, newId)
        profileDao.migrateUser(guestId, newId)
    }
}
