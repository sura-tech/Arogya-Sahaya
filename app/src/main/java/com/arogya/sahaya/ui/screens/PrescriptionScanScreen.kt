package com.arogya.sahaya.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arogya.sahaya.ui.theme.*
import com.arogya.sahaya.util.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ParsedMedicine(val name: String, val dosage: String, val slots: String)

fun parseMedicineLines(raw: String): List<ParsedMedicine> {
    return raw.lines()
        .filter { it.contains("MEDICINE:") }
        .mapNotNull { line ->
            try {
                val name = line.substringAfter("MEDICINE:").substringBefore("|").trim()
                val dosage = line.substringAfter("DOSAGE:").substringBefore("|").trim()
                val slots = line.substringAfter("SLOTS:").trim()
                if (name.isNotBlank()) ParsedMedicine(name, dosage, slots) else null
            } catch (e: Exception) { null }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScanScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gemini = remember { GeminiService() }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var parsedMedicines by remember { mutableStateOf<List<ParsedMedicine>>(emptyList()) }
    var rawResponse by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
        parsedMedicines = emptyList()
        rawResponse = ""
        errorMessage = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescription Scan", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0277BD),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF01579B), Color(0xFF0288D1))),
                        RoundedCornerShape(20.dp)).padding(20.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(56.dp).background(Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.DocumentScanner, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                    Column {
                        Text("AI Prescription Scanner", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Powered by Gemini Vision — auto-extract medicines",
                            style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }

            // Upload area
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, if (selectedUri != null) Color(0xFF0277BD) else Divider, RoundedCornerShape(18.dp))
                    .background(if (selectedUri != null) Color(0xFFE1F5FE) else Background)
                    .height(if (selectedUri != null) 220.dp else 140.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedUri != null) {
                    AsyncImage(model = selectedUri, contentDescription = "Prescription",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Fit)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AddPhotoAlternate, null,
                            tint = SubText, modifier = Modifier.size(48.dp))
                        Text("Tap below to pick a prescription image",
                            style = MaterialTheme.typography.bodyLarge, color = SubText)
                    }
                }
            }

            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, null, tint = Color(0xFF0277BD))
                Spacer(Modifier.width(8.dp))
                Text(if (selectedUri != null) "Change Image" else "Pick from Gallery",
                    style = MaterialTheme.typography.labelLarge, color = Color(0xFF0277BD))
            }

            if (selectedUri != null) {
                Button(
                    onClick = {
                        scope.launch {
                            isAnalyzing = true; errorMessage = ""; parsedMedicines = emptyList()
                            try {
                                val bitmap = withContext(Dispatchers.IO) {
                                    context.contentResolver.openInputStream(selectedUri!!)
                                        ?.use { BitmapFactory.decodeStream(it) }
                                }
                                if (bitmap != null) {
                                    rawResponse = gemini.parsePrescription(bitmap)
                                    parsedMedicines = parseMedicineLines(rawResponse)
                                    if (parsedMedicines.isEmpty()) {
                                        errorMessage = "No medicines detected. Try a clearer image."
                                    }
                                } else {
                                    errorMessage = "Could not read the image."
                                }
                            } catch (e: Exception) {
                                errorMessage = "Scan failed: ${e.message}"
                            } finally {
                                isAnalyzing = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                    enabled = !isAnalyzing
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp),
                            color = Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                    }
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isAnalyzing) "Analysing..." else "Scan with Gemini AI",
                        style = MaterialTheme.typography.labelLarge)
                }
            }

            if (errorMessage.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFD32F2F))
                        Text(errorMessage, style = MaterialTheme.typography.bodyLarge, color = Color(0xFFD32F2F))
                    }
                }
            }

            AnimatedVisibility(visible = parsedMedicines.isNotEmpty(), enter = fadeIn()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, null, tint = Primary700)
                        Text("${parsedMedicines.size} medicine(s) detected",
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    parsedMedicines.forEach { med ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(3.dp)) {
                            Row(modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(44.dp).background(Primary50, CircleShape),
                                    contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Medication, null,
                                        tint = Primary700, modifier = Modifier.size(24.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(med.name, style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold)
                                    Text("Dosage: ${med.dosage}", style = MaterialTheme.typography.bodySmall, color = SubText)
                                    Text("Slots: ${med.slots}", style = MaterialTheme.typography.bodySmall, color = SubText)
                                }
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = { /* navigate to add medicine screen */ },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add All to Medicine List", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Key, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                    Text("Set your Gemini API key in util/Constants.kt. Get it free at aistudio.google.com/app/apikey",
                        style = MaterialTheme.typography.bodySmall, color = Color(0xFF92400E))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrescriptionScanScreenPreview() {
    ArogyaSahayaTheme { PrescriptionScanScreen(onBack = {}) }
}
