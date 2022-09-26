package com.example.runningevents.domain.repositories

import com.example.runningevents.domain.models.Cities
import com.example.runningevents.domain.models.Position
import com.example.runningevents.utils.Result

interface CitiesRepository {

    suspend fun getCitiesInCountry(country: String): Result<Cities>
    suspend fun getCityPositionDetails(city: String): Result<Position>
}