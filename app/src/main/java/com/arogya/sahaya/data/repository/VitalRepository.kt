package com.arogya.sahaya.data.repository

import com.arogya.sahaya.data.local.dao.VitalDao
import com.arogya.sahaya.data.local.entity.VitalEntryEntity
import com.arogya.sahaya.data.model.VitalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VitalRepository(private val dao: VitalDao) {

    fun getLast7(userId: String): Flow<List<VitalEntry>> =
        dao.getLast7ForUser(userId).map { entities -> entities.map { it.toVitalEntry() }.reversed() }

    suspend fun add(entry: VitalEntry): Long =
        dao.insert(VitalEntryEntity.fromVitalEntry(entry))

    suspend fun deleteAllForUser(userId: String) =
        dao.deleteAllForUser(userId)
}
