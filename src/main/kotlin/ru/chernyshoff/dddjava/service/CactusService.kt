package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.dao.CactusRepository
import ru.chernyshoff.dddjava.domain.*

interface CactusService {
    fun createCactus(name: String, price: Money, careLevel: CareLevel): Cactus
    fun getCactus(id: CactusId): Cactus?
    fun getAllCactuses(): List<Cactus>
    fun updateCactusPrice(id: CactusId, newPrice: Money): Result<Cactus>
    fun markAsUnavailable(id: CactusId): Result<Cactus>
    fun markAsAvailable(id: CactusId): Result<Cactus>
    fun deleteCactus(id: CactusId): Boolean
}

class CactusServiceImpl(
    private val cactusRepository: CactusRepository
) : CactusService {

    override fun createCactus(name: String, price: Money, careLevel: CareLevel): Cactus {
        val cactus = Cactus.create(name, price, careLevel)
        return cactusRepository.save(cactus)
    }

    override fun getCactus(id: CactusId): Cactus? {
        return cactusRepository.findById(id)
    }

    override fun getAllCactuses(): List<Cactus> {
        return cactusRepository.findAll()
    }

    override fun updateCactusPrice(id: CactusId, newPrice: Money): Result<Cactus> {
        val cactus = cactusRepository.findById(id)
            ?: return Result.failure(IllegalArgumentException("Cactus not found: $id"))

        val updatedCactus = cactus.updatePrice(newPrice)
        return Result.success(cactusRepository.save(updatedCactus))
    }

    override fun markAsUnavailable(id: CactusId): Result<Cactus> {
        val cactus = cactusRepository.findById(id)
            ?: return Result.failure(IllegalArgumentException("Cactus not found: $id"))

        cactus.markAsUnavailable()
        return Result.success(cactusRepository.save(cactus))
    }

    override fun markAsAvailable(id: CactusId): Result<Cactus> {
        val cactus = cactusRepository.findById(id)
            ?: return Result.failure(IllegalArgumentException("Cactus not found: $id"))

        cactus.markAsAvailable()
        return Result.success(cactusRepository.save(cactus))
    }

    override fun deleteCactus(id: CactusId): Boolean {
        return cactusRepository.delete(id)
    }
}
