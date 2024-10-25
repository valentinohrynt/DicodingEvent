package com.inoo.dicodingevent.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inoo.dicodingevent.data.EventRepository
import com.inoo.dicodingevent.di.Injection
import com.inoo.dicodingevent.ui.setting.SettingPreferences

class ViewModelFactory(
    private val pref: SettingPreferences,
    private val eventRepository: EventRepository,
    private val workManager: androidx.work.WorkManager
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref, eventRepository, workManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, pref: SettingPreferences): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(pref, Injection.provideRepository(context), Injection.provideWorkManager(context))
            }.also { instance = it }
    }
}
