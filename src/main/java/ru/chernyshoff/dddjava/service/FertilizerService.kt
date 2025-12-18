package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.domain.Fertilizer

interface FertilizerService {
    fun create(fertilizer: Fertilizer): Fertilizer
    fun read(name: String): Fertilizer?
    fun update(name: String, updatedFertilizer: Fertilizer): Fertilizer?
    fun delete(name: String): Boolean
    fun list(): List<Fertilizer>
}
