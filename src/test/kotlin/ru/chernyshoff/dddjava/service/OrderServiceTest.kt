package ru.chernyshoff.dddjava.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.chernyshoff.dddjava.dao.inmemory.*
import ru.chernyshoff.dddjava.domain.*

class OrderServiceTest {
    private lateinit var orderRepository: InMemoryOrderRepository
    private lateinit var customerRepository: InMemoryCustomerRepository
    private lateinit var cactusRepository: InMemoryCactusRepository
    private lateinit var fertilizerRepository: InMemoryFertilizerRepository
    private lateinit var service: OrderService

    @BeforeEach
    fun setUp() {
        orderRepository = InMemoryOrderRepository()
        customerRepository = InMemoryCustomerRepository()
        cactusRepository = InMemoryCactusRepository()
        fertilizerRepository = InMemoryFertilizerRepository()
        service = OrderServiceImpl(
            orderRepository,
            customerRepository,
            cactusRepository,
            fertilizerRepository
        )
    }

    private fun createTestCustomer(): Customer {
        val customer = Customer.create(
            "John Doe",
            "john@example.com",
            "+1234567890",
            Address("123 Main St", "New York", "10001")
        )
        return customerRepository.save(customer)
    }

    private fun createTestCactus(price: Money = Money(10.0), isAvailable: Boolean = true): Cactus {
        val cactus = Cactus.create("Test Cactus", price, CareLevel.EASY)
        if (!isAvailable) {
            cactus.markAsUnavailable()
        }
        return cactusRepository.save(cactus)
    }

    private fun createTestFertilizer(price: Money = Money(5.0)): Fertilizer {
        val fertilizer = Fertilizer.create(
            "Test Fertilizer",
            price,
            250,
            setOf(CareLevel.EASY)
        )
        return fertilizerRepository.save(fertilizer)
    }

    @Test
    fun `should place order with cactus items successfully`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus(Money(25.0))

        val items = listOf(
            OrderItemRequest(
                ProductId.CactusProductId(cactus.id),
                ProductType.CACTUS,
                2
            )
        )

        // When
        val result = service.placeOrder(customer.id, items)

