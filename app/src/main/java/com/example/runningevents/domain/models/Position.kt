package com.example.runningevents.domain.models

data class Position(
    val data: List<PositionData>
)

data class PositionData(
    val latitude: Double,
    val longitude: Double
)
