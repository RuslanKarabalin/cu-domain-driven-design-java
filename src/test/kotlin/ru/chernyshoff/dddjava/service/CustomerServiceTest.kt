package ru.chernyshoff.dddjava.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.chernyshoff.dddjava.dao.inmemory.InMemoryCustomerRepository
import ru.chernyshoff.dddjava.domain.Address

class CustomerServiceTest {
    private lateinit var repository: InMemoryCustomerRepository
    private lateinit var service: CustomerService

    @BeforeEach
    fun setUp() {
        repository = InMemoryCustomerRepository()
        service = CustomerServiceImpl(repository)
    }

    @Test
    fun `should register customer with valid data`() {
        // Given
        val name = "John Doe"
        val email = "john.doe@example.com"
        val phone = "+1234567890"
        val address = Address("123 Main St", "New York", "10001")

        // When
        val result = service.registerCustomer(name, email, phone, address)

        // Then
        assertTrue(result.isSuccess)
        val customer = result.getOrNull()
        assertNotNull(customer)
        assertEquals(name, customer?.name)
        assertEquals(email, customer?.email?.value)
        assertEquals(phone, customer?.phone?.value)
        assertEquals(address, customer?.address)
    }

    @Test
    fun `should fail to register customer with invalid email`() {
        // Given
        val name = "John Doe"
        val invalidEmail = "invalid-email"
        val phone = "+1234567890"
        val address = Address("123 Main St", "New York", "10001")

        // When
        val result = service.registerCustomer(name, invalidEmail, phone, address)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should fail to register customer with invalid phone`() {
        // Given
        val name = "John Doe"
        val email = "john.doe@example.com"
        val shortPhone = "123"
        val address = Address("123 Main St", "New York", "10001")

        // When
        val result = service.registerCustomer(name, email, shortPhone, address)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should fail to register customer with duplicate email`() {
        // Given
        val name = "John Doe"
        val email = "john.doe@example.com"
        val phone = "+1234567890"
        val address = Address("123 Main St", "New York", "10001")

        service.registerCustomer(name, email, phone, address)

        // When
        val result = service.registerCustomer("Jane Doe", email, "+9876543210", address)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("already exists") == true)
    }

    @Test
    fun `should retrieve customer by id`() {
        // Given
        val result = service.registerCustomer(
            "John Doe",
            "john.doe@example.com",
            "+1234567890",
            Address("123 Main St", "New York", "10001")
        )
        val customerId = result.getOrNull()?.id!!

        // When
        val customer = service.getCustomer(customerId)

        // Then
        assertNotNull(customer)
        assertEquals(customerId, customer?.id)
    }

    @Test
    fun `should return null when customer not found by id`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.CustomerId.generate()

        // When
        val customer = service.getCustomer(nonExistentId)

        // Then
        assertNull(customer)
    }

    @Test
    fun `should retrieve customer by email`() {
        // Given
        val email = "john.doe@example.com"
        service.registerCustomer(
            "John Doe",
            email,
            "+1234567890",
            Address("123 Main St", "New York", "10001")
        )

        // When
        val customer = service.getCustomerByEmail(email)

        // Then
        assertNotNull(customer)
        assertEquals(email, customer?.email?.value)
    }

    @Test
    fun `should return null when customer not found by email`() {
        // Given
        val nonExistentEmail = "nonexistent@example.com"

        // When
        val customer = service.getCustomerByEmail(nonExistentEmail)

        // Then
        assertNull(customer)
    }

    @Test
    fun `should get all customers`() {
        // Given
        service.registerCustomer(
            "John Doe",
            "john.doe@example.com",
            "+1234567890",
            Address("123 Main St", "New York", "10001")
        )
        service.registerCustomer(
            "Jane Doe",
            "jane.doe@example.com",
            "+9876543210",
            Address("456 Oak Ave", "Boston", "02101")
        )

        // When
        val customers = service.getAllCustomers()

        // Then
        assertEquals(2, customers.size)
    }

    @Test
    fun `should update customer address successfully`() {
        // Given
        val result = service.registerCustomer(
            "John Doe",
            "john.doe@example.com",
            "+1234567890",
            Address("123 Main St", "New York", "10001")
        )
        val customerId = result.getOrNull()?.id!!
        val newAddress = Address("789 Elm St", "Los Angeles", "90001")

        // When
        val updateResult = service.updateAddress(customerId, newAddress)

        // Then
        assertTrue(updateResult.isSuccess)
        assertEquals(newAddress, updateResult.getOrNull()?.address)
    }

    @Test
    fun `should fail to update address when customer not found`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.CustomerId.generate()
        val newAddress = Address("789 Elm St", "Los Angeles", "90001")

        // When
        val result = service.updateAddress(nonExistentId, newAddress)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should update customer contact info successfully`() {
        // Given
        val result = service.registerCustomer(
            "John Doe",
            "john.doe@example.com",
            "+1234567890",
            Address("123 Main St", "New York", "10001")
        )
        val customerId = result.getOrNull()?.id!!
        val newEmail = "john.new@example.com"
        val newPhone = "+9999999999"

        // When
        val updateResult = service.updateContactInfo(customerId, newEmail, newPhone)

        // Then
        assertTrue(updateResult.isSuccess)
        assertEquals(newEmail, updateResult.getOrNull()?.email?.value)
        assertEquals(newPhone, updateResult.getOrNull()?.phone?.value)
    }

    @Test
    fun `should fail to update contact info with invalid email`() {
        // Given
        val result = service.registerCustomer(
            "John Doe",
            "john.doe@example.com",
            "+1234567890",
            Address("123 Main St", "New York", "10001")
        )
        val customerId = result.getOrNull()?.id!!
        val invalidEmail = "invalid-email"
        val newPhone = "+9999999999"

        // When
        val updateResult = service.updateContactInfo(customerId, invalidEmail, newPhone)

        // Then
        assertTrue(updateResult.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, updateResult.exceptionOrNull())
    }

    @Test
    fun `should delete customer successfully`() {
        // Given
        val result = service.registerCustomer(
            "John Doe",
            "john.doe@example.com",
            "+1234567890",
            Address("123 Main St", "New York", "10001")
        )
        val customerId = result.getOrNull()?.id!!

        // When
        val deleted = service.deleteCustomer(customerId)

        // Then
        assertTrue(deleted)
        assertNull(service.getCustomer(customerId))
    }

    @Test
    fun `should return false when deleting non-existent customer`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.CustomerId.generate()

        // When
        val deleted = service.deleteCustomer(nonExistentId)

        // Then
        assertFalse(deleted)
    }
}
