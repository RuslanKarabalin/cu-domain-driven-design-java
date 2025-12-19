package ru.chernyshoff.dddjava.dao.inmemory

import ru.chernyshoff.dddjava.dao.CactusRepository
import ru.chernyshoff.dddjava.domain.Cactus
import ru.chernyshoff.dddjava.domain.CactusId
import java.util.concurrent.ConcurrentHashMap

class InMemoryCactusRepository : CactusRepository {
    private val storage = ConcurrentHashMap<CactusId, Cactus>()

    override fun save(cactus: Cactus): Cactus {
        storage[cactus.id] = cactus
        return cactus
    }

    override fun findById(id: CactusId): Cactus? {
        return storage[id]
    }

    override fun findAll(): List<Cactus> {
        return storage.values.toList()
    }

    override fun delete(id: CactusId): Boolean {
        return storage.remove(id) != null
    }

    override fun existsById(id: CactusId): Boolean {
        return storage.containsKey(id)
    }

    fun clear() {
        storage.clear()
    }
}
