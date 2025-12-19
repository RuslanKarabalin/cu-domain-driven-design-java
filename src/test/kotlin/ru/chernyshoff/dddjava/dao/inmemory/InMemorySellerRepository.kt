package ru.chernyshoff.dddjava.dao.inmemory

import ru.chernyshoff.dddjava.dao.SellerRepository
import ru.chernyshoff.dddjava.domain.Email
import ru.chernyshoff.dddjava.domain.Seller
import ru.chernyshoff.dddjava.domain.SellerId
import java.util.concurrent.ConcurrentHashMap

class InMemorySellerRepository : SellerRepository {
    private val storage = ConcurrentHashMap<SellerId, Seller>()

    override fun save(seller: Seller): Seller {
        storage[seller.id] = seller
        return seller
    }

    override fun findById(id: SellerId): Seller? {
        return storage[id]
    }

    override fun findByEmail(email: Email): Seller? {
        return storage.values.find { it.contactEmail == email }
    }

    override fun findAllActive(): List<Seller> {
        return storage.values.filter { it.isActive }
    }

    override fun findAll(): List<Seller> {
        return storage.values.toList()
    }

    override fun delete(id: SellerId): Boolean {
        return storage.remove(id) != null
    }

    override fun existsById(id: SellerId): Boolean {
        return storage.containsKey(id)
    }

    fun clear() {
        storage.clear()
    }
}
