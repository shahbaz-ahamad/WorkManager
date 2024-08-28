package com.shahbaz.workmanager.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.shahbaz.workmanager.repo.UploadImageRepo
import javax.inject.Inject

class ImageUploaderWorkerFactory @Inject constructor(
    private val uploadImageRepo: UploadImageRepo,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? = ImageUploaderWorker(uploadImageRepo, appContext, workerParameters)

}