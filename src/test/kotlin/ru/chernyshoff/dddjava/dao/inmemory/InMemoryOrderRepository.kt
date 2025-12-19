package ru.chernyshoff.dddjava.dao.inmemory

import ru.chernyshoff.dddjava.dao.OrderRepository
import ru.chernyshoff.dddjava.domain.CustomerId
import ru.chernyshoff.dddjava.domain.Order
import ru.chernyshoff.dddjava.domain.OrderId
import ru.chernyshoff.dddjava.domain.OrderStatus
import java.util.concurrent.ConcurrentHashMap

class InMemoryOrderRepository : OrderRepository {
    private val storage = ConcurrentHashMap<OrderId, Order>()

    override fun save(order: Order): Order {
        storage[order.id] = order
        return order
    }

    override fun findById(id: OrderId): Order? {
        return storage[id]
    }

    override fun findByCustomerId(customerId: CustomerId): List<Order> {
        return storage.values.filter { it.customerId == customerId }
    }

    override fun findByStatus(status: OrderStatus): List<Order> {
        return storage.values.filter { it.status == status }
    }

    override fun findAll(): List<Order> {
        return storage.values.toList()
    }

    override fun delete(id: OrderId): Boolean {
        return storage.remove(id) != null
    }

    override fun existsById(id: OrderId): Boolean {
        return storage.containsKey(id)
    }

    fun clear() {
        storage.clear()
    }
}
