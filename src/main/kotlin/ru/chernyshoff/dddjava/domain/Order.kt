package ru.chernyshoff.dddjava.domain

import java.time.Instant
import java.util.UUID

class Order private constructor(
    val id: OrderId,
    val customerId: CustomerId,
    private val _items: MutableList<OrderItem>,
    private var _status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Instant = Instant.now(),
    private var _updatedAt: Instant = Instant.now(),
    private val _domainEvents: MutableList<DomainEvent> = mutableListOf()
) {
    val items: List<OrderItem>
        get() = _items.toList()

    val status: OrderStatus
        get() = _status

    val updatedAt: Instant
        get() = _updatedAt

    val domainEvents: List<DomainEvent>
        get() = _domainEvents.toList()

    companion object {
        fun create(customerId: CustomerId, items: List<OrderItem>): Order {
            require(items.isNotEmpty()) { "Order must contain at least one item" }

            val order = Order(
                OrderId.generate(),
                customerId,
                items.toMutableList()
            )
            order._domainEvents.add(OrderCreatedEvent(order.id, customerId, Instant.now()))
            return order
        }

        fun reconstitute(
            id: OrderId,
            customerId: CustomerId,
            items: List<OrderItem>,
            status: OrderStatus,
            createdAt: Instant,
            updatedAt: Instant
        ): Order {
            return Order(id, customerId, items.toMutableList(), status, createdAt, updatedAt)
        }
    }

    fun calculateTotalAmount(): Money {
        return _items
            .map { it.totalPrice() }
            .fold(Money.ZERO) { acc, money -> acc + money }
    }

    fun confirm() {
        require(_status == OrderStatus.PENDING) { "Can only confirm pending orders" }
        _status = OrderStatus.CONFIRMED
        _updatedAt = Instant.now()
        _domainEvents.add(OrderConfirmedEvent(id, Instant.now()))
    }

    fun ship() {
        require(_status == OrderStatus.CONFIRMED) { "Can only ship confirmed orders" }
        _status = OrderStatus.SHIPPED
        _updatedAt = Instant.now()
        _domainEvents.add(OrderShippedEvent(id, Instant.now()))
    }

    fun deliver() {
        require(_status == OrderStatus.SHIPPED) { "Can only deliver shipped orders" }
        _status = OrderStatus.DELIVERED
        _updatedAt = Instant.now()
        _domainEvents.add(OrderDeliveredEvent(id, Instant.now()))
    }

    fun cancel() {
        require(_status in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)) {
            "Can only cancel pending or confirmed orders"
        }
        _status = OrderStatus.CANCELLED
        _updatedAt = Instant.now()
        _domainEvents.add(OrderCancelledEvent(id, Instant.now()))
    }

    fun addItem(item: OrderItem) {
        require(_status == OrderStatus.PENDING) { "Can only add items to pending orders" }
        _items.add(item)
        _updatedAt = Instant.now()
    }

    fun removeItem(productId: ProductId) {
        require(_status == OrderStatus.PENDING) { "Can only remove items from pending orders" }
        _items.removeIf { it.productId == productId }
        _updatedAt = Instant.now()
    }

    fun clearDomainEvents() {
        _domainEvents.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Order) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

data class OrderItem(
    val productId: ProductId,
    val productType: ProductType,
    val quantity: Int,
    val unitPrice: Money
) {
    init {
        require(quantity > 0) { "Quantity must be positive" }
    }

    fun totalPrice(): Money = unitPrice * quantity
}

sealed class ProductId {
    data class CactusProductId(val cactusId: CactusId) : ProductId()
    data class FertilizerProductId(val fertilizerId: FertilizerId) : ProductId()
}

enum class ProductType {
    CACTUS, FERTILIZER
}

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

@JvmInline
value class OrderId(val value: UUID) {
    companion object {
        fun generate() = OrderId(UUID.randomUUID())
        fun from(uuid: UUID) = OrderId(uuid)
        fun from(string: String) = OrderId(UUID.fromString(string))
    }

    override fun toString(): String = value.toString()
}

sealed interface DomainEvent {
    val occurredAt: Instant
}

data class OrderCreatedEvent(
    val orderId: OrderId,
    val customerId: CustomerId,
    override val occurredAt: Instant
) : DomainEvent

data class OrderConfirmedEvent(
    val orderId: OrderId,
    override val occurredAt: Instant
) : DomainEvent

data class OrderShippedEvent(
    val orderId: OrderId,
    override val occurredAt: Instant
) : DomainEvent

data class OrderDeliveredEvent(
    val orderId: OrderId,
    override val occurredAt: Instant
) : DomainEvent

data class OrderCancelledEvent(
    val orderId: OrderId,
    override val occurredAt: Instant
) : DomainEvent
