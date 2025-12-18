package ru.chernyshoff.dddjava.domain

import java.util.UUID

data class Order(
    val id: UUID,
    val cactuses: List<Cactus>,
    val fertilizers: List<Fertilizer>,
    val totalAmount: Double,
    val status: OrderStatus = OrderStatus.PENDING
)

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
