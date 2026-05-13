package com.arogya.sahaya.ui.screens

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.app.AlarmManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arogya.sahaya.ui.components.MedicineCard
import com.arogya.sahaya.ui.theme.ArogyaSahayaTheme
import com.arogya.sahaya.util.AlarmScheduler
import com.arogya.sahaya.viewmodel.MedicineViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    onBack: () -> Unit,
    isKannada: Boolean = false,
    vm: MedicineViewModel = viewModel()
) {
    val context  = LocalContext.current
    val medicines by vm.medicines.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var medicineName  by remember { mutableStateOf("") }
    var dosage        by remember { mutableStateOf("") }

    val slotOptions  = listOf("Morning", "Afternoon", "Night")
    val slotLabelsKn = listOf("ಬೆಳಿಗ್ಗೆ", "ಮಧ್ಯಾಹ್ನ", "ರಾತ್ರಿ")
    val selectedSlots = remember { mutableStateListOf<String>() }
    // Stored as "HH:mm" (24-h) from TimePicker; displayed as "h:mm AM/PM"
    val slotTimings   = remember { mutableStateMapOf<String, String>() }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    if (isKannada) "ಔಷಧ ಸೇರಿಸಿ" else "Add Medicine",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = medicineName, onValueChange = { medicineName = it },
                        label = {
                            Text(
                                if (isKannada) "ಔಷಧದ ಹೆಸರು" else "Medicine Name",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dosage, onValueChange = { dosage = it },
                        label = {
                            Text(
                                if (isKannada) "ಡೋಸೇಜ್ (ಉದಾ. 500mg)" else "Dosage (e.g. 500mg)",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Text(
                        if (isKannada) "ಸಮಯದ ಸ್ಲಾಟ್ ಮತ್ತು ಸಮಯ" else "Time Slots & Reminder Time",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    slotOptions.forEachIndexed { index, slot ->
                        val label = if (isKannada) slotLabelsKn[index] else slot
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedSlots.contains(slot),
                                    onCheckedChange = {
                                        if (it) selectedSlots.add(slot)
                                        else { selectedSlots.remove(slot); slotTimings.remove(slot) }
                                    }
                                )
                                Text(label, style = MaterialTheme.typography.bodyLarge)
                            }
                            if (selectedSlots.contains(slot)) {
                                val stored  = slotTimings[slot] ?: ""
                                val display = if (stored.isNotBlank())
                                    AlarmScheduler.formatForDisplay(stored)
                                else
                                    if (isKannada) "ಸಮಯ ಆರಿಸಿ" else "Pick time"

                                OutlinedButton(
                                    onClick = { showTimePicker(context, slot, slotTimings) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 40.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(display, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (medicineName.isNotBlank() && dosage.isNotBlank() && selectedSlots.isNotEmpty()) {
                            val timingsMap = slotTimings.toMap()
                            vm.add(medicineName, dosage, selectedSlots.toList(), timingsMap)
                            scheduleExactReminders(context, medicineName, selectedSlots.toList(), timingsMap)
                            medicineName = ""; dosage = ""
                            selectedSlots.clear(); slotTimings.clear()
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isKannada) "ಸೇರಿಸಿ" else "Add", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isKannada) "ರದ್ದು" else "Cancel", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isKannada) "ಔಷಧಗಳು" else "Medicines",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        if (isKannada) "ಔಷಧ ಸೇರಿಸಿ" else "Add Medicine",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (medicines.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(
                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                if (isKannada) "ಇನ್ನೂ ಯಾವುದೇ ಔಷಧ ಸೇರಿಸಿಲ್ಲ" else "No medicines added yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                if (isKannada) "'+' ಒತ್ತಿ ಔಷಧ ಸೇರಿಸಿ" else "Tap '+' to add your first medicine",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            items(medicines, key = { it.id }) { medicine ->
                MedicineCard(medicine = medicine, onDelete = { vm.delete(it.id) })
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

/** Opens Android's native TimePicker and stores the selected time as "HH:mm". */
private fun showTimePicker(
    context: Context,
    slot: String,
    slotTimings: MutableMap<String, String>
) {
    val now = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            slotTimings[slot] = "%02d:%02d".format(hour, minute)
        },
        now.get(Calendar.HOUR_OF_DAY),
        now.get(Calendar.MINUTE),
        false // show 12-hour picker with AM/PM
    ).show()
}

fun scheduleExactReminders(
    context: Context,
    medicineName: String,
    slots: List<String>,
    timings: Map<String, String>
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
    } else true

    slots.forEach { slot ->
        val timeString = timings[slot] ?: ""
        if (canScheduleExact) {
            AlarmScheduler.scheduleAlarm(context, medicineName, slot, timeString)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicineScreenPreview() {
    ArogyaSahayaTheme { MedicineScreen(onBack = {}) }
}
