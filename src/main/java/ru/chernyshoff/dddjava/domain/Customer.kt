package ru.chernyshoff.dddjava.domain

data class Customer(
    val name: String,
    val email: String,
    val phone: String,
    val address: Address,
    val orders: List<Order>
)

data class Address(
    val street: String,
    val city: String,
    val postalCode: String,
)
