package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey val supplierId: String,
    val name: String,
    val mobile: String? = null,
    val address: String? = null,
    val category: String? = null
)
