package ru.chernyshoff.dddjava.dao.inmemory

import ru.chernyshoff.dddjava.dao.CustomerRepository
import ru.chernyshoff.dddjava.domain.Customer
import ru.chernyshoff.dddjava.domain.CustomerId
import ru.chernyshoff.dddjava.domain.Email
import java.util.concurrent.ConcurrentHashMap

class InMemoryCustomerRepository : CustomerRepository {
    private val storage = ConcurrentHashMap<CustomerId, Customer>()

    override fun save(customer: Customer): Customer {
        storage[customer.id] = customer
        return customer
    }

    override fun findById(id: CustomerId): Customer? {
        return storage[id]
    }

    override fun findByEmail(email: Email): Customer? {
        return storage.values.find { it.email == email }
    }

    override fun findAll(): List<Customer> {
        return storage.values.toList()
    }

    override fun delete(id: CustomerId): Boolean {
        return storage.remove(id) != null
    }

    override fun existsById(id: CustomerId): Boolean {
        return storage.containsKey(id)
    }

    fun clear() {
        storage.clear()
    }
}
