package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey val billId: String,
    val supplierId: String? = null,
    val siteId: String? = null,
    val billNumber: String? = null,
    val billDate: Long,
    val subtotal: Long = 0,
    val tax: Long = 0,
    val total: Long = 0,
    val paid: Long = 0,
    val imagePath: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /** PART 6.2 मध्ये `balance` वेगळा column होता — इथे derived ठेवलाय, कारण तो
     * नेहमी total - paid इतकाच असतो; दोन ठिकाणी sync ठेवायची गरज उरत नाही. */
    val balance: Long get() = total - paid
}

/** PART 21 — Bill List filters. साठवलेला नाही, paid/total वरून प्रत्येक वेळी ठरतो. */
enum class BillStatus { PAID, UNPAID, PARTIAL }

fun BillEntity.status(): BillStatus = when {
    paid <= 0 -> BillStatus.UNPAID
    paid >= total -> BillStatus.PAID
    else -> BillStatus.PARTIAL
}
