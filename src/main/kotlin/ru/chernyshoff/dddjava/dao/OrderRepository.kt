package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.CustomerId
import ru.chernyshoff.dddjava.domain.Order
import ru.chernyshoff.dddjava.domain.OrderId
import ru.chernyshoff.dddjava.domain.OrderStatus

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: OrderId): Order?
    fun findByCustomerId(customerId: CustomerId): List<Order>
    fun findByStatus(status: OrderStatus): List<Order>
    fun findAll(): List<Order>
    fun delete(id: OrderId): Boolean
    fun existsById(id: OrderId): Boolean
}
