package ru.chernyshoff.dddjava.dao

import ru.chernyshoff.dddjava.domain.Email
import ru.chernyshoff.dddjava.domain.Seller
import ru.chernyshoff.dddjava.domain.SellerId

interface SellerRepository {
    fun save(seller: Seller): Seller
    fun findById(id: SellerId): Seller?
    fun findByEmail(email: Email): Seller?
    fun findAllActive(): List<Seller>
    fun findAll(): List<Seller>
    fun delete(id: SellerId): Boolean
    fun existsById(id: SellerId): Boolean
}
