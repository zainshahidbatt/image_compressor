package com.example.imagecom.ui.video_compress_epic.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.imagecom.R
import com.example.imagecom.utils.Constant
import kotlinx.coroutines.coroutineScope

class CompressVideoWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private var notificationBuilder : NotificationCompat.Builder? = null

    override suspend fun doWork(): Result = coroutineScope {
        try {
            setForeground(createForegroundInfo("Ini content notification"))
            Result.success()
        }catch (exception: Exception){
            Result.failure()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(){
        val manager = applicationContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager?
        val channel = NotificationChannel(Constant.NOTIFICATION_CHANNEL_ID, Constant.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        manager?.createNotificationChannel(channel)
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val cancel = Constant.NOTIFICATION_CANCEL_NAME
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        notificationBuilder = NotificationCompat.Builder(applicationContext, Constant.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(Constant.NOTIFICATION_NAME)
            .setTicker(Constant.NOTIFICATION_NAME)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)

        return ForegroundInfo(Constant.NOTIFICATION_ID, notificationBuilder?.build()!!)
    }


}