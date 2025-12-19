package ru.chernyshoff.dddjava.domain

import java.util.UUID


class Cactus private constructor(
    val id: CactusId,
    val name: String,
    val price: Money,
    val careLevel: CareLevel,
    private var _isAvailable: Boolean = true
) {
    val isAvailable: Boolean
        get() = _isAvailable

    companion object {
        fun create(name: String, price: Money, careLevel: CareLevel): Cactus {
            require(name.isNotBlank()) { "Cactus name cannot be blank" }
            return Cactus(CactusId.generate(), name, price, careLevel)
        }

        fun reconstitute(id: CactusId, name: String, price: Money, careLevel: CareLevel, isAvailable: Boolean): Cactus {
            return Cactus(id, name, price, careLevel, isAvailable)
        }
    }

    fun markAsUnavailable() {
        _isAvailable = false
    }

    fun markAsAvailable() {
        _isAvailable = true
    }

    fun updatePrice(newPrice: Money): Cactus {
        return Cactus(id, name, newPrice, careLevel, _isAvailable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cactus) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

enum class CareLevel {
    EASY, MEDIUM, HARD
}

@JvmInline
value class CactusId(val value: UUID) {
    companion object {
        fun generate() = CactusId(UUID.randomUUID())
        fun from(uuid: UUID) = CactusId(uuid)
        fun from(string: String) = CactusId(UUID.fromString(string))
    }

    override fun toString(): String = value.toString()
}

data class Money(val amount: Double) {
    init {
        require(amount >= 0) { "Money amount cannot be negative, but was: $amount" }
    }

    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun times(multiplier: Int) = Money(amount * multiplier)

    companion object {
        val ZERO = Money(0.0)
    }
}
