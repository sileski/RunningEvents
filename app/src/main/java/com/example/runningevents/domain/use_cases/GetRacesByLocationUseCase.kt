package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.repositories.RacesRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class GetRacesByLocationUseCase @Inject constructor(
    private val racesRepository: RacesRepository
) {
    suspend fun execute(
        isRefreshing: Boolean,
        latitude: Double,
        longitude: Double,
        radius: Int,
        racesDistanceFilter: List<String>
    ): Result<List<Race>> {
        return racesRepository.getRacesByLocation(
            isRefreshing = isRefreshing,
            latitude = latitude,
            longitude = longitude,
            radius = radius,
            racesDistanceFilter = racesDistanceFilter
        )
    }
}