package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.dao.FertilizerRepository
import ru.chernyshoff.dddjava.domain.*

interface FertilizerService {
    fun createFertilizer(name: String, price: Money, volumeMl: Int, recommendedFor: Set<CareLevel>): Fertilizer
    fun getFertilizer(id: FertilizerId): Fertilizer?
    fun getAllFertilizers(): List<Fertilizer>
    fun getFertilizersForCareLevel(careLevel: CareLevel): List<Fertilizer>
    fun deleteFertilizer(id: FertilizerId): Boolean
}

class FertilizerServiceImpl(
    private val fertilizerRepository: FertilizerRepository
) : FertilizerService {

    override fun createFertilizer(
        name: String,
        price: Money,
        volumeMl: Int,
        recommendedFor: Set<CareLevel>
    ): Fertilizer {
        val fertilizer = Fertilizer.create(name, price, volumeMl, recommendedFor)
        return fertilizerRepository.save(fertilizer)
    }

    override fun getFertilizer(id: FertilizerId): Fertilizer? {
        return fertilizerRepository.findById(id)
    }

    override fun getAllFertilizers(): List<Fertilizer> {
        return fertilizerRepository.findAll()
    }

    override fun getFertilizersForCareLevel(careLevel: CareLevel): List<Fertilizer> {
        return fertilizerRepository.findAll()
            .filter { it.isRecommendedFor(careLevel) }
    }

    override fun deleteFertilizer(id: FertilizerId): Boolean {
        return fertilizerRepository.delete(id)
    }
}
