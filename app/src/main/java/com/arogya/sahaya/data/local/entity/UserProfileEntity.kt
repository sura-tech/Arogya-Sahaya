package com.arogya.sahaya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arogya.sahaya.data.model.UserProfile

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val age: String,
    val chronicConditions: String,
    val emergencyContact: String,
    val emergencyName: String
) {
    fun toUserProfile() = UserProfile(name, age, chronicConditions, emergencyContact, emergencyName)

    companion object {
        fun fromUserProfile(userId: String, p: UserProfile) = UserProfileEntity(
            userId = userId,
            name = p.name,
            age = p.age,
            chronicConditions = p.chronicConditions,
            emergencyContact = p.emergencyContact,
            emergencyName = p.emergencyName
        )
    }
}
