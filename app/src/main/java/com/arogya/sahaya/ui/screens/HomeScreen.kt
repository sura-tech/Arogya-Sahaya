package com.arogya.sahaya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arogya.sahaya.data.model.Medicine
import com.arogya.sahaya.data.model.UserProfile
import com.arogya.sahaya.ui.components.SosButton
import com.arogya.sahaya.ui.theme.*
import com.arogya.sahaya.util.AlarmScheduler

@Composable
fun HomeScreen(
    isKannada: Boolean = false,
    onLanguageToggle: () -> Unit = {},
    profileName: String = "",
    profile: UserProfile = UserProfile(),
    medicines: List<Medicine> = emptyList(),
    onNavigate: (String) -> Unit
) {
    val displayName = profileName.ifBlank { if (isKannada) "ಬಳಕೆದಾರ" else "User" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Gradient Header ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Primary900, Primary700)))
                .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        if (isKannada) "ನಮಸ್ಕಾರ," else "Hello,",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.clickable { onLanguageToggle() },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isKannada) "EN" else "ಕನ್ನಡ",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person, contentDescription = "Profile",
                            tint = Color.White, modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .offset(y = (-18).dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Background)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // SOS button — wired to real profile emergency contact
            SosButton(
                emergencyName    = profile.emergencyName,
                emergencyContact = profile.emergencyContact,
                senderName       = profile.name.ifBlank { "User" }
            )

            TodayReminderCard(isKannada, medicines)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun TodayReminderCard(isKannada: Boolean = false, medicines: List<Medicine> = emptyList()) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(0.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(Primary700, Teal500)),
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Alarm, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(24.dp)
                    )
                    Text(
                        if (isKannada) "ಇಂದಿನ ನೆನಪಿಕೆಗಳು" else "Today's Reminders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (medicines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Medication, contentDescription = null,
                            tint = SubText, modifier = Modifier.size(32.dp)
                        )
                        Text(
                            if (isKannada) "ಇನ್ನೂ ಯಾವುದೇ ಔಷಧ ಸೇರಿಸಿಲ್ಲ" else "No medicines added yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SubText
                        )
                    }
                }
            } else {
                val slotOrder        = listOf("Morning", "Afternoon", "Night")
                val slotDotColors    = mapOf(
                    "Morning"   to Color(0xFFF59E0B),
                    "Afternoon" to Primary500,
                    "Night"     to Teal700
                )
                val slotEmojis       = mapOf(
                    "Morning"   to "\uD83C\uDF05",
                    "Afternoon" to "\u2600\uFE0F",
                    "Night"     to "\uD83C\uDF19"
                )
                val slotLabelsKn = mapOf(
                    "Morning"   to "ಬೆಳಿಗ್ಗೆ",
                    "Afternoon" to "ಮಧ್ಯಾಹ್ನ",
                    "Night"     to "ರಾತ್ರಿ"
                )

                val slotsWithMeds = slotOrder.filter { slot ->
                    medicines.any { it.slots.contains(slot) }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    slotsWithMeds.forEachIndexed { index, slot ->
                        val medsForSlot = medicines.filter { it.slots.contains(slot) }
                        // Format stored "HH:mm" → "h:mm AM/PM" for display
                        val rawTime     = medsForSlot.firstOrNull()?.timings?.get(slot) ?: ""
                        val displayTime = AlarmScheduler.formatForDisplay(rawTime)
                        val medNames    = medsForSlot.joinToString(", ") { "${it.name} ${it.dosage}" }
                        val slotLabel   = if (isKannada) slotLabelsKn[slot] ?: slot else slot

                        ReminderSlotRow(
                            emoji    = slotEmojis[slot] ?: "💊",
                            slot     = slotLabel,
                            time     = displayTime,
                            medicine = medNames,
                            dotColor = slotDotColors[slot] ?: Primary500
                        )
                        if (index < slotsWithMeds.size - 1) {
                            HorizontalDivider(color = Divider)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderSlotRow(
    emoji: String,
    slot: String,
    time: String,
    medicine: String,
    dotColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(dotColor.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 20.sp) }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(slot, style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold, color = OnSurface)
                if (time.isNotBlank()) {
                    Text(time, style = MaterialTheme.typography.labelMedium, color = SubText)
                }
            }
            Text(medicine, style = MaterialTheme.typography.bodySmall, color = SubText)
        }
        Box(modifier = Modifier.size(10.dp).background(dotColor, CircleShape))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ArogyaSahayaTheme { HomeScreen(onNavigate = {}) }
}
