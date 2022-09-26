package com.example.runningevents.data.mappers

import com.example.runningevents.data.dto.CitiesDto
import com.example.runningevents.domain.models.Cities

fun CitiesDto.toCities(): Cities {
    return Cities(
        data = data
    )
}