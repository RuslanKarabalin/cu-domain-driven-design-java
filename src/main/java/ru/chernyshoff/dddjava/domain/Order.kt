package ru.chernyshoff.dddjava.domain

data class Order(
    val cactuses: List<Cactus>,
    val fertilizers: List<Fertilizer>,
    val totalAmount: Double,
    val status: OrderStatus = OrderStatus.PENDING
)

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
