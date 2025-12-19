package ru.chernyshoff.dddjava.domain

import java.util.UUID

class Customer private constructor(
    val id: CustomerId,
    val name: String,
    val email: Email,
    val phone: PhoneNumber,
    val address: Address
) {
    companion object {
        fun create(name: String, email: String, phone: String, address: Address): Customer {
            require(name.isNotBlank()) { "Customer name cannot be blank" }
            return Customer(
                CustomerId.generate(),
                name,
                Email(email),
                PhoneNumber(phone),
                address
            )
        }

        fun reconstitute(
            id: CustomerId,
            name: String,
            email: String,
            phone: String,
            address: Address
        ): Customer {
            return Customer(id, name, Email(email), PhoneNumber(phone), address)
        }
    }

    fun updateAddress(newAddress: Address): Customer {
        return Customer(id, name, email, phone, newAddress)
    }

    fun updateContactInfo(newEmail: String, newPhone: String): Customer {
        return Customer(id, name, Email(newEmail), PhoneNumber(newPhone), address)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Customer) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

@JvmInline
value class CustomerId(val value: UUID) {
    companion object {
        fun generate() = CustomerId(UUID.randomUUID())
        fun from(uuid: UUID) = CustomerId(uuid)
        fun from(string: String) = CustomerId(UUID.fromString(string))
    }

    override fun toString(): String = value.toString()
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        require(value.matches(EMAIL_REGEX)) { "Invalid email format: $value" }
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }

    override fun toString(): String = value
}

@JvmInline
value class PhoneNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Phone number cannot be blank" }
        // Simple validation - can be enhanced based on requirements
        require(value.length >= 10) { "Phone number must be at least 10 characters" }
    }

    override fun toString(): String = value
}

data class Address(
    val street: String,
    val city: String,
    val postalCode: String
) {
    init {
        require(street.isNotBlank()) { "Street cannot be blank" }
        require(city.isNotBlank()) { "City cannot be blank" }
        require(postalCode.isNotBlank()) { "Postal code cannot be blank" }
    }

    fun fullAddress(): String = "$street, $city, $postalCode"
}
