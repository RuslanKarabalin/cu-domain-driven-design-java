package ru.chernyshoff.dddjava.dao.inmemory

import ru.chernyshoff.dddjava.dao.FertilizerRepository
import ru.chernyshoff.dddjava.domain.Fertilizer
import ru.chernyshoff.dddjava.domain.FertilizerId
import java.util.concurrent.ConcurrentHashMap

class InMemoryFertilizerRepository : FertilizerRepository {
    private val storage = ConcurrentHashMap<FertilizerId, Fertilizer>()

    override fun save(fertilizer: Fertilizer): Fertilizer {
        storage[fertilizer.id] = fertilizer
        return fertilizer
    }

    override fun findById(id: FertilizerId): Fertilizer? {
        return storage[id]
    }

    override fun findAll(): List<Fertilizer> {
        return storage.values.toList()
    }

    override fun delete(id: FertilizerId): Boolean {
        return storage.remove(id) != null
    }

    override fun existsById(id: FertilizerId): Boolean {
        return storage.containsKey(id)
    }

    fun clear() {
        storage.clear()
    }
}
