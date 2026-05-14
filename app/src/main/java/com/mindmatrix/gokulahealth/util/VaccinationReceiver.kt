package com.mindmatrix.gokulahealth.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mindmatrix.gokulahealth.MainActivity

class VaccinationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        val vaccineName = intent?.getStringExtra("vaccineName") ?: "Vaccination"
        val cattleName = intent?.getStringExtra("cattleName") ?: "Your cattle"
        val cattleId = intent?.getIntExtra("cattleId", -1) ?: -1

        showNotification(context, vaccineName, cattleName, cattleId)
    }

    private fun showNotification(
        context: Context,
        vaccineName: String,
        cattleName: String,
        cattleId: Int
    ) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // ✅ FIXED: Check API level BEFORE creating channel!
        // Was crashing on Android < API 26 before!
        createNotificationChannel(notificationManager)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("cattleId", cattleId)
        }

        val tapPendingIntent = PendingIntent.getActivity(
            context,
            cattleId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💉 Vaccination Due Today!")
            // ✅ FIXED: Shows cattle NAME not ID!
            .setContentText("$vaccineName due for $cattleName today!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        """
                        Vaccination Reminder!
                        Cattle: $cattleName
                        Vaccine: $vaccineName
                        Please contact your veterinarian today.
                        Tap to open Gokula-Health app.
                        """.trimIndent()
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(tapPendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(
            cattleId.coerceAtLeast(1),
            notification
        )
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager
    ) {
        // ✅ FIXED: Added Build.VERSION check!
        // NotificationChannel only exists on API 26+!
        // Without this check → CRASH on Android 7.x!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you when cattle vaccination is due today"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "vaccination_reminders"
        const val CHANNEL_NAME = "Vaccination Reminders"
    }
}