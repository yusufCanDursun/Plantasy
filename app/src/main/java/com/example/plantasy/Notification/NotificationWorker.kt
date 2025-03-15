package com.example.plantasy.Notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Bildirim gönder
        sendNotification(applicationContext)

        return Result.success() // İş başarılı tamamlandı
    }
}
