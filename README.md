 ArogyaSahaya 🏥


ArogyaSahaya is a smart Android application designed to help users manage their medication schedules effectively. It delivers precise, reliable reminders so patients never miss their medicines.



 ✨ Features

- Smart Medication Scheduling — Add medicines and assign them to specific slots (Morning, Afternoon, Night)
- Precision Alarms — Uses `AlarmManager` with `setAlarmClock()` — the most reliable method on Android
- Smart Time Handling — Supports both 24-hour (HH:mm) and 12-hour (h:mm AM/PM) formats
- Auto Rescheduling — Automatically schedules the next day’s alarm after a reminder is triggered
- Persistent Storage — Built with Room Database for reliable data storage
- Modern UI — Beautiful and smooth interface using Jetpack Compose
- Boot Persistence — Automatically restores all alarms after device restart



 🛠️ Tech Stack

- Language: Kotlin
- UI: Jetpack Compose (Material 3)
- Architecture: MVVM
- Database: Room Persistence Library
- Alarms: AlarmManager + BroadcastReceiver
- Background Handling: `setAlarmClock()` & `setExactAndAllowWhileIdle()`
- Async: Kotlin Coroutines + Lifecycle ViewModel



 ⚙️ How It Works (Alarm Logic)

The core intelligence lies in `AlarmScheduler.kt`:

1. Parses the user-provided time string (flexible format support)
2. Automatically advances to the next day if the time has already passed today
3. Creates a unique `PendingIntent` for each medicine + slot
4. Uses the highest precision alarm API based on Android version:
   - API 21+: `setAlarmClock()`
   - API 23+: `setExactAndAllowWhileIdle()`

This ensures alarms fire **exactly** on time, even in Doze mode.



 📥 Installation

 Prerequisites
- Android Studio (Ladybug or newer recommended)
- Minimum SDK: Android 5.0 (API 21)

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/sura-tech/Arogya-Sahaya.git
