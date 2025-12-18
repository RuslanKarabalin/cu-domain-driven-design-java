package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.domain.Cactus

interface CactusService {
    fun create(cactus: Cactus): Cactus
    fun read(name: String): Cactus?
    fun update(name: String, updatedCactus: Cactus): Cactus?
    fun delete(name: String): Boolean
    fun list(): List<Cactus>
}
