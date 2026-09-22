package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_payments")
data class ClientPaymentEntity(
    @PrimaryKey val paymentId: String,
    val clientId: String? = null,
    val siteId: String? = null,
    val amount: Long,
    val date: Long,
    val paymentMethod: String = PaymentMethod.CASH.name,
    val reference: String? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
