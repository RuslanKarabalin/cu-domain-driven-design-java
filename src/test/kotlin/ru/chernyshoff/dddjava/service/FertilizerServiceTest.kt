package ru.chernyshoff.dddjava.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.chernyshoff.dddjava.dao.inmemory.InMemoryFertilizerRepository
import ru.chernyshoff.dddjava.domain.CareLevel
import ru.chernyshoff.dddjava.domain.Money

class FertilizerServiceTest {
    private lateinit var repository: InMemoryFertilizerRepository
    private lateinit var service: FertilizerService

    @BeforeEach
    fun setUp() {
        repository = InMemoryFertilizerRepository()
        service = FertilizerServiceImpl(repository)
    }

    @Test
    fun `should create fertilizer with valid data`() {
        // Given
        val name = "Cactus Boost"
        val price = Money(8.99)
        val volumeMl = 250
        val recommendedFor = setOf(CareLevel.EASY, CareLevel.MEDIUM)

        // When
        val fertilizer = service.createFertilizer(name, price, volumeMl, recommendedFor)

        // Then
        assertNotNull(fertilizer.id)
        assertEquals(name, fertilizer.name)
        assertEquals(price, fertilizer.price)
        assertEquals(volumeMl, fertilizer.volume.milliliters)
        assertEquals(recommendedFor, fertilizer.recommendedFor)
    }

    @Test
    fun `should throw exception when creating fertilizer with blank name`() {
        // Given
        val blankName = "  "
        val price = Money(8.99)
        val volumeMl = 250
        val recommendedFor = setOf(CareLevel.EASY)

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            service.createFertilizer(blankName, price, volumeMl, recommendedFor)
        }
    }

    @Test
    fun `should throw exception when creating fertilizer with zero volume`() {
        // Given
        val name = "Cactus Boost"
        val price = Money(8.99)
        val zeroVolume = 0
        val recommendedFor = setOf(CareLevel.EASY)

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            service.createFertilizer(name, price, zeroVolume, recommendedFor)
        }
    }

    @Test
    fun `should throw exception when creating fertilizer with empty recommendation set`() {
        // Given
        val name = "Cactus Boost"
        val price = Money(8.99)
        val volumeMl = 250
        val emptyRecommendations = emptySet<CareLevel>()

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            service.createFertilizer(name, price, volumeMl, emptyRecommendations)
        }
    }

    @Test
    fun `should retrieve fertilizer by id`() {
        // Given
        val fertilizer = service.createFertilizer(
            "Test Fertilizer",
            Money(10.0),
            250,
            setOf(CareLevel.EASY)
        )

        // When
        val retrieved = service.getFertilizer(fertilizer.id)

        // Then
        assertNotNull(retrieved)
        assertEquals(fertilizer.id, retrieved?.id)
        assertEquals(fertilizer.name, retrieved?.name)
    }

    @Test
    fun `should return null when fertilizer not found`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.FertilizerId.generate()

        // When
        val result = service.getFertilizer(nonExistentId)

        // Then
        assertNull(result)
    }

    @Test
    fun `should get all fertilizers`() {
        // Given
        service.createFertilizer("Fertilizer 1", Money(10.0), 250, setOf(CareLevel.EASY))
        service.createFertilizer("Fertilizer 2", Money(15.0), 500, setOf(CareLevel.MEDIUM))
        service.createFertilizer("Fertilizer 3", Money(20.0), 1000, setOf(CareLevel.HARD))

        // When
        val allFertilizers = service.getAllFertilizers()

        // Then
        assertEquals(3, allFertilizers.size)
    }

    @Test
    fun `should get fertilizers for specific care level`() {
        // Given
        service.createFertilizer("Easy Fertilizer", Money(10.0), 250, setOf(CareLevel.EASY))
        service.createFertilizer("Medium Fertilizer", Money(15.0), 500, setOf(CareLevel.MEDIUM))
        service.createFertilizer("Multi Fertilizer", Money(20.0), 1000, setOf(CareLevel.EASY, CareLevel.MEDIUM))

        // When
        val easyFertilizers = service.getFertilizersForCareLevel(CareLevel.EASY)

        // Then
        assertEquals(2, easyFertilizers.size)
        assertTrue(easyFertilizers.all { it.isRecommendedFor(CareLevel.EASY) })
    }

    @Test
    fun `should return empty list when no fertilizers match care level`() {
        // Given
        service.createFertilizer("Easy Fertilizer", Money(10.0), 250, setOf(CareLevel.EASY))
        service.createFertilizer("Medium Fertilizer", Money(15.0), 500, setOf(CareLevel.MEDIUM))

        // When
        val hardFertilizers = service.getFertilizersForCareLevel(CareLevel.HARD)

        // Then
        assertTrue(hardFertilizers.isEmpty())
    }

    @Test
    fun `should verify fertilizer is recommended for correct care levels`() {
        // Given
        val recommendedFor = setOf(CareLevel.EASY, CareLevel.MEDIUM)
        val fertilizer = service.createFertilizer("Multi Fertilizer", Money(15.0), 500, recommendedFor)

        // When & Then
        assertTrue(fertilizer.isRecommendedFor(CareLevel.EASY))
        assertTrue(fertilizer.isRecommendedFor(CareLevel.MEDIUM))
        assertFalse(fertilizer.isRecommendedFor(CareLevel.HARD))
    }

    @Test
    fun `should delete fertilizer successfully`() {
        // Given
        val fertilizer = service.createFertilizer("Test Fertilizer", Money(10.0), 250, setOf(CareLevel.EASY))

        // When
        val deleted = service.deleteFertilizer(fertilizer.id)

        // Then
        assertTrue(deleted)
        assertNull(service.getFertilizer(fertilizer.id))
    }

    @Test
    fun `should return false when deleting non-existent fertilizer`() {
        // Given
        val nonExistentId = ru.chernyshoff.dddjava.domain.FertilizerId.generate()

        // When
        val deleted = service.deleteFertilizer(nonExistentId)

        // Then
        assertFalse(deleted)
    }
}
