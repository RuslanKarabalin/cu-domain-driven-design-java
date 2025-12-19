package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.Cactus
import ru.chernyshoff.dddjava.domain.CactusId

interface CactusRepository {
    fun save(cactus: Cactus): Cactus
    fun findById(id: CactusId): Cactus?
    fun findAll(): List<Cactus>
    fun delete(id: CactusId): Boolean
    fun existsById(id: CactusId): Boolean
}
