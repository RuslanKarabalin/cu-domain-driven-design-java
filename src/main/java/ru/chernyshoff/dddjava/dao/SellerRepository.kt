package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.Seller

interface SellerRepository {
    fun create(seller: Seller): Seller
    fun read(contactEmail: String): Seller?
    fun update(contactEmail: String, updatedSeller: Seller): Seller?
    fun delete(contactEmail: String): Boolean
    fun list(): List<Seller>
}
