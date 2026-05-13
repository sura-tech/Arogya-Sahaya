package com.arogya.sahaya.ui.screens

import android.content.Context
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arogya.sahaya.ui.theme.*
import com.arogya.sahaya.viewmodel.ProfileViewModel

enum class LoginMethod { EMAIL, PHONE }

fun saveLoginState(context: Context, isLoggedIn: Boolean, userId: String = "") {
    val prefs = context.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .putBoolean("is_logged_in", isLoggedIn)
        .putString("user_id", userId)
        .apply()
}

fun saveCredentials(context: Context, userId: String, password: String) {
    val prefs = context.getSharedPreferences("arogya_credentials", Context.MODE_PRIVATE)
    prefs.edit().putString("cred_$userId", password).apply()
}

fun verifyCredentials(context: Context, userId: String, password: String): Boolean {
    val prefs = context.getSharedPreferences("arogya_credentials", Context.MODE_PRIVATE)
    val stored = prefs.getString("cred_$userId", null) ?: return false
    return stored == password
}

fun isRegistered(context: Context, userId: String): Boolean {
    val prefs = context.getSharedPreferences("arogya_credentials", Context.MODE_PRIVATE)
    return prefs.contains("cred_$userId")
}

@Composable
fun LoginScreen(
    profileVm: ProfileViewModel,
    onLoginSuccess: (isNewUser: Boolean) -> Unit
) {
    val context = LocalContext.current
    var loginMethod by remember { mutableStateOf(LoginMethod.EMAIL) }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var showInvalidDialog by remember { mutableStateOf(false) }

    if (showInvalidDialog) {
        AlertDialog(
            onDismissRequest = { showInvalidDialog = false },
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = {
                Text(
                    "Login Failed",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    "Invalid login details. Please check your credentials and try again.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { showInvalidDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Try Again", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Primary900, Primary700, Background)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            Box(
                modifier = Modifier.size(80.dp).background(
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(24.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MedicalServices, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Arogya Sahaya", style = MaterialTheme.typography.headlineMedium,
                color = Color.White, fontWeight = FontWeight.Bold
            )
            Text(
                "Digital Health Companion", style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (isSignUp) "Create Account" else "Welcome Back",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Primary900
                    )
                    Text(
                        if (isSignUp) "Fill in your details to register" else "Sign in to continue",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText
                    )

                    TabRow(
                        selectedTabIndex = if (loginMethod == LoginMethod.EMAIL) 0 else 1,
                        containerColor = Primary50,
                        contentColor = Primary700,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Tab(
                            selected = loginMethod == LoginMethod.EMAIL,
                            onClick = { loginMethod = LoginMethod.EMAIL; emailOrPhone = ""; errorMessage = "" },
                            icon = { Icon(Icons.Default.Email, contentDescription = null) },
                            text = { Text("Email", style = MaterialTheme.typography.labelLarge) }
                        )
                        Tab(
                            selected = loginMethod == LoginMethod.PHONE,
                            onClick = { loginMethod = LoginMethod.PHONE; emailOrPhone = ""; errorMessage = "" },
                            icon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            text = { Text("Phone", style = MaterialTheme.typography.labelLarge) }
                        )
                    }

                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it; errorMessage = "" },
                        label = {
                            Text(
                                if (loginMethod == LoginMethod.EMAIL) "Email Address" else "Phone Number",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        placeholder = {
                            Text(
                                if (loginMethod == LoginMethod.EMAIL) "you@example.com" else "+91 98765 43210",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        leadingIcon = {
                            Icon(
                                if (loginMethod == LoginMethod.EMAIL) Icons.Default.Email else Icons.Default.Phone,
                                contentDescription = null, tint = Primary700
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (loginMethod == LoginMethod.EMAIL) KeyboardType.Email else KeyboardType.Phone
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = "" },
                        label = { Text("Password", style = MaterialTheme.typography.bodyLarge) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Primary700) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null, tint = Primary700
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (isSignUp) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; errorMessage = "" },
                            label = { Text("Confirm Password", style = MaterialTheme.typography.bodyLarge) },
                            leadingIcon = {
                                Icon(Icons.Default.LockOpen, contentDescription = null, tint = Primary700)
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null, tint = Primary700
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    if (errorMessage.isNotBlank()) {
                        Text(
                            errorMessage, style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            when {
                                emailOrPhone.isBlank() -> errorMessage =
                                    if (loginMethod == LoginMethod.EMAIL) "Enter your email." else "Enter your phone number."
                                password.isBlank() -> errorMessage = "Enter your password."
                                loginMethod == LoginMethod.EMAIL && !emailOrPhone.contains("@") ->
                                    errorMessage = "Enter a valid email."
                                loginMethod == LoginMethod.PHONE && emailOrPhone.length < 10 ->
                                    errorMessage = "Enter a valid phone number."
                                isSignUp && confirmPassword.isBlank() ->
                                    errorMessage = "Please confirm your password."
                                isSignUp && password != confirmPassword ->
                                    errorMessage = "Passwords do not match."
                                isSignUp -> {
                                    if (isRegistered(context, emailOrPhone)) {
                                        errorMessage = "This account already exists. Please log in."
                                    } else {
                                        val prefs = context.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)
                                        val currentId = prefs.getString("user_id", "guest") ?: "guest"

                                        saveCredentials(context, emailOrPhone, password)
                                        saveLoginState(context, true, emailOrPhone)
                                        
                                        if (currentId == "guest") {
                                            profileVm.migrateGuestData(emailOrPhone) {
                                                onLoginSuccess(true)
                                            }
                                        } else {
                                            onLoginSuccess(true)
                                        }
                                    }
                                }
                                else -> {
                                    if (!isRegistered(context, emailOrPhone)) {
                                        showInvalidDialog = true
                                    } else if (!verifyCredentials(context, emailOrPhone, password)) {
                                        showInvalidDialog = true
                                    } else {
                                        saveLoginState(context, true, emailOrPhone)
                                        onLoginSuccess(false)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary700)
                    ) {
                        Text(
                            if (isSignUp) "Register" else "Sign In",
                            style = MaterialTheme.typography.labelLarge, fontSize = 18.sp
                        )
                    }

                    if (!isSignUp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f))
                            Text("OR", style = MaterialTheme.typography.labelMedium, color = SubText)
                            HorizontalDivider(modifier = Modifier.weight(1f))
                        }

                        OutlinedButton(
                            onClick = {
                                saveLoginState(context, true, "guest")
                                onLoginSuccess(false)
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary700)
                        ) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Continue as Guest",
                                style = MaterialTheme.typography.labelLarge, fontSize = 18.sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isSignUp) "Already have an account?" else "New here?",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        TextButton(onClick = {
                            isSignUp = !isSignUp
                            errorMessage = ""
                            password = ""
                            confirmPassword = ""
                        }) {
                            Text(
                                if (isSignUp) "Log In" else "Register",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Primary700,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
