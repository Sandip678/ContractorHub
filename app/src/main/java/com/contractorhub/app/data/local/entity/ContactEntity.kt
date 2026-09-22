package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PART 38 — Contact Module. Site शी लिंक ऐच्छिक (siteId nullable) — सामान्य
 * business contact (Supplier, Transporter) कोणत्याही specific site शी बांधलेला
 * नसू शकतो; Site-specific असेल तर siteId भरला जातो (PART 39).
 */
@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val contactId: String,
    val name: String,
    val category: String,
    val mobile: String? = null,
    val alternateMobile: String? = null,
    val company: String? = null,
    val address: String? = null,
    val siteId: String? = null,
    val notes: String? = null,
    val favorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ContactCategory {
    ENGINEER, ARCHITECT, CONTRACTOR, SUB_CONTRACTOR, LABOUR_CONTRACTOR,
    ELECTRICIAN, PLUMBER, SUPPLIER, TRANSPORTER, HARDWARE_SHOP,
    MACHINERY, CLIENT, EMERGENCY, OTHER
}
