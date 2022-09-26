package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.repositories.RacesRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class GetMyRacesUseCase @Inject constructor(
    private val racesRepository: RacesRepository
) {
    suspend fun execute(
        isRefreshing: Boolean,
        userId: String
    ): Result<List<Race>> {
        return racesRepository.getMyRaces(
            isRefreshing = isRefreshing,
            userId = userId
        )
    }
}