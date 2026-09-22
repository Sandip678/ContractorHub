package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "labour_transactions",
    foreignKeys = [
        ForeignKey(
            entity = LabourEntity::class,
            parentColumns = ["labourId"],
            childColumns = ["labourId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("labourId")]
)
data class LabourTransactionEntity(
    @PrimaryKey val transactionId: String,
    val labourId: String,
    val siteId: String? = null,
    val date: Long,
    val type: String,
    val amount: Long,
    val note: String? = null
)

/** PART 16 — Advance / Angavar / Payment / Adjustment / Settlement. Wage पूर्णपणे
 * attendance वरून auto-calculate होतो (PART 17), त्यामुळे इथे वेगळा WAGE type
 * नाही — कोणी manually जास्तीचं wage adjustment द्यायचं असेल तर ADJUSTMENT वापरायचा. */
enum class LabourTransactionType { ADVANCE, ANGAVAR, PAYMENT, ADJUSTMENT, SETTLEMENT }
