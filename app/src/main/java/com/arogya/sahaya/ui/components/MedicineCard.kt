package com.arogya.sahaya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arogya.sahaya.data.model.Medicine
import com.arogya.sahaya.ui.theme.*

@Composable
fun MedicineCard(medicine: Medicine, onDelete: (Medicine) -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Primary50, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Medication, contentDescription = null,
                    tint = Primary700, modifier = Modifier.size(26.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(medicine.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text("Dosage: ${medicine.dosage}", style = MaterialTheme.typography.bodySmall, color = SubText)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    medicine.slots.forEach { slot ->
                        val timing = medicine.timings[slot]
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Primary100,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    slot,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary900
                                )
                            }
                            if (!timing.isNullOrBlank()) {
                                Text(timing, style = MaterialTheme.typography.labelSmall, color = SubText)
                            }
                        }
                    }
                }
            }
            IconButton(onClick = { onDelete(medicine) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicineCardPreview() {
    ArogyaSahayaTheme {
        Box(Modifier.padding(16.dp)) {
            MedicineCard(
                Medicine(
                    id = 1,
                    userId = "preview@test.com",
                    name = "Metformin",
                    dosage = "500mg",
                    slots = listOf("Morning", "Night"),
                    timings = mapOf("Morning" to "8:00 AM", "Night" to "9:00 PM")
                )
            )
        }
    }
}
