package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.RacesRepository
import javax.inject.Inject

class GetRaceFilterDistancesUseCase @Inject constructor(
    private val racesRepository: RacesRepository
) {
    fun execute(): List<String> {
        return racesRepository.getRaceFilterDistances()
    }
}