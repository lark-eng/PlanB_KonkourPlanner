package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class StudyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val subject = intent.getStringExtra("EXTRA_SUBJECT") ?: "درس"
        val topic = intent.getStringExtra("EXTRA_TOPIC") ?: ""
        val startTime = intent.getStringExtra("EXTRA_START_TIME") ?: ""
        val sessionId = intent.getLongExtra("EXTRA_SESSION_ID", System.currentTimeMillis())

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "plan_b_study_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "یادآوری پارت‌های مطالعه Plan B",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "ارسال اعلان در زمان شروع پارت‌های مطالعاتی"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            sessionId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val contentText = if (topic.isNotBlank()) {
            "شروع پارت مطالعه «$subject» - مبحث: $topic (ساعت $startTime)"
        } else {
            "زمان مطالعه درس «$subject» فرا رسید (ساعت $startTime)"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_open_book)
            .setContentTitle("Plan B | وقت مطالعه است! 📖")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$contentText\nتمرکز کن و پرانرژی برو جلو!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(sessionId.toInt(), notification)
    }
}
