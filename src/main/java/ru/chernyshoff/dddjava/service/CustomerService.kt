package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.domain.Customer

interface CustomerService {
    fun create(customer: Customer): Customer
    fun read(email: String): Customer?
    fun update(email: String, updatedCustomer: Customer): Customer?
    fun delete(email: String): Boolean
    fun list(): List<Customer>
}
