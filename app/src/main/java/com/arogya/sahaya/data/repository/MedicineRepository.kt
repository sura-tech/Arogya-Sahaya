package com.arogya.sahaya.data.repository

import com.arogya.sahaya.data.local.dao.MedicineDao
import com.arogya.sahaya.data.local.entity.MedicineEntity
import com.arogya.sahaya.data.model.Medicine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicineRepository(private val dao: MedicineDao) {

    fun getAllMedicines(userId: String): Flow<List<Medicine>> =
        dao.getAllForUser(userId).map { entities -> entities.map { it.toMedicine() } }

    suspend fun add(medicine: Medicine): Long =
        dao.insert(MedicineEntity.fromMedicine(medicine))

    suspend fun delete(id: Int, userId: String) = dao.deleteById(id, userId)

    suspend fun deleteAllForUser(userId: String) =
        dao.deleteAllForUser(userId)
}
