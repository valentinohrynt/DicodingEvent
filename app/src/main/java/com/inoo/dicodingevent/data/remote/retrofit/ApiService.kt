package com.inoo.dicodingevent.data.remote.retrofit

import com.inoo.dicodingevent.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): EventResponse
}
