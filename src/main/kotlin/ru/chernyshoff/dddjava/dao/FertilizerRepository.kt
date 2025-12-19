package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.Fertilizer
import ru.chernyshoff.dddjava.domain.FertilizerId

interface FertilizerRepository {
    fun save(fertilizer: Fertilizer): Fertilizer
    fun findById(id: FertilizerId): Fertilizer?
    fun findAll(): List<Fertilizer>
    fun delete(id: FertilizerId): Boolean
    fun existsById(id: FertilizerId): Boolean
}
