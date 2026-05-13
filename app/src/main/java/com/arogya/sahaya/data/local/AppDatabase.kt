package com.arogya.sahaya.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arogya.sahaya.data.local.dao.MedicineDao
import com.arogya.sahaya.data.local.dao.UserProfileDao
import com.arogya.sahaya.data.local.dao.VitalDao
import com.arogya.sahaya.data.local.entity.MedicineEntity
import com.arogya.sahaya.data.local.entity.UserProfileEntity
import com.arogya.sahaya.data.local.entity.VitalEntryEntity

@Database(
    entities = [MedicineEntity::class, VitalEntryEntity::class, UserProfileEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun vitalDao(): VitalDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arogya_sahaya_db"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
