package com.arogya.sahaya.data.model

data class VitalEntry(
    val id: Int,
    val userId: String,
    val day: String,
    val systolic: Int,
    val diastolic: Int,
    val heartRate: Int,
    val date: String = ""
)
