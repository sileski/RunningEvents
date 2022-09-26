package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.RacesRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class DeleteRaceUseCase @Inject constructor(
    private val racesRepository: RacesRepository
) {
    suspend fun execute(raceId: String): Result<Unit> {
        return racesRepository.deleteRace(raceId = raceId)
    }
}