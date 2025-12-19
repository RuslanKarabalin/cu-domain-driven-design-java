package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.dao.CustomerRepository
import ru.chernyshoff.dddjava.domain.*

interface CustomerService {
    fun registerCustomer(name: String, email: String, phone: String, address: Address): Result<Customer>
    fun getCustomer(id: CustomerId): Customer?
    fun getCustomerByEmail(email: String): Customer?
    fun getAllCustomers(): List<Customer>
    fun updateAddress(id: CustomerId, newAddress: Address): Result<Customer>
    fun updateContactInfo(id: CustomerId, newEmail: String, newPhone: String): Result<Customer>
    fun deleteCustomer(id: CustomerId): Boolean
}

class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
) : CustomerService {

    override fun registerCustomer(
        name: String,
        email: String,
        phone: String,
        address: Address
    ): Result<Customer> {
        return try {
            val existingCustomer = customerRepository.findByEmail(Email(email))
            if (existingCustomer != null) {
                return Result.failure(IllegalArgumentException("Customer with email $email already exists"))
            }

            val customer = Customer.create(name, email, phone, address)
            val saved = customerRepository.save(customer)
            Result.success(saved)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCustomer(id: CustomerId): Customer? {
        return customerRepository.findById(id)
    }

    override fun getCustomerByEmail(email: String): Customer? {
        return try {
            customerRepository.findByEmail(Email(email))
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override fun getAllCustomers(): List<Customer> {
        return customerRepository.findAll()
    }

    override fun updateAddress(id: CustomerId, newAddress: Address): Result<Customer> {
        val customer = customerRepository.findById(id)
            ?: return Result.failure(IllegalArgumentException("Customer not found: $id"))

        val updatedCustomer = customer.updateAddress(newAddress)
        return Result.success(customerRepository.save(updatedCustomer))
    }

    override fun updateContactInfo(id: CustomerId, newEmail: String, newPhone: String): Result<Customer> {
        val customer = customerRepository.findById(id)
            ?: return Result.failure(IllegalArgumentException("Customer not found: $id"))

        return try {
            val updatedCustomer = customer.updateContactInfo(newEmail, newPhone)
            Result.success(customerRepository.save(updatedCustomer))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun deleteCustomer(id: CustomerId): Boolean {
        return customerRepository.delete(id)
    }
}
