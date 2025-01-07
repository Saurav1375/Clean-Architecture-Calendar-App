package com.example.litecalendar.data.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.litecalendar.R
import com.example.litecalendar.presentation.MainActivity
import com.example.litecalendar.utils.Constants

class NotificationWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {
    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {

        val title = inputData.getString("title") ?: "Event Happening"
        val message = inputData.getString("message") ?: "8:00 - 9:00 pm"

        showNotifications(context = applicationContext, title, message)
        return Result.Success()
    }

    private fun showNotifications(context: Context, title: String, message: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                context.getSystemService(NotificationManager::class.java) as NotificationManager
            manager.createNotificationChannel(channel)

        }
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_calendar_month_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.notify(System.currentTimeMillis().toInt(), notification)
    }
}
