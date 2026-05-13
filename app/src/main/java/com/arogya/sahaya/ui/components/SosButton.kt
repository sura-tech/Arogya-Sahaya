package com.arogya.sahaya.ui.components

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arogya.sahaya.ui.theme.ArogyaSahayaTheme
import com.arogya.sahaya.ui.theme.RedSOS

@Composable
fun SosButton(
    emergencyName: String = "",
    emergencyContact: String = "",
    senderName: String = ""
) {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSentDialog by remember { mutableStateOf(false) }
    var smsSendResult by remember { mutableStateOf("") }

    val displayName    = emergencyName.ifBlank { "Emergency Contact" }
    val displayContact = emergencyContact.ifBlank { "Not set" }
    val displaySender  = senderName.ifBlank { "User" }

    // Pulsing ring animation
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f, targetValue = 1.10f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOut), RepeatMode.Reverse),
        label = "scale"
    )

    // Step 1 — Confirm before sending
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFFFEBEE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null,
                        tint = RedSOS, modifier = Modifier.size(36.dp))
                }
            },
            title = {
                Text(
                    "Send Emergency Alert?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = RedSOS,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    "This will send an emergency SMS to:\n\n" +
                    "$displayName\n$displayContact\n\n" +
                    "Only confirm if you truly need help.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        smsSendResult = sendSosMessage(
                            context         = context,
                            phoneNumber     = emergencyContact,
                            senderName      = displaySender,
                            emergencyName   = displayName
                        )
                        showSentDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedSOS),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Yes, Send SOS Alert", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmDialog = false },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("Cancel — I am safe", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    // Step 2 — Alert sent confirmation
    if (showSentDialog) {
        AlertDialog(
            onDismissRequest = { showSentDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFFFEBEE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Sos, contentDescription = null,
                        tint = RedSOS, modifier = Modifier.size(36.dp))
                }
            },
            title = {
                Text(
                    if (smsSendResult == "success") "Alert Sent!" else "Alert Attempted",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = RedSOS,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                )
            },
            text = {
                val bodyText = if (smsSendResult == "success") {
                    "Emergency SMS sent to:\n\n$displayName — $displayContact\n\n" +
                    "\"$displaySender needs urgent help! Please respond immediately.\"\n— Arogya Sahaya"
                } else {
                    "Could not send SMS automatically.\n\nPlease call $displayName directly:\n$displayContact\n\n" +
                    "(Check SIM card and SEND_SMS permission)"
                }
                Text(
                    bodyText,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSentDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RedSOS),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("OK, I understand", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    // SOS Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFB71C1C), Color(0xFFE53935))))
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Emergency SOS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Tap to alert your emergency contact",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    if (displayName != "Emergency Contact" || displayContact != "Not set")
                        "$displayName · $displayContact"
                    else
                        "Set emergency contact in Profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
            Spacer(Modifier.width(16.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(pulse)
                    .size(72.dp)
                    .background(Color.White, CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { showConfirmDialog = true })
                    }
            ) {
                Icon(
                    Icons.Default.Sos,
                    contentDescription = "SOS",
                    modifier = Modifier.size(38.dp),
                    tint = RedSOS
                )
            }
        }
    }
}

private fun sendSosMessage(
    context: Context,
    phoneNumber: String,
    senderName: String,
    emergencyName: String
): String {
    if (phoneNumber.isBlank()) return "no_number"
    return try {
        val message = "$senderName needs urgent help! Please respond immediately. - Sent via Arogya Sahaya"
        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
        // Split if message is longer than 160 chars
        val parts = smsManager.divideMessage(message)
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
        "success"
    } catch (e: Exception) {
        "error: ${e.message}"
    }
}

@Preview
@Composable
fun SosButtonPreview() {
    ArogyaSahayaTheme {
        Box(Modifier.padding(16.dp)) {
            SosButton(
                emergencyName    = "Ramesh Kumar",
                emergencyContact = "+91 98765 43210",
                senderName       = "Lakshmi Devi"
            )
        }
    }
}
