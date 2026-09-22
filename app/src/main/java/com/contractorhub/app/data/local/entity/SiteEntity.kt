package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * PART 58 — Database Relationship Principle: Client → Site → ... सुरू इथून होतं.
 * clientId वर SET_NULL ठेवलंय जेणेकरून client delete झाला तरी site चा financial
 * data (contractAmount वगैरे) हरवत नाही — फक्त client-link तुटतो.
 */
@Entity(
    tableName = "sites",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("clientId")]
)
data class SiteEntity(
    @PrimaryKey val siteId: String,
    val siteName: String,
    val clientId: String? = null,
    val address: String? = null,
    val startDate: Long? = null,
    val expectedEndDate: Long? = null,
    val contractAmount: Long = 0,
    val status: String = SiteStatus.ACTIVE.name,
    val progress: Int = 0,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class SiteStatus {
    ACTIVE, ON_HOLD, COMPLETED
}
