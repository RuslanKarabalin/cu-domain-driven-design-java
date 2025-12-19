package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.dao.CactusRepository
import ru.chernyshoff.dddjava.dao.CustomerRepository
import ru.chernyshoff.dddjava.dao.FertilizerRepository
import ru.chernyshoff.dddjava.dao.OrderRepository
import ru.chernyshoff.dddjava.domain.*

interface OrderService {
    fun placeOrder(customerId: CustomerId, items: List<OrderItemRequest>): Result<Order>
    fun confirmOrder(orderId: OrderId): Result<Order>
    fun shipOrder(orderId: OrderId): Result<Order>
    fun deliverOrder(orderId: OrderId): Result<Order>
    fun cancelOrder(orderId: OrderId): Result<Order>
    fun getOrder(orderId: OrderId): Order?
    fun getCustomerOrders(customerId: CustomerId): List<Order>
    fun getOrdersByStatus(status: OrderStatus): List<Order>
}

data class OrderItemRequest(
    val productId: ProductId,
    val productType: ProductType,
    val quantity: Int
)

class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val cactusRepository: CactusRepository,
    private val fertilizerRepository: FertilizerRepository
) : OrderService {

    override fun placeOrder(customerId: CustomerId, items: List<OrderItemRequest>): Result<Order> {
        val customer = customerRepository.findById(customerId)
            ?: return Result.failure(IllegalArgumentException("Customer not found: $customerId"))

        if (items.isEmpty()) {
            return Result.failure(IllegalArgumentException("Order must contain at least one item"))
        }

        val orderItems = items.mapNotNull { request ->
            when (request.productId) {
                is ProductId.CactusProductId -> {
                    val cactus = cactusRepository.findById(request.productId.cactusId)
                        ?: return Result.failure(IllegalArgumentException("Cactus not found: ${request.productId.cactusId}"))

                    if (!cactus.isAvailable) {
                        return Result.failure(IllegalStateException("Cactus is not available: ${cactus.name}"))
                    }

                    OrderItem(
                        productId = request.productId,
                        productType = ProductType.CACTUS,
                        quantity = request.quantity,
                        unitPrice = cactus.price
                    )
                }
                is ProductId.FertilizerProductId -> {
                    val fertilizer = fertilizerRepository.findById(request.productId.fertilizerId)
                        ?: return Result.failure(IllegalArgumentException("Fertilizer not found: ${request.productId.fertilizerId}"))

                    OrderItem(
                        productId = request.productId,
                        productType = ProductType.FERTILIZER,
                        quantity = request.quantity,
                        unitPrice = fertilizer.price
                    )
                }
            }
        }

        val order = Order.create(customerId, orderItems)
        val savedOrder = orderRepository.save(order)

        savedOrder.domainEvents.forEach { event ->
            // TODO: Publish to event bus
            println("Domain event: $event")
        }
        savedOrder.clearDomainEvents()

        return Result.success(savedOrder)
    }

    override fun confirmOrder(orderId: OrderId): Result<Order> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found: $orderId"))

        return try {
            order.confirm()
            val savedOrder = orderRepository.save(order)
            publishEvents(savedOrder)
            Result.success(savedOrder)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    override fun shipOrder(orderId: OrderId): Result<Order> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found: $orderId"))

        return try {
            order.ship()
            val savedOrder = orderRepository.save(order)
            publishEvents(savedOrder)
            Result.success(savedOrder)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    override fun deliverOrder(orderId: OrderId): Result<Order> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found: $orderId"))

        return try {
            order.deliver()
            val savedOrder = orderRepository.save(order)
            publishEvents(savedOrder)
            Result.success(savedOrder)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    override fun cancelOrder(orderId: OrderId): Result<Order> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found: $orderId"))

        return try {
            order.cancel()
            val savedOrder = orderRepository.save(order)
            publishEvents(savedOrder)
            Result.success(savedOrder)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    override fun getOrder(orderId: OrderId): Order? {
        return orderRepository.findById(orderId)
    }

    override fun getCustomerOrders(customerId: CustomerId): List<Order> {
        return orderRepository.findByCustomerId(customerId)
    }

    override fun getOrdersByStatus(status: OrderStatus): List<Order> {
        return orderRepository.findByStatus(status)
    }

    private fun publishEvents(order: Order) {
        order.domainEvents.forEach { event ->
            // TODO: Publish to event bus
            println("Domain event: $event")
        }
        order.clearDomainEvents()
    }
}
