package com.inoo.dicodingevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.inoo.dicodingevent.data.local.entity.EventEntity
import com.inoo.dicodingevent.data.local.room.EventDao
import com.inoo.dicodingevent.data.remote.retrofit.ApiService

class EventRepository private constructor (
    private val apiService: ApiService,
    private val eventDao: EventDao,
    ) {

    fun getActiveEvent(): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)

        try {
            val localData = eventDao.getActiveEvent()
            val response = apiService.getEvents(1)
            val events = response.listEvents

            if (localData.isNotEmpty() && localData.size == events.size || localData == events) {
                emit(Result.Success(localData))
            } else {
                try {
                    val eventList = events.map { event ->
                        val isFavorited = eventDao.isFavorited(event.id.toString())
                        EventEntity(
                            event.id.toString(),
                            event.name.toString(),
                            event.summary.toString(),
                            event.description.toString(),
                            event.imageLogo,
                            event.mediaCover,
                            event.category.toString(),
                            event.ownerName.toString(),
                            event.cityName.toString(),
                            event.quota!!.toInt(),
                            event.registrants!!.toInt(),
                            event.beginTime.toString(),
                            event.endTime.toString(),
                            event.link,
                            isFavorited,
                            isActive = true
                        )
                    }
                    eventDao.deleteActiveEvents()
                    eventDao.insertEvent(eventList)
                    emit(Result.Success(eventList))
                } catch (e: Exception) {
                    emit(Result.Error("No internet connection and no local data available"))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun getInactiveEvent(): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)

        try {
            val localData = eventDao.getInactiveEvent()
            val response = apiService.getEvents(0)
            val events = response.listEvents

            if (localData.isNotEmpty() && localData.size == events.size || localData == events) {
                emit(Result.Success(localData))
            } else {
                try {
                    val eventList = events.map { event ->
                        val isFavorited = eventDao.isFavorited(event.id.toString())
                        EventEntity(
                            event.id.toString(),
                            event.name.toString(),
                            event.summary.toString(),
                            event.description.toString(),
                            event.imageLogo,
                            event.mediaCover,
                            event.category.toString(),
                            event.ownerName.toString(),
                            event.cityName.toString(),
                            event.quota!!.toInt(),
                            event.registrants!!.toInt(),
                            event.beginTime.toString(),
                            event.endTime.toString(),
                            event.link,
                            isFavorited,
                            isActive = false
                        )
                    }
                    eventDao.deleteInactiveEvents()
                    eventDao.insertEvent(eventList)
                    emit(Result.Success(eventList))
                } catch (e: Exception) {
                    emit(Result.Error("No internet connection and no local data available"))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun searchEvent(active: Int, query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localEvent = eventDao.searchEvent(active, query)

            emitSource(localEvent.map { eventList ->
                if (eventList.isEmpty()) {
                    Result.Success(emptyList())
                } else {
                    Result.Success(eventList)
                }
            })
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getEvent(id: String): LiveData<Result<EventEntity>> = liveData {
        emit(Result.Loading)
        try {
            val localEvent = eventDao.getEventbyId(id)

            if (localEvent != null) {
                emit(Result.Success(localEvent))
            } else {
                emit(Result.Error("Event not found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getFavoritedEvent(): LiveData<List<EventEntity>> {
        return eventDao.getFavoritedEvent()
    }

    suspend fun setFavoritedEvent(event: EventEntity, favoriteState: Boolean) {
        event.isFavorited = favoriteState
        eventDao.updateEvent(event)
    }

    suspend fun getNearestActiveEvent(): EventEntity? {
        val currentTime = System.currentTimeMillis()
        val nearestEvent = eventDao.getNearestActiveEvent(currentTime)
        return nearestEvent
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }.also {
                instance = it
            }
    }
}
