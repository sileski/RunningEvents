package com.example.runningevents.data.mappers

import com.example.runningevents.data.dto.RaceCategoryDto
import com.example.runningevents.data.dto.RaceDto
import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.models.RaceCategory

fun RaceDto.toRace(): Race {
    return Race(
        canceled = this.canceled,
        city = this.city,
        country = this.country,
        description = this.description,
        createdBy = this.createdBy,
        date = this.date,
        geohash = this.geohash,
        imageUrl = this.imageUrl,
        latitude = this.latitude,
        longitude = this.longitude,
        raceId = this.raceId,
        raceName = this.raceName,
        websiteUrl = this.websiteUrl,
        raceCategories = this.categories.map { it.toRaceCategory() },
        distanceFilter = this.distanceFilter
    )
}

fun RaceCategoryDto.toRaceCategory(): RaceCategory {
    return RaceCategory(
        currency = this.currency,
        date = this.date,
        distance = this.distance,
        price = this.price
    )
}

fun Race.toRaceDto(): RaceDto {
    return RaceDto(
        canceled = this.canceled,
        city = this.city,
        country = this.country,
        createdBy = this.createdBy,
        description = this.description,
        date = this.date,
        geohash = this.geohash,
        imageUrl = this.imageUrl,
        latitude = this.latitude,
        longitude = this.longitude,
        raceId = this.raceId,
        raceName = this.raceName,
        websiteUrl = this.websiteUrl,
        categories = this.raceCategories.map { it.toRaceCategoryDto() },
        distanceFilter = this.distanceFilter
    )
}

fun RaceCategory.toRaceCategoryDto(): RaceCategoryDto {
    return RaceCategoryDto(
        currency = this.currency,
        date = this.date,
        distance = this.distance,
        price = this.price
    )
}