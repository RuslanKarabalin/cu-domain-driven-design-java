package ru.chernyshoff.dddjava.domain

import java.util.UUID

class Seller private constructor(
    val id: SellerId,
    val storeName: String,
    val contactEmail: Email,
    private var _isActive: Boolean = true
) {
    val isActive: Boolean
        get() = _isActive

    companion object {
        fun create(storeName: String, contactEmail: String): Seller {
            require(storeName.isNotBlank()) { "Store name cannot be blank" }
            return Seller(
                SellerId.generate(),
                storeName,
                Email(contactEmail)
            )
        }

        fun reconstitute(id: SellerId, storeName: String, contactEmail: String, isActive: Boolean): Seller {
            return Seller(id, storeName, Email(contactEmail), isActive)
        }
    }

    fun deactivate() {
        require(_isActive) { "Seller is already inactive" }
        _isActive = false
    }

    fun activate() {
        require(!_isActive) { "Seller is already active" }
        _isActive = true
    }

    fun updateContactEmail(newEmail: String): Seller {
        return Seller(id, storeName, Email(newEmail), _isActive)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Seller) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

@JvmInline
value class SellerId(val value: UUID) {
    companion object {
        fun generate() = SellerId(UUID.randomUUID())
        fun from(uuid: UUID) = SellerId(uuid)
        fun from(string: String) = SellerId(UUID.fromString(string))
    }

    override fun toString(): String = value.toString()
}
