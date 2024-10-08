package com.inoo.dicodingevent.data.retrofit

import com.inoo.dicodingevent.data.response.EventResponse
import com.inoo.dicodingevent.data.response.SpecificEventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events/{id}")
    fun getEventDetail(
        @Path("id") id: String
    ): Call<SpecificEventResponse>

    @GET("events?active=1")
    fun getActiveEvent(): Call<EventResponse>

    @GET("events?active=0")
    fun getInactiveEvent(): Call<EventResponse>

    @GET("events")
    fun searchEvent(
        @Query("active") active: Int,
        @Query("q") query: String
    ): Call<EventResponse>
}