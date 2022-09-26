package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.RacesRepository
import com.example.runningevents.utils.RacesSortedBy
import javax.inject.Inject

class GetRacesSortedByUseCase @Inject constructor(
    private val racesRepository: RacesRepository
) {
    fun execute(): RacesSortedBy {
        return racesRepository.getRacesSortedBy()
    }
}