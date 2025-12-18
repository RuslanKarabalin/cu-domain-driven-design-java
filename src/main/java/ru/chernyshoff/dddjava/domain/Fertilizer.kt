package ru.chernyshoff.dddjava.domain

data class Fertilizer(
    val name: String,
    val price: Double,
    val volumeMl: Int,
    val recommendedFor: List<CareLevel>
)
