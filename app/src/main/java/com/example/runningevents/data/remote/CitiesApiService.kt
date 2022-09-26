package com.example.runningevents.data.remote

import com.example.runningevents.data.dto.CitiesDto
import com.example.runningevents.data.dto.CountryDto
import retrofit2.http.Body
import retrofit2.http.POST

interface CitiesApiService {

    @POST("api/v0.1/countries/cities")
    suspend fun getCities(
        @Body countryBody: CountryDto
    ): CitiesDto
}