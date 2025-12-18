package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.Fertilizer

interface FertilizerRepository {
    fun create(fertilizer: Fertilizer): Fertilizer
    fun read(name: String): Fertilizer?
    fun update(name: String, updatedFertilizer: Fertilizer): Fertilizer?
    fun delete(name: String): Boolean
    fun list(): List<Fertilizer>
}
