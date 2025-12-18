package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.Customer

interface CustomerRepository {
    fun create(customer: Customer): Customer
    fun read(email: String): Customer?
    fun update(email: String, updatedCustomer: Customer): Customer?
    fun delete(email: String): Boolean
    fun list(): List<Customer>
}
