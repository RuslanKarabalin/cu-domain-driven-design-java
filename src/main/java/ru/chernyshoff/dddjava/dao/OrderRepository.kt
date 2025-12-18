package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.Order
import java.util.UUID

interface OrderRepository {
    fun create(order: Order): Order
    fun read(id: UUID): Order?
    fun update(id: UUID, updatedOrder: Order): Order?
    fun delete(id: UUID): Boolean
    fun list(): List<Order>
}
