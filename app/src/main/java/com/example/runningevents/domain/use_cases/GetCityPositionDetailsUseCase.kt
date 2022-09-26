package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.models.Position
import com.example.runningevents.domain.repositories.CitiesRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class GetCityPositionDetailsUseCase @Inject constructor(
    private val citiesRepository: CitiesRepository
) {
    suspend fun execute(city: String): Result<Position> {
        return citiesRepository.getCityPositionDetails(city = city)
    }
}