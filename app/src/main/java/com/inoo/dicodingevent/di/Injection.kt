package com.inoo.dicodingevent.di

import android.content.Context
import com.inoo.dicodingevent.data.EventRepository
import com.inoo.dicodingevent.data.local.room.EventDatabase
import com.inoo.dicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }
    fun provideWorkManager(context: Context): androidx.work.WorkManager {
        return androidx.work.WorkManager.getInstance(context)
    }
}