package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.domain.Order
import java.util.UUID

interface OrderService {
    fun create(order: Order): Order
    fun read(id: UUID): Order?
    fun update(id: UUID, updatedOrder: Order): Order?
    fun delete(id: UUID): Boolean
    fun list(): List<Order>
}
