package ru.chernyshoff.dddjava.domain

import java.util.UUID

class Fertilizer private constructor(
    val id: FertilizerId,
    val name: String,
    val price: Money,
    val volume: Volume,
    val recommendedFor: Set<CareLevel>
) {
    companion object {
        fun create(name: String, price: Money, volumeMl: Int, recommendedFor: Set<CareLevel>): Fertilizer {
            require(name.isNotBlank()) { "Fertilizer name cannot be blank" }
            require(recommendedFor.isNotEmpty()) { "Fertilizer must be recommended for at least one care level" }
            return Fertilizer(
                FertilizerId.generate(),
                name,
                price,
                Volume(volumeMl),
                recommendedFor
            )
        }

        fun reconstitute(
            id: FertilizerId,
            name: String,
            price: Money,
            volumeMl: Int,
            recommendedFor: Set<CareLevel>
        ): Fertilizer {
            return Fertilizer(id, name, price, Volume(volumeMl), recommendedFor)
        }
    }

    fun isRecommendedFor(careLevel: CareLevel): Boolean {
        return careLevel in recommendedFor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Fertilizer) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

@JvmInline
value class FertilizerId(val value: UUID) {
    companion object {
        fun generate() = FertilizerId(UUID.randomUUID())
        fun from(uuid: UUID) = FertilizerId(uuid)
        fun from(string: String) = FertilizerId(UUID.fromString(string))
    }

    override fun toString(): String = value.toString()
}

@JvmInline
value class Volume(val milliliters: Int) {
    init {
        require(milliliters > 0) { "Volume must be positive, but was: $milliliters" }
    }
}
