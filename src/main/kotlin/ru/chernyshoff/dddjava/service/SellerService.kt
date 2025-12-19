package ru.chernyshoff.dddjava.service

import ru.chernyshoff.dddjava.dao.SellerRepository
import ru.chernyshoff.dddjava.domain.*

interface SellerService {
    fun registerSeller(storeName: String, contactEmail: String): Result<Seller>
    fun getSeller(id: SellerId): Seller?
    fun getSellerByEmail(email: String): Seller?
    fun getAllActiveSellers(): List<Seller>
    fun getAllSellers(): List<Seller>
    fun deactivateSeller(id: SellerId): Result<Seller>
    fun activateSeller(id: SellerId): Result<Seller>
    fun updateContactEmail(id: SellerId, newEmail: String): Result<Seller>
    fun deleteSeller(id: SellerId): Boolean
}

class SellerServiceImpl(
    private val sellerRepository: SellerRepository
) : SellerService {

    override fun registerSeller(storeName: String, contactEmail: String): Result<Seller> {
        return try {
            val existingSeller = sellerRepository.findByEmail(Email(contactEmail))
            if (existingSeller != null) {
                return Result.failure(IllegalArgumentException("Seller with email $contactEmail already exists"))
            }

            val seller = Seller.create(storeName, contactEmail)
            val saved = sellerRepository.save(seller)
            Result.success(saved)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSeller(id: SellerId): Seller? {
        return sellerRepository.findById(id)
    }

    override fun getSellerByEmail(email: String): Seller? {
        return try {
            sellerRepository.findByEmail(Email(email))
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override fun getAllActiveSellers(): List<Seller> {
        return sellerRepository.findAllActive()
    }

    override fun getAllSellers(): List<Seller> {
        return sellerRepository.findAll()
    }

    override fun deactivateSeller(id: SellerId): Result<Seller> {
        val seller = sellerRepository.findById(id)
            ?: return Result.failure(IllegalArgumentException("Seller not found: $id"))

        return try {
            seller.deactivate()
            Result.success(sellerRepository.save(seller))
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    override fun activateSeller(id: SellerId): Result<Seller> {
        val seller = sellerRepository.findById(id)
            ?: return Result.failure(IllegalArgumentException("Seller not found: $id"))

        return try {
            seller.activate()
            Result.success(sellerRepository.save(seller))
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    override fun updateContactEmail(id: SellerId, newEmail: String): Result<Seller> {
        val seller = sellerRepository.findById(id)
            ?: return Result.failure(IllegalArgumentException("Seller not found: $id"))

        return try {
            val updatedSeller = seller.updateContactEmail(newEmail)
            Result.success(sellerRepository.save(updatedSeller))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun deleteSeller(id: SellerId): Boolean {
        return sellerRepository.delete(id)
    }
}
