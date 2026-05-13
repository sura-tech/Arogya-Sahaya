package com.arogya.sahaya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.arogya.sahaya.data.local.Converters
import com.arogya.sahaya.data.model.Medicine

@Entity(tableName = "medicines")
@TypeConverters(Converters::class)
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val name: String,
    val dosage: String,
    val slots: List<String>,
    val timings: Map<String, String>
) {
    fun toMedicine() = Medicine(id, userId, name, dosage, slots, timings)

    companion object {
        fun fromMedicine(m: Medicine) = MedicineEntity(
            id = m.id,
            userId = m.userId,
            name = m.name,
            dosage = m.dosage,
            slots = m.slots,
            timings = m.timings
        )
    }
}
