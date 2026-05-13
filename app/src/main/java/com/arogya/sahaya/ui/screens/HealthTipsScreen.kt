package com.arogya.sahaya.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arogya.sahaya.ui.theme.*
import com.arogya.sahaya.util.GeminiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTipsScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gemini = remember { GeminiService() }

    var tipsText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var hasLoaded by remember { mutableStateOf(false) }

    val vitalsContext = "Systolic BP: 129 mmHg (slightly elevated), Diastolic BP: 83 mmHg, " +
            "Heart Rate: 75 bpm, Patient: 67-year-old female, Conditions: Type 2 Diabetes, Hypertension"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Health Tips", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleAI,
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
            // Hero banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(PurpleAI, Color(0xFF9C27B0))),
                        RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(56.dp).background(Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                    Column {
                        Text("Gemini AI Health Tips", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Personalised advice based on your vitals",
                            style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }

            // Vitals context card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleLight)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your Recent Vitals", style = MaterialTheme.typography.labelLarge,
                        color = PurpleAI, fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        VitalChip("BP", "129/83 mmHg")
                        VitalChip("HR", "75 bpm")
                        VitalChip("Age", "67 yrs")
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true; errorMessage = ""; tipsText = ""
                        try {
                            tipsText = gemini.getHealthTips(vitalsContext)
                            hasLoaded = true
                        } catch (e: Exception) {
                            errorMessage = "Could not fetch tips. Check your API key in Constants.kt"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleAI),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp),
                        color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                }
                Icon(Icons.Default.AutoAwesome, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isLoading) "Generating..." else "Get AI Health Tips",
                    style = MaterialTheme.typography.labelLarge)
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

            AnimatedVisibility(visible = hasLoaded && tipsText.isNotBlank(),
                enter = fadeIn() + slideInVertically()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Lightbulb, null, tint = PurpleAI)
                            Text("Your Personalised Tips", style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = PurpleAI)
                        }
                        HorizontalDivider(color = PurpleLight)
                        Text(tipsText, style = MaterialTheme.typography.bodyLarge,
                            color = OnSurface, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight)
                    }
                }
            }

            // API key reminder
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Key, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                    Text("Add your Gemini API key in util/Constants.kt → GEMINI_API_KEY. " +
                            "Get it free at aistudio.google.com/app/apikey",
                        style = MaterialTheme.typography.bodySmall, color = Color(0xFF92400E))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun VitalChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = PurpleAI)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = OnSurface)
    }
}

@Preview(showBackground = true)
@Composable
fun HealthTipsScreenPreview() {
    ArogyaSahayaTheme { HealthTipsScreen(onBack = {}) }
}
