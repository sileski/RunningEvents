package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.RacesRepository
import javax.inject.Inject

class GetRacesRadiusDistance @Inject constructor(
    private val racesRepository: RacesRepository
) {
    fun execute(): Int {
        return racesRepository.getRacesRadiusDistance()
    }
}