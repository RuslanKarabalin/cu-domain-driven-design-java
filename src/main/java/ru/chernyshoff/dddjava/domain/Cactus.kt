package ru.chernyshoff.dddjava.domain

data class Cactus(
    val name: String,
    val price: Double,
    val careLevel: CareLevel,
    val isAvailable: Boolean = true
)

enum class CareLevel {
    EASY, MEDIUM, HARD
}
