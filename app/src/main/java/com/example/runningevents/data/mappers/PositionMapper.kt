package com.example.runningevents.data.mappers

import com.example.runningevents.data.dto.PositionDataDto
import com.example.runningevents.data.dto.PositionDto
import com.example.runningevents.domain.models.Position
import com.example.runningevents.domain.models.PositionData

fun PositionDto.toPosition(): Position {
    return Position(
        data = data.map { it.toPositionData() }
    )
}

fun PositionDataDto.toPositionData(): PositionData {
    return PositionData(
        latitude = latitude,
        longitude = longitude
    )
}