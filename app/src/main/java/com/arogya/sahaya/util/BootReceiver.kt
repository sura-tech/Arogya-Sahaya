package com.arogya.sahaya.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.arogya.sahaya.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON") return

        val prefs = context.getSharedPreferences("arogya_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val userId = prefs.getString("user_id", null)

        if (!isLoggedIn || userId.isNullOrBlank()) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(context).medicineDao()
                // Use .first() to get a single snapshot — avoids the Flow never completing
                val medicines = dao.getAllForUser(userId).first()
                medicines.forEach { entity ->
                    entity.slots.forEach { slot ->
                        val timeString = entity.timings[slot] ?: ""
                        AlarmScheduler.scheduleAlarm(
                            context      = context,
                            medicineName = entity.name,
                            slot         = slot,
                            timeString   = timeString
                        )
                    }
                }
            } catch (e: Exception) {
                // Silently ignore — alarms will reschedule next time user opens app
            } finally {
                pendingResult.finish()
            }
        }
    }
}
