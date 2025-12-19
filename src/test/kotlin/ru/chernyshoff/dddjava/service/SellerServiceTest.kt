package ru.chernyshoff.dddjava.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.chernyshoff.dddjava.dao.inmemory.InMemorySellerRepository

class SellerServiceTest {
    private lateinit var repository: InMemorySellerRepository
    private lateinit var service: SellerService

    @BeforeEach
    fun setUp() {
        repository = InMemorySellerRepository()
        service = SellerServiceImpl(repository)
    }

    @Test
    fun `should register seller with valid data`() {
        // Given
        val storeName = "Cactus Paradise"
        val contactEmail = "contact@cactusparadise.com"

        // When
        val result = service.registerSeller(storeName, contactEmail)

        // Then
        assertTrue(result.isSuccess)
        val seller = result.getOrNull()
        assertNotNull(seller)
        assertEquals(storeName, seller?.storeName)
        assertEquals(contactEmail, seller?.contactEmail?.value)
        assertTrue(seller?.isActive ?: false)
    }

    @Test
    fun `should fail to register seller with blank store name`() {
        // Given
        val blankStoreName = "  "
        val contactEmail = "contact@example.com"

        // When
        val result = service.registerSeller(blankStoreName, contactEmail)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should fail to register seller with invalid email`() {
        // Given
        val storeName = "Cactus Paradise"
        val invalidEmail = "invalid-email"

        // When
        val result = service.registerSeller(storeName, invalidEmail)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should fail to register seller with duplicate email`() {
        // Given
        val storeName = "Cactus Paradise"
        val email = "contact@cactusparadise.com"

        service.registerSeller(storeName, email)

        // When
        val result = service.registerSeller("Another Store", email)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("already exists") == true)
    }

    @Test
    fun `should retrieve seller by id`() {
        // Given
        val result = service.registerSeller("Cactus Paradise", "contact@cactusparadise.com")
        val sellerId = result.getOrNull()?.id!!

        // When
        val seller = service.getSeller(sellerId)

        // Then
        assertNotNull(seller)
        assertEquals(sellerId, seller?.id)
    }

    @Test
    fun `should return null when seller not found by id`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.SellerId.generate()

        // When
        val seller = service.getSeller(nonExistentId)

        // Then
        assertNull(seller)
    }

    @Test
    fun `should retrieve seller by email`() {
        // Given
        val email = "contact@cactusparadise.com"
        service.registerSeller("Cactus Paradise", email)

        // When
        val seller = service.getSellerByEmail(email)

        // Then
        assertNotNull(seller)
        assertEquals(email, seller?.contactEmail?.value)
    }

    @Test
    fun `should return null when seller not found by email`() {
        // Given
        val nonExistentEmail = "nonexistent@example.com"

        // When
        val seller = service.getSellerByEmail(nonExistentEmail)

        // Then
        assertNull(seller)
    }

    @Test
    fun `should get all active sellers`() {
        // Given
        val result1 = service.registerSeller("Store 1", "store1@example.com")
        val result2 = service.registerSeller("Store 2", "store2@example.com")
        val result3 = service.registerSeller("Store 3", "store3@example.com")

        // Deactivate one seller
        service.deactivateSeller(result2.getOrNull()?.id!!)

        // When
        val activeSellers = service.getAllActiveSellers()

        // Then
        assertEquals(2, activeSellers.size)
        assertTrue(activeSellers.all { it.isActive })
    }

    @Test
    fun `should get all sellers including inactive`() {
        // Given
        val result1 = service.registerSeller("Store 1", "store1@example.com")
        val result2 = service.registerSeller("Store 2", "store2@example.com")

        service.deactivateSeller(result1.getOrNull()?.id!!)

        // When
        val allSellers = service.getAllSellers()

        // Then
        assertEquals(2, allSellers.size)
    }

    @Test
    fun `should deactivate seller successfully`() {
        // Given
        val result = service.registerSeller("Cactus Paradise", "contact@cactusparadise.com")
        val sellerId = result.getOrNull()?.id!!
        assertTrue(result.getOrNull()?.isActive ?: false)

        // When
        val deactivateResult = service.deactivateSeller(sellerId)

        // Then
        assertTrue(deactivateResult.isSuccess)
        assertFalse(deactivateResult.getOrNull()?.isActive ?: true)
    }

    @Test
    fun `should fail to deactivate already inactive seller`() {
        // Given
        val result = service.registerSeller("Cactus Paradise", "contact@cactusparadise.com")
        val sellerId = result.getOrNull()?.id!!

        service.deactivateSeller(sellerId)

        // When
        val deactivateResult = service.deactivateSeller(sellerId)

        // Then
        assertTrue(deactivateResult.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, deactivateResult.exceptionOrNull())
    }

    @Test
    fun `should fail to deactivate non-existent seller`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.SellerId.generate()

        // When
        val result = service.deactivateSeller(nonExistentId)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should activate seller successfully`() {
        // Given
        val result = service.registerSeller("Cactus Paradise", "contact@cactusparadise.com")
        val sellerId = result.getOrNull()?.id!!

        service.deactivateSeller(sellerId)

        // When
        val activateResult = service.activateSeller(sellerId)

        // Then
        assertTrue(activateResult.isSuccess)
        assertTrue(activateResult.getOrNull()?.isActive ?: false)
    }

    @Test
    fun `should fail to activate already active seller`() {
        // Given
        val result = service.registerSeller("Cactus Paradise", "contact@cactusparadise.com")
        val sellerId = result.getOrNull()?.id!!

        // When
        val activateResult = service.activateSeller(sellerId)

        // Then
        assertTrue(activateResult.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, activateResult.exceptionOrNull())
    }

    @Test
    fun `should update contact email successfully`() {
        // Given
        val result = service.registerSeller("Cactus Paradise", "old@cactusparadise.com")
        val sellerId = result.getOrNull()?.id!!
        val newEmail = "new@cactusparadise.com"

        // When
        val updateResult = service.updateContactEmail(sellerId, newEmail)

        // Then
        assertTrue(updateResult.isSuccess)
        assertEquals(newEmail, updateResult.getOrNull()?.contactEmail?.value)
    }

    @Test
    fun `should fail to update contact email with invalid email`() {
        // Given
        val result = service.registerSeller("Cactus Paradise", "contact@cactusparadise.com")
        val sellerId = result.getOrNull()?.id!!
        val invalidEmail = "invalid-email"

        // When
        val updateResult = service.updateContactEmail(sellerId, invalidEmail)

        // Then
        assertTrue(updateResult.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, updateResult.exceptionOrNull())
    }

    @Test
    fun `should fail to update contact email for non-existent seller`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.SellerId.generate()
        val newEmail = "new@example.com"

        // When
        val result = service.updateContactEmail(nonExistentId, newEmail)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should delete seller successfully`() {
        // Given
        val result = service.registerSeller("Cactus Paradise", "contact@cactusparadise.com")
        val sellerId = result.getOrNull()?.id!!

        // When
        val deleted = service.deleteSeller(sellerId)

        // Then
        assertTrue(deleted)
        assertNull(service.getSeller(sellerId))
    }

    @Test
    fun `should return false when deleting non-existent seller`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.SellerId.generate()

        // When
        val deleted = service.deleteSeller(nonExistentId)

        // Then
        assertFalse(deleted)
    }
}
