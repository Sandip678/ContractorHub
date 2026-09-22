package com.contractorhub.app.data.repository

import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.BillEntity
import com.contractorhub.app.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * PART 5 — Architecture: UI → ViewModel → Repository → Room.
 *
 * PART 7 — Automatic Accounting Flow: roadmap नुसार Bill save झाल्यावर Site
 * Expense, Supplier Ledger, Diary, P&L सगळीकडे आपोआप पोहोचायला हवं. Expense/
 * Diary/P&L modules अजून अस्तित्वात नाहीत (ते Phase 10-13 मध्ये येतील) —
 * तोपर्यंत फक्त Bill record आणि (material purchase असल्यास वेगळा) stock ledger
 * एवढंच होतं. त्या phases मध्ये इथेच पुढचं जोडायचं — Bill स्वतःचा data model
 * आधीच त्या दिशेने तयार आहे (siteId, supplierId दोन्ही स्टोअर होतात).
 */
class BillRepository(private val db: AppDatabase) {

    private val billDao = db.billDao()
    private val supplierDao = db.supplierDao()

    fun getBills(): Flow<List<BillEntity>> = billDao.getAll()

    suspend fun getBillById(billId: String): BillEntity? = billDao.getById(billId)

    suspend fun deleteBill(bill: BillEntity) = billDao.delete(bill)

    fun getSuppliers() = supplierDao.getAll()

    suspend fun findOrCreateSupplier(name: String): String {
        val existing = supplierDao.findByName(name.trim())
        if (existing != null) return existing.supplierId
        val newId = UUID.randomUUID().toString()
        supplierDao.insert(SupplierEntity(supplierId = newId, name = name.trim()))
        return newId
    }

    /** PART 24 — Duplicate Bill Check. Bill save करण्यापूर्वी call करायचं. */
    suspend fun findPossibleDuplicate(
        supplierId: String?,
        billNumber: String?,
        billDate: Long,
        total: Long
    ): BillEntity? {
        if (supplierId == null || billNumber.isNullOrBlank()) return null
        return billDao.findPossibleDuplicate(supplierId, billNumber, billDate, total)
    }

    suspend fun saveBill(
        existingBill: BillEntity?,
        supplierId: String?,
        siteId: String?,
        billNumber: String?,
        billDate: Long,
        subtotal: Long,
        tax: Long,
        total: Long,
        paid: Long,
        imagePath: String?,
        notes: String?
    ) {
        val bill = existingBill?.copy(
            supplierId = supplierId,
            siteId = siteId,
            billNumber = billNumber,
            billDate = billDate,
            subtotal = subtotal,
            tax = tax,
            total = total,
            paid = paid,
            imagePath = imagePath ?: existingBill.imagePath,
            notes = notes
        ) ?: BillEntity(
            billId = UUID.randomUUID().toString(),
            supplierId = supplierId,
            siteId = siteId,
            billNumber = billNumber,
            billDate = billDate,
            subtotal = subtotal,
            tax = tax,
            total = total,
            paid = paid,
            imagePath = imagePath,
            notes = notes
        )
        billDao.insert(bill)
    }
}
