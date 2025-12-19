package ru.chernyshoff.dddjava.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.chernyshoff.dddjava.dao.inmemory.InMemoryCactusRepository
import ru.chernyshoff.dddjava.domain.CareLevel
import ru.chernyshoff.dddjava.domain.Money

class CactusServiceTest {
    private lateinit var repository: InMemoryCactusRepository
    private lateinit var service: CactusService

    @BeforeEach
    fun setUp() {
        repository = InMemoryCactusRepository()
        service = CactusServiceImpl(repository)
    }

    @Test
    fun `should create cactus with valid data`() {
        // Given
        val name = "Aloe Vera"
        val price = Money(15.99)
        val careLevel = CareLevel.EASY

        // When
        val cactus = service.createCactus(name, price, careLevel)

        // Then
        assertNotNull(cactus.id)
        assertEquals(name, cactus.name)
        assertEquals(price, cactus.price)
        assertEquals(careLevel, cactus.careLevel)
        assertTrue(cactus.isAvailable)
    }

    @Test
    fun `should throw exception when creating cactus with blank name`() {
        // Given
        val blankName = "  "
        val price = Money(15.99)
        val careLevel = CareLevel.EASY

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            service.createCactus(blankName, price, careLevel)
        }
    }

    @Test
    fun `should retrieve cactus by id`() {
        // Given
        val cactus = service.createCactus("Test Cactus", Money(10.0), CareLevel.MEDIUM)

        // When
        val retrieved = service.getCactus(cactus.id)

        // Then
        assertNotNull(retrieved)
        assertEquals(cactus.id, retrieved?.id)
        assertEquals(cactus.name, retrieved?.name)
    }

    @Test
    fun `should return null when cactus not found`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.CactusId.generate()

        // When
        val result = service.getCactus(nonExistentId)

        // Then
        assertNull(result)
    }

    @Test
    fun `should get all cactuses`() {
        // Given
        service.createCactus("Cactus 1", Money(10.0), CareLevel.EASY)
        service.createCactus("Cactus 2", Money(20.0), CareLevel.MEDIUM)
        service.createCactus("Cactus 3", Money(30.0), CareLevel.HARD)

        // When
        val allCactuses = service.getAllCactuses()

        // Then
        assertEquals(3, allCactuses.size)
    }

    @Test
    fun `should update cactus price successfully`() {
        // Given
        val cactus = service.createCactus("Test Cactus", Money(10.0), CareLevel.EASY)
        val newPrice = Money(15.0)

        // When
        val result = service.updateCactusPrice(cactus.id, newPrice)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(newPrice, result.getOrNull()?.price)
    }

    @Test
    fun `should fail to update price when cactus not found`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.CactusId.generate()
        val newPrice = Money(15.0)

        // When
        val result = service.updateCactusPrice(nonExistentId, newPrice)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should mark cactus as unavailable`() {
        // Given
        val cactus = service.createCactus("Test Cactus", Money(10.0), CareLevel.EASY)
        assertTrue(cactus.isAvailable)

        // When
        val result = service.markAsUnavailable(cactus.id)

        // Then
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()?.isAvailable ?: true)
    }

    @Test
    fun `should mark cactus as available`() {
        // Given
        val cactus = service.createCactus("Test Cactus", Money(10.0), CareLevel.EASY)
        service.markAsUnavailable(cactus.id)

        // When
        val result = service.markAsAvailable(cactus.id)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isAvailable ?: false)
    }

    @Test
    fun `should fail to mark non-existent cactus as unavailable`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.CactusId.generate()

        // When
        val result = service.markAsUnavailable(nonExistentId)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(IllegalArgumentException::class.java, result.exceptionOrNull())
    }

    @Test
    fun `should delete cactus successfully`() {
        // Given
        val cactus = service.createCactus("Test Cactus", Money(10.0), CareLevel.EASY)

        // When
        val deleted = service.deleteCactus(cactus.id)

        // Then
        assertTrue(deleted)
        assertNull(service.getCactus(cactus.id))
    }

    @Test
    fun `should return false when deleting non-existent cactus`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.CactusId.generate()

        // When
        val deleted = service.deleteCactus(nonExistentId)

        // Then
        assertFalse(deleted)
    }
}
