package com.arogya.sahaya.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arogya.sahaya.data.local.AppDatabase
import com.arogya.sahaya.data.model.VitalEntry
import com.arogya.sahaya.data.repository.VitalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class VitalViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)

    private val _userId = MutableStateFlow(prefs.getString("user_id", "guest") ?: "guest")

    private val repository = VitalRepository(
        AppDatabase.getInstance(application).vitalDao()
    )

    val entries: StateFlow<List<VitalEntry>> = _userId
        .flatMapLatest { uid -> repository.getLast7(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun refreshUserId() {
        _userId.value = prefs.getString("user_id", "guest") ?: "guest"
    }

    fun add(day: String, systolic: Int, diastolic: Int, heartRate: Int, date: String) {
        viewModelScope.launch {
            repository.add(VitalEntry(0, _userId.value, day, systolic, diastolic, heartRate, date))
        }
    }

    fun latest(): VitalEntry? = entries.value.lastOrNull()
}