        // Then
        assertTrue(result.isSuccess)
        val order = result.getOrNull()
        assertNotNull(order)
        assertEquals(customer.id, order?.customerId)
        assertEquals(1, order?.items?.size)
        assertEquals(Money(50.0), order?.calculateTotalAmount())
        assertEquals(OrderStatus.PENDING, order?.status)
    }

    @Test
    fun `should place order with fertilizer items successfully`() {
        // Given
        val customer = createTestCustomer()
        val fertilizer = createTestFertilizer(Money(8.0))

        val items = listOf(
            OrderItemRequest(
                ProductId.FertilizerProductId(fertilizer.id),
                ProductType.FERTILIZER,
                3
            )
        )

        // When
        val result = service.placeOrder(customer.id, items)

        // Then
        assertTrue(result.isSuccess)
        val order = result.getOrNull()
        assertNotNull(order)
        assertEquals(Money(24.0), order?.calculateTotalAmount())
    }

    @Test
    fun `should place order with mixed items successfully`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus(Money(20.0))
        val fertilizer = createTestFertilizer(Money(10.0))

        val items = listOf(
            OrderItemRequest(
                ProductId.CactusProductId(cactus.id),
                ProductType.CACTUS,
                1
            ),
            OrderItemRequest(
                ProductId.FertilizerProductId(fertilizer.id),
                ProductType.FERTILIZER,
                2
            )
        )

        // When
        val result = service.placeOrder(customer.id, items)

        // Then
        assertTrue(result.isSuccess)
        val order = result.getOrNull()
        assertEquals(2, order?.items?.size)
        assertEquals(Money(40.0), order?.calculateTotalAmount())
    }

    @Test
    fun `should fail to place order for non-existent customer`() {
        // Given
        val nonExistentCustomerId = CustomerId.generate()
        val cactus = createTestCactus()

        val items = listOf(
            OrderItemRequest(
                ProductId.CactusProductId(cactus.id),
                ProductType.CACTUS,
                1
            )
        )

        // When
        val result = service.placeOrder(nonExistentCustomerId, items)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("Customer not found") == true)
    }

    @Test
    fun `should fail to place order with empty items list`() {
        // Given
        val customer = createTestCustomer()
        val emptyItems = emptyList<OrderItemRequest>()

        // When
        val result = service.placeOrder(customer.id, emptyItems)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("at least one item") == true)
    }

    @Test
    fun `should fail to place order with non-existent cactus`() {
        // Given
        val customer = createTestCustomer()
        val nonExistentCactusId = CactusId.generate()

        val items = listOf(
            OrderItemRequest(
                ProductId.CactusProductId(nonExistentCactusId),
                ProductType.CACTUS,
                1
            )
        )

        // When
        val result = service.placeOrder(customer.id, items)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("Cactus not found") == true)
    }

    @Test
    fun `should fail to place order with unavailable cactus`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus(isAvailable = false)

        val items = listOf(
            OrderItemRequest(
                ProductId.CactusProductId(cactus.id),
                ProductType.CACTUS,
                1
            )
        )

        // When
        val result = service.placeOrder(customer.id, items)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalStateException::class.java, result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("not available") == true)
    }

    @Test
    fun `should fail to place order with non-existent fertilizer`() {
        // Given
        val customer = createTestCustomer()
        val nonExistentFertilizerId = FertilizerId.generate()

        val items = listOf(
            OrderItemRequest(
                ProductId.FertilizerProductId(nonExistentFertilizerId),
                ProductType.FERTILIZER,
                1
            )
        )

        // When
        val result = service.placeOrder(customer.id, items)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("Fertilizer not found") == true)
    }

    @Test
    fun `should confirm order successfully`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!

        // When
        val result = service.confirmOrder(placedOrder.id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(OrderStatus.CONFIRMED, result.getOrNull()?.status)
    }

    @Test
    fun `should fail to confirm non-existent order`() {
        // Given
        val nonExistentOrderId = OrderId.generate()

        // When
        val result = service.confirmOrder(nonExistentOrderId)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should fail to confirm already confirmed order`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!
        service.confirmOrder(placedOrder.id)

        // When
        val result = service.confirmOrder(placedOrder.id)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should ship order successfully`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!
        service.confirmOrder(placedOrder.id)

        // When
        val result = service.shipOrder(placedOrder.id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(OrderStatus.SHIPPED, result.getOrNull()?.status)
    }

    @Test
    fun `should fail to ship pending order`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!

        // When
        val result = service.shipOrder(placedOrder.id)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should deliver order successfully`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!
        service.confirmOrder(placedOrder.id)
        service.shipOrder(placedOrder.id)

        // When
        val result = service.deliverOrder(placedOrder.id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(OrderStatus.DELIVERED, result.getOrNull()?.status)
    }

    @Test
    fun `should fail to deliver non-shipped order`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!
        service.confirmOrder(placedOrder.id)

        // When
        val result = service.deliverOrder(placedOrder.id)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should cancel pending order successfully`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!

        // When
        val result = service.cancelOrder(placedOrder.id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(OrderStatus.CANCELLED, result.getOrNull()?.status)
    }

    @Test
    fun `should cancel confirmed order successfully`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!
        service.confirmOrder(placedOrder.id)

        // When
        val result = service.cancelOrder(placedOrder.id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(OrderStatus.CANCELLED, result.getOrNull()?.status)
    }

    @Test
    fun `should fail to cancel shipped order`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!
        service.confirmOrder(placedOrder.id)
        service.shipOrder(placedOrder.id)

        // When
        val result = service.cancelOrder(placedOrder.id)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should get order by id`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )
        val placedOrder = service.placeOrder(customer.id, items).getOrNull()!!

        // When
        val retrievedOrder = service.getOrder(placedOrder.id)

        // Then
        assertNotNull(retrievedOrder)
        assertEquals(placedOrder.id, retrievedOrder?.id)
    }

    @Test
    fun `should get customer orders`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )

        service.placeOrder(customer.id, items)
        service.placeOrder(customer.id, items)

        // When
        val customerOrders = service.getCustomerOrders(customer.id)

        // Then
        assertEquals(2, customerOrders.size)
        assertTrue(customerOrders.all { it.customerId == customer.id })
    }

    @Test
    fun `should get orders by status`() {
        // Given
        val customer = createTestCustomer()
        val cactus = createTestCactus()
        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus.id), ProductType.CACTUS, 1)
        )

        val order1 = service.placeOrder(customer.id, items).getOrNull()!!
        val order2 = service.placeOrder(customer.id, items).getOrNull()!!

        service.confirmOrder(order1.id)

        // When
        val pendingOrders = service.getOrdersByStatus(OrderStatus.PENDING)
        val confirmedOrders = service.getOrdersByStatus(OrderStatus.CONFIRMED)

        // Then
        assertEquals(1, pendingOrders.size)
        assertEquals(1, confirmedOrders.size)
        assertEquals(OrderStatus.PENDING, pendingOrders[0].status)
        assertEquals(OrderStatus.CONFIRMED, confirmedOrders[0].status)
    }

    @Test
    fun `should calculate total amount correctly for multiple items`() {
        // Given
        val customer = createTestCustomer()
        val cactus1 = createTestCactus(Money(10.0))
        val cactus2 = createTestCactus(Money(15.0))
        val fertilizer = createTestFertilizer(Money(5.0))

        val items = listOf(
            OrderItemRequest(ProductId.CactusProductId(cactus1.id), ProductType.CACTUS, 2),
            OrderItemRequest(ProductId.CactusProductId(cactus2.id), ProductType.CACTUS, 1),
            OrderItemRequest(ProductId.FertilizerProductId(fertilizer.id), ProductType.FERTILIZER, 3)
        )

        // When
        val result = service.placeOrder(customer.id, items)

        // Then
        assertTrue(result.isSuccess)
        val order = result.getOrNull()
        // 2 * 10 + 1 * 15 + 3 * 5 = 20 + 15 + 15 = 50
        assertEquals(Money(50.0), order?.calculateTotalAmount())
    }
}
