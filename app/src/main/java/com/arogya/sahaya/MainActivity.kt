package com.arogya.sahaya

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arogya.sahaya.ui.screens.*
import com.arogya.sahaya.ui.theme.ArogyaSahayaTheme
import com.arogya.sahaya.viewmodel.MedicineViewModel
import com.arogya.sahaya.viewmodel.ProfileViewModel
import com.arogya.sahaya.viewmodel.VitalViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Login           : Screen("login",           "Login",     Icons.Default.Login)
    object CreateProfile   : Screen("create_profile",  "Profile",   Icons.Default.Person)
    object Home            : Screen("home",            "Home",      Icons.Default.Home)
    object Medicine        : Screen("medicine",        "Reminders", Icons.Default.Alarm)
    object VitalLog        : Screen("vitallog",        "Vitals",    Icons.Default.MonitorHeart)
    object Asha            : Screen("asha",            "ASHA",      Icons.Default.Groups)
    object Profile         : Screen("profile",         "Profile",   Icons.Default.Person)
    object HealthTips      : Screen("healthtips",      "AI Tips",   Icons.Default.AutoAwesome)
    object PrescriptionScan: Screen("prescriptionscan","Scan",      Icons.Default.DocumentScanner)
}

val bottomNavItems = listOf(
    Screen.Home, Screen.Medicine, Screen.VitalLog, Screen.Asha, Screen.Profile
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ArogyaSahayaTheme { AppNavigation() } }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    // ViewModels live here — use refreshUserId() to re-point after login/logout
    val profileVm: ProfileViewModel = viewModel()
    val medicineVm: MedicineViewModel = viewModel()
    val vitalVm: VitalViewModel = viewModel()

    val profile  by profileVm.profile.collectAsStateWithLifecycle()
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()

    var isKannada by remember { mutableStateOf(false) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val startDestination = remember {
        val prefs = context.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("is_logged_in", false)) Screen.Home.route else Screen.Login.route
    }

    val mainRoutes = bottomNavItems.map { it.route }
    val showBottomBar = currentRoute in mainRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(tween(250)) { it },
                exit  = slideOutVertically(tween(200)) { it }
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.height(84.dp)
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        val label = when (screen) {
                            Screen.Home     -> if (isKannada) "ಮುಖ್ಯ"    else screen.label
                            Screen.Medicine -> if (isKannada) "ಔಷಧ"      else screen.label
                            Screen.VitalLog -> if (isKannada) "ಆರೋಗ್ಯ"   else screen.label
                            Screen.Asha     -> if (isKannada) "ಆಶಾ"      else screen.label
                            Screen.Profile  -> if (isKannada) "ಪ್ರೊಫೈಲ್" else screen.label
                            else -> screen.label
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick  = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = { Icon(screen.icon, contentDescription = label, modifier = Modifier.size(22.dp)) },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1, softWrap = false) },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.primary,
                                selectedTextColor   = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { 60 } },
            exitTransition   = { fadeOut(tween(180)) },
            popEnterTransition  = { fadeIn(tween(220)) },
            popExitTransition   = { fadeOut(tween(180)) + slideOutHorizontally(tween(200)) { 60 } }
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    profileVm = profileVm,
                    onLoginSuccess = { isNewUser ->
                        // After login credentials are saved, refresh ViewModels so they
                        // query the newly logged-in user's data — not the previous cached userId
                        profileVm.refreshUserId()
                        medicineVm.refreshUserId()
                        vitalVm.refreshUserId()

                        if (isNewUser) {
                            navController.navigate(Screen.CreateProfile.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.CreateProfile.route) {
                CreateProfileScreen(
                    onProfileSaved = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.CreateProfile.route) { inclusive = true }
                        }
                    },
                    vm = profileVm
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    isKannada        = isKannada,
                    onLanguageToggle = { isKannada = !isKannada },
                    profileName      = profile.name,
                    profile          = profile,
                    medicines        = medicines,
                    onNavigate       = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.Medicine.route) {
                MedicineScreen(
                    onBack    = { navController.popBackStack() },
                    isKannada = isKannada,
                    vm        = medicineVm
                )
            }

            composable(Screen.VitalLog.route) {
                VitalLogScreen(
                    onBack = { navController.popBackStack() },
                    vm = vitalVm
                )
            }

            composable(Screen.Asha.route) {
                AshaConnectScreen(
                    onBack    = { navController.popBackStack() },
                    isKannada = isKannada
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBack   = { navController.popBackStack() },
                    onLogout = {
                        // Clear session state
                        context.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("is_logged_in", false)
                            .putString("user_id", "guest")
                            .apply()
                        // Refresh VMs so they no longer show this user's data
                        profileVm.refreshUserId()
                        medicineVm.refreshUserId()
                        vitalVm.refreshUserId()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onProfileDeleted = {
                        val prefs = context.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)
                        val userId = prefs.getString("user_id", "guest") ?: "guest"

                        // Delete login credentials so this account can't be reused
                        if (userId != "guest" && userId.isNotBlank()) {
                            context.getSharedPreferences("arogya_credentials", Context.MODE_PRIVATE)
                                .edit()
                                .remove("cred_$userId")
                                .apply()
                        }

                        // Clear session
                        prefs.edit()
                            .putBoolean("is_logged_in", false)
                            .putString("user_id", "guest")
                            .apply()

                        // Refresh VMs
                        profileVm.refreshUserId()
                        medicineVm.refreshUserId()
                        vitalVm.refreshUserId()

                        // Go back to Login — user must re-register to use the app again
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    isKannada = isKannada,
                    vm        = profileVm
                )
            }

            composable(Screen.HealthTips.route) {
                HealthTipsScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.PrescriptionScan.route) {
                PrescriptionScanScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
