package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.repositories.RacesRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class AddNewRaceUseCase @Inject constructor(
    private val racesRepository: RacesRepository
) {
    suspend fun execute(race: Race): Result<Unit> {
        return racesRepository.addNewRace(race = race)
    }
}