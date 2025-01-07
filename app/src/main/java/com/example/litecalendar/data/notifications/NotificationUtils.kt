package com.example.litecalendar.data.notifications

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import java.util.UUID
import java.util.concurrent.TimeUnit

@SuppressLint("RestrictedApi")
fun scheduleSingleNotification(context: Context, delay: Long, title: String, message: String) : String {
    val data = Data.Builder()
        .putString("title", title)
        .putString("message", message)
        .build()
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setConstraints(constraints)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)

    return workRequest.id.toString()
}

@SuppressLint("RestrictedApi")
fun schedulePeriodicNotification(context: Context, intervalHours: Long, initialDelay : Long, title: String, message: String) : String {
    val data = Data.Builder()
        .putString("title", title)
        .putString("message", message)
        .build()
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .build()

    val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
        intervalHours, TimeUnit.HOURS
    )
        .setConstraints(constraints)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .setInputData(data).build()

    WorkManager.getInstance(context).enqueue(periodicWorkRequest)

    return periodicWorkRequest.id.toString()
}