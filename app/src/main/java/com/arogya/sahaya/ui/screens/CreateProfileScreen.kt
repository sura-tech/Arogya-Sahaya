package com.arogya.sahaya.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arogya.sahaya.data.model.UserProfile
import com.arogya.sahaya.ui.theme.*
import com.arogya.sahaya.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    onProfileSaved: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var chronicConditions by remember { mutableStateOf("") }
    var emergencyName by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create Your Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Primary700, Primary500)))
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Text(
                        "Welcome to Arogya Sahaya!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Please set up your health profile to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Personal Info Section
                SectionHeader(icon = Icons.Default.Person, title = "Personal Information")

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Full Name *", style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Primary700)
                    },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Name is required", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age (years)", style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = {
                        Icon(Icons.Default.Cake, contentDescription = null, tint = Primary700)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                // Health Section
                SectionHeader(icon = Icons.Default.MedicalServices, title = "Health Information")

                OutlinedTextField(
                    value = chronicConditions,
                    onValueChange = { chronicConditions = it },
                    label = { Text("Chronic Conditions", style = MaterialTheme.typography.bodyLarge) },
                    placeholder = { Text("e.g. Diabetes, Hypertension", style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = {
                        Icon(Icons.Default.Healing, contentDescription = null, tint = Primary700)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 3
                )

                // Emergency Contact Section
                SectionHeader(icon = Icons.Default.Call, title = "Emergency Contact")

                OutlinedTextField(
                    value = emergencyName,
                    onValueChange = { emergencyName = it },
                    label = { Text("Contact Name", style = MaterialTheme.typography.bodyLarge) },
                    placeholder = { Text("e.g. Ramesh (Son)", style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = {
                        Icon(Icons.Default.PersonOutline, contentDescription = null, tint = Primary700)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Contact Phone Number", style = MaterialTheme.typography.bodyLarge) },
                    placeholder = { Text("+91 98765 43210", style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Primary700)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            nameError = true
                            return@Button
                        }
                        val profile = UserProfile(
                            name = name.trim(),
                            age = age.trim(),
                            chronicConditions = chronicConditions.trim(),
                            emergencyContact = emergencyContact.trim(),
                            emergencyName = emergencyName.trim()
                        )
                        vm.save(profile)
                        onProfileSaved()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary700)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Save Profile & Continue",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize
                    )
                }

                TextButton(
                    onClick = { onProfileSaved() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Skip for now",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SubText
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Primary50, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Primary700, modifier = Modifier.size(20.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Primary900
        )
    }
    HorizontalDivider(color = Divider)
}
