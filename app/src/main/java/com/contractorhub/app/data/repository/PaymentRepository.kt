package com.contractorhub.app.data.repository

import androidx.room.withTransaction
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.BillPaymentEntity
import com.contractorhub.app.data.local.entity.ClientEntity
import com.contractorhub.app.data.local.entity.ClientPaymentEntity
import com.contractorhub.app.data.local.entity.DiaryEntryType
import com.contractorhub.app.data.local.entity.DiaryTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * PART 26 — Client Payment आणि Supplier(Bill) Payment दोन्ही.
 * PART 69 — Bill Payment: bill_payments नोंद + BillEntity.paid update एकत्र.
 * PART 7 — Client Payment save झाला की Diary Income entry आपोआप तयार होतो.
 */
class PaymentRepository(private val db: AppDatabase) {

    private val clientPaymentDao = db.clientPaymentDao()
    private val billPaymentDao = db.billPaymentDao()
    private val billDao = db.billDao()
    private val clientDao = db.clientDao()
    private val diaryDao = db.diaryDao()

    // ---- Client Payments ----

    fun getClientPayments(): Flow<List<ClientPaymentEntity>> = clientPaymentDao.getAll()

    fun getClientPaymentsTotalForSite(siteId: String): Flow<Long> =
        clientPaymentDao.getTotalForSite(siteId)

    fun getTodayTotal(): Flow<Long> {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis - 1
        return clientPaymentDao.getTotalForDateRange(start, end)
    }

    suspend fun getClientPaymentById(paymentId: String): ClientPaymentEntity? =
        clientPaymentDao.getById(paymentId)

    suspend fun deleteClientPayment(payment: ClientPaymentEntity) {
        db.withTransaction {
            clientPaymentDao.delete(payment)
            diaryDao.deleteById(diaryIdForClientPayment(payment.paymentId))
        }
    }

    /** Client नाव आधीच असेल तर तोच वापरतो (Sites च्या client पॅटर्नसारखं, duplicate टाळायला). */
    suspend fun findOrCreateClient(name: String): String {
        val existing = clientDao.findByName(name.trim())
        if (existing != null) return existing.clientId
        val newId = UUID.randomUUID().toString()
        clientDao.insert(ClientEntity(clientId = newId, name = name.trim()))
        return newId
    }

    suspend fun saveClientPayment(
        existingPayment: ClientPaymentEntity?,
        clientId: String?,
        siteId: String?,
        amount: Long,
        date: Long,
        paymentMethod: String,
        reference: String?,
        note: String?
    ) {
        val payment = existingPayment?.copy(
            clientId = clientId,
            siteId = siteId,
            amount = amount,
            date = date,
            paymentMethod = paymentMethod,
            reference = reference,
            note = note
        ) ?: ClientPaymentEntity(
            paymentId = UUID.randomUUID().toString(),
            clientId = clientId,
            siteId = siteId,
            amount = amount,
            date = date,
            paymentMethod = paymentMethod,
            reference = reference,
            note = note
        )

        db.withTransaction {
            clientPaymentDao.insert(payment)
            diaryDao.insert(
                DiaryTransactionEntity(
                    diaryId = diaryIdForClientPayment(payment.paymentId),
                    date = payment.date,
                    type = DiaryEntryType.INCOME.name,
                    category = null,
                    amount = payment.amount,
                    sourceType = "CLIENT_PAYMENT",
                    sourceId = payment.paymentId,
                    siteId = payment.siteId,
                    note = payment.note
                )
            )
        }
    }

    private fun diaryIdForClientPayment(paymentId: String) = "payment_$paymentId"

    // ---- Bill (Supplier) Payments ----

    fun getBillPayments(billId: String): Flow<List<BillPaymentEntity>> = billPaymentDao.getForBill(billId)

    suspend fun recordBillPayment(
        billId: String,
        supplierId: String?,
        amount: Long,
        date: Long,
        paymentMethod: String,
        note: String?
    ) {
        val payment = BillPaymentEntity(
            paymentId = UUID.randomUUID().toString(),
            billId = billId,
            supplierId = supplierId,
            amount = amount,
            date = date,
            paymentMethod = paymentMethod,
            note = note
        )
        db.withTransaction {
            billPaymentDao.insert(payment)
            val bill = billDao.getById(billId) ?: return@withTransaction
            billDao.insert(bill.copy(paid = bill.paid + amount))
        }
    }

    suspend fun deleteBillPayment(payment: BillPaymentEntity) {
        db.withTransaction {
            billPaymentDao.delete(payment)
            val bill = billDao.getById(payment.billId) ?: return@withTransaction
            billDao.insert(bill.copy(paid = bill.paid - payment.amount))
        }
    }
}
