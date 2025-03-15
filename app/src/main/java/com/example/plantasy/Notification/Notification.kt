package com.example.plantasy.Notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// Bildirim gönderen fonksiyon
fun sendNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Android 8.0 ve sonrasında kanal oluşturulmalı
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "default_channel_id",
            "Default Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "This is the default channel for notifications."
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notification: Notification = NotificationCompat.Builder(context, "default_channel_id")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Notification")
        .setContentText("Don't forget to water your plants")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    NotificationManagerCompat.from(context).notify(1, notification)
}
