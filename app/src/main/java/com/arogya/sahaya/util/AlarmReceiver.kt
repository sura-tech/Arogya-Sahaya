package com.arogya.sahaya.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.arogya.sahaya.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("medicine_name") ?: "your medicine"
        val slot         = intent.getStringExtra("slot")          ?: "now"
        val timeString   = intent.getStringExtra("time_string")   ?: ""
        val notifId      = intent.getIntExtra("notif_id", System.currentTimeMillis().toInt())

        // ── Show notification ────────────────────────────────────────────────
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingTap = PendingIntent.getActivity(
            context, notifId, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val displayTime = AlarmScheduler.formatForDisplay(timeString)
        val timeLabel   = if (displayTime.isNotBlank()) " at $displayTime" else ""

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Medicine Reminder 💊")
            .setContentText("Time to take $medicineName ($slot dose$timeLabel)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("It's time for your $slot dose of $medicineName$timeLabel. Stay healthy! 🌿")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setContentIntent(pendingTap)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notifId, notification)

        // ── Auto-reschedule for the same time tomorrow (daily repeat) ────────
        if (timeString.isNotBlank()) {
            AlarmScheduler.scheduleAlarm(context, medicineName, slot, timeString)
        }
    }
}
