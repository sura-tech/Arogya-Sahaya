package com.arogya.sahaya.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arogya.sahaya.data.local.AppDatabase
import com.arogya.sahaya.data.model.Medicine
import com.arogya.sahaya.data.repository.MedicineRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)

    // MutableStateFlow so we can push a new userId after login without recreating the VM
    private val _userId = MutableStateFlow(prefs.getString("user_id", "guest") ?: "guest")

    private val repository = MedicineRepository(
        AppDatabase.getInstance(application).medicineDao()
    )

    val medicines: StateFlow<List<Medicine>> = _userId
        .flatMapLatest { uid -> repository.getAllMedicines(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Call this immediately after login / logout so the Flow queries the correct user. */
    fun refreshUserId() {
        _userId.value = prefs.getString("user_id", "guest") ?: "guest"
    }

    fun add(name: String, dosage: String, slots: List<String>, timings: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            repository.add(Medicine(0, _userId.value, name, dosage, slots, timings))
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch { repository.delete(id, _userId.value) }
    }
}
