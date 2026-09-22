package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * PART 26 — Supplier Payment: Supplier, Bill, Amount, Date, Payment method.
 * इथे थेट `billId` ला जोडलंय (Bill.paid हा त्यावरूनच atomically अपडेट होतो,
 * MaterialStockTransaction/currentStock पॅटर्नसारखं — PART 69).
 */
@Entity(
    tableName = "bill_payments",
    foreignKeys = [
        ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["billId"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("billId")]
)
data class BillPaymentEntity(
    @PrimaryKey val paymentId: String,
    val billId: String,
    val supplierId: String? = null,
    val amount: Long,
    val date: Long,
    val paymentMethod: String = PaymentMethod.CASH.name,
    val note: String? = null
)
