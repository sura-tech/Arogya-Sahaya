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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arogya.sahaya.data.model.UserProfile
import com.arogya.sahaya.ui.theme.*
import com.arogya.sahaya.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit = {},
    onProfileDeleted: () -> Unit = {},
    isKannada: Boolean = false,
    vm: ProfileViewModel = viewModel()
) {
    val profile by vm.profile.collectAsStateWithLifecycle()
    var isEditing by remember { mutableStateOf(false) }
    var draft by remember(profile) { mutableStateOf(profile) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ── Logout Confirmation ───────────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            title = {
                Text(
                    if (isKannada) "ಲಾಗ್ ಔಟ್" else "Log Out",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    if (isKannada) "ನೀವು ಖಂಡಿತವಾಗಿ ಲಾಗ್ ಔಟ್ ಆಗಲು ಬಯಸುವಿರಾ?" else "Are you sure you want to log out?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogout() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (isKannada) "ಲಾಗ್ ಔಟ್" else "Log Out", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }, shape = RoundedCornerShape(12.dp)) {
                    Text(if (isKannada) "ರದ್ದು" else "Cancel", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    // ── Delete Profile Confirmation ───────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFFFEBEE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    if (isKannada) "ಪ್ರೊಫೈಲ್ ಅಳಿಸಿ" else "Delete Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    if (isKannada)
                        "ಇದು ನಿಮ್ಮ ಎಲ್ಲಾ ಪ್ರೊಫೈಲ್ ಡೇಟಾವನ್ನು ಶಾಶ್ವತವಾಗಿ ಅಳಿಸುತ್ತದೆ. ಇದನ್ನು ರದ್ದು ಮಾಡಲು ಸಾಧ್ಯವಿಲ್ಲ."
                    else
                        "This will permanently delete your profile data including name, age, chronic conditions and emergency contact.\n\nThis action cannot be undone.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        vm.deleteProfile { onProfileDeleted() }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (isKannada) "ಹೌದು, ಅಳಿಸಿ" else "Yes, Delete Profile",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
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
                        if (isKannada) "ನನ್ನ ಪ್ರೊಫೈಲ್" else "My Profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Profile Header Card ───────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Primary700),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(44.dp)
                        )
                    }
                    Text(
                        profile.name.ifBlank { if (isKannada) "ನಿಮ್ಮ ಹೆಸರು ಹೊಂದಿಸಿ" else "Set Your Name" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                    Text(
                        "${if (isKannada) "ವಯಸ್ಸು" else "Age"}: ${profile.age.ifBlank { "--" }} ${if (isKannada) "ವರ್ಷ" else "years"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // ── Edit Mode ────────────────────────────────────────────────────
            if (isEditing) {
                Text(
                    if (isKannada) "ಪ್ರೊಫೈಲ್ ಸಂಪಾದಿಸಿ" else "Edit Profile",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold
                )
                ProfileEditField(if (isKannada) "ಪೂರ್ಣ ಹೆಸರು" else "Full Name", draft.name) { draft = draft.copy(name = it) }
                ProfileEditField(if (isKannada) "ವಯಸ್ಸು" else "Age", draft.age) { draft = draft.copy(age = it) }
                ProfileEditField(if (isKannada) "ದೀರ್ಘಕಾಲಿಕ ಕಾಯಿಲೆಗಳು" else "Chronic Conditions", draft.chronicConditions) { draft = draft.copy(chronicConditions = it) }
                ProfileEditField(if (isKannada) "ತುರ್ತು ಸಂಪರ್ಕ ಹೆಸರು" else "Emergency Contact Name", draft.emergencyName) { draft = draft.copy(emergencyName = it) }
                ProfileEditField(if (isKannada) "ತುರ್ತು ದೂರವಾಣಿ ಸಂಖ್ಯೆ" else "Emergency Phone Number", draft.emergencyContact) { draft = draft.copy(emergencyContact = it) }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { vm.save(draft); isEditing = false },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isKannada) "ಉಳಿಸಿ" else "Save", style = MaterialTheme.typography.labelLarge)
                    }
                    OutlinedButton(
                        onClick = { draft = profile; isEditing = false },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isKannada) "ರದ್ದು" else "Cancel", style = MaterialTheme.typography.labelLarge)
                    }
                }
            } else {
                // ── View Mode ────────────────────────────────────────────────
                Text(
                    if (isKannada) "ಆರೋಗ್ಯ ಮಾಹಿತಿ" else "Health Information",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold
                )
                ProfileInfoCard(
                    Icons.Default.MedicalServices,
                    if (isKannada) "ದೀರ್ಘಕಾಲಿಕ ಕಾಯಿಲೆಗಳು" else "Chronic Conditions",
                    profile.chronicConditions.ifBlank { if (isKannada) "ಯಾವುದೂ ಇಲ್ಲ" else "None listed" }
                )
                ProfileInfoCard(
                    Icons.Default.Call,
                    if (isKannada) "ತುರ್ತು ಸಂಪರ್ಕ" else "Emergency Contact",
                    if (profile.emergencyName.isBlank() && profile.emergencyContact.isBlank())
                        "Not set"
                    else
                        "${profile.emergencyName}\n${profile.emergencyContact}"
                )

                HorizontalDivider(color = Divider)

                // Edit Profile
                Button(
                    onClick = { draft = profile; isEditing = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isKannada) "ಪ್ರೊಫೈಲ್ ಸಂಪಾದಿಸಿ" else "Edit Profile", style = MaterialTheme.typography.labelLarge)
                }

                // Delete Profile
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isKannada) "ಪ್ರೊಫೈಲ್ ಅಳಿಸಿ" else "Delete Profile",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Log Out
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isKannada) "ಲಾಗ್ ಔಟ್" else "Log Out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun ProfileEditField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ProfileInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Primary50, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Primary700, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = SubText)
                Spacer(Modifier.height(4.dp))
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ArogyaSahayaTheme { ProfileScreen(onBack = {}) }
}
