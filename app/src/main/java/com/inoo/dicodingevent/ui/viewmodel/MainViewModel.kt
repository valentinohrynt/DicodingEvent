package com.inoo.dicodingevent.ui.viewmodel

import com.inoo.dicodingevent.ui.setting.SettingPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import com.inoo.dicodingevent.data.EventRepository
import com.inoo.dicodingevent.data.local.entity.EventEntity
import com.inoo.dicodingevent.ui.notification.DailyReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(private val pref: SettingPreferences, private val eventRepository: EventRepository, private val workManager: androidx.work.WorkManager) : ViewModel() {

    private val _isReminderEnabled = MutableLiveData<Boolean>()
    val isReminderEnabled: LiveData<Boolean> = _isReminderEnabled

    init {
        viewModelScope.launch {
            pref.getReminderPreference().collect {
                _isReminderEnabled.value = it
            }
        }
    }

    fun fetchActiveEvents() = eventRepository.getActiveEvent()

    fun fetchInactiveEvents() = eventRepository.getInactiveEvent()

    fun searchEvents(active: Int, query: String) = eventRepository.searchEvent(active, query)

    fun getDetailEvent(id: String) = eventRepository.getEvent(id)

    fun fetchFavoritedEvents() = eventRepository.getFavoritedEvent()

    fun updateFavoriteStatus(event: EventEntity, favoriteState: Boolean) {
        viewModelScope.launch {
            setFavoritedEvent(event, favoriteState)
        }
    }

    private suspend fun setFavoritedEvent(event: EventEntity, favoriteState: Boolean) {
        eventRepository.setFavoritedEvent(event, favoriteState)
    }
        fun getThemeSettings() : LiveData<Boolean> {
            return pref.getThemeSetting().asLiveData()
        }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun toggleReminder(enabled: Boolean) {
        viewModelScope.launch {
            pref.saveReminderPreference(enabled)
            _isReminderEnabled.value = enabled
            if (enabled) {
                scheduleReminder()
            } else {
                cancelReminder()
            }
        }
    }

    private fun scheduleReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
    }

    private fun cancelReminder() {
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }

    companion object {
        private const val REMINDER_WORK_NAME = "daily_event_reminder"
    }
    
}
