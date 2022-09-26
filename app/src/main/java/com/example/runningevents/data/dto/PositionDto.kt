package com.example.runningevents.data.dto

data class PositionDto(
    val data: List<PositionDataDto>
)

data class PositionDataDto(
    val latitude: Double,
    val longitude: Double
)
