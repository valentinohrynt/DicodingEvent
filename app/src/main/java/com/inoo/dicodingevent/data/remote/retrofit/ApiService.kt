package com.inoo.dicodingevent.data.remote.retrofit

import com.inoo.dicodingevent.data.remote.response.EventResponse
import retrofit2.http.GET

interface ApiService {
    @GET("events?active=1")
    suspend fun getActiveEvent(): EventResponse

    @GET("events?active=0")
    suspend fun getInactiveEvent(): EventResponse
}