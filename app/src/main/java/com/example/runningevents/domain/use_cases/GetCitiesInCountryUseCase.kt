package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.models.Cities
import com.example.runningevents.domain.repositories.CitiesRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class GetCitiesInCountryUseCase @Inject constructor(
    private val citiesRepository: CitiesRepository
) {
    suspend fun execute(country: String): Result<Cities> {
        return citiesRepository.getCitiesInCountry(country = country)
    }
}