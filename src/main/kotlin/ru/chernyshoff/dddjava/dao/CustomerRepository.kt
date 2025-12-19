package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.Customer
import ru.chernyshoff.dddjava.domain.CustomerId
import ru.chernyshoff.dddjava.domain.Email

interface CustomerRepository {
    fun save(customer: Customer): Customer
    fun findById(id: CustomerId): Customer?
    fun findByEmail(email: Email): Customer?
    fun findAll(): List<Customer>
    fun delete(id: CustomerId): Boolean
    fun existsById(id: CustomerId): Boolean
}
