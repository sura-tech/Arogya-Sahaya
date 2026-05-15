ArogyaSahaya 🏥
ArogyaSahaya is an Android application designed to help users manage their medication schedules effectively. It ensures patients never miss a dose by providing precise, automated reminders (alarms) for different times of the day.
🚀 Features
  •Smart Medication Scheduling: Add medicines and assign them to specific slots (Morning, Afternoon, Night).
  •Precision Alarms: Uses AlarmManager with setAlarmClock (the gold standard for Android) to ensure reminders fire exactly on time, even if the device is in Doze mode.
  •Flexible Time Input: Supports both 24-hour (HH:mm) and 12-hour (h:mm AM/PM) formats.
  •Automatic Rescheduling: Once a notification is triggered, the app automatically schedules the next alarm for the following day.
  •Persistent Storage: Built with Room Database to keep your medication data safe.
  •Modern UI: Developed using Jetpack Compose for a smooth, material-design experience.

🛠️ Tech Stack
•Language: Kotlin
•UI Framework: Jetpack Compose
•Architecture: MVVM (Model-View-ViewModel)
•Database: Room Persistence Library
•Background Tasks: AlarmManager & Broadcast Receivers
•Dependency Injection: Kotlin Coroutines & Lifecycle ViewModel

⚙️ How it Works: Alarm Logic
The core logic resides in AlarmScheduler.kt. It calculates the exact time for the alarm:
1.It parses the user-provided time string.
2.If the time has already passed for today, it automatically advances the calendar to the next day.
3.It creates a PendingIntent unique to that medicine and slot using .hashCode().
4.It uses the highest precision alarm method available for the user's Android version:
           ◦API 21+: setAlarmClock
           ◦API 23+: setExactAndAllowWhileIdle



📥 Installation
1.Clone the repository:
Shell Script
git clone https://github.com/your-username/ArogyaSahaya.git
2.Open the project in Android Studio (Ladybug or newer).
3.Sync Project with Gradle Files.
4.Build and Run on an emulator or physical device (Android 5.0+).

🛡️ Permissions Required
The app requires the following permissions to function correctly:
•SCHEDULE_EXACT_ALARM: To trigger reminders at the exact second.
•POST_NOTIFICATIONS: (For Android 13+) To show medication alerts.
•RECEIVE_BOOT_COMPLETED: To reschedule alarms if the phone is restarted.
