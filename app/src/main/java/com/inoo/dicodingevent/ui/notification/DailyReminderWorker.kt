package com.inoo.dicodingevent.ui.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.inoo.dicodingevent.data.EventRepository
import com.inoo.dicodingevent.data.local.entity.EventEntity
import com.inoo.dicodingevent.data.local.room.EventDatabase
import com.inoo.dicodingevent.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.coroutineScope

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val repository = EventRepository.getInstance(
                ApiConfig.getApiService(),
                EventDatabase.getInstance(applicationContext).eventDao()
            )
            val nearestEvent = repository.getNearestActiveEvent()

            if (nearestEvent != null) {
                showNotification(nearestEvent)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(event: EventEntity) {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showNotification(event)
    }
}
