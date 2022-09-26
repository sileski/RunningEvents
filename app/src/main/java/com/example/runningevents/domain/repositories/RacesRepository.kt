package com.example.runningevents.domain.repositories

import com.example.runningevents.domain.models.Race
import com.example.runningevents.utils.RacesSortedBy
import com.example.runningevents.utils.Result

interface RacesRepository {

    suspend fun getRacesByDate(
        isRefreshing: Boolean,
        racesDistanceFilter: List<String>,
    ): Result<List<Race>>

    suspend fun getRacesByLocation(
        isRefreshing: Boolean,
        latitude: Double,
        longitude: Double,
        radius: Int,
        racesDistanceFilter: List<String>
    ): Result<List<Race>>

    suspend fun addNewRace(race: Race): Result<Unit>
    fun getRacesSortedBy(): RacesSortedBy
    fun getRaceFilterDistances(): List<String>
    fun getRacesRadiusDistance(): Int
    suspend fun getMyRaces(isRefreshing: Boolean, userId: String): Result<List<Race>>
    suspend fun deleteRace(raceId: String): Result<Unit>
}