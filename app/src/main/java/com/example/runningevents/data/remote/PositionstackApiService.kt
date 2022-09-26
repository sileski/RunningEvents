package com.example.runningevents.data.remote

import com.example.runningevents.BuildConfig
import com.example.runningevents.data.dto.PositionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PositionstackApiService {

    @GET("v1/forward")
    suspend fun getCityPosition(
        @Query("access_key") accessKey: String = BuildConfig.POSITIONSTACK_API_KEY,
        @Query("query") cityName: String
    ): PositionDto
}