package com.example.runningevents.data.repositories

import com.example.runningevents.data.dto.CountryDto
import com.example.runningevents.data.mappers.toCities
import com.example.runningevents.data.mappers.toPosition
import com.example.runningevents.data.remote.CitiesApiService
import com.example.runningevents.data.remote.PositionstackApiService
import com.example.runningevents.domain.models.Cities
import com.example.runningevents.domain.models.Position
import com.example.runningevents.domain.repositories.CitiesRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class CitiesRepositoryImpl @Inject constructor(
    private val citiesApi: CitiesApiService,
    private val positionstackApi: PositionstackApiService
) : CitiesRepository {

    override suspend fun getCitiesInCountry(country: String): Result<Cities> {
        return try {
            val body = CountryDto(country = country)
            val response = citiesApi.getCities(countryBody = body)
            Result.Success(response.toCities())
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override suspend fun getCityPositionDetails(
        city: String
    ): Result<Position> {
        return try {
            val response = positionstackApi.getCityPosition(cityName = city)
            Result.Success(response.toPosition())
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }
}