package com.arogya.sahaya.data.model

data class Medicine(
    val id: Int,
    val userId: String,
    val name: String,
    val dosage: String,
    val slots: List<String>,
    val timings: Map<String, String> = emptyMap()
)
