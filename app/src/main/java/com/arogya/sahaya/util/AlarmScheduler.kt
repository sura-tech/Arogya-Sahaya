package com.arogya.sahaya.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {

    fun scheduleAlarm(
        context: Context,
        medicineName: String,
        slot: String,
        timeString: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val cal = parseTime(timeString) ?: defaultTimeForSlot(slot)

        // Always schedule for the future — advance to next day if the time already passed today
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val notifId = (medicineName + slot).hashCode()
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicine_name", medicineName)
            putExtra("slot",          slot)
            putExtra("time_string",   timeString)   // stored so receiver can re-schedule tomorrow
            putExtra("notif_id",      notifId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setAlarmClock for maximum precision (Android 5.0+)
        // This is the "gold standard" for exact timing and works even in Doze mode.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val alarmClockInfo = AlarmManager.AlarmClockInfo(cal.timeInMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context, medicineName: String, slot: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notifId = (medicineName + slot).hashCode()
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Accepts both formats:
     *  • "HH:mm"     — 24-hour (e.g. "20:00")  stored by the new TimePicker
     *  • "h:mm AM/PM"— 12-hour (e.g. "8:00 PM") legacy free-text entries
     */
    fun parseTime(timeString: String): Calendar? {
        return try {
            val trimmed = timeString.trim()
            val upper   = trimmed.uppercase()
            val isPm = upper.endsWith("PM")
            val isAm = upper.endsWith("AM")

            val timePart = trimmed
                .replace(Regex("(?i)\\s*PM\\s*$"), "")
                .replace(Regex("(?i)\\s*AM\\s*$"), "")
                .trim()

            val parts  = timePart.split(":")
            var hour   = parts[0].trim().toInt()
            val minute = if (parts.size > 1) parts[1].trim().toInt() else 0

            // Only adjust for AM/PM if that suffix was present
            if (isPm && hour != 12) hour += 12
            if (isAm && hour == 12) hour  = 0
            // Pure 24-h "HH:mm" — no suffix, no adjustment needed

            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE,      minute)
                set(Calendar.SECOND,      0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Convert any stored time string to a user-readable "h:mm AM/PM" label. */
    fun formatForDisplay(timeString: String): String {
        if (timeString.isBlank()) return ""
        val upper = timeString.trim().uppercase()
        // Already in 12-h format — return as-is
        if (upper.contains("AM") || upper.contains("PM")) return timeString.trim()
        return try {
            val parts  = timeString.trim().split(":")
            val hour   = parts[0].toInt()
            val minute = if (parts.size > 1) parts[1].toInt() else 0
            val amPm   = if (hour < 12) "AM" else "PM"
            val h12    = when {
                hour == 0  -> 12
                hour > 12  -> hour - 12
                else       -> hour
            }
            "%d:%02d %s".format(h12, minute, amPm)
        } catch (e: Exception) {
            timeString
        }
    }

    private fun defaultTimeForSlot(slot: String): Calendar = Calendar.getInstance().apply {
        when (slot.lowercase()) {
            "morning"   -> { set(Calendar.HOUR_OF_DAY, 8);  set(Calendar.MINUTE, 0) }
            "afternoon" -> { set(Calendar.HOUR_OF_DAY, 13); set(Calendar.MINUTE, 0) }
            "night"     -> { set(Calendar.HOUR_OF_DAY, 21); set(Calendar.MINUTE, 0) }
            else        -> { set(Calendar.HOUR_OF_DAY, 9);  set(Calendar.MINUTE, 0) }
        }
        set(Calendar.SECOND,      0)
        set(Calendar.MILLISECOND, 0)
    }
}
