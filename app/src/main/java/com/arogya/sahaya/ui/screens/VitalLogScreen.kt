package com.arogya.sahaya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arogya.sahaya.data.model.VitalEntry
import com.arogya.sahaya.ui.theme.*
import com.arogya.sahaya.viewmodel.VitalViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalLogScreen(onBack: () -> Unit, vm: VitalViewModel = viewModel()) {
    val entries by vm.entries.collectAsStateWithLifecycle()

    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vital Log", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary50)
                ) {
                    Row(modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary700)
                        Text("Vitals saved!", style = MaterialTheme.typography.bodyLarge, color = Primary700)
                    }
                }
            }

            Text("Log Today's Vitals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = systolic, onValueChange = { systolic = it },
                            label = { Text("Systolic (mmHg)", style = MaterialTheme.typography.bodySmall) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f), singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = diastolic, onValueChange = { diastolic = it },
                            label = { Text("Diastolic (mmHg)", style = MaterialTheme.typography.bodySmall) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f), singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    OutlinedTextField(
                        value = heartRate, onValueChange = { heartRate = it },
                        label = { Text("Heart Rate (bpm)", style = MaterialTheme.typography.bodyLarge) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Favorite, null, tint = Color(0xFFD32F2F)) }
                    )
                    Button(
                        onClick = {
                            val sys = systolic.toIntOrNull() ?: 0
                            val dia = diastolic.toIntOrNull() ?: 0
                            val hr = heartRate.toIntOrNull() ?: 0
                            if (sys > 0 && dia > 0 && hr > 0) {
                                val day = SimpleDateFormat("EEE", Locale.getDefault()).format(Date())
                                val date = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
                                vm.add(day, sys, dia, hr, date)
                                systolic = ""; diastolic = ""; heartRate = ""
                                showSuccess = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Save Vitals", style = MaterialTheme.typography.labelLarge) }
                }
            }

            Text("7-Day Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            VitalChart(entries)

            Text("Recent Readings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            entries.reversed().forEach { VitalHistoryRow(it) }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun VitalChart(vitals: List<VitalEntry>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                LegendItem(Color(0xFFD32F2F), "Systolic BP")
                LegendItem(Teal500, "Heart Rate")
            }
            Spacer(Modifier.height(12.dp))
            if (vitals.size < 2) {
                Box(Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                    Text("Not enough data", style = MaterialTheme.typography.bodyLarge, color = SubText)
                }
            } else {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                    drawLineChart(vitals.map { it.systolic.toFloat() }, 50f, 180f, Color(0xFFD32F2F))
                    drawLineChart(vitals.map { it.heartRate.toFloat() }, 50f, 180f, Teal500)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    vitals.forEach { Text(it.day, style = MaterialTheme.typography.labelSmall, color = SubText) }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, style = MaterialTheme.typography.labelMedium, color = SubText)
    }
}

fun DrawScope.drawLineChart(values: List<Float>, minVal: Float, maxVal: Float, color: Color) {
    if (values.size < 2) return
    val stepX = size.width / (values.size - 1)
    fun x(i: Int) = i * stepX
    fun y(v: Float) = size.height - ((v - minVal) / (maxVal - minVal)) * size.height
    val path = Path().apply {
        moveTo(x(0), y(values[0]))
        for (i in 1 until values.size) lineTo(x(i), y(values[i]))
    }
    drawPath(path, color, style = Stroke(width = 4f))
    values.forEachIndexed { i, v ->
        drawCircle(color, 7f, Offset(x(i), y(v)))
        drawCircle(Color.White, 4f, Offset(x(i), y(v)))
    }
}

@Composable
fun VitalHistoryRow(entry: VitalEntry) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(entry.date.ifBlank { entry.day }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(entry.day, style = MaterialTheme.typography.labelSmall, color = SubText)
            }
            Text("${entry.systolic}/${entry.diastolic} mmHg", style = MaterialTheme.typography.bodyLarge)
            Text("${entry.heartRate} bpm", style = MaterialTheme.typography.bodyLarge, color = Teal500)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VitalLogScreenPreview() {
    ArogyaSahayaTheme { VitalLogScreen(onBack = {}) }
}
