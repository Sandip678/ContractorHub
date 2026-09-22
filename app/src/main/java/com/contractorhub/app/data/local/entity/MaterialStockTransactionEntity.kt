package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * PART 19 — Stock Tracker: Purchase / Issue / Return / Adjustment / Waste.
 * PART 6.2 मध्ये वेगळे MaterialPurchase आणि MaterialIssue entities सुचवले होते,
 * पण दोन्ही मुळात एकाच गोष्टीचं वेगळं रूप आहे — "material quantity ने current
 * stock बदलणारी नोंद". इथे तेच एकत्र, एका general ledger-style table मध्ये
 * ठेवलंय (LabourTransaction साठी वापरलेल्या पॅटर्नसारखं) — यामुळे material चा
 * पूर्ण movement history एकाच query ने मिळतो.
 *
 * `isIncrease` फक्त ADJUSTMENT type साठी वापरतो (स्टॉक वाढवायचा की कमी करायचा) —
 * बाकीच्या types चा direction fixed आहे (PURCHASE/RETURN = +, ISSUE/WASTE = -).
 */
@Entity(
    tableName = "material_stock_transactions",
    foreignKeys = [
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["materialId"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("materialId")]
)
data class MaterialStockTransactionEntity(
    @PrimaryKey val transactionId: String,
    val materialId: String,
    val siteId: String? = null,
    val supplierId: String? = null,
    val type: String,
    val quantity: Double,
    val isIncrease: Boolean = true,
    val rate: Long? = null,
    val date: Long,
    val note: String? = null
)

enum class StockTransactionType { PURCHASE, ISSUE, RETURN, ADJUSTMENT, WASTE }
