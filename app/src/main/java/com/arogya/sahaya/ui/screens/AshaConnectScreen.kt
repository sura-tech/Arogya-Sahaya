package com.arogya.sahaya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arogya.sahaya.ui.theme.*
import java.util.Calendar

data class AshaEvent(val date: String, val title: String, val location: String, val type: String, val day: Int, val month: Int)

val ashaEvents = listOf(
    AshaEvent("May 3, 2026",  "ASHA Worker Visit",      "Village Panchayat Hall",         "ASHA Visit",  3,  5),
    AshaEvent("May 5, 2026",  "Eye Check-up Camp",      "Primary Health Centre, Tumkur",  "Health Camp", 5,  5),
    AshaEvent("May 10, 2026", "Diabetes Screening",     "Gram Panchayat Office",          "Health Camp", 10, 5),
    AshaEvent("May 14, 2026", "ASHA Worker Visit",      "Village Panchayat Hall",         "ASHA Visit",  14, 5),
    AshaEvent("May 18, 2026", "Blood Pressure Check",   "Sub-District Hospital",          "Health Camp", 18, 5),
    AshaEvent("May 22, 2026", "Vaccination Drive",      "Aanganwadi Centre",              "Health Camp", 22, 5),
    AshaEvent("May 28, 2026", "ASHA Worker Visit",      "Village Panchayat Hall",         "ASHA Visit",  28, 5),
    AshaEvent("June 1, 2026", "General Health Camp",    "Primary Health Centre, Tumkur",  "Health Camp", 1,  6),
    AshaEvent("June 8, 2026", "Dental Check-up Camp",   "Mobile Health Van",              "Health Camp", 8,  6),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AshaConnectScreen(onBack: () -> Unit, isKannada: Boolean = false) {
    // Show May 2026 calendar
    val calendarMonth = 5 // May
    val calendarYear = 2026
    val eventDaysInMay = ashaEvents.filter { it.month == 5 }.map { it.day }.toSet()
    val ashaVisitDaysInMay = ashaEvents.filter { it.month == 5 && it.type == "ASHA Visit" }.map { it.day }.toSet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isKannada) "ಆಶಾ ಸಂಪರ್ಕ" else "ASHA Connect", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal700,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Teal100)) {
                    Row(modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Teal700)
                        Text(
                            if (isKannada) "ನಿಮ್ಮ ಪ್ರದೇಶದಲ್ಲಿ ಆರೋಗ್ಯ ಶಿಬಿರಗಳು ಮತ್ತು ಆಶಾ ಕಾರ್ಯಕರ್ತರ ಭೇಟಿಗಳು."
                            else "Upcoming health camps and ASHA worker visits in your area.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Calendar
            item {
                AshaCalendar(
                    month = calendarMonth,
                    year = calendarYear,
                    eventDays = eventDaysInMay,
                    ashaVisitDays = ashaVisitDaysInMay,
                    isKannada = isKannada
                )
            }

            item {
                Text(
                    if (isKannada) "ಮುಂದಿನ ಕಾರ್ಯಕ್ರಮಗಳು" else "Upcoming Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(ashaEvents) { AshaEventCard(it, isKannada) }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun AshaCalendar(
    month: Int,
    year: Int,
    eventDays: Set<Int>,
    ashaVisitDays: Set<Int>,
    isKannada: Boolean = false
) {
    val monthName = if (isKannada) "ಮೇ 2026" else "May 2026"
    val dayLabels = if (isKannada)
        listOf("ಭಾ", "ಸೋ", "ಮಂ", "ಬು", "ಗು", "ಶು", "ಶ")
    else
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    // May 2026 starts on Friday (index 5)
    val firstDayOfWeek = 5
    val daysInMonth = 31

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Teal700, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(monthName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Teal700)
            }

            // Day labels row
            Row(modifier = Modifier.fillMaxWidth()) {
                dayLabels.forEach { day ->
                    Text(
                        day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = SubText,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Calendar grid
            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7

            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNum = cellIndex - firstDayOfWeek + 1
                        Box(
                            modifier = Modifier.weight(1f).aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNum in 1..daysInMonth) {
                                val isAshaVisit = dayNum in ashaVisitDays
                                val isEvent = dayNum in eventDays
                                val bgColor = when {
                                    isAshaVisit -> Teal700
                                    isEvent -> Primary700
                                    else -> Color.Transparent
                                }
                                val textColor = when {
                                    isAshaVisit || isEvent -> Color.White
                                    else -> OnSurface
                                }
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(bgColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$dayNum",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textColor,
                                        fontWeight = if (isEvent || isAshaVisit) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(10.dp).background(Teal700, CircleShape))
                    Text(if (isKannada) "ಆಶಾ ಭೇಟಿ" else "ASHA Visit", style = MaterialTheme.typography.labelSmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(10.dp).background(Primary700, CircleShape))
                    Text(if (isKannada) "ಆರೋಗ್ಯ ಶಿಬಿರ" else "Health Camp", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun AshaEventCard(event: AshaEvent, isKannada: Boolean = false) {
    val isAshaVisit = event.type == "ASHA Visit"
    val chipColor = if (isAshaVisit) Teal700 else Primary700
    val chipBg = if (isAshaVisit) Teal100 else Primary50

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)) {
        Row(modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(chipBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isAshaVisit) Icons.Default.People else Icons.Default.MedicalServices,
                    contentDescription = null, tint = chipColor, modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(event.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(event.date, style = MaterialTheme.typography.bodySmall, color = SubText)
                Text(event.location, style = MaterialTheme.typography.bodySmall, color = SubText)
            }
            Surface(color = chipBg, shape = RoundedCornerShape(8.dp)) {
                Text(
                    if (isKannada && isAshaVisit) "ಆಶಾ ಭೇಟಿ"
                    else if (isKannada) "ಆರೋಗ್ಯ ಶಿಬಿರ"
                    else event.type,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall, color = chipColor, fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AshaConnectScreenPreview() {
    ArogyaSahayaTheme { AshaConnectScreen(onBack = {}) }
}
