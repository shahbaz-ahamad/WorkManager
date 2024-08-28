package com.shahbaz.workmanager.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shahbaz.workmanager.repo.UploadImageRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@HiltWorker
class ImageUploaderWorker @AssistedInject constructor(
    val uploadImageRepo: UploadImageRepo,
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        println("Response form the worker")
        val fileUri = inputData.getString("fileUri") ?: return Result.failure()

        // Start the foreground service
        setForeground(createForegroundInfo(0))

        return try {
            uploadImageRepo.uploadFile(
                fileUri = fileUri,
                onProgress = { progress ->
                    withContext(Dispatchers.IO) {
                        setForeground(createForegroundInfo(progress))
                    }
                }
            )
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }


    }


    private fun createForegroundInfo(progress: Int): ForegroundInfo {
        val notification = getNotification("Uploading file...", progress)

        // Include the correct Foreground Service Type for API 31+
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                1, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC // or the correct type for your use case
            )
        } else {
            ForegroundInfo(1, notification)
        }
    }


    private fun getNotification(fileName: String, progress: Int = 0): Notification =
        NotificationCompat.Builder(context, "Uploader")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("File is Uploading")
            .setContentText(fileName)
            .setProgress(100, progress, false)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
}