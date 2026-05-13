package com.arogya.sahaya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arogya.sahaya.data.model.VitalEntry

@Entity(tableName = "vital_entries")
data class VitalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val day: String,
    val systolic: Int,
    val diastolic: Int,
    val heartRate: Int,
    val date: String = ""
) {
    fun toVitalEntry() = VitalEntry(id, userId, day, systolic, diastolic, heartRate, date)

    companion object {
        fun fromVitalEntry(v: VitalEntry) = VitalEntryEntity(
            id = v.id,
            userId = v.userId,
            day = v.day,
            systolic = v.systolic,
            diastolic = v.diastolic,
            heartRate = v.heartRate,
            date = v.date
        )
    }
}
