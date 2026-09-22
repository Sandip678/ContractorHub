package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val expenseId: String,
    val siteId: String? = null,
    val category: String,
    val amount: Long,
    val date: Long,
    val paymentMethod: String = PaymentMethod.CASH.name,
    val billId: String? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ExpenseCategory {
    MATERIAL, LABOUR, ELECTRICAL, PLUMBING, TRANSPORT, FUEL, MACHINERY, TOOLS, FOOD, OTHER
}
