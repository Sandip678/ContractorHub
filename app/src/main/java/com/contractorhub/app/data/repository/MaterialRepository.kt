package com.contractorhub.app.data.repository

import androidx.room.withTransaction
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.MaterialEntity
import com.contractorhub.app.data.local.entity.MaterialStockTransactionEntity
import com.contractorhub.app.data.local.entity.StockTransactionType
import com.contractorhub.app.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * PART 5 — Architecture: UI → ViewModel → Repository → Room.
 * PART 69 — Database Transactions: प्रत्येक stock movement (ledger नोंद +
 * material.currentStock update) `db.withTransaction { }` मध्ये एकत्र —
 * अर्धवट राहणार नाही, error आला तर दोन्ही rollback होतात.
 */
class MaterialRepository(private val db: AppDatabase) {

    private val materialDao = db.materialDao()
    private val stockDao = db.materialStockTransactionDao()
    private val supplierDao = db.supplierDao()

    fun getMaterials(): Flow<List<MaterialEntity>> = materialDao.getAll()

    fun getLowStockCount(): Flow<Int> = materialDao.getLowStockCount()

    suspend fun getMaterialById(materialId: String): MaterialEntity? = materialDao.getById(materialId)

    suspend fun deleteMaterial(material: MaterialEntity) = materialDao.delete(material)

    fun getSuppliers(): Flow<List<SupplierEntity>> = supplierDao.getAll()

    suspend fun saveMaterial(
        existingMaterial: MaterialEntity?,
        name: String,
        category: String,
        unit: String,
        minimumStock: Double,
        rate: Long
    ) {
        val material = existingMaterial?.copy(
            name = name,
            category = category,
            unit = unit,
            minimumStock = minimumStock,
            rate = rate
        ) ?: MaterialEntity(
            materialId = UUID.randomUUID().toString(),
            name = name,
            category = category,
            unit = unit,
            minimumStock = minimumStock,
            rate = rate
        )
        materialDao.insert(material)
    }

    fun getStockTransactions(materialId: String): Flow<List<MaterialStockTransactionEntity>> =
        stockDao.getForMaterial(materialId)

    /** Purchase करताना supplier नाव आधीच असेल तर तोच वापरतो, नसेल तर नवीन तयार करतो. */
    suspend fun findOrCreateSupplier(name: String): String {
        val existing = supplierDao.findByName(name.trim())
        if (existing != null) return existing.supplierId
        val newId = UUID.randomUUID().toString()
        supplierDao.insert(SupplierEntity(supplierId = newId, name = name.trim()))
        return newId
    }

    suspend fun recordStockTransaction(
        materialId: String,
        siteId: String?,
        supplierId: String?,
        type: String,
        quantity: Double,
        isIncrease: Boolean,
        rate: Long?,
        date: Long,
        note: String?
    ) {
        val transaction = MaterialStockTransactionEntity(
            transactionId = UUID.randomUUID().toString(),
            materialId = materialId,
            siteId = siteId,
            supplierId = supplierId,
            type = type,
            quantity = quantity,
            isIncrease = isIncrease,
            rate = rate,
            date = date,
            note = note
        )
        db.withTransaction {
            stockDao.insert(transaction)
            materialDao.adjustStock(materialId, stockDelta(transaction))
        }
    }

    suspend fun deleteStockTransaction(transaction: MaterialStockTransactionEntity) {
        db.withTransaction {
            stockDao.delete(transaction)
            // उलट दिशेने stock परत फिरवायचा — delete म्हणजे ती नोंद कधी झालीच नव्हती.
            materialDao.adjustStock(transaction.materialId, -stockDelta(transaction))
        }
    }

    private fun stockDelta(transaction: MaterialStockTransactionEntity): Double = when (transaction.type) {
        StockTransactionType.PURCHASE.name, StockTransactionType.RETURN.name -> transaction.quantity
        StockTransactionType.ISSUE.name, StockTransactionType.WASTE.name -> -transaction.quantity
        StockTransactionType.ADJUSTMENT.name ->
            if (transaction.isIncrease) transaction.quantity else -transaction.quantity
        else -> 0.0
    }
}
