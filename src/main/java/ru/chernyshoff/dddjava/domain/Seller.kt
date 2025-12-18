package ru.chernyshoff.dddjava.domain

data class Seller(
    val storeName: String,
    val contactEmail: String,
    val isActive: Boolean = true,
    val cactuses: List<Cactus>,
    val fertilizers: List<Fertilizer>
)
