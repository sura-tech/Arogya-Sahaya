package com.arogya.sahaya.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arogya.sahaya.data.local.AppDatabase
import com.arogya.sahaya.data.model.UserProfile
import com.arogya.sahaya.data.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)

    // Dynamic userId — updates reactively after login/logout
    private val _userId = MutableStateFlow(prefs.getString("user_id", "guest") ?: "guest")

    private val db = AppDatabase.getInstance(application)
    private val repository = ProfileRepository(
        profileDao = db.userProfileDao(),
        vitalDao = db.vitalDao(),
        medicineDao = db.medicineDao()
    )

    val profile: StateFlow<UserProfile> = _userId
        .flatMapLatest { uid -> repository.getProfile(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile())

    /** Call immediately after login / logout to point the Flow at the correct user row. */
    fun refreshUserId() {
        _userId.value = prefs.getString("user_id", "guest") ?: "guest"
    }

    fun save(updated: UserProfile) {
        viewModelScope.launch { repository.save(_userId.value, updated) }
    }

    fun deleteProfile(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteProfile(_userId.value)
            onDeleted()
        }
    }

    fun migrateGuestData(newUserId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.migrateGuestData(newUserId)
            refreshUserId()
            onComplete()
        }
    }
}
